package com.example.administrator.testintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import static com.example.administrator.testintent.DetailsActivity.detailsActionStart;

/**
 * Created by Administrator on 2016/7/5.
 */
public class SecondActivity extends MainActivity implements View.OnClickListener{

    private ImageView back;
    private ImageView details;
    private ImageView search;
    private EditText inputText;
    int[] Images;
    public void onCreate(Bundle savedInstanceState)
    {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.second_layout);
//       Button btn2 = (Button)findViewById(R.id.button2);
       Images = new int[]{R.drawable.vn,R.drawable.aolafu,R.drawable.baonv,R.drawable.caoxi,
                R.drawable.dachong,R.drawable.dachongzi,R.drawable.daocao,
                R.drawable.daofeng,R.drawable.daomei,R.drawable.eyu,R.drawable.famingjia,
                R.drawable.fengnv,R.drawable.houzi,R.drawable.huli,R.drawable.huonv,
               R.drawable.huxian,R.drawable.jiakesi,R.drawable.jie,R.drawable.jingkesi,
               R.drawable.jiqiren,R.drawable.kapai,R.drawable.naer,R.drawable.nvjing,
               R.drawable.nvqiang,R.drawable.riwen,R.drawable.shahuang,R.drawable.shenv,
               R.drawable.shitou,R.drawable.timo,R.drawable.weilusi,R.drawable.xiaofa,R.drawable.xiaopao,
               R.drawable.zelasi
        };
//       btn2.setOnClickListener(new View.OnClickListener() {
//           @Override
//           public void onClick(View view) {
//               ActivityCollector.finishAll();
//           }
//       });

       back = (ImageView)findViewById(R.id.back);
       details = (ImageView)findViewById(R.id.details);
       search = (ImageView)findViewById(R.id.search);
       inputText = (EditText)findViewById(R.id.inputContent);
       search.bringToFront();
       back.setOnClickListener(this);
       details.setOnClickListener(this);
       search.setOnClickListener(this);
       Intent getIntent = getIntent();
       String usrName = getIntent.getStringExtra("usrName");
       String usrPSW = getIntent.getStringExtra("usrPSW");
        LoadHero();

    }
    public void LoadHero()
    {
        String[] heroName = new String[]{"暗夜猎手","狂战士","狂野女猎手","唤潮鲛姬","深渊巨口",
                "虚空恐惧","末日使者","刀锋之影","刀锋意志","荒漠屠夫","大发明家","风暴之怒","齐天大圣",
                "九尾妖狐","黑暗之女","圣枪游侠","武器大师","影流之王","暴走萝莉","蒸汽机器人","卡牌大师",
                "迷失之牙","皮城女警","赏金猎人","放逐之刃","沙漠皇帝","魔蛇之拥","熔岩巨兽","迅捷斥候",
                "惩戒之箭","邪恶小法师","麦林炮手","远古巫灵"};
        GridView gridview = (GridView) findViewById(R.id.GridView);
        ArrayList<HashMap<String, Object>> meumList = new ArrayList<HashMap<String, Object>>();
        for(int i = 0;i < Images.length;i++)
        {

            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ItemImage",Images[i]);
            map.put("ItemText", heroName[i]);
            meumList.add(map);
        }
        SimpleAdapter saItem = new SimpleAdapter(this,
                meumList, //数据源
                R.layout.item, //xml实现
                new String[]{"ItemImage","ItemText"}, //对应map的Key
                new int[]{R.id.ItemImage,R.id.ItemText});  //对应R的Id

        //添加Item到网格中
        gridview.setAdapter(saItem);
        //添加点击事件
        gridview.setOnItemClickListener(
                new AdapterView.OnItemClickListener()
                {
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3)
                    {
                        DetailsActivity.detailsActionStart(SecondActivity.this,"xxx","xxx");
                        int index=arg2+1;//id是从0开始的，所以需要+1
                        Toast.makeText(getApplicationContext(), "你按下了选项："+index, Toast.LENGTH_SHORT).show();
                        //Toast用于向用户显示一些帮助/提示
                    }
                }
        );

    }
    public static void actionStart(Context context,String data1,String data2)
    {
        Intent intent = new Intent(context,SecondActivity.class);
        intent.putExtra("usrName",data1);
        intent.putExtra("usrPSW",data2);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.back:
                this.finish();
                break;
            case R.id.details:
                Toast.makeText(SecondActivity.this,"Click details imageView",Toast.LENGTH_SHORT).show();
            case R.id.search:
                String input = inputText.getText().toString();
                Toast.makeText(SecondActivity.this,input,Toast.LENGTH_SHORT).show();
            default:break;
        }
    }
}
