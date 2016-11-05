package com.arbo.hero;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.arbo.hero.network.Constant;

import java.io.IOException;
import java.net.URL;

import cn.bmob.v3.BmobUser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SplashActivity extends Activity {

    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String tempresponse = (String) msg.obj;
                    Constant.setResponsedata(tempresponse);
                    Log.i("SplashActivity:", "" + tempresponse);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        requestData(Constant.API_ALL_HERO);
        if((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0){
            finish();
            return;
        }

        Handler handler = new Handler();
        int SPLASH_DISPLAY_LENGHT = 2000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loginWithCache();
            }
        }, SPLASH_DISPLAY_LENGHT);

    }

    private void loginWithCache(){
        BmobUser bmobUser = BmobUser.getCurrentUser();
        Intent mainintent;
        if(bmobUser != null){
            mainintent = new Intent(SplashActivity.this,SearchActivity.class);
            //Log.e("getCurrentUser",""+bmobUser.getObjectId());

        }else{
            //Log.e("getCurrentUser","没有缓存用户");
            mainintent = new Intent(SplashActivity.this,LoginActivity.class);
            //缓存用户对象为空时， 可打开用户注册界面…
        }
        SplashActivity.this.startActivity(mainintent);
        SplashActivity.this.finish();
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
                        Message message = mhandler.obtainMessage();
                        message.obj = tempResponse;
                        message.what = 1;
                        mhandler.sendMessage(message);
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
