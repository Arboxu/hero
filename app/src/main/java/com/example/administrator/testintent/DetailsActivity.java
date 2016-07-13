package com.example.administrator.testintent;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.administrator.testintent.FirstFrament;

/**
 * Created by Administrator on 2016/7/10.
 */
public class DetailsActivity extends Activity implements View.OnClickListener {
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private FirstFrament firstFrament;
    private SecondFragment secondFragment;
    private ThirdFragment thirdFragment;
    private FragmentTransaction transaction;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);
        ButtonProcess();


    }
    private void init_date(){
        transaction = getFragmentManager()
                .beginTransaction();
        if (null == firstFrament) {
            firstFrament = new FirstFrament();
        }
        transaction.add(R.id.fragment_container,
                firstFrament);
        // Commit the transaction
        transaction.commit();
    }

    public static void detailsActionStart(Context context, String data1, String data2) {
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.putExtra("usrName", data1);
        intent.putExtra("usrPSW", data2);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dtBack:
                this.finish();
                break;
            case R.id.dtfavorite:
                ImageView dtfavorite = (ImageView) findViewById(R.id.dtfavorite);
                dtfavorite.setImageResource(R.drawable.unsubscribe_normal);
                Toast toast = Toast.makeText(getApplicationContext(), "\t" + "已收藏至" + "\t" + "\n" + "\t" + " -我的", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                break;
            case R.id.rbtn_skill:
            case R.id.rbtn_equip:
            case R.id.rbtn_story:
            case R.id.rbtn_strategy:
                RadioButton rbtn = (RadioButton) findViewById(view.getId());
                RadioButton rbtn_skill = (RadioButton) findViewById(R.id.rbtn_skill);
                RadioButton rbtn_equip = (RadioButton) findViewById(R.id.rbtn_equip);
                RadioButton rbtn_story = (RadioButton) findViewById(R.id.rbtn_story);
                RadioButton rbtn_strategy = (RadioButton) findViewById(R.id.rbtn_strategy);
                rbtn_skill.setTextSize(15);
                rbtn_equip.setTextSize(15);
                rbtn_story.setTextSize(15);
                rbtn_strategy.setTextSize(15);
                if (rbtn.isChecked()) {
                    rbtn.setTextSize(20);
                }
                if(view.getId() == R.id.rbtn_skill){
                    if (null == firstFrament) {
                        firstFrament = new FirstFrament();
                    }
                    transaction = getFragmentManager()
                            .beginTransaction();
                    transaction.replace(R.id.fragment_container,
                            firstFrament);
                    // Commit the transaction
                    transaction.commit();
                }
                else if(view.getId() == R.id.rbtn_equip){
                    if (null == secondFragment) {
                        secondFragment = new SecondFragment();
                    }
                    transaction = getFragmentManager()
                            .beginTransaction();
                    transaction.replace(R.id.fragment_container,
                            secondFragment);
                    // Commit the transaction
                    transaction.commit();
                }
                else if(view.getId() == R.id.rbtn_story)
                {
                    if (null == thirdFragment) {
                        thirdFragment = new ThirdFragment();
                    }
                    transaction = getFragmentManager()
                            .beginTransaction();
                    transaction.replace(R.id.fragment_container,
                            thirdFragment);
                    // Commit the transaction
                    transaction.commit();
                }
                break;
            /*case R.id.SkillQ:
            case R.id.SkillW:
            case R.id.SkillE:
            case R.id.SkillR:
            case R.id.beidong:
                ImageButton skillselect = (ImageButton) findViewById(view.getId());
                ImageButton skillp = (ImageButton) findViewById(R.id.beidong);
                ImageButton skillq = (ImageButton) findViewById(R.id.SkillQ);
                ImageButton skillw = (ImageButton) findViewById(R.id.SkillW);
                ImageButton skille = (ImageButton) findViewById(R.id.SkillE);
                ImageButton skillr = (ImageButton) findViewById(R.id.SkillR);
                skillp.getDrawable().setAlpha(255);
                skillq.getDrawable().setAlpha(255);
                skillw.getDrawable().setAlpha(255);
                skille.getDrawable().setAlpha(255);
                skillr.getDrawable().setAlpha(255);
                if (skillselect.isPressed()) {
                    skillselect.getDrawable().setAlpha(150);
                }
                skillq.invalidate();
                break;*/

        }
    }

    public void ButtonProcess() {
        ImageView dtback = (ImageView) findViewById(R.id.dtBack);
        dtback.bringToFront();
        dtback.setOnClickListener(this);
        ImageView dtfavorite = (ImageView) findViewById(R.id.dtfavorite);
        dtfavorite.bringToFront();
        dtfavorite.setOnClickListener(this);
        RadioButton rbtn_skill = (RadioButton) findViewById(R.id.rbtn_skill);
        RadioButton rbtn_equip = (RadioButton) findViewById(R.id.rbtn_equip);
        RadioButton rbtn_story = (RadioButton) findViewById(R.id.rbtn_story);
        RadioButton rbtn_strategy = (RadioButton) findViewById(R.id.rbtn_strategy);
        rbtn_skill.setOnClickListener(this);
        rbtn_equip.setOnClickListener(this);
        rbtn_story.setOnClickListener(this);
        rbtn_strategy.setOnClickListener(this);
       /* ImageButton skillp = (ImageButton) findViewById(R.id.beidong);
        ImageButton skillq = (ImageButton) findViewById(R.id.SkillQ);
        ImageButton skillw = (ImageButton) findViewById(R.id.SkillW);
        ImageButton skille = (ImageButton) findViewById(R.id.SkillE);
        ImageButton skillr = (ImageButton) findViewById(R.id.SkillR);
        skillp.setOnClickListener(this);
        skillq.setOnClickListener(this);
        skillw.setOnClickListener(this);
        skille.setOnClickListener(this);
        skillr.setOnClickListener(this);*/
    }


}
