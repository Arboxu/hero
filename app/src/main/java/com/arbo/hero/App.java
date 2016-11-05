package com.arbo.hero;

import android.app.Application;

import com.arbo.hero.network.Constant;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import cn.bmob.v3.Bmob;


public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Bmob.initialize(this, Constant.BMOB_APPID);
        UMShareAPI.get(this);
        /**
         * 调用统计SDK
         *
         * @param appKey
         *            Bmob平台的Application ID
         * @param channel
         *            当前包所在渠道，可以为空
         * @return 是否成功，如果失败请看logcat，可能是混淆或so文件未正确配置
         */

        cn.bmob.statistics.AppStat.i(Constant.BMOB_APPID, null);
        Config.REDIRECT_URL = "http://sns.whalecloud.com/sina2/callback";
    }

    //各个平台的配置，建议放在全局Application或者程序入口
    {
        //微信 wx12342956d1cab4f9,a5ae111de7d9ea137e88a5e02c07c94d
        PlatformConfig.setWeixin("wxa7fb417b70a8492b", "715e90ea8dd1e9c12cad0ca8a69c7104");
        //新浪微博
        PlatformConfig.setSinaWeibo("3048726243", "cc27951abe54ae333c23d98af212c614");
        PlatformConfig.setQQZone("1105692009", "wzXcMfWsuzZi6M17");
    }



}
