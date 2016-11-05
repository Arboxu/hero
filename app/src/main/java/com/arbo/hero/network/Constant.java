package com.arbo.hero.network;

public class Constant {
    public static String responsedata;

    public static String getResponsedata() {
        return responsedata;
    }

    public static void setResponsedata(String responsedata) {
        Constant.responsedata = responsedata;
    }


    public static final String API_ALL_HERO = "http://lolbox.duowan.com/phone/apiHeroes.php?type=all";
    public static final String API_HERO_DETAILS = "http://lolbox.duowan.com/phone/apiHeroDetail.php?heroName=";
    public static final String API_HEAD_IMG = "http://img.lolbox.duowan.com/champions/";
    public static final String API_VEDIO_HTML = "http://v.huya.com/lol/hero/";
    public static final int MSG_WHAT_CHAMP_Str = 4;
    public static final int MSG_WHAT_GET_HTML = 7;
    public static final String API_ALL_SKILL = "http://img.lolbox.duowan.com/abilities/";
    public static final String API_FIGHT = "http://lolbox.duowan.com/playerDetail.php?"
            + "callback=jQuery111109943135452922434_1413010378460&token=524740466"
            + "&serverName=";

    //Bmob APPID
    public static final String BMOB_APPID ="d4aab64fa8f0f1eb749cfc6b9251b897";

    public static final String DESCRIPTOR = "com.umeng.share";

    private static final String TIPS = "请移步官方网站 ";
    private static final String END_TIPS = ", 查看相关说明.";
    public static final String TENCENT_OPEN_URL = TIPS + "http://wiki.connect.qq.com/android_sdk使用说明"
            + END_TIPS;
    public static final String PERMISSION_URL = TIPS + "http://wiki.connect.qq.com/openapi权限申请"
            + END_TIPS;

}
