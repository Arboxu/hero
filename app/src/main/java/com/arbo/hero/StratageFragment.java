package com.arbo.hero;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.arbo.hero.network.Constant;
import com.arbo.hero.network.HttpUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/12.
 */
public class StratageFragment extends Fragment implements AdapterView.OnItemClickListener{
    private ListView mListView;
    private String response;
    private TitleAdapter mTitleAdapter;
    private List<TitleData> mTitleDataList;
    public String getRequestName() {
        return requestName;
    }
    public void setRequestName(String requestName) {
        this.requestName = requestName;
    }
    private String requestName ;
    final List<TitleData> listItem = new ArrayList<TitleData>();
    private static Context context;
    private int count;

    private final int MSG_WHAT_HTML =  6;
    private final int MSG_WHAT_IMG  =  7;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_WHAT_HTML:
                    Elements units;
                    units = (Elements) msg.obj;
                    DetailsActivity.VedioBean vedioBean = new DetailsActivity.VedioBean();
                    final List<DetailsActivity.VedioBean.VedioMsg> list = vedioBean.getAll();
                    for(int i = 0;i<units.size();i++){
                        DetailsActivity.VedioBean.VedioMsg vedioMsg = new DetailsActivity.VedioBean.VedioMsg();
                        Element unit_ele = units.get(i);
                        String title = unit_ele.select("a").attr("title");
                        String vedioLink = unit_ele.select("a").attr("href");
                        String ImgLink = unit_ele.select("a").get(0).select("img").attr("src");
                        String vedioLong = unit_ele.select("a").get(0).select("span").text();
                        String AuthorLink = unit_ele.select("a").get(1).select("img").attr("src");
                        String author = unit_ele.select("a").get(1).select("em").text();
                        //Log.i("title",""+title+";"+ImgLink+";"+vedioLink+";"+author+";");
                        vedioMsg.setVedioTitle(""+title);
                        vedioMsg.setImgLink(""+ImgLink);
                        vedioMsg.setVedioTime(""+vedioLong);
                        vedioMsg.setAuthorName(""+author);
                        vedioMsg.setAuthorLink(""+vedioLink);
                        list.add(i,vedioMsg);
                    }
                    if(list!= null){
                        for (int i = 0; (i < list.size()) && (listItem.size()<24); i++) {
                            TitleData data = new TitleData(list.get(i).getVedioTitle(),
                                    null, list.get(i).getAuthorName(), list.get(i).getVedioTime(),list.get(i).getAuthorLink());
                            listItem.add(data);
                        }
                    }
                    mTitleDataList = listItem;
                    mTitleAdapter.notifyDataSetChanged();
                    getVedioImg(list);
                    break;
                case MSG_WHAT_IMG:
                    Bitmap bitmap = (Bitmap)msg.obj;
                    mTitleDataList.get(count).setmIcon(bitmap);
                    mTitleAdapter.notifyDataSetChanged();
                    count++;
                    if(count == mTitleDataList.size()){
                        count = 0;
                    }
                    break;
            }
        }
    };


    public void getVedioImg(final List<DetailsActivity.VedioBean.VedioMsg> list){
        Thread thread = new Thread(){
            public void run(){
                Bitmap bitmap = null;
                for(int i = 0; i <list.size() ; i ++){
                    String url = list.get(i).getImgLink();
                    if(url!=""){
                        bitmap = DetailsActivity.imageLoader.getBitmap(url);
                        Message message = new Message();
                        message.what = MSG_WHAT_IMG;
                        message.obj  = bitmap;
                        handler.sendMessage(message);
                    }
                }
            }
        };
        thread.start();
    }


    public void onCreate(Bundle savedInstanceState)
    {
        context = getActivity();
        super.onCreate(savedInstanceState);


    }
    public StratageFragment() {

    }

    @SuppressLint("ValidFragment")
    public StratageFragment(String response, String requestName) {
        this.response = response;
        this.requestName = requestName;
    }

    public String getResponse() {
        return this.response;
    }
    public View onCreateView(LayoutInflater Inflater,ViewGroup container,Bundle savedInstanceState) {

        final View titleView = Inflater.inflate(R.layout.stratagefragment,container,false);
        mTitleDataList = getTitleDataList();
        mListView = (ListView) titleView.findViewById(R.id.titleListView);
        mTitleAdapter = new TitleAdapter();

        mListView.setAdapter(mTitleAdapter);
        mListView.setOnItemClickListener(this);

        if(getResponse()!=null){
            if(mTitleDataList.size() == 0){
                updateUI();
            }
        }else{
            requestData();
        }
        return titleView;
    }

    public List<TitleData> getTitleDataList() {
        List<TitleData> listItem = new ArrayList<TitleData>();
        return listItem;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long args) {
        String link = mTitleDataList.get(position).getmVedioLink();
       // Toast.makeText(getActivity(),"您点击了"+position+link,Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(StratageFragment.context,PlayVedioActivity.class);
        intent.putExtra("vedioUrl",link);
        StratageFragment.context.startActivity(intent);
    }

    class TitleData {
        private String mTitle;

        public void setmIcon(Bitmap mIcon) {
            this.mIcon = mIcon;
        }

        public String getmVedioLink() {
            return mVedioLink;
        }

        public void setmVedioLink(String mVedioLink) {
            this.mVedioLink = mVedioLink;
        }

        private String mVedioLink;
        private Bitmap mIcon;
        private String mIntroduce;
        private String mIntroTime;
        public TitleData(String title, Bitmap icon, String Intro, String IntroTime,String vediolink) {
            this.mTitle = title;
            this.mIcon = icon;
            this.mIntroduce = Intro;
            this.mIntroTime  = IntroTime;
            this.mVedioLink  = vediolink;
        }
        public String getmIntroTime()
        {
            return mIntroTime;
        }
        public String getmIntro(){
            return mIntroduce;
        }
        public String getmTitle() {
            return mTitle;
        }
        public Bitmap getmIcon() {
            return mIcon;
        }
    }

    public void updateUI(){
        Thread thread = new Thread(){
            @Override
            public void run() {
                    String getHtml  = getResponse(); //获取传递过来的html数据
                    Log.i("StratageFragment:HTML",""+getHtml);
                    Document document = Jsoup.parse(getHtml);
                    Elements units = document.getElementsByClass("uiVideo__item");
                    Message message = handler.obtainMessage();
                    message.what = MSG_WHAT_HTML;
                    message.obj = units;
                    handler.sendMessage(message);
            }
        };
        thread.start();
    }

    public void requestData(){
        final String name = getRequestName();
        if(name==null)return;
        Thread thread = new Thread(){
            @Override
            public void run() {
                String url = Constant.API_VEDIO_HTML+ name +".html";
                if(name.equals("MonkeyKing")){
                    url = Constant.API_VEDIO_HTML+ "wukong" +".html";
                }
                InputStream inputStream = HttpUtil.getInputStream(url);
                try {
                    String getHtml  = HttpUtil.inputStream2String(inputStream);
                    Log.i("html",""+getHtml);
                    Document document = Jsoup.parse(getHtml);
                    Elements units = document.getElementsByClass("uiVideo__item");
                    Message message = handler.obtainMessage();
                    message.what = MSG_WHAT_HTML;
                    message.obj = units;
                    handler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    class TitleAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mTitleDataList.size();
        }

        @Override
        public Object getItem(int position) {
            return mTitleDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            View view = convertView;

            if(view == null) {
                view = getActivity().getLayoutInflater().inflate(R.layout.list_view_item, null);
                viewHolder = new ViewHolder();
                viewHolder.mTitleImageView = (ImageView) view.findViewById(R.id.titleImageView);
                viewHolder.mTitleTextView = (TextView) view.findViewById(R.id.titleTextView);
                viewHolder.mIntroTextView = (TextView)view.findViewById(R.id.titleIntro);
                viewHolder.mIntroTimeTextView = (TextView)view.findViewById(R.id.titleIntroTime);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            final TitleData titleData = (TitleData) getItem(position);
            viewHolder.mTitleTextView.setText(titleData.getmTitle());
            viewHolder.mTitleImageView.setImageBitmap(titleData.getmIcon());
            viewHolder.mIntroTextView.setText(titleData.getmIntro());
            viewHolder.mIntroTimeTextView.setText(titleData.getmIntroTime());
            return view;
        }

        class ViewHolder {
            protected ImageView mTitleImageView;
            protected TextView mTitleTextView;
            protected TextView mIntroTextView;
            protected TextView mIntroTimeTextView;
        }
    }

}
