package com.arbo.hero;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.arbo.hero.javabean.ChampionListBean;
import com.arbo.hero.network.Constant;
import com.arbo.hero.network.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShowSearchResult extends FragmentActivity {
    GridView gridView;
    ImageView notfound;
    TextView notfoundtv;
    LinearLayout notfoundlay;
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showsearch);
        gridView = (GridView) findViewById(R.id.search_gridview);
        Button clear = (Button)findViewById(R.id.clearback);
        notfound = (ImageView)findViewById(R.id.notfoundimg);
        notfoundtv = (TextView)findViewById(R.id.notfoundtv);
        notfoundlay = (LinearLayout)findViewById(R.id.notfoundlayout);
        Intent intent = getIntent();
        String input = intent.getStringExtra("searchInput");
        String reponsedata = intent.getStringExtra("responseval");
        LoadSearch(input,reponsedata);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowSearchResult.this.finish();
            }
        });
    }

    public void LoadSearch(final String searchName,String responsedata){
        final ArrayList<HashMap<String, Object>> meumList = new ArrayList<HashMap<String, Object>>();
        final ImageLoader imageLoader = new ImageLoader(ShowSearchResult.this,true);
        List<ChampionListBean.AllBean> ls;
        ChampionListBean championListBean;
        championListBean = JSON.parseObject(responsedata,ChampionListBean.class);
        ls = championListBean.getAll();
        for (int i = 0; i < ls.size(); i++) {
            final ChampionListBean.AllBean allBean = ls.get(i);
            Thread thread = new Thread(){
                @Override
                public void run() {
                    String url = "";
                    if((allBean.getTitle().contains(searchName) || (allBean.getCnName().contains(searchName)))) {
                        url = Constant.API_HEAD_IMG + allBean.getEnName() + "_120x120.jpg";
                        Bitmap bitmap = imageLoader.getBitmap(url);
                        HashMap<String, Object> map = new HashMap<String, Object>();
                        if(url!=""){
                            map.put("ItemName",allBean.getEnName());
                            map.put("ItemImage", bitmap);
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
                }
            };
            thread.start();
        }
        if(meumList.isEmpty()){
            notfoundlay.setVisibility(View.VISIBLE);
            String tempStr;
            if(searchName.length()>10){
                tempStr = searchName.substring(0,10);
                notfoundtv.setText("                未找到 "+"< "+tempStr+".. >"+"相关英雄");
            }
            else{
                notfoundtv.setText("                未找到 "+"< "+searchName+" >"+"相关英雄");
            }
            notfoundtv.append("\n"+"请尝试搜索英雄名或称号(部分文字也可以噢！)");
        }
        SimpleAdapter saItem = new SimpleAdapter(ShowSearchResult.this,
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
        gridView.setAdapter(saItem);
        //添加点击事件
        gridView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        DetailsActivity.detailsActionStart(ShowSearchResult.this, arg2, (String)meumList.get(arg2).get("ItemName"));
                    }
                }
        );
    }
}
