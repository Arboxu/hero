package com.arbo.hero.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.arbo.hero.R;

/**
 * Created by Administrator on 2016/9/8.
 */
public class AnimationUtil extends ProgressDialog{

    private AnimationDrawable mAnimation;
    private Context mContext;
    private ImageView mImageView;
    private String mLoadingTip;
    private TextView mLoadingTv;
    private int count = 0;
    private String oldLoadingTip;
    private int mResid;

    public AnimationUtil(Context context, String content, int id) {
        super(context);
        this.mContext = context;
        this.mResid = id;
        this.mLoadingTip = content;
        setCanceledOnTouchOutside(true);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    public void setContent(String str) {
        mLoadingTv.setText(str);
    }

    private void initView(){
        setContentView(R.layout.progress_dialog);
        mLoadingTv = (TextView)findViewById(R.id.loadingTv);
        mImageView = (ImageView)findViewById(R.id.loadingIv);
    }

    private void initData(){
        mImageView.setBackgroundResource(mResid);
        mAnimation = (AnimationDrawable)mImageView.getBackground();
        mImageView.post(new Runnable() {
            @Override
            public void run() {
                mAnimation.start();
            }
        });
        mLoadingTv.setText(mLoadingTip);
    }

}
