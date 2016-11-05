package com.arbo.hero;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.arbo.hero.network.AnimationUtil;
import com.arbo.hero.network.Constant;
import com.arbo.hero.spinner.CustomerSpinner;
import com.arbo.hero.util.SaveUtil;

import java.net.URLEncoder;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/9/11.
 */
public class FightNumActivity extends Activity implements View.OnClickListener {

    @BindView(R.id.back_btn)
    ImageButton backBtn;
    @BindView(R.id.char1)
    Button char1;
    @BindView(R.id.char2)
    Button char2;
    @BindView(R.id.char3)
    Button char3;
    @BindView(R.id.char4)
    Button char4;
    @BindView(R.id.char5)
    Button char5;
    // KeyboardView keyboardView;
    SaveUtil saveUtil ;
    private WebView power_webView;
    private View xCustomView;
    private WebChromeClient.CustomViewCallback xCustomViewCallback;
    private AnimationUtil dialog = null;
    private CustomerSpinner spinner = null;
    private String[] server = new String[]{"艾欧尼亚", "祖安", "诺克萨斯", "班德尔城", "皮尔特沃夫",
            "战争学院", "巨神峰", "雷瑟守备", "裁决之地", "黑色玫瑰", "暗影岛", "钢铁烈阳", "均衡教派",
            "水晶之痕", "影流", "守望之海", "征服之海", "卡拉曼达", "皮城警备", "比尔吉沃特", "德玛西亚",
            "弗雷尔卓德", "无畏先锋", "恕瑞玛", "扭曲丛林", "巨龙之巢", "教育网专区"};
    ArrayAdapter<String> serverAdapter = null;  //服务器适配器
    private EditText showServerName;

    public int getServerPosition() {
        return serverPosition;
    }

    public void setServerPosition(int serverPosition) {
        this.serverPosition = serverPosition;
    }

    public static ArrayList<String> list = new ArrayList<String>();
    private int serverPosition = 0;
    private ImageButton search_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acount);
        ButterKnife.bind(this);
        buttonInit();
        saveUtil = new SaveUtil(this);
        power_webView = (WebView) findViewById(R.id.power_webview);
        showServerName = (EditText) findViewById(R.id.showServerName);
        search_btn = (ImageButton) findViewById(R.id.search_btn);
        spinner = (CustomerSpinner) findViewById(R.id.spin_server);
        if(saveUtil.load("wanjiaid")!=null)
            showServerName.setText(saveUtil.load("wanjiaid"));
        spinnerInit();
        changeHintText();
        spinner.setList(list);
        setSpinner();
        dialog = new AnimationUtil(FightNumActivity.this, "正在加载..", R.drawable.animation1);
        WebSettings ws = power_webView.getSettings();
        ws.setUseWideViewPort(true);// 可任意比例缩放
        setSettings(ws);
        power_webView.setWebChromeClient(new WebChromeClient());
        power_webView.setWebViewClient(new myWebViewClient());
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uid = showServerName.getText().toString();
                String strUrl = null;
                if (uid != "") {
                    saveUtil.save(uid,"wanjiaid");
                    strUrl = Constant.API_FIGHT + URLEncoder.encode(server[getServerPosition()]) + "&playerName="
                            + URLEncoder.encode(uid);
                } else {
                    Toast.makeText(FightNumActivity.this, "请输入您要查找的召唤师名称！", Toast.LENGTH_SHORT).show();
                }
                if (strUrl != null) {
                    power_webView.loadUrl(strUrl);
                    dialog.show();
                }
            }
        });
        // new KeyboardUtil(this, this, showServerName).showKeyboard();

    }


    public void changeHintText() {
        String hint = showServerName.getHint().toString();
        SpannableString hintChanged = new SpannableString(hint);
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(15, true);
        hintChanged.setSpan(ass, 0, hintChanged.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        showServerName.setHint(hintChanged);
    }

    private void setSpinner() {
        //绑定适配器和值
        serverAdapter = new ArrayAdapter<String>(FightNumActivity.this,
                android.R.layout.simple_spinner_item, server);
        spinner.setAdapter(serverAdapter);
        spinner.setSelection(0, true);  //设置默认选中项，此处为默认选中第4个值

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                setServerPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }

        });
    }

    public void spinnerInit() {
        for (String aServer : server) {
            list.add(aServer);
        }
    }

    @SuppressLint("NewApi")
    private void setSettings(WebSettings setting) {
        setting.setJavaScriptEnabled(true);
        setting.setBuiltInZoomControls(true);
        setting.setDisplayZoomControls(false);
        setting.setSupportZoom(true);

        setting.setDomStorageEnabled(true);
        setting.setDatabaseEnabled(true);
        // 全屏显示
        setting.setUseWideViewPort(true);
        setting.setLoadWithOverviewMode(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
           /* case R.id.btn_simpleType:
            case R.id.btn_webType:
                RadioButton rbtn = (RadioButton) findViewById(view.getId());
                btnWebType.setTextSize(15);
                btnSimpleType.setTextSize(15);
                if (rbtn.isChecked()) {
                    rbtn.setTextSize(20);
                }
                break;*/
            case R.id.back_btn:
                FightNumActivity.this.finish();
                break;
            case R.id.char1:
            case R.id.char2:
            case R.id.char3:
            case R.id.char4:
            case R.id.char5:

                Button btn = (Button) findViewById(view.getId());
                showServerName.append(btn.getText());
                break;

        }
    }

    private void buttonInit() {
       // btnSimpleType.setOnClickListener(this);
       // btnWebType.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        char1.setOnClickListener(this);
        char2.setOnClickListener(this);
        char3.setOnClickListener(this);
        char4.setOnClickListener(this);
        char5.setOnClickListener(this);
    }

    public class myWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            dialog.dismiss();
        }
    }

    /**
     * 判断是否是全屏
     *
     * @return
     */
    public boolean inCustomView() {
        return (xCustomView != null);
    }

    /**
     * 全屏时按返加键执行退出全屏方法
     */
    public void hideCustomView() {
        //xwebchromeclient.onHideCustomView();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        super.onResume();
        power_webView.onResume();
        power_webView.resumeTimers();
        /**
         * 设置为横屏
         */
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        power_webView.onPause();
        power_webView.pauseTimers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        super.onDestroy();
        //  video_fullView.removeAllViews();
        power_webView.loadUrl("about:blank");
        power_webView.stopLoading();
        power_webView.setWebChromeClient(null);
        power_webView.setWebViewClient(null);
        power_webView.destroy();
        power_webView = null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (inCustomView()) {
                // webViewDetails.loadUrl("about:blank");
                hideCustomView();
                return true;
            } else {
                power_webView.loadUrl("about:blank");
                FightNumActivity.this.finish();
            }
        }
        return false;
    }

    /**
     * Created by Administrator on 2016/9/28.
     */
    public static class SignUpActivity extends Activity {


    }
}
