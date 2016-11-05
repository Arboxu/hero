package com.arbo.hero;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.arbo.hero.javabean.ChampionMessage;
import com.arbo.hero.network.AnimationUtil;
import com.arbo.hero.network.Constant;
import com.arbo.hero.network.HttpUtil;
import com.arbo.hero.network.ImageLoader;
import com.arbo.hero.util.RoundBitmap;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends Activity implements View.OnClickListener {

    @BindView(R.id.bg)
    ImageView bg;
    @BindView(R.id.herotitle)
    TextView herotitle;
    @BindView(R.id.heroName)
    TextView heroName;
    @BindView(R.id.herotype)
    TextView herotype;
    @BindView(R.id.comefrom)
    ImageView comefrom;
    @BindView(R.id.attack)
    TextView attack;
    @BindView(R.id.magic)
    TextView magic;
    @BindView(R.id.defense)
    TextView defense;
    @BindView(R.id.RadioG2)
    RadioGroup RadioG2;
    @BindView(R.id.fragment_container)
    FrameLayout fragmentContainer;
    @BindView(R.id.dtfavorite)
    ImageView dtfavorite;
    @BindView(R.id.dtBack)
    ImageView dtBack;
    @BindView(R.id.gong)
    TextView gong;
    @BindView(R.id.fa)
    TextView fa;
    @BindView(R.id.fang)
    TextView fang;
    @BindView(R.id.dific)
    TextView dific;
    @BindView(R.id.coin)
    TextView coin;
    @BindView(R.id.coupon)
    TextView coupon;
    @BindView(R.id.rbtn_skill)
    RadioButton rbtnSkill;
    @BindView(R.id.rbtn_equip)
    RadioButton rbtnEquip;
    @BindView(R.id.rbtn_story)
    RadioButton rbtnStory;
    @BindView(R.id.rbtn_strategy)
    RadioButton rbtnStrategy;
    private List<Fragment> fragments = new ArrayList<Fragment>();
    private SkillFragment skillFragment;
    private EquipFragment equipFragment;
    private StoryFragment storyFragment;
    private StratageFragment stratageFragment;
    private FragmentTransaction transaction;
    private String response;
    public static AnimationUtil dialog;
    public static ImageLoader imageLoader ;
    //射手 adc 181200   战士 120018   法师181200  辅助 001218
    private int[] giftRating = new int[]{181200, 120018, 181200, 1218};
    private int[] bgResId = new int[]{R.drawable.bg1, R.drawable.bg2, R.drawable.bg3, R.drawable.bg4, R.drawable.bg5};

    public String getVedioHtmlData() {
        return vedioHtmlData;
    }

    public void setVedioHtmlData(String vedioHtmlData) {
        this.vedioHtmlData = vedioHtmlData;
    }

    private String vedioHtmlData;

    public String getSendName() {
        return sendName;
    }

    public void setSendName(String sendName) {
        this.sendName = sendName;
    }

    private String sendName;

    public String getResponse2() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
    static DetailsActivity detailsActivity;

    private boolean isfavorite = false;
    private int heroID = 0;
    public boolean getIsfavorite() {return isfavorite;}

    public void setIsfavorite(boolean isfavorite) {this.isfavorite = isfavorite;}
    public int getHeroID() {return heroID;}

    public void setHeroID(int heroID) {this.heroID = heroID;}
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_WHAT_CHAMP_Str:
                    String responseData = (String) msg.obj;
                    setResponse(responseData);
                    ChampionMessage championMessage = JSON.parseObject(responseData, ChampionMessage.class);
                    championMessage.getTitle();
                    setHeroID(championMessage.getId());
                    heroMassageInit(championMessage);
                    initTabView(responseData);
                    getHtmlData(championMessage.getName());
                   // dialog.dismiss();
                    String getCheck = checkdata(getHeroID());
                    if(getCheck.equals("true")){
                        dtfavorite.setImageResource(R.drawable.unsubscribe_normal);
                        setIsfavorite(true);
                    }
                    break;
                case Constant.MSG_WHAT_GET_HTML:
                    String html = (String) msg.obj;
                    setVedioHtmlData(html);
                    break;
            }
        }
    };


    @Override
    public void onBackPressed(){
        saveFavorite();
        super.onBackPressed();
    }



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);
        ButterKnife.bind(this);
        buttonProcess();
        detailsActivity = this;
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(this,true);
        int tempId = (int) (Math.random() * 5);
        bg.setBackgroundResource(bgResId[tempId]);
        Intent getIntent = getIntent();
        String name = getIntent.getStringExtra("heroname");
        loadHeadImage(name);
        String path = Constant.API_HERO_DETAILS + name;
        HttpUtil.getResponse(path, handler, 2);
        dialog = new AnimationUtil(this, "玩命加载中", R.drawable.animation1);
        dialog.show();
    }


    public void loadHeadImage(String name) {
        String url = Constant.API_HEAD_IMG + name + "_120x120.jpg";
        ImageLoader imageLoader = new ImageLoader(this, true);
        Bitmap get = imageLoader.getBitmap(url);
        Bitmap bitmap;
        if(get==null){
            Resources res = getResources();
            Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.loading_pic);
            bitmap = RoundBitmap.toRoundBitmap(bmp);
        }else
            bitmap = RoundBitmap.toRoundBitmap(get);
        comefrom.setImageBitmap(bitmap);
    }

    public void getHtmlData(final String heroname) {
        final Thread thread = new Thread() {
            @Override
            public void run() {
                String url = Constant.API_VEDIO_HTML + heroname + ".html";
                if (heroname.equals("MonkeyKing")) {
                    url = Constant.API_VEDIO_HTML + "wukong" + ".html";
                }
                InputStream inputStream = HttpUtil.getInputStream(url);
                try {
                    if(inputStream == null){
                        throw new NullPointerException("缺少必须的参数");
                    }else{
                        String getHtml = HttpUtil.inputStream2String(inputStream);
                        Message message = handler.obtainMessage();
                        message.what = Constant.MSG_WHAT_GET_HTML;
                        message.obj = getHtml;
                        handler.sendMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NullPointerException e){
                    e.printStackTrace();
                    Toast.makeText(DetailsActivity.this,"获取数据失败，请重试",Toast.LENGTH_SHORT).show();
                }
            }
        };
        thread.start();
    }


    public static void detailsActionStart(Context context, int data1, String data2) {
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.putExtra("getId", data1);
        intent.putExtra("heroname", data2);
        context.startActivity(intent);
    }

    public void heroMassageInit(ChampionMessage championMessage) {
        herotitle.setText(championMessage.getDisplayName());
        heroName.setText(championMessage.getTitle());
        setSendName(championMessage.getName());
        herotype.setText(championMessage.getTags());
        String price = championMessage.getPrice();
        String coinPrice = price.substring(0, price.lastIndexOf(','));
        String couponPrice = price.substring(price.lastIndexOf(',') + 1);
        coin.setText(coinPrice);
        coupon.setText(couponPrice);
        attack.setText(championMessage.getRatingAttack());
        magic.setText(championMessage.getRatingMagic());
        defense.setText(championMessage.getRatingDefense());
        dific.setText(championMessage.getRatingDifficulty());
        String getTags = championMessage.getTags();
        if (getTags.equals(""))
            getTags = "射手 ADC";
        herotype.setText(getTags);
    }


    /**
     * 初始化Radiogroup以及fragment
     */
    private void initTabView(String responseData) {
        skillFragment = new SkillFragment(responseData);
        equipFragment = new EquipFragment(getSendName());
        storyFragment = new StoryFragment(getResponse2());
        stratageFragment = new StratageFragment(getVedioHtmlData(), getSendName());
        if (!rbtnSkill.isChecked()) {
            rbtnSkill.setChecked(true);
        }
        fragments.add(skillFragment);
        fragments.add(equipFragment);
        fragments.add(storyFragment);
        fragments.add(stratageFragment);
        transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, skillFragment);
        transaction.add(R.id.fragment_container, equipFragment);
        transaction.add(R.id.fragment_container, storyFragment);
        transaction.add(R.id.fragment_container, stratageFragment);
        transaction.show(skillFragment).hide(equipFragment).hide(storyFragment).hide(stratageFragment);
        transaction.commitAllowingStateLoss();
    }

    /**
     * 更改Fragment对象
     *
     * @param index
     */
    private void switchFragment(int index) {
        transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        for (int i = 0; i < fragments.size(); i++) {
            if (index == i) {
                transaction.show(fragments.get(index));
            } else {
                transaction.hide(fragments.get(i));
            }
        }
        transaction.commit();
    }

    private String checkdata(int id){
        String ischeck = "";
        SQLiteDatabase db = SearchActivity.dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from mfavorites where heroid="+id,null);
        while (cursor.moveToNext()){
            ischeck = cursor.getString(2);
        }
        cursor.close();
        db.close();
        return ischeck;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dtBack:
                onBackPressed();
                break;
            case R.id.dtfavorite:
                Toast toast;
                if(getIsfavorite()){
                    setIsfavorite(false);
                    toast = Toast.makeText(getApplicationContext(),
                            "\t" + "已从<我的收藏>中" + "\t" + "\n" + "\t" + "移除", Toast.LENGTH_SHORT);
                    dtfavorite.setImageResource(R.drawable.favorite);
                }else{
                    setIsfavorite(true);
                    dtfavorite.setImageResource(R.drawable.unsubscribe_normal);
                    toast = Toast.makeText(getApplicationContext(),
                            "\t" + "已收藏至" + "\t" + "\n" + "\t" + " <我的收藏>", Toast.LENGTH_SHORT);
                }
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                break;
            case R.id.rbtn_skill:
            case R.id.rbtn_equip:
            case R.id.rbtn_story:
            case R.id.rbtn_strategy:
                changeButtonStyle(view.getId());
                if (view.getId() == R.id.rbtn_skill) {
                    if (null == skillFragment) {
                        skillFragment = new SkillFragment();
                    }
                    switchFragment(0);
                } else if (view.getId() == R.id.rbtn_equip) {
                    if (null == equipFragment) {
                        equipFragment = new EquipFragment(getSendName());
                    }
                    switchFragment(1);
                } else if (view.getId() == R.id.rbtn_story) {
                    if (null == storyFragment) {
                        storyFragment = new StoryFragment(getResponse2());
                    }
                    switchFragment(2);
                } else {
                    if (null == stratageFragment) {
                        stratageFragment = new StratageFragment(getVedioHtmlData(), getSendName());
                    }
                    switchFragment(3);
                }
                break;
        }
    }


    private void changeButtonStyle(int id){
        RadioButton rbtn = (RadioButton) findViewById(id);
        rbtnSkill.setTextSize(15);
        rbtnEquip.setTextSize(15);
        rbtnStory.setTextSize(15);
        rbtnStrategy.setTextSize(15);
        if (rbtn.isChecked()) {
            rbtn.setTextSize(20);
        }
    }

    public void buttonProcess() {
        dtBack.bringToFront();
        dtBack.setOnClickListener(this);
        dtfavorite.bringToFront();
        dtfavorite.setOnClickListener(this);
        rbtnSkill.setOnClickListener(this);
        rbtnEquip.setOnClickListener(this);
        rbtnStory.setOnClickListener(this);
        rbtnStrategy.setOnClickListener(this);
    }

    private void saveFavorite(){
        String enname = getSendName();
        String cnname = herotitle.getText().toString();
        Log.d("英雄信息：",enname+";"+cnname);
        boolean b = getIsfavorite();
        String check;
        if(b)
            check = "true";
        else
            check = "false";
        Log.d("getIsfavorite",""+check);
        SQLiteDatabase db = SearchActivity.dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from mfavorites where heroid="+getHeroID(),null);
        if(cursor.getCount() <= 1){
            ContentValues values = new ContentValues();
            db.beginTransaction();  //开启事务
            try{
                values.put("heroid", getHeroID());
                values.put("favorite", check);
                values.put("enname",enname);
                values.put("cnname",cnname);
                if(cursor.getCount() == 0){
                    db.insert("mfavorites",null,values);
                }else{
                    String[] args = {String.valueOf(getHeroID())};
                    db.update("mfavorites",values, "heroid=?",args);
                }
                values.clear();
                //此处一定要设置事务成功，否则失败
                db.setTransactionSuccessful();
            }catch(Exception e)
            {
                e.printStackTrace();
            }finally{
                //事务结束之后，要设置end，然后将数据库关闭
                db.endTransaction();
                cursor.close();
                db.close();
            }
        }
    }


    /**
     * Created by 许建波 on 2016/8/24.
     */
    public static class VedioBean {
        private List<VedioMsg> allVedio;

        public List<VedioMsg> getAll() {
            allVedio = new ArrayList<VedioMsg>();
            return allVedio;
        }

        public void setAll(List<VedioMsg> allVedio) {

            this.allVedio = allVedio;
        }

        static class VedioMsg{
            private String imgLink;
            private String vedioTitle;
            private String authorLink;
            private String vedioTime;
            private String authorName;

            public String getVedioTime() {
                return vedioTime;
            }

            public void setVedioTime(String vedioTime) {
                this.vedioTime = vedioTime;
            }

            public String getAuthorLink() {
                return authorLink;
            }

            public void setAuthorLink(String authorLink) {
                this.authorLink = authorLink;
            }


            public String getImgLink() {
                return imgLink;
            }

            public void setImgLink(String imgLink) {
                this.imgLink = imgLink;
            }

            public String getVedioTitle() {
                return vedioTitle;
            }

            public void setVedioTitle(String vedioTitle) {
                this.vedioTitle = vedioTitle;
            }


            public String getAuthorName() {
                return authorName;
            }

            public void setAuthorName(String authorName) {
                this.authorName = authorName;
            }
        }

    }

    /**
     * Created by Administrator on 2016/10/28.
     */

    public static class AccountSetting extends AppCompatActivity {

    }
}
