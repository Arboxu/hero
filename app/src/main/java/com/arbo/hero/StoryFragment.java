package com.arbo.hero;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.arbo.hero.javabean.ChampionMessage;
import com.arbo.hero.network.Constant;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Administrator on 2016/7/12.
 */
public class StoryFragment extends Fragment {
    @BindView(R.id.like1Dsc)
    TextView like1Dsc;
    @BindView(R.id.like2Dsc)
    TextView like2Dsc;
    @BindView(R.id.hate1Dsc)
    TextView hate1Dsc;
    @BindView(R.id.hate2Dsc)
    TextView hate2Dsc;
    @BindView(R.id.storyDsc)
    TextView storyDsc;
    @BindView(R.id.like1Img)
    ImageView like1Img;
    @BindView(R.id.like2Img)
    ImageView like2Img;
    @BindView(R.id.hate1Img)
    ImageView hate1Img;
    @BindView(R.id.hate2Img)
    ImageView hate2Img;
    private String response;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public StoryFragment() {

    }
    @SuppressLint("ValidFragment")
    public StoryFragment(String response) {
        this.response = response;
    }

    public String getResponse() {
        return this.response;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.storyfragment, container, false);
        ButterKnife.bind(this, view);
        String getresponse = getResponse();
        if(getresponse!=null){
            parseUI(getresponse, view);
        }else{
            Toast.makeText(getActivity(),"网路异常，请检查您的网络连接",Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    public String parseUI(String response, View view) {
        String heroName = "undefine";
        Log.i("response", "" + response);
        if(response == null){
            like1Dsc.setText("获取数据失败！");
            return "error";
        }
        ChampionMessage championMessage = JSON.parseObject(response, ChampionMessage.class);
        String description = championMessage.getDescription();
        List<ChampionMessage.LikeBean> likelist = championMessage.getLike();
        List<ChampionMessage.HateBean> hatelist = championMessage.getHate();

        String pathHead = Constant.API_HEAD_IMG;
        if(likelist == null){
            like1Dsc.setText("获取数据失败！");
            return "error";
        }
        if (!likelist.isEmpty()){
            ChampionMessage.LikeBean like1 = likelist.get(0);
            String like1DscText = like1.getDes();
            ChampionMessage.LikeBean like2 = likelist.get(1);
            String like2DscText = like2.getDes();
            like1Dsc.setText(like1DscText);
            like2Dsc.setText(like2DscText);
            DetailsActivity.imageLoader.DisplayImage(pathHead+like1.getPartner()+"_120x120.jpg",like1Img);
            DetailsActivity.imageLoader.DisplayImage(pathHead+like2.getPartner()+"_120x120.jpg",like2Img);
        }
        if (!hatelist.isEmpty()) {
            ChampionMessage.HateBean hete1 = hatelist.get(0);
            String hate1DscText = hete1.getDes();
            ChampionMessage.HateBean hete2 = hatelist.get(1);
            String hate2DscText = hete2.getDes();
            hate1Dsc.setText(hate1DscText);
            hate2Dsc.setText(hate2DscText);

            DetailsActivity.imageLoader.DisplayImage(pathHead+hete1.getPartner()+"_120x120.jpg",hate1Img);
            DetailsActivity.imageLoader.DisplayImage(pathHead+hete2.getPartner()+"_120x120.jpg",hate2Img);
        }else{
            hate1Dsc.setText("获取数据失败");
        }
        storyDsc.setText(description);
        heroName = championMessage.getName();
        return heroName;
    }
}
