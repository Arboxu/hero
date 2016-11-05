package com.arbo.hero;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.arbo.hero.javabean.ChampionListBean;
import com.arbo.hero.network.Constant;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static cn.bmob.v3.Bmob.getApplicationContext;


@SuppressLint("ValidFragment")
public class GridFragment extends Fragment
{
    private boolean noneImageNum = false ;
    private int newsType = 0;
    private PullToRefreshGridView gridview;
    private List<ChampionListBean.AllBean> ls = null;

    private final int FIRSTLOAD = 1;
    private final int RELOAD = 2;
    private final int ENDLOAD = 3;

    Handler handler  =  new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case FIRSTLOAD:
                    ls= (List<ChampionListBean.AllBean>) msg.obj;
                    loadHero(ls);
                    break;
                case RELOAD:
                    loadHero(ls);
                    break;
                case ENDLOAD:
                    updateGridview(msg);
                    gridview.onRefreshComplete();
                    break;
                case 4:
                    SearchActivity.animationDialog.dismiss();
                    String tempresponse = (String) msg.obj;
                    Constant.setResponsedata(tempresponse);
                    firstload();
                    break;
            }
        }
    };
    public GridFragment()
    {}
    public GridFragment(int newsType)
    {
        this.newsType = newsType;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.tab_item_fragment_main, null);

        gridview = (PullToRefreshGridView) view.findViewById(R.id.GridView);
        initIndicator();
        gridview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<GridView>() {
            @Override
            public void onRefresh(PullToRefreshBase<GridView> refreshView) {
                String label = DateUtils.formatDateTime(
                        getApplicationContext(),
                        System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME
                                | DateUtils.FORMAT_SHOW_DATE
                                | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy()
                        .setLastUpdatedLabel(label);
                loadHero(ls);

            }
        });
        firstload();
        return view;
    }

    private void initIndicator()
    {
        ILoadingLayout startLabels = gridview
                .getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("往下点...");// 刚下拉时，显示的提示
        startLabels.setRefreshingLabel("全军出击...");// 刷新时
        startLabels.setReleaseLabel("松开刷新...");// 下来达到一定距离时，显示的提示
    }

    private void requestImage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    Message message = handler.obtainMessage();
                    message.what = RELOAD;
                    handler.sendMessage(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void firstload(){
        ChampionListBean championListBean ;
        championListBean = JSON.parseObject(Constant.responsedata,ChampionListBean.class);
        if(Constant.responsedata == null){
            requestData(Constant.API_ALL_HERO);
            Toast.makeText(getActivity(),"网络异常,下拉刷新试试",Toast.LENGTH_SHORT).show();
            return;
        }
        final List<ChampionListBean.AllBean> allBeen;
        allBeen = championListBean.getAll();
        final ArrayList<HashMap<String, Object>> meumList = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < allBeen.size(); i++) {
            final ChampionListBean.AllBean allBean = allBeen.get(i);
            boolean ismatch = false;
            switch (newsType)
            {
                case 0:
                    if(allBean.getTags().contains("assassin"))
                        ismatch = true;
                    break;
                case 1:
                    if(allBean.getTags().contains("fighter"))
                        ismatch = true;
                    break;
                case 2:
                    if(allBean.getTags().contains("mage"))
                        ismatch = true;
                    break;
                case 3:
                    if(allBean.getTags().contains("tank"))
                        ismatch = true;
                    break;
                case 4:
                    if(allBean.getTags().contains("marksman"))
                        ismatch = true;
                    break;
                case 5:
                    if(allBean.getTags().contains("support"))
                        ismatch = true;
                    break;
                case 6:
                    ismatch = true;
                    break;
                default:
                    ismatch = true;
                    break;
            }
            HashMap<String, Object> map = new HashMap<String, Object>();
            if(ismatch){
                map.put("ItemName",allBean.getEnName());
                if(allBean.getTitle().length()>5)
                {
                    String title = allBean.getTitle().substring(0,4)+"..";
                    map.put("ItemText",title);

                }else{
                    map.put("ItemText", allBean.getTitle());
                }
                meumList.add(map);
            }
        }
        SimpleAdapter saItem = new SimpleAdapter(getContext(),
                meumList, //数据源
                R.layout.gridview_item, //xml实现
                new String[]{"ItemImage", "ItemText"}, //对应map的Key
                new int[]{R.id.ItemImage, R.id.ItemText});  //对应R的Id
        //通过setViewBinder将bitmap转化后使用
        saItem.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object bitmapData, String s) {
                if(view instanceof ImageView && bitmapData instanceof Bitmap){
                    ImageView i = (ImageView)view;
                    i.setImageBitmap((Bitmap) bitmapData);
                    return true;
                }
                return false;
            }
        });

        Thread thread = new Thread(){
            public void run(){
                Message message = handler.obtainMessage();
                message.obj = allBeen;
                message.what = FIRSTLOAD;
                handler.sendMessage(message);
            }
        };
        thread.start();
        //添加Item到网格中
        gridview.setAdapter(saItem);
        //添加点击事件
        gridview.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        DetailsActivity.detailsActionStart(getActivity(), arg2, (String)meumList.get(arg2).get("ItemName"));
                    }
                }
        );
    }

    public void updateGridview(Message msg){
        final ArrayList<HashMap<String, Object>> meumList;
        meumList = (ArrayList<HashMap<String, Object>>) msg.obj;
        SimpleAdapter saItem = new SimpleAdapter(getContext(),
                meumList, //数据源
                R.layout.gridview_item, //xml实现
                new String[]{"ItemImage", "ItemText"}, //对应map的Key
                new int[]{R.id.ItemImage, R.id.ItemText});  //对应R的Id
        //通过setViewBinder将bitmap转化后使用
        saItem.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object bitmapData, String s) {
                if(view instanceof ImageView && bitmapData instanceof Bitmap){
                    ImageView i = (ImageView)view;
                    i.setImageBitmap((Bitmap) bitmapData);
                    return true;
                }
                return false;
            }
        });

        //添加Item到网格中
        gridview.setAdapter(saItem);
        //添加点击事件
        gridview.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        DetailsActivity.detailsActionStart(getActivity(), arg2, (String)meumList.get(arg2).get("ItemName"));
                    }
                }
        );
        SearchActivity.animationDialog.dismiss();
    }

    public void loadHero(List<ChampionListBean.AllBean> ls){
        SearchActivity.animationDialog.show();
        final ArrayList<HashMap<String, Object>> meumList = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < ls.size(); i++) {
            final ChampionListBean.AllBean allBean = ls.get(i);
            final Thread thread = new Thread(){
                @Override
                public void run() {
                    String url = "";
                    switch (newsType)
                    {
                        case 0:
                            if(allBean.getTags().contains("assassin"))
                                url = Constant.API_HEAD_IMG+allBean.getEnName()+"_120x120.jpg";
                            break;
                        case 1:
                            if(allBean.getTags().contains("fighter"))
                                url = Constant.API_HEAD_IMG+allBean.getEnName()+"_120x120.jpg";
                            break;
                        case 2:
                            if(allBean.getTags().contains("mage"))
                                url = Constant.API_HEAD_IMG+allBean.getEnName()+"_120x120.jpg";
                            break;
                        case 3:
                            if(allBean.getTags().contains("tank"))
                                url = Constant.API_HEAD_IMG+allBean.getEnName()+"_120x120.jpg";
                            break;
                        case 4:
                            if(allBean.getTags().contains("marksman"))
                                url = Constant.API_HEAD_IMG+allBean.getEnName()+"_120x120.jpg";
                            break;
                        case 5:
                            if(allBean.getTags().contains("support"))
                                url = Constant.API_HEAD_IMG+allBean.getEnName()+"_120x120.jpg";
                            break;
                        case 6:
                            url = Constant.API_HEAD_IMG+allBean.getEnName()+"_120x120.jpg";
                            break;
                        default:
                            url = Constant.API_HEAD_IMG+allBean.getEnName()+"_120x120.jpg";
                            break;
                    }
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    if(url!=""){
                        Bitmap bitmap = SearchActivity.imageLoader.getBitmap(url);
                        map.put("ItemName",allBean.getEnName());
                        if(bitmap==null){
                            Resources res = getResources();
                            BitmapFactory.Options opts = new BitmapFactory.Options();
                            opts.inSampleSize = 2;
                            Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.progress_0,opts);
                            map.put("ItemImage", bmp);
                            if(!noneImageNum){
                                requestImage();
                                noneImageNum = true;
                            }
                        }else
                            map.put("ItemImage", bitmap);
                        if(allBean.getTitle().length()>5)
                        {
                            String title = allBean.getTitle().substring(0,4)+"..";
                            map.put("ItemText",title);

                        }else{
                            map.put("ItemText", allBean.getTitle());
                        }
                        meumList.add(map);
                        Message message = handler.obtainMessage();
                        message.obj = meumList;
                        message.what = ENDLOAD;
                        handler.sendMessage(message);
                    }
                }
            };
            thread.start();
        }
    }

    public void requestData(final String path) {
        final OkHttpClient client = new OkHttpClient();
        final Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(path);
                    Request request = new Request.Builder().url(url).build();
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String tempResponse = response.body().string();
                        Message message = handler.obtainMessage();
                        message.obj = tempResponse;
                        message.what = 4;
                        handler.sendMessage(message);
                    } else {
                        throw new IOException("Unexpected code " + response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

}
