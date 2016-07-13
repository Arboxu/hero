package com.example.administrator.testintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class MainActivity extends Activity implements CompoundButton.OnCheckedChangeListener {
    private EditText psw;
    private EditText usr;
    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;
    private CheckBox cbx_psw;
    private CheckBox cbx_autologin;
    private boolean psw_Cheacked = false;
    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver,intentFilter);

        Button btnSent = (Button)findViewById(R.id.sentBtn);
        Button btn = (Button)findViewById(R.id.button);
        ActivityCollector.addActivity(this);        //调用外部类添加
        usr = (EditText)findViewById(R.id.UserName);
        psw = (EditText)findViewById(R.id.UserPsw);
        cbx_psw = (CheckBox)findViewById(R.id.cbx_psw);
        cbx_autologin = (CheckBox)findViewById(R.id.cbx_autologin);
        cbx_psw.setOnCheckedChangeListener(this);

        cbx_autologin.setOnCheckedChangeListener(this);
        String jizhu= load("jizhumima");
        if(jizhu.equals("jizhu"))
        {
            cbx_psw.setChecked(true);
            String usrn= load("usrn");
            String passw= load("passw");
            if(!TextUtils.isEmpty(usrn))
            {
                usr.setText(usrn);
                usr.setSelection(usrn.length());
            }
            psw.setText(passw);
        }
        else
        {
            cbx_psw.setChecked(false);
            usr.setText("");
            psw.setText("");
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String passw = "passw";
                String usrn = "usrn";
                String usrGet = usr.getText().toString();
                String pswGet = psw.getText().toString();
                if("".equals(usrGet))
                {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                    dialogBuilder.setTitle("Warning");
                    dialogBuilder.setMessage("请输入用户名");
                    dialogBuilder.setPositiveButton("Ok", null);
                    dialogBuilder.show();
                    return;
                }
                if(usrGet.equals("admin") && pswGet.equals("123456"))
                {
                    if(psw_Cheacked)
                    {
                        String inputText = usr.getText().toString();
                        String inputText2 = psw.getText().toString();
                        save(inputText,usrn);
                        save(inputText2,passw);
                    }
                    else
                    {
                        save("",usrn);
                        save("",passw);
                    }
                    SecondActivity.actionStart(MainActivity.this,usrGet,pswGet);
                }
                else
                    Toast.makeText(MainActivity.this,"用户不存在或密码错误！",Toast.LENGTH_SHORT).show();
            }
        });

        btnSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sentIntent = new Intent("com.example.administrator.MY_BROADCAST");
                sentIntent.putExtra("callxiaoming","小明，你妈喊你回家吃饭了！");
                sendBroadcast(sentIntent);
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

        switch(compoundButton.getId())
        {
            case R.id.cbx_psw:
                if(b){
                    save("jizhu","jizhumima");
                    psw_Cheacked = true;
                }else{
                    save("wangji","jizhumima");
                    psw_Cheacked = false;
                }
                break;
            case R.id.cbx_autologin:
                if(b){
                    ;
                }
                break;
        }
    }


    class NetworkChangeReceiver extends BroadcastReceiver{


        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectionManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
            if(networkInfo!= null && networkInfo.isAvailable())
               ;// Toast.makeText(context,"网络已连接",Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context,"网络已断开",Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    protected void onDestroy(){
        super.onDestroy();

        ActivityCollector.removeActivity(this);
        unregisterReceiver(networkChangeReceiver);
    }

    public void save(String str,String type)
    {
        FileOutputStream out = null ;
        BufferedWriter writer = null;
        try{
            out = openFileOutput(type, Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(str);
        }catch(IOException e)
        {
            e.printStackTrace();
        }finally{
            try{
                if(writer != null)
                    writer.close();
            }catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    public String load(String type)
    {
        FileInputStream in = null;
        BufferedReader reader = null;
        String line = "";
        StringBuilder content = new StringBuilder();
        try{
            in = openFileInput(type);
            reader = new BufferedReader(new InputStreamReader(in));

            while((line = reader.readLine())!=null){
                content.append(line);
            }
        }catch(IOException e)
        {
            e.printStackTrace();
        }finally{
            try{
                if(in!=null)
                {
                    in.close();
                }
            }catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        return content.toString();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
