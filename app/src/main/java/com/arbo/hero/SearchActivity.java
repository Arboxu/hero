package com.arbo.hero;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arbo.hero.model.Defaultcontent;
import com.arbo.hero.network.AnimationUtil;
import com.arbo.hero.network.Constant;
import com.arbo.hero.network.ImageLoader;
import com.arbo.hero.storage.DataCleanManager;
import com.arbo.hero.storage.MyDatabaseHelper;
import com.arbo.hero.util.ActivityCollector;
import com.arbo.hero.util.ClearEditText;
import com.arbo.hero.util.MyDialog;
import com.arbo.hero.util.RoundBitmap;
import com.arbo.hero.util.SaveUtil;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.viewpagerindicator.TabPageIndicator;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;

import static com.arbo.hero.HeadPortrait.imageCropUri;

public class SearchActivity extends FragmentActivity implements View.OnClickListener {
    @BindView(R.id.account)
    ImageView account;
    @BindView(R.id.search)
    ImageButton search;
    @BindView(R.id.inputContent)
    ClearEditText inputContent;
    @BindView(R.id.showLayout)
    LinearLayout showLayout;
    GridFragment gridFragment;
    GridFragment gridFragment2;
    GridFragment gridFragment3;
    GridFragment gridFragment4;
    GridFragment gridFragment5;
    GridFragment gridFragment6;
    GridFragment gridFragment7;
    ArrayList<Fragment> listFragmentsa;
    public String input;
    public static AnimationUtil animationDialog;
    SlidingMenu menu;
    private List<TitleData> mTitleDataList;
    public static MyDatabaseHelper dbHelper;
    ImageView headimg;
    private static final int HEAD_REQUEST_CODE = 0x05;
    public static ImageLoader imageLoader = ImageLoader.getInstance();
    private String cacheSize ;
    MyDialog myDialog;
    public ArrayList<SnsPlatform> platforms = new ArrayList<SnsPlatform>();
    private long clickTime = 0;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
        ButterKnife.bind(this);
        ActivityCollector.addActivity(this);
        initSlidingMenu();
        makeLoadDialog();
        imageLoader.init(this,true);
        initsearchContent();
        loadFragment();
        initFragment();
        initPlatforms();
        myDialog = new MyDialog(SearchActivity.this);
        changeHintText();
        search.bringToFront();
        account.setOnClickListener(this);
        search.setOnClickListener(this);
        dbHelper = new MyDatabaseHelper(this, "userFavor.db", null, 1);
    }


    public static void actionStart(Context context) {
        Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keycode,KeyEvent event){
        if(keycode == KeyEvent.KEYCODE_BACK){
            myExit();
            return true;
        }
        return super.onKeyDown(keycode,event);
    }

    private void myExit(){
        if((System.currentTimeMillis() - clickTime) >2000){
            clickTime = System.currentTimeMillis();
            Toast.makeText(SearchActivity.this,"再次点击Back退出程序！",Toast.LENGTH_SHORT).show();
        }else{
            this.finish();
        }
    }
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.account:
                menu.showMenu();
                break;
            case R.id.search:
                input = inputContent.getText().toString();
                if (input.equals("")) {
                    Toast toast = Toast.makeText(this, "请输入英雄名称", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    Intent intent1 = new Intent(SearchActivity.this, ShowSearchResult.class);
                    intent1.putExtra("searchInput", input);
                    intent1.putExtra("responseval", Constant.responsedata);
                    SearchActivity.this.startActivity(intent1);
                }
                break;
            default:
                break;
        }
    }

    public void changeHintText() {
        inputContent.setGravity(Gravity.CENTER);
        String hint = inputContent.getHint().toString();
        SpannableString hintChanged = new SpannableString(hint);
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(12, true);
        hintChanged.setSpan(ass, 0, hintChanged.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        inputContent.setHint(hintChanged);
    }

    public void loadFragment() {
        listFragmentsa = new ArrayList<>();
        gridFragment = new GridFragment(0);
        gridFragment2 = new GridFragment(1);
        gridFragment3 = new GridFragment(2);
        gridFragment4 = new GridFragment(3);
        gridFragment5 = new GridFragment(4);
        gridFragment6 = new GridFragment(5);
        gridFragment7 = new GridFragment(6);
        listFragmentsa.add(gridFragment);
        listFragmentsa.add(gridFragment2);
        listFragmentsa.add(gridFragment3);
        listFragmentsa.add(gridFragment4);
        listFragmentsa.add(gridFragment5);
        listFragmentsa.add(gridFragment6);
        listFragmentsa.add(gridFragment7);
    }

    private void initFragment(){
        TabPageIndicator mIndicator = (TabPageIndicator) findViewById(R.id.id_indicator);
        ViewPager mViewPager = (ViewPager) findViewById(R.id.id_pager);
        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                // 当当天条目是0的时候，设置可以在任意位置拖拽出SlidingMenu
                if (position == 0) {
                    menu.setTouchModeAbove(
                            SlidingMenu.TOUCHMODE_FULLSCREEN);
                } else {
                    // 当在其他位置的时候，设置不可以拖拽出来(SlidingMenu.TOUCHMODE_NONE)，或只有在边缘位置才可以拖拽出来TOUCHMODE_MARGIN
                    menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        FragmentPagerAdapter mAdapter = new TabAdapter(getSupportFragmentManager(), listFragmentsa);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(1);
        mIndicator.setViewPager(mViewPager, 0);
    }

    private void getCacheSize(){
        try {
            cacheSize = DataCleanManager.getMyCacheSize(SearchActivity.this) ;
        } catch (Exception e) {
            cacheSize = "0";
            e.printStackTrace();
        }
    }


    private void makeLoadDialog() {
        animationDialog = new AnimationUtil(this, "  全军出击..", R.drawable.animation1);
        animationDialog.show();
    }

    private void initsearchContent() {
        //用于获取焦点，避免edittext 截取焦点弹出输入法
        //使RelativeLayout 获取焦点，防止 EditText 截取
        final RelativeLayout rlytTimerName = (RelativeLayout) findViewById(R.id.topLayout);
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
                    inputContent.setGravity(Gravity.CENTER);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }


/**
 * 以下为侧滑菜单部分 代码
 */

    /***
     * 初始化侧滑菜单
     */
    private void initSlidingMenu() {

        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        // 设置触摸屏幕的模式
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
//        menu.setShadowDrawable(R.drawable.shadow);
        // 设置滑动菜单视图的宽度
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        // 设置渐入渐出效果的值
        menu.setFadeDegree(0.60f);
        /**
         * SLIDING_WINDOW will include the Title/ActionBar in the content
         * section of the SlidingMenu, while SLIDING_CONTENT does not.
         */
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        //为侧滑菜单设置布局
        menu.setMenu(getLeftMenu());
    }


    /**
     * 设置侧滑菜单的布局、添加监听事件
     * */
    public View getLeftMenu() {
        TitleAdapter mTitleAdapter = new TitleAdapter();
        getCacheSize();
        //初始化后，TitleAdapter内就有了内容。否则会报错。
        mTitleDataList = getTitleDataList();
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.left_fragment, null);
        ListView listview = (ListView) v.findViewById(R.id.slidelistview);
        headimg = (ImageView)v.findViewById(R.id.account_head);
        ImageButton setting = (ImageButton)v.findViewById(R.id.account_setting);
        ImageButton shared = (ImageButton)v.findViewById(R.id.account_shared);
        Bitmap bitmap ;
        if(imageCropUri ==null){
            bitmap = ((BitmapDrawable) headimg.getDrawable()).getBitmap();
        }else{
            bitmap = decodeUriAsBitmap(this,imageCropUri);
        }


        headimg.setImageBitmap(RoundBitmap.toRoundBitmap(bitmap));
        mTitleAdapter.notifyDataSetChanged();
        listview.setAdapter(mTitleAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                switch (position)
                {
                    case 0:
                        //favorite
                        Intent intent3 = new Intent(SearchActivity.this, UserFavorite.class);
                        intent3.putExtra("searchInput", input);
                        intent3.putExtra("responseval", Constant.responsedata);
                        SearchActivity.this.startActivity(intent3);
                        break;
                    case 1:
                        //fight number
                        Intent intent = new Intent(SearchActivity.this, FightNumActivity.class);
                        SearchActivity.this.startActivity(intent);
                        break;
                    case 2:
                        //check for update
                        Toast.makeText(SearchActivity.this,"当前已是最新版本",Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        //clear cache
                        try {
                            makedialog();
                            Log.d("cacheSize",""+cacheSize);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 4:
                        new SaveUtil(SearchActivity.this).save("unchecked","autoload");
                        Intent intent2 = new Intent(SearchActivity.this, LoginActivity.class);
                        BmobUser.logOut();  //清楚缓存用户
                        SearchActivity.this.startActivity(intent2);
                        SearchActivity.this.finish();
                        break;
                    default:break;
                }
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchActivity.this,AcountSetting.class);
                startActivity(intent);
            }
        });
        headimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchActivity.this, HeadPortrait.class);
                SearchActivity.this.startActivityForResult(intent,HEAD_REQUEST_CODE);
            }
        });
        shared.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ShareAction(SearchActivity.this).setDisplayList(SHARE_MEDIA.SINA,SHARE_MEDIA.QQ,SHARE_MEDIA.QZONE,SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.WEIXIN_FAVORITE,SHARE_MEDIA.MORE)
                        .withTitle(Defaultcontent.title)
                        .withText(Defaultcontent.text+"——来自")
                        .withMedia(new UMImage(SearchActivity.this, Defaultcontent.imageurl))
                        .withTargetUrl("http://herocollege.bmob.cn/")
                        .setCallback(umShareListener)
                        .open();
            }
        });
        return v;
    }

    private void makedialog(){
        Dialog dialog;
        MyDialog.Builder clearBuilder = new
                MyDialog.Builder(SearchActivity.this);
        clearBuilder.setTitle("清理缓存")
                .setMessage(" 清理缓存后将自动重启“英雄学堂”，确定清理？");
        clearBuilder.setTitle("清理缓存")
                .setMessage(" 清理缓存后将自动重启“英雄学堂”，确定清理？")
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                DataCleanManager.clearMyCache(SearchActivity.this);
                                Intent i = getBaseContext().getPackageManager()
                                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                SearchActivity.this.finish();
                                dialog.dismiss();
                            }
                        });
        dialog = clearBuilder.create();
        Window dialogWindow = dialog.getWindow();
        WindowManager m = this.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.55); // 高度设置为屏幕的0.55
        p.width = (int) (d.getWidth() * 0.7); // 宽度设置为屏幕的0.7
        dialogWindow.setAttributes(p);
        dialog.show();
    }

    /**
     * 设置头像
     *
     *
     * */
    //从Uri中获取Bitmap格式的图片
    private static Bitmap decodeUriAsBitmap(Context context, Uri uri) {
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode)
        {
            case HEAD_REQUEST_CODE:
                int s = data.getIntExtra("CODE_RESULT_VAL",0);
                if(s == 1){
                    Bitmap bitmap = decodeUriAsBitmap(this,imageCropUri);
                    if(bitmap!=null)
                        headimg.setImageBitmap(RoundBitmap.toRoundBitmap(bitmap));
                }
                break;
            default:
                /** attention to this below ,must add this**/
                UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
                com.umeng.socialize.utils.Log.d("result","onActivityResult");
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initPlatforms(){
        platforms.clear();
        for (SHARE_MEDIA e : SHARE_MEDIA.values()) {
            if (!e.toString().equals(SHARE_MEDIA.GENERIC.toString())){
                platforms.add(e.toSnsPlatform());
            }
        }
    }
    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            com.umeng.socialize.utils.Log.d("plat","platform"+platform);
            if(platform.name().equals("WEIXIN_FAVORITE")){
                Toast.makeText(SearchActivity.this,platform + " 收藏成功啦",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(SearchActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(SearchActivity.this,platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
            if(t!=null){
                com.umeng.socialize.utils.Log.d("throw","throw:"+t.getMessage());
            }
        }
        @Override
        public void onCancel(SHARE_MEDIA platform) {
            //Toast.makeText(SearchActivity.this,platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };


    public List<TitleData> getTitleDataList() {
        List<TitleData> listItem = new ArrayList<>();
        TitleData data = new TitleData("我的收藏", R.drawable.heart,"");
        listItem.add(data);
        TitleData data2 = new TitleData("战斗力查询", R.drawable.fightnumber,"");
        listItem.add(data2);
        TitleData data3 = new TitleData("检测更新", R.drawable.update,"");
        listItem.add(data3);
        TitleData data4 = new TitleData("清理缓存", R.drawable.clearcache,"("+cacheSize+")");
        listItem.add(data4);
        TitleData data5 = new TitleData("退出登录", R.drawable.signout,"");
        listItem.add(data5);
        return listItem;
    }

    class TitleData {
        private Bitmap bitmap;
        private String mTitle;
        private Bitmap mIcon;
        private String mCache;
        public String getmCache() {
            return mCache;
        }

        public void setmCache(String mCache) {
            this.mCache = mCache;
        }
        public void setmIcon(int mIcon) {
            bitmap = BitmapFactory.decodeResource(getResources(), mIcon);
            this.mIcon = bitmap;
        }
        TitleData(String title, int icon,String cachesize) {
            bitmap = BitmapFactory.decodeResource(getResources(), icon);
            this.mTitle = title;
            this.mIcon = bitmap;
            this.mCache = cachesize;
        }
        String getmTitle() {
            return mTitle;
        }
        Bitmap getmIcon() {
            return mIcon;
        }


    }

    class TitleAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mTitleDataList.size();
        }
        @Override
        public Object getItem(int position) {
            return mTitleDataList.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            View view = convertView;
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.sliding_menu_row, null);
                viewHolder = new ViewHolder();
                viewHolder.mTitleImageView = (ImageView) view.findViewById(R.id.row_icon);
                viewHolder.mTitleTextView = (TextView) view.findViewById(R.id.row_title);
                viewHolder.mTitleCache = (TextView)view.findViewById(R.id.cache);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            final TitleData titleData = (TitleData) getItem(position);
            viewHolder.mTitleTextView.setText(titleData.getmTitle());
            viewHolder.mTitleImageView.setImageBitmap(titleData.getmIcon());
            viewHolder.mTitleCache.setText(titleData.getmCache());
            return view;
        }

        class ViewHolder {
            protected ImageView mTitleImageView;
            protected TextView mTitleTextView;
            protected TextView mTitleCache;
        }
    }
}
