package com.arbo.hero;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.arbo.hero.javabean.ChampionListBean;
import com.arbo.hero.network.Constant;
import com.arbo.hero.network.ImageLoader;
import com.arbo.hero.util.CustomProgressDialog;
import com.arbo.hero.util.MyDialog;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserFavorite extends AppCompatActivity {
    GridView gridView;
    TextView heronum;
    TextView coinnum;
    TextView couponnum;
    private  int price,coupon,number,magenum,adcnum,fighternum;
    private  int supportnum,assassinnum,tanknum;
    private  PieChart pieChart;
    static CustomProgressDialog customProgressDialog;

    private  int[] colors;//颜色集合
    private  String[] labels={"ADC","AP  ","战士","刺客","坦克","辅助"};//标签文本
    private  int[] datas={1,1,1,1,1,1};//数据，可以是任何类型的数据，如String,int
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    customProgressDialog.dismiss();
                    PieData mPieData = getPieData(6, 100);
                    showChart(pieChart, mPieData);
                    heronum.setText("总共："+number+"个英雄,价值：");
                    coinnum.setText(""+price);
                    couponnum.setText(""+coupon);
                    break;
                case 2:
                    String getResponse = (String) msg.obj;
                    loadGridView(getResponse);
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.userfavorite);
        setActionBar();
        heronum = (TextView)findViewById(R.id.heronum);
        coinnum = (TextView)findViewById(R.id.coinnum);
        couponnum = (TextView)findViewById(R.id.couponnum);
        gridView = (GridView)findViewById(R.id.favoritegridview);
        pieChart = (PieChart)findViewById(R.id.piechart);
        makeProgressDialog();
        getData();
    }

    public void makeProgressDialog() {
        customProgressDialog = CustomProgressDialog.createDialog(UserFavorite.this);
        customProgressDialog.show();
    }

    private void getData(){
        Intent intent =  getIntent();
        String response = intent.getStringExtra("responseval");
        if(response==null ||response.isEmpty()){
            requestData(Constant.API_ALL_HERO);
            return;
        }else{
            loadGridView(response);
        }
    }

    public void requestData(final String path) {
        final OkHttpClient client = new OkHttpClient();
        final Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(path);
                    Request request = new Request.Builder().url(url).build();
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String tempResponse = response.body().string();
                        Message message = handler.obtainMessage();
                        message.obj = tempResponse;
                        message.what = 2;
                        handler.sendMessage(message);
                    } else {
                        throw new IOException("Unexpected code " + response);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        thread.start();
    }

    private void loadGridView(String response){
        SQLiteDatabase db =  SearchActivity.dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from mfavorites where favorite='true'",null);
        String tempName = "";
        number = cursor.getCount();
        if(number == 0){
            MyDialog myDialog = new MyDialog(this);
            myDialog.makeDialog(this,0,"提示","您没有收藏任何英雄，请在英雄的详情界面,点击右上角的“☆”收藏英雄");
        }
        while (cursor.moveToNext()){
            int id;
            id = cursor.getInt(1);
            String enname = cursor.getString(3);
            String cnname = cursor.getString(4);
            Log.d("mfavorites is true:",id+";"+enname+";"+cnname);
            tempName = tempName+enname;
        }
        cursor.close();
        db.close();


        final String searchName = tempName;
        final ArrayList<HashMap<String,Object>> mList = new ArrayList<>();
        final ImageLoader imageLoader = new ImageLoader(UserFavorite.this,true);
        List<ChampionListBean.AllBean> ls ;
        ChampionListBean championListBean ;
        championListBean = JSON.parseObject(response,ChampionListBean.class);
        ls = championListBean.getAll();
        for(int i = 0 ; i <ls.size() ; i++){
            final ChampionListBean.AllBean allBean = ls.get(i);
            Thread thread = new Thread(){
                @Override
                public void run() {
                    String url ;
                    if(searchName.contains(allBean.getEnName())){
                        int index1 = allBean.getPrice().indexOf(",");
                        String price2=allBean.getPrice().substring(0,index1);
                        price+=Integer.parseInt(price2);
                        coupon+=Integer.parseInt(allBean.getPrice().substring(index1+1));
                        countNum(allBean.getTags());
                        url = Constant.API_HEAD_IMG + allBean.getEnName() + "_120x120.jpg";
                        Bitmap bitmap = imageLoader.getBitmap(url);
                        HashMap<String,Object> map = new HashMap<>();
                        map.put("ItemName",allBean.getEnName());
                        map.put("ItemImage", bitmap);
                        if(allBean.getTitle().length()>5)
                        {
                            String title = allBean.getTitle().substring(0,4)+"..";
                            map.put("ItemText",title);
                        }else{
                            map.put("ItemText", allBean.getTitle());
                        }
                        mList.add(map);
                        Log.d("总共：",number+"个英雄,价值："+price
                                +";  ADC："+adcnum+";  AP："+magenum+";  战士："
                                +fighternum+";  辅助："+supportnum+";  坦克："
                                +tanknum+";  刺客："+assassinnum);
                    }
                }
            };
            thread.start();
            SimpleAdapter simpleAdapter = new SimpleAdapter(UserFavorite.this,
                    mList,R.layout.gridview_item,new String[] {"ItemImage","ItemText"},
                    new int[]{R.id.ItemImage,R.id.ItemText}
                    );
            //通过setViewBinder将bitmap转化后使用
            simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Object bitmapData, String s) {
                    if(view instanceof ImageView && bitmapData instanceof Bitmap){
                        ImageView i = (ImageView)view;
                        i.setImageBitmap((Bitmap) bitmapData);
                        return true;
                    }
                    return false;
                }
            });
            //添加Item到网格中
            gridView.setAdapter(simpleAdapter);
            gridView.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                            DetailsActivity.detailsActionStart(UserFavorite.this, arg2, (String)mList.get(arg2).get("ItemName"));
                        }
                    }
            );
        }
        Message message = handler.obtainMessage();
        message.what = 1;
        handler.sendMessage(message);
    }

    private void setActionBar(){
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.myactionbar);
        View view = actionBar.getCustomView();
        ImageButton btn = (ImageButton)view.findViewById(R.id.more) ;
        btn.setVisibility(View.GONE);
        ImageButton imageButton = (ImageButton) view.findViewById(R.id.back);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserFavorite.this.finish();
            }
        });
    }


    private void countNum(String tag){
        if(tag.contains("marksman"))
            adcnum++;
        else if(tag.contains("mage"))
            magenum++;
        else if(tag.contains("fighter"))
            fighternum++;
        else if(tag.contains("tank"))
            tanknum++;
        else if(tag.contains("support"))
            supportnum++;
        else if(tag.contains("assassin"))
            assassinnum++;

    }

    private void showChart(PieChart pieChart, PieData pieData) {
        pieChart.setHoleColor(Color.argb(0,0,0,0));
        pieChart.setHoleRadius(40f);  //半径
        pieChart.setTransparentCircleRadius(54f); // 半透明圈

        pieChart.setDescription(" ");

        pieChart.setDrawCenterText(true);  //饼状图中间可以添加文字

        pieChart.setDrawHoleEnabled(true);
        pieChart.setDrawSliceText(false);
        pieChart.setRotationAngle(90); // 初始旋转角度

        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(true); // 可以手动旋转

        // display percentage values
        pieChart.setUsePercentValues(true);  //显示成百分比
        // mChart.setUnit(" €");
        // mChart.setDrawUnitsInChart(true);
        // add a selection listener
//      mChart.setOnChartValueSelectedListener(this);
        // mChart.setTouchEnabled(false);
        pieChart.setCenterText("我的英雄");  //饼状图中间的文字
        pieChart.setCenterTextColor(Color.rgb(200, 200, 200));
        //设置数据
        pieChart.setData(pieData);

        Legend mLegend = pieChart.getLegend();  //设置比例图
        mLegend.setEnabled(false);

        colors=mLegend.getColors();
        pieChart.animateXY(1000, 1000);  //设置动画
        // mChart.spin(2000, 0, 360);
        customizeLegend();
    }

    /**
     * 定制图例，通过代码生成布局
     */
    private void customizeLegend(){
        for(int i=0;i<datas.length;i++){
            LinearLayout.LayoutParams lp=new LinearLayout.
                    LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.weight=1;//设置比重为1
            LinearLayout layout=new LinearLayout(this);//单个图例的布局
            layout.setOrientation(LinearLayout.HORIZONTAL);//水平排列
            layout.setGravity(Gravity.CENTER_VERTICAL);//垂直居中
            layout.setLayoutParams(lp);

            //添加color
            LinearLayout.LayoutParams colorLP=new LinearLayout.
                    LayoutParams(20,20);
            colorLP.setMargins(0, 0, 20, 0);
            LinearLayout colorLayout=new LinearLayout(this);
            colorLayout.setLayoutParams(colorLP);
            colorLayout.setBackgroundColor(colors[i]);
            layout.addView(colorLayout);

            //添加label
            TextView labelTV=new TextView(this);
            labelTV.setText(labels[i]+"  ");
            labelTV.setTextColor(Color.rgb(200,200,200));
            layout.addView(labelTV);

            //添加data
            TextView dataTV=new TextView(this);
            dataTV.setText(datas[i]+"");
            dataTV.setTextColor(Color.rgb(200,200,200));
            layout.addView(dataTV);

            LinearLayout legendLayout = (LinearLayout)findViewById(R.id.legendlayout);
            legendLayout.addView(layout);//legendLayout为外层布局即整个图例布局，是在xml文件中定义

        }
    }


    /**
     *
     * @param count 分成几部分
     * @param range
     */
    private PieData getPieData(int count, float range) {

        ArrayList<PieEntry> yValues = new ArrayList<>();  //yVals用来表示封装每个饼块的实际数据

        // 饼图数据
        /**
         * 将一个饼形图分成四部分， 四部分的数值比例为14:14:34:38
         * 所以 14代表的百分比就是14%
         */
        datas[0] = adcnum;
        datas[1] = magenum;
        datas[2] = fighternum;
        datas[3] = assassinnum;
        datas[4] = tanknum;
        datas[5] = supportnum;
        for(int i = 0 ; i < 6; i++){
            yValues.add(new PieEntry(datas[i],i));
        }

        //y轴的集合
        PieDataSet pieDataSet = new PieDataSet(yValues, "Quarterly Revenue 2014");/*显示在比例图上*/
        pieDataSet.setValueTextColor(Color.rgb(200,200,200));
        pieDataSet.setValueTextSize(10f);
        pieDataSet.setSliceSpace(0f); //设置个饼状图之间的距离
        pieDataSet.setDrawValues(true);
        ArrayList<Integer> colors = new ArrayList<Integer>();

        // 饼图颜色
        colors.add(Color.rgb(163,27,27));
        colors.add(Color.rgb(63, 38, 165));
        colors.add(Color.rgb(30,84, 41));
        colors.add(Color.rgb(175, 52, 209));
        colors.add(Color.rgb(103, 126, 10));
        colors.add(Color.rgb(32,131, 122));
        pieDataSet.setColors(colors);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = 5 * (metrics.densityDpi / 160f);
        pieDataSet.setSelectionShift(px); // 选中态多出的长度

        PieData pieData = new PieData(pieDataSet);
        return pieData;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}
