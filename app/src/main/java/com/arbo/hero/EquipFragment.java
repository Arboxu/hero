package com.arbo.hero;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.arbo.hero.network.HttpUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class EquipFragment extends Fragment implements AdapterView.OnItemClickListener{
    private TitleAdapter mTitleAdapter;
    private List<TitleData> mTitleDataList;
    ArrayList<List<String>> imageUrlList = new ArrayList<>();
    private String response;
    private ListView mListView;

    private int czcount = 0  ;
    private int zbcount = 0  ;
    List<TitleData> listItem = new ArrayList<TitleData>();
    Handler mhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    DetailsActivity.dialog.dismiss();
                    Elements units;
                    TitleData titleData = new TitleData();
                    EquipBean equipBean = new EquipBean();
                    final List<EquipBean.EquipMsg> list =equipBean.getAll();
                    units = (Elements) msg.obj;
                    for(int i = 0;i<units.size();i++){      //4
                        EquipBean.EquipMsg equipmsg = new EquipBean.EquipMsg();
                        Element unit_ele = units.get(i);
                        String title = unit_ele.getElementsByClass("equip-list-hd").select("b").text();
                        String title2 = unit_ele.getElementsByClass("equip-list-hd").select("span").get(0).text();
                        String title3 = unit_ele.getElementsByClass("equip-list-hd").select("span").get(1).text();
                        Elements imge = unit_ele.getElementsByClass("equip").get(0).child(0).select("ul").select("li");
                        String good = unit_ele.getElementsByClass("name").select("p").get(0).text();
                        String bad = unit_ele.getElementsByClass("name").select("p").get(1).text();
                        for(int j = 0 ; j <imge.size();j++){        //6
                            String imageurl = imge.get(j).select("img").attr("src");
                            if(j==0){
                                equipmsg.setImgLink1(imageurl);
                            }else if(j==1){
                                equipmsg.setImgLink2(imageurl);
                            }else if(j==2){
                                equipmsg.setImgLink3(imageurl);
                            }else if(j==3){
                                equipmsg.setImgLink4(imageurl);
                            }else if(j==4){
                                equipmsg.setImgLink5(imageurl);
                            }else if(j==5){
                                equipmsg.setImgLink6(imageurl);
                            }
                        }
                        equipmsg.setEquipTitle(title);
                        equipmsg.setEquipAuthor(title2);
                        equipmsg.setEquipNum(title3);
                        equipmsg.setEquipGood(good);
                        equipmsg.setEquipBad(bad);

                        list.add(i,equipmsg);
                    }
                    if(list!= null){
                        for (int i = 0; (i < list.size()) && (listItem.size()<4); i++) {
                            TitleData data = new TitleData(list.get(i).getEquipTitle(),
                                    list.get(i).getEquipAuthor(),list.get(i).getEquipNum(),
                                    list.get(i).getEquipGood(),list.get(i).getEquipBad(),
                                    null,null,null,null,null,null);
                            listItem.add(data);
                        }
                    }
                    mTitleDataList = listItem;
                    mTitleAdapter.notifyDataSetChanged();
                    getVedioImg(list);
                    break;
                case 2:
                    Bitmap img = (Bitmap) msg.obj;
                    if(!mTitleDataList.isEmpty()){
                        if(zbcount == 0){
                            mTitleDataList.get(czcount).setEquipImage1(img);
                        }else if(zbcount == 1){
                            mTitleDataList.get(czcount).setEquipImage2(img);
                        }else if(zbcount == 2){
                            mTitleDataList.get(czcount).setEquipImage3(img);
                        }else if(zbcount == 3){
                            mTitleDataList.get(czcount).setEquipImage4(img);
                        }else if(zbcount == 4){
                            mTitleDataList.get(czcount).setEquipImage5(img);
                        }else if(zbcount == 5){
                            mTitleDataList.get(czcount).setEquipImage6(img);
                        }
                        mTitleAdapter.notifyDataSetChanged();
                        zbcount++;
                        if(zbcount == 6){
                            zbcount = 0;
                            czcount++;
                            if(czcount == 4){
                                czcount = 0 ;
                            }
                        }
                    }
                    break;
            }
        }
    };


    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    public EquipFragment(){

    }
    @SuppressLint("ValidFragment")
    public EquipFragment(String response)
    {
        this.response = response;
    }
    public String getResponse(){
        return this.response;
    }
    public View onCreateView(LayoutInflater layoutInflater,ViewGroup container,Bundle savedInstanceState)
    {

        final View titleView = layoutInflater.inflate(R.layout.equipfragment,container,false);
        mTitleDataList = getTitleDataList();
        mListView = (ListView) titleView.findViewById(R.id.equip_lv);
        mTitleAdapter = new TitleAdapter();

        mListView.setAdapter(mTitleAdapter);
        mListView.setOnItemClickListener(this);

     //   requestData2();
        requestData();
        return titleView;
    }




    public List<TitleData> getTitleDataList() {
        List<TitleData> listItem = new ArrayList<>();
        return listItem;
    }

    public void getVedioImg(final List<EquipBean.EquipMsg> list  ){
        Thread thread = new Thread(){
            public void run(){
                Bitmap bitmap = null;
                for(int i = 0 ; i<list.size();i++)      //4个出装信息
                {
                    for (int j = 0; j < 6; j++) {
                        if(j==0){
                            String url = list.get(i).getImgLink1()     ;
                            if(url!="")
                                bitmap = DetailsActivity.imageLoader.getBitmap(url);   //装备1~~6
                        }else if(j==1){
                            String url = list.get(i).getImgLink2()    ;
                            if(url!="")
                                bitmap = DetailsActivity.imageLoader.getBitmap(url);   //装备1~~6
                        }else if(j==2){
                            String url = list.get(i).getImgLink3()    ;
                            if(url!="")
                                bitmap = DetailsActivity.imageLoader.getBitmap(url);   //装备1~~6
                        }else if(j==3){
                            String url = list.get(i).getImgLink4()    ;
                            if(url!="")
                                bitmap = DetailsActivity.imageLoader.getBitmap(url);   //装备1~~6
                        }else if(j==4){
                            String url = list.get(i).getImgLink5()    ;
                            if(url!="")
                                bitmap = DetailsActivity.imageLoader.getBitmap(url);   //装备1~~6
                        }else if(j==5){
                            String url = list.get(i).getImgLink6()    ;
                            if(url!="")
                                bitmap = DetailsActivity.imageLoader.getBitmap(url);   //装备1~~6
                        }
                        if(bitmap!=null){
                            Message msg = mhandler.obtainMessage();
                            msg.what = 2;
                            msg.obj = bitmap;
                            mhandler.sendMessage(msg);
                        }
                    }
                }
            }
        };
        thread.start();
    }

    public void requestData(){
        Thread thread = new Thread(){
            @Override
            public void run() {
                String path = "http://db.duowan.com/lolcz/list.html?name="+ getResponse() +"&flag=1";
                InputStream inputStream = HttpUtil.getInputStream(path);
                try {
                    String getHtml  = HttpUtil.inputStream2String(inputStream);
                    Document document = Jsoup.parse(getHtml);
                    Elements units = document.getElementsByClass("list");
                    Message message = mhandler.obtainMessage();
                    message.what = 1;
                    message.obj = units;
                    mhandler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

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
            TitleAdapter.ViewHolder viewHolder;
            View view = convertView;

            if(view == null) {
                view = getActivity().getLayoutInflater().inflate(R.layout.equip_list_view_item, null);
                viewHolder = new TitleAdapter.ViewHolder();
                viewHolder.mTitleImageView1 = (ImageView) view.findViewById(R.id.eq_img1);
                viewHolder.mTitleImageView2 = (ImageView) view.findViewById(R.id.eq_img2);
                viewHolder.mTitleImageView3 = (ImageView) view.findViewById(R.id.eq_img3);
                viewHolder.mTitleImageView4 = (ImageView) view.findViewById(R.id.eq_img4);
                viewHolder.mTitleImageView5 = (ImageView) view.findViewById(R.id.eq_img5);
                viewHolder.mTitleImageView6 = (ImageView) view.findViewById(R.id.eq_img6);
                viewHolder.mTitleTextView = (TextView) view.findViewById(R.id.equip_tv);
                viewHolder.mauthorTextView = (TextView) view.findViewById(R.id.author_tv);
                viewHolder.mnumTextView = (TextView) view.findViewById(R.id.fightnum_tv);
                viewHolder.mgoodTextView = (TextView) view.findViewById(R.id.good_tv);
                viewHolder.mbadTextView = (TextView) view.findViewById(R.id.bad_tv);
                view.setTag(viewHolder);
            } else {
                viewHolder = (TitleAdapter.ViewHolder) view.getTag();
            }

            final TitleData titleData = (TitleData) getItem(position);
            viewHolder.mTitleTextView.setText(titleData.getmTitle());
            viewHolder.mauthorTextView.setText(titleData.getmAuthor());
            viewHolder.mnumTextView.setText(titleData.getmNum());
            viewHolder.mgoodTextView.setText(titleData.getMgood());
            viewHolder.mbadTextView.setText(titleData.getMbad());
            viewHolder.mTitleImageView1.setImageBitmap(titleData.equipImage1);
            viewHolder.mTitleImageView2.setImageBitmap(titleData.equipImage2);
            viewHolder.mTitleImageView3.setImageBitmap(titleData.equipImage3);
            viewHolder.mTitleImageView4.setImageBitmap(titleData.equipImage4);
            viewHolder.mTitleImageView5.setImageBitmap(titleData.equipImage5);
            viewHolder.mTitleImageView6.setImageBitmap(titleData.equipImage6);
            return view;
        }

        class ViewHolder {
            protected ImageView mTitleImageView1;
            protected ImageView mTitleImageView2;
            protected ImageView mTitleImageView3;
            protected ImageView mTitleImageView4;
            protected ImageView mTitleImageView5;
            protected ImageView mTitleImageView6;
            protected TextView mTitleTextView;
            protected TextView mauthorTextView;
            protected TextView mnumTextView;
            protected TextView mgoodTextView;
            protected TextView mbadTextView;
        }
    }



    class TitleData {
        String getmTitle() {
            return mTitle;
        }

        void setmTitle(String mTitle) {
            this.mTitle = mTitle;
        }

        private String mTitle;

        public String getmNum() {
            return mNum;
        }

        public void setmNum(String mNum) {
            this.mNum = mNum;
        }

        public String getMgood() {
            return mgood;
        }

        public void setMgood(String mgood) {
            this.mgood = mgood;
        }

        public String getMbad() {
            return mbad;
        }

        public void setMbad(String mbad) {
            this.mbad = mbad;
        }


        private String mNum;
        private String mgood;
        private String mbad;
        private String mAuthor;
        private Bitmap equipImage1;
        private Bitmap equipImage2;
        private Bitmap equipImage3;
        private Bitmap equipImage4;
        private Bitmap equipImage5;
        private Bitmap equipImage6;
        public TitleData(){

        }

        public TitleData(String title,String mAuthor,String num,String good,String bad, Bitmap equipImage1,Bitmap equipImage2,Bitmap equipImage3
                         ,Bitmap equipImage4,Bitmap equipImage5,Bitmap equipImage6) {
            this.mTitle = title;
            this.mAuthor = mAuthor;
            this.mNum = num;
            this.mgood = good;
            this.mbad = bad;
            this.equipImage1 = equipImage1;
            this.equipImage2 = equipImage2;
            this.equipImage3 = equipImage3;
            this.equipImage4 = equipImage4;
            this.equipImage5 = equipImage5;
            this.equipImage6 = equipImage6;

        }

        public Bitmap getEquipImage1() {
            return equipImage1;
        }

        void setEquipImage1(Bitmap equipImage1) {
            this.equipImage1 = equipImage1;
        }

        public Bitmap getEquipImage2() {
            return equipImage2;
        }

        void setEquipImage2(Bitmap equipImage2) {
            this.equipImage2 = equipImage2;
        }

        public Bitmap getEquipImage3() {
            return equipImage3;
        }

        void setEquipImage3(Bitmap equipImage3) {
            this.equipImage3 = equipImage3;
        }

        public Bitmap getEquipImage4() {
            return equipImage4;
        }

        void setEquipImage4(Bitmap equipImage4) {
            this.equipImage4 = equipImage4;
        }

        public Bitmap getEquipImage5() {
            return equipImage5;
        }

        void setEquipImage5(Bitmap equipImage5) {
            this.equipImage5 = equipImage5;
        }

        public Bitmap getEquipImage6() {
            return equipImage6;
        }

        void setEquipImage6(Bitmap equipImage6) {
            this.equipImage6 = equipImage6;
        }

        public String getmAuthor() {
            return mAuthor;
        }

        public void setmAuthor(String mAuthor) {
            this.mAuthor = mAuthor;
        }
    }

    public static class EquipBean {
        private List<EquipMsg> allEquip;

        public List<EquipMsg> getAll() {
            allEquip = new ArrayList<EquipMsg>();
            return allEquip;
        }

        public void setAll(List<EquipMsg> allEquip) {

            this.allEquip = allEquip;
        }

        static class EquipMsg{
            private String imgLink1;
            private String imgLink2;
            private String imgLink3;
            private String imgLink4;
            private String imgLink5;
            private String imgLink6;
            private String equipTitle;
            private String equipAuthor;
            private String equipNum;

            public String getEquipAuthor() {
                return equipAuthor;
            }

            public void setEquipAuthor(String equipAuthor) {
                this.equipAuthor = equipAuthor;
            }

            public String getEquipNum() {
                return equipNum;
            }

            public void setEquipNum(String equipNum) {
                this.equipNum = equipNum;
            }

            public String getEquipGood() {
                return equipGood;
            }

            public void setEquipGood(String equipGood) {
                this.equipGood = equipGood;
            }

            public String getEquipBad() {
                return equipBad;
            }

            public void setEquipBad(String equipBad) {
                this.equipBad = equipBad;
            }

            private String equipGood;
            private String equipBad;

            public String getImgLink1() {
                return imgLink1;
            }

            public void setImgLink1(String imgLink1) {
                this.imgLink1 = imgLink1;
            }

            public String getImgLink2() {
                return imgLink2;
            }

            public void setImgLink2(String imgLink2) {
                this.imgLink2 = imgLink2;
            }

            public String getImgLink3() {
                return imgLink3;
            }

            public void setImgLink3(String imgLink3) {
                this.imgLink3 = imgLink3;
            }

            public String getImgLink4() {
                return imgLink4;
            }

            public void setImgLink4(String imgLink4) {
                this.imgLink4 = imgLink4;
            }

            public String getImgLink5() {
                return imgLink5;
            }

            public void setImgLink5(String imgLink5) {
                this.imgLink5 = imgLink5;
            }

            public String getImgLink6() {
                return imgLink6;
            }

            public void setImgLink6(String imgLink6) {
                this.imgLink6 = imgLink6;
            }

            public String getEquipTitle() {
                return equipTitle;
            }

            public void setEquipTitle(String equipTitle) {
                this.equipTitle = equipTitle;
            }
        }

    }
}
