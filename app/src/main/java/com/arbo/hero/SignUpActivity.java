package com.arbo.hero;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.arbo.hero.util.ClearEditText;
import com.arbo.hero.util.CustomProgressDialog;
import com.arbo.hero.util.EmailAutoCompleteTextView;
import com.arbo.hero.util.MsgDigest;
import com.arbo.hero.util.MyDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;


public class SignUpActivity extends Activity implements View.OnClickListener {
    @BindView(R.id.psw_first)
    EditText pswFirst;
    @BindView(R.id.psw_confirm)
    EditText pswConfirm;
    @BindView(R.id.btn_registe)
    Button btnRegiste;
    @BindView(R.id.back_to_login1)
    ImageButton backToLogin1;
    @BindView(R.id.cbx_showpsw)
    CheckBox cbxShowpsw;
    @BindView(R.id.back_to_login2)
    Button backToLogin2;
    EmailAutoCompleteTextView emails;
    CustomProgressDialog customProgressDialog;
    private final int CLOSEDIALOG = 0;
    private final int CANCELDIALOG = 1;

    @BindView(R.id.registeLayout)
    LinearLayout registeLayout;
    @BindView(R.id.usrnametv)
    ClearEditText usrnametv;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        emails = (EmailAutoCompleteTextView) findViewById(R.id.emailstv);
        ButterKnife.bind(this);
        initsearchContent();

        changeViewHint(emails);
        changeViewHint(pswConfirm);
        changeViewHint(pswFirst);
        changeViewHint(usrnametv);

        btnRegiste.setOnClickListener(this);
        backToLogin1.setOnClickListener(this);
        backToLogin2.setOnClickListener(this);
        cbxShowpsw.setOnClickListener(this);
    }

    public void changeViewHint(View view){
        EditText editText = (EditText)view;
        String hint = editText.getHint().toString();
        SpannableString hintChanged = new SpannableString(hint);
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(12, true);
        hintChanged.setSpan(ass, 0, hintChanged.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setHint(hintChanged);
    }

    public void newUserSignUp() {
        final String emailstext = emails.getText().toString();
        String username = usrnametv.getText().toString();
        String psw1 = pswFirst.getText().toString();
        String psw2 = pswConfirm.getText().toString();
        if (psw1.isEmpty()) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if ((!psw1.equals(psw2)) && (!psw1.isEmpty()))
            Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show();
        else {
            makeProgressDialog();
            String encrymd5 = new MsgDigest().encryUserPSW(psw1,psw1.length());
            BmobUser bmobUser = new BmobUser();
            if(!emailstext.isEmpty())
                bmobUser.setEmail(emailstext);
            bmobUser.setUsername(username);
            bmobUser.setPassword(encrymd5);
            bmobUser.signUp(new SaveListener<BmobUser>() {
                @Override
                public void done(BmobUser bu, BmobException e){
                    customProgressDialog.dismiss();
                    final MyDialog myDialog = new MyDialog(SignUpActivity.this);
                    if(e == null){
                        BmobUser.requestEmailVerify(emailstext, new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    myDialog.makeDialog(SignUpActivity.this,CLOSEDIALOG, "发送验证邮件", "请前往邮箱验证..."+"\n"+"已验证？确定返回登录！");
                                } else {
                                    myDialog.makeDialog(SignUpActivity.this,CANCELDIALOG, "发送验证失败", e.getMessage());
                                }
                            }
                        });
                    }else{
                        switch (e.getErrorCode())
                        {
                            case 9016:
                                myDialog.makeDialog(SignUpActivity.this,CANCELDIALOG,"注册失败","网络不可用,请检查您的网络！");
                                break;
                            case 202:
                                myDialog.makeDialog(SignUpActivity.this,CANCELDIALOG,"注册失败","用户名已被使用,请使用其他用户名！");
                                break;
                            case 203:
                                myDialog.makeDialog(SignUpActivity.this,CANCELDIALOG,"注册失败","邮箱已被使用,请使用其他邮箱！");
                                break;
                            default:
                                Log.e("注册失败",e.getErrorCode()+e.getMessage());
                                myDialog.makeDialog(SignUpActivity.this,CANCELDIALOG,"注册失败","对不起，服务器异常，正在修复中！");
                                break;
                        }
                    }
                }
            });
        }
    }

    public void makeProgressDialog() {
        customProgressDialog = CustomProgressDialog.createDialog(SignUpActivity.this);
        customProgressDialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_registe:
                newUserSignUp();
                break;
            case R.id.back_to_login1:
            case R.id.back_to_login2:
                SignUpActivity.this.finish();
                break;
            case R.id.cbx_showpsw:
                if (cbxShowpsw.isChecked()) {
                    pswFirst.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    pswConfirm.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    pswFirst.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    pswConfirm.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                pswFirst.postInvalidate();
                //切换后将EditText光标置于末尾
                CharSequence charSequence = pswFirst.getText();
                if (charSequence instanceof Spannable) {
                    Spannable spanText = (Spannable) charSequence;
                    Selection.setSelection(spanText, charSequence.length());
                }
                break;
        }
    }

    private void initsearchContent() {
        // 使RelativeLayout 获取焦点，防止 EditText 截取
        final LinearLayout rlytTimerName = (LinearLayout) findViewById(R.id.registeLayout);
        rlytTimerName.setFocusable(true);
        rlytTimerName.setFocusableInTouchMode(true);
        rlytTimerName.requestFocus();
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // 获取当前焦点所在的控件；
            View view = getCurrentFocus();
            if (view != null && view instanceof EditText) {
                Rect r = new Rect();
                view.getGlobalVisibleRect(r);
                int rawX = (int) ev.getRawX();
                int rawY = (int) ev.getRawY();

                // 判断点击的点是否落在当前焦点所在的 view 上；
                if (!r.contains(rawX, rawY)) {
                    view.clearFocus();
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}
