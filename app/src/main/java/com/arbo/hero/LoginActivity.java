package com.arbo.hero;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.arbo.hero.util.ActivityCollector;
import com.arbo.hero.util.ClearEditText;
import com.arbo.hero.util.CustomProgressDialog;
import com.arbo.hero.util.MsgDigest;
import com.arbo.hero.util.MyDialog;
import com.arbo.hero.util.SaveUtil;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONObject;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

public class LoginActivity extends Activity implements CompoundButton.OnCheckedChangeListener{
    @BindView(R.id.btn_login)
    Button btn_login;
    @BindView(R.id.UserPsw)
    ClearEditText userPsw;
    @BindView(R.id.cbx_psw)
    CheckBox cbxPsw;
    @BindView(R.id.signUp)
    TextView signUp;
    @BindView(R.id.UserName)
    ClearEditText userName;
    @BindView(R.id.forgotPSW)
    TextView forgotPSW;
    @BindView(R.id.btn_wechatloadgin)
    Button btnWechatloadgin;
    @BindView(R.id.btn_weiboloadgin)
    Button btnWeiboloadgin;
    @BindView(R.id.btn_qqloadgin)
    Button btnQqloadgin;
    @BindView(R.id.cbx_autoload)
    CheckBox cbxAutoload;

    private UMShareAPI mShareAPI = null;
    private boolean psw_Cheacked = false;
    private boolean auto_Cheacked = false;
    private boolean psw_retype = false;
    private String isFirstLoad;
    CustomProgressDialog customProgressDialog;
    SaveUtil saveUtil ;
    MyDialog myDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userlogin);
        ButterKnife.bind(this);
        ActivityCollector.addActivity(this);        //调用外部类添加
        cbxPsw.setOnCheckedChangeListener(this);
        cbxAutoload.setOnCheckedChangeListener(this);
        saveUtil = new SaveUtil(this);
        /** init auth api**/
        mShareAPI = UMShareAPI.get( this );
        changeHintStyle();
        rememberPsw();
        getautoload();
        firstLoad();
        myDialog = new MyDialog(LoginActivity.this);
        btn_login.setOnClickListener(onClickListener);
        signUp.setOnClickListener(onClickListener);
        btnQqloadgin.setOnClickListener(onClickListener);
        btnWechatloadgin.setOnClickListener(onClickListener);
        btnWeiboloadgin.setOnClickListener(onClickListener);
        cbxAutoload.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            switch (v.getId()) {
                case R.id.cbx_autoload:
                    if(cbxAutoload.isChecked()){
                        cbxPsw.setChecked(true);
                    }else{
                        cbxPsw.setChecked(false);
                    }
                    break;
                case R.id.btn_login:
                    userlogin();
                    break;
                case R.id.signUp:
                    newUser();
                    break;
                case R.id.btn_wechatloadgin:
                    Toast.makeText(getApplicationContext(), "尚未开放", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btn_qqloadgin:
                case R.id.btn_weiboloadgin:
                    Toast.makeText(getApplicationContext(), "授权中,请稍等", Toast.LENGTH_SHORT).show();
                    SHARE_MEDIA platform = null;
                    if (v.getId() == R.id.btn_weiboloadgin){
                        platform = SHARE_MEDIA.SINA;
                    }else if (v.getId() == R.id.btn_qqloadgin){
                        platform = SHARE_MEDIA.QQ;
                    }else if (v.getId() == R.id.btn_wechatloadgin){
                        platform = SHARE_MEDIA.WEIXIN;
                    }
                    /**begin invoke umeng api**/
                    mShareAPI.doOauthVerify(LoginActivity.this, platform, umAuthListener);
                    break;
            }
        }
    };

    public void loginWithAuth(final BmobUser.BmobThirdUserAuth authInfo){
        BmobUser.loginWithAuthData(authInfo, new LogInListener<JSONObject>() {
            @Override
            public void done(JSONObject jsonObject, BmobException e) {
                SearchActivity.actionStart(LoginActivity.this);
                LoginActivity.this.finish();
            }
        });
    }

    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            for (String key : data.keySet()) {
                com.umeng.socialize.utils.Log.e("xxxxxx key = "+key+"    value= "+data.get(key));
            }
            String uid = data.get("uid");
            String expires = data.get("expires_in");
            String token = data.get("access_token");
            String platType = "";
            switch (platform){
                case SINA:
                    platType = BmobUser.BmobThirdUserAuth.SNS_TYPE_WEIBO;
                    break;
                case QQ:
                    platType = BmobUser.BmobThirdUserAuth.SNS_TYPE_QQ;
                    break;
                case WEIXIN:
                    platType = BmobUser.BmobThirdUserAuth.SNS_TYPE_WEIXIN;
                    break;
            }
            Log.i("smile", "微博授权成功后返回的信息:token = "+token+",expires ="+expires+",uid = "+uid+"平台："+platform);
            BmobUser.BmobThirdUserAuth authInfo = new BmobUser.BmobThirdUserAuth(platType,token, expires,uid);
            loginWithAuth(authInfo);
            makeProgressDialog();
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText( getApplicationContext(), "一键登录失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            //Toast.makeText( getApplicationContext(), "Authorize cancel", Toast.LENGTH_SHORT).show();
        }
    };
    /** delauth callback interface**/
    private UMAuthListener umdelAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            Toast.makeText(getApplicationContext(), "delete Authorize succeed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText( getApplicationContext(), "delete Authorize fail", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText( getApplicationContext(), "delete Authorize cancel", Toast.LENGTH_SHORT).show();
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.HandleQQError(this,requestCode,umAuthListener);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }


    public void userlogin() {
        String name = userName.getText().toString();
        String pass = userPsw.getText().toString();
        final String nameStr = userName.getText().toString();
        String passwordStr = userPsw.getText().toString();
        String encrymd5 = "";
        if (name.isEmpty() || pass.isEmpty()) {
            myDialog.makeDialog(LoginActivity.this,1,"提示", "用户名或密码为空 !");
            return;
        }
        makeProgressDialog();
        if(userPsw.istextchange()){
            encrymd5 = new MsgDigest().encryUserPSW(passwordStr,passwordStr.length());
            Log.d("ispswchanged",userPsw.istextchange()+"当前密码："+encrymd5);
        }else{
            encrymd5 = passwordStr;
            Log.d("ispswchanged",userPsw.istextchange()+"当前密码："+encrymd5);
        }
        final String finalEncrymd = encrymd5;
        BmobUser.loginByAccount(nameStr, encrymd5, new LogInListener<BmobUser>() {
            @Override
            public void done(BmobUser bmobUser, BmobException e) {
                customProgressDialog.dismiss();
                if (bmobUser != null) {
                    if (psw_Cheacked) {
                        saveUtil.save(nameStr, "UserNamen");
                        saveUtil.save(finalEncrymd, "passw");
                    } else {
                        saveUtil.save("", "UserNamen");
                        saveUtil.save("", "passw");
                    }
                    SearchActivity.actionStart(LoginActivity.this);
                    LoginActivity.this.finish();
                } else {
                    switch (e.getErrorCode()) {
                        case 9016:
                            myDialog.makeDialog(LoginActivity.this,1,"登录失败", "网络不可用,请检查您的网络！");
                            break;
                        case 101:
                            myDialog.makeDialog(LoginActivity.this,1,"登录失败", "用户名或密码不正确！");
                            break;
                        default:
                            myDialog.makeDialog(LoginActivity.this,1,"登录失败", "对不起，服务器正在修复中！");
                            break;
                    }
                }
            }
        });
    }


    public void makeProgressDialog() {
        customProgressDialog = CustomProgressDialog.createDialog(LoginActivity.this);
        customProgressDialog.show();
    }

    private void newUser() {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        LoginActivity.this.startActivity(intent);
    }

    private void forgotPSW() {
        Intent intent = new Intent(LoginActivity.this, Forgot.class);
        LoginActivity.this.startActivity(intent);
    }

    private void firstLoad() {
        String loadtime = saveUtil.load("firstLoadFlag");
        if (loadtime.equals("")) {
            saveUtil.save("noFirst", "firstLoadFlag");
            isFirstLoad = "yes";
        } else {
            isFirstLoad = "no";
        }
    }

    private void rememberPsw() {
        String jizhu = saveUtil.load("jizhumima");
        if (jizhu.equals("jizhu")) {
            cbxPsw.setChecked(true);
            String UserNamen = saveUtil.load("UserNamen");
            String passw = saveUtil.load("passw");
            if (!TextUtils.isEmpty(UserNamen)) {
                userName.setText(UserNamen);
                userName.setSelection(UserNamen.length());
            }
            userPsw.setText(passw);
            Log.d("记住密码：","当前密码为"+passw);
        } else {
            cbxPsw.setChecked(false);
            userName.setText("");
            userPsw.setText("");
        }
        userPsw.setIstextchange(false);
    }

    private void getautoload() {
        String ischecked = saveUtil.load("autoload");
        if (ischecked.equals("checked")) {
            cbxPsw.post(new Runnable() {
                @Override
                public void run() {
                    cbxPsw.performClick();
                }
            });
            cbxAutoload.setChecked(true);
            btn_login.post(new Runnable(){
                @Override
                public void run() {
                    btn_login.performClick();
                }
            });
        } else {
            cbxAutoload.setChecked(false);
        }
    }

    private void changeHintStyle() {
        String hint = userName.getHint().toString();
        SpannableString hintChanged = new SpannableString(hint);
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(12, true);
        hintChanged.setSpan(ass, 0, hintChanged.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        userName.setHint(hintChanged);
    }
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.cbx_psw:
                if (b) {
                    saveUtil.save("jizhu", "jizhumima");
                    psw_Cheacked = true;
                } else {
                    saveUtil.save("wangji", "jizhumima");
                    psw_Cheacked = false;
                }
                break;
            case R.id.cbx_autoload:
                if (b) {
                    Log.d("cbx_autoload",""+b);
                    saveUtil.save("checked", "autoload");
                    auto_Cheacked = true;
                } else {
                    Log.d("cbx_autoload",""+b);
                    saveUtil.save("unchecked", "autoload");
                    auto_Cheacked = false;
                }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
