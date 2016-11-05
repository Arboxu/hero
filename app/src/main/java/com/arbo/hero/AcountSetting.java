package com.arbo.hero;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.arbo.hero.util.ClearEditText;
import com.arbo.hero.util.MsgDigest;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2016/10/28.
 */

public class AcountSetting extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.oldpsw)
    ClearEditText oldpsw;
    @BindView(R.id.newpsw)
    ClearEditText newpsw;
    @BindView(R.id.newpsw2)
    ClearEditText newpsw2;
    @BindView(R.id.changePsw)
    Button changePsw;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_setting);
        ButterKnife.bind(this);
        setActionBar();
    }

    private void setActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.myactionbar);
        View view = actionBar.getCustomView();
        TextView tv = (TextView) view.findViewById(R.id.action_bar_title);
        tv.setText("账号设置");
        ImageButton back = (ImageButton) view.findViewById(R.id.back);
        ImageButton btn = (ImageButton) view.findViewById(R.id.more);
        btn.setVisibility(View.GONE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backPressed();
            }
        });
        changePsw.setOnClickListener(this);
    }

    private void backPressed() {
        this.finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.changePsw:
                String oldPsw = oldpsw.getText().toString();
                String newPsw = newpsw.getText().toString();
                String newPsw2 = newpsw2.getText().toString();
                if(newPsw.equals(newPsw2)){
                    String oldencrymd5 = new MsgDigest().encryUserPSW(oldPsw,oldPsw.length());
                    String newencrymd5 = new MsgDigest().encryUserPSW(newPsw2,newPsw2.length());
                    BmobUser.updateCurrentUserPassword(oldencrymd5,newencrymd5, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e == null){
                                Toast.makeText(AcountSetting.this,
                                        "修改密码成功",Toast.LENGTH_SHORT).show();
                            } else{
                                String msg = "";
                                switch (e.getErrorCode()){
                                    case 206:
                                        msg = "只有账号登录用户才能修改密码";
                                        break;
                                    case 210:
                                        msg = "修改失败，原密码错误!";
                                        break;
                                    case 9016:
                                        msg = "修改失败,网络不可用,请检查您的网络!";
                                        break;
                                    default:
                                        msg = "修改失败!"+e.getErrorCode()+e.getMessage();
                                }
                                Toast.makeText(AcountSetting.this
                                        ,msg,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(AcountSetting.this,"新密码不一致",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
