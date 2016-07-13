package com.example.administrator.testintent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by 许建波 on 2016/7/7.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Intent getIntent = getIntent();
        String intentMsg = intent.getStringExtra("callxiaoming");
        Toast.makeText(context, intentMsg, Toast.LENGTH_SHORT).show();
    }
}