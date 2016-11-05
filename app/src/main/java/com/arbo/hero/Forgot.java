package com.arbo.hero;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.arbo.hero.util.EmailAutoCompleteTextView;
import com.arbo.hero.util.MyDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2016/10/18.
 */

public class Forgot extends AppCompatActivity implements View.OnClickListener{
    @BindView(R.id.btn_verify)
    Button btnVerify;
    EmailAutoCompleteTextView emails;
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.forgot);
        ButterKnife.bind(this);
        emails = (EmailAutoCompleteTextView)findViewById(R.id.et_mail) ;
        setActionBar();
        btnVerify.setOnClickListener(this);
    }

    private void setActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.myactionbar);
        View view = actionBar.getCustomView();
        TextView tv = (TextView) view.findViewById(R.id.action_bar_title);
        tv.setText("重置密码");
        ImageButton imageButton = (ImageButton) view.findViewById(R.id.back);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Forgot.this.finish();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btn_verify:

                final String email = emails.getText().toString();
                BmobUser.resetPasswordByEmail(email, new UpdateListener() {
                    MyDialog myDialog = new MyDialog(Forgot.this);
                    @Override
                    public void done(BmobException e) {
                        Log.d("Forgot","点击了按钮");
                        if(e==null){
                            myDialog.makeDialog(Forgot.this,1,"重置","重置密码请求成功，请到" + email + "邮箱进行密码重置操作");
                        }else{
                            myDialog.makeDialog(Forgot.this,1,"失败", ""+e.getMessage());
                        }
                    }
                });
                break;
        }
    }
}
