package com.arbo.hero;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
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
import com.arbo.hero.network.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Administrator on 2016/7/12.
 */
public class SkillFragment extends Fragment {
    @BindView(R.id.beidong)
    ImageView beidong;
    @BindView(R.id.SkillQ)
    ImageView skillQ;
    @BindView(R.id.SkillW)
    ImageView skillW;
    @BindView(R.id.SkillE)
    ImageView skillE;
    @BindView(R.id.SkillR)
    ImageView skillR;
    @BindView(R.id.skill_Name)
    TextView skillName;
    @BindView(R.id.skill_Desc)
    TextView skillDesc;
    private int Id;
    private String response;
    ImageLoader imageLoader;

    HashMap<Integer, String> skillMsgMapName;
    HashMap<Integer, String> skillMsgMapDsc;
    public SkillFragment() {
    }

    @SuppressLint("ValidFragment")
    public SkillFragment(String response) {
        this.response = response;
    }

    public String getResponse() {
        return this.response;
    }

    public int get_Id() {
        return this.Id;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View v = layoutInflater.inflate(R.layout.skillfragment, container, false);
        ButterKnife.bind(this, v);
        String getresponse = getResponse();
        if (getresponse != null) {
            String heroname = parseUI(getResponse(), v);
            skillListener(v, heroname);
        } else {
            Toast.makeText(getActivity(), "网路异常，请检查您的网络连接", Toast.LENGTH_SHORT).show();
        }
        beidong.post(new Runnable() {
            @Override
            public void run() {
                beidong.performClick();
            }
        });
        return v;
    }






    public String parseUI(String response, View view) {
        String tips = "undefine";
        String opponentTips = "undefine";
        String heroName = "undefine";
        ChampionMessage championMessage = JSON.parseObject(response, ChampionMessage.class);
        tips = championMessage.getTips();
        opponentTips = championMessage.getOpponentTips();
        TextView tv_skill = (TextView) view.findViewById(R.id.operateText);
        tv_skill.setText(tips);
        TextView team_workText = (TextView) view.findViewById(R.id.team_workText);
        team_workText.setText(opponentTips);
        heroName = championMessage.getName();
        return heroName;
    }

    public void skillListener(View v, String heroName) {
        final ImageView skillp = (ImageView) v.findViewById(R.id.beidong);
        final ImageView skillq = (ImageView) v.findViewById(R.id.SkillQ);
        final ImageView skillw = (ImageView) v.findViewById(R.id.SkillW);
        final ImageView skille = (ImageView) v.findViewById(R.id.SkillE);
        final ImageView skillr = (ImageView) v.findViewById(R.id.SkillR);

        Context mContext;
        mContext = getActivity();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(mContext, false);
        HashMap<Integer, String> skillName = new HashMap<>();
        skillName.put(0, Constant.API_ALL_SKILL + heroName + "_B_64x64.png");
        skillName.put(1, Constant.API_ALL_SKILL + heroName + "_Q_64x64.png");
        skillName.put(2, Constant.API_ALL_SKILL + heroName + "_W_64x64.png");
        skillName.put(3, Constant.API_ALL_SKILL + heroName + "_E_64x64.png");
        skillName.put(4, Constant.API_ALL_SKILL + heroName + "_R_64x64.png");
        Log.i("path", skillName.get(0));
        imageLoader.DisplayImage(skillName.get(0), skillp);
        imageLoader.DisplayImage(skillName.get(1), skillq);
        imageLoader.DisplayImage(skillName.get(2), skillw);
        imageLoader.DisplayImage(skillName.get(3), skille);
        imageLoader.DisplayImage(skillName.get(4), skillr);
        updateSkill(v, heroName);
    }

    private class MyListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            beidong.getBackground().setAlpha(255);
            skillQ.getBackground().setAlpha(255);
            skillW.getBackground().setAlpha(255);
            skillE.getBackground().setAlpha(255);
            skillR.getBackground().setAlpha(255);
            switch (view.getId()) {
                case R.id.beidong:
                    beidong.getBackground().setAlpha(150);
                    skillName.setText("" + skillMsgMapName.get(0));
                    skillDesc.setText("" + skillMsgMapDsc.get(0));
                    break;
                case R.id.SkillQ:
                    skillQ.getBackground().setAlpha(150);
                    skillName.setText("" + skillMsgMapName.get(1));
                    skillDesc.setText("" + skillMsgMapDsc.get(1));
                    break;
                case R.id.SkillW:
                    skillW.getBackground().setAlpha(150);
                    skillName.setText("" + skillMsgMapName.get(2));
                    skillDesc.setText("" + skillMsgMapDsc.get(2));
                    break;
                case R.id.SkillE:
                    skillE.getBackground().setAlpha(150);
                    skillName.setText("" + skillMsgMapName.get(3));
                    skillDesc.setText("" + skillMsgMapDsc.get(3));
                    break;
                case R.id.SkillR:
                    skillR.getBackground().setAlpha(150);
                    skillName.setText("" + skillMsgMapName.get(4));
                    skillDesc.setText("" + skillMsgMapDsc.get(4));
                    break;
            }
        }
    }

    public void updateSkill(View v, String heroName) {
        HashMap<Integer, String> skillIDMap = new HashMap<>();
        skillMsgMapName = new HashMap<>();
        skillMsgMapDsc = new HashMap<>();

        skillIDMap.put(0, heroName + "_B");
        skillIDMap.put(1, heroName + "_Q");
        skillIDMap.put(2, heroName + "_W");
        skillIDMap.put(3, heroName + "_E");
        skillIDMap.put(4, heroName + "_R");
        Log.i("技能名字：", skillIDMap.get(0) + ";" + skillIDMap.get(1));
        try {
            for (int i = 0; i < 5; i++) {
                JSONObject object = new JSONObject(response);
                JSONObject object1 = new JSONObject();
                object1 = object.getJSONObject(skillIDMap.get(i));
                String effect = object1.getString("effect");
                if (effect.equals("")) {
                    effect = object1.getString("description");
                }
                String skillname = object1.getString("name");
                skillMsgMapName.put(i, skillname);
                skillMsgMapDsc.put(i, effect);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MyListener myListener = new MyListener();
        beidong.setOnClickListener(myListener);
        skillW.setOnClickListener(myListener);
        skillE.setOnClickListener(myListener);
        skillR.setOnClickListener(myListener);
        skillQ.setOnClickListener(myListener);
        beidong.setPressed(true);

    }

}
