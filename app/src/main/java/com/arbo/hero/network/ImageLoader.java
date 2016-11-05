package com.arbo.hero.network;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.arbo.hero.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2016/8/10.
 */
public class ImageLoader {

    // 获取应用程序最大可用内存

   // private LruCache lruCache;
    //private MemoryCache memoryCache = new MemoryCache();
    int maxMemory = (int) Runtime.getRuntime().maxMemory();
    int mycacheSize = maxMemory / 8;
    private LruCache lruCache = new LruCache(mycacheSize);
    private FileCache fileCache ;
    private Map<ImageView,String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    private ExecutorService executorService;
    private static boolean isSrc;
    private static Context mContext;
    private static class LazyHolder{
        private static final ImageLoader INSTANCE = new ImageLoader(mContext,isSrc);
    }

    public static final ImageLoader getInstance(){
        return LazyHolder.INSTANCE;
    }
    public ImageLoader(){}

    public void init(Context context,boolean flag){
        mContext = context;
        fileCache = new FileCache(context);
        executorService = Executors.newFixedThreadPool(5);
        isSrc = flag;
    }
    /**
     * @param context
     *            上下文对象
     * @param flag
     *            true为source资源，false为background资源
     */

    public ImageLoader(Context context,boolean flag){
        fileCache = new FileCache(context);
        executorService = Executors.newFixedThreadPool(5);
        isSrc = flag;
    }

    final int stub_id = R.drawable.loading_square;
    public boolean DisplayImage(String url,ImageView imageView){
        String u1 = url.substring(0,url.lastIndexOf("/")+1);        //获取链接前面部分
        String u2 = url.substring(url.lastIndexOf("/")+1);//获取图片的名字
        try{
            u2 = URLEncoder.encode(u2,"UTF-8"); //将图片名字 进行URL编码
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        url = u1+u2;    //拼接
        imageViews.put(imageView,url);
        //Bitmap bitmap = memoryCache.get(url);
        Bitmap bitmap = (Bitmap) lruCache.get(url);
        if(bitmap != null){
            if(isSrc)   //source 文件
                imageView.setImageBitmap(bitmap);
            else
                imageView.setImageDrawable(new BitmapDrawable(bitmap));
            return true;
        }else{
            queuePhoto(url,imageView);
            if(isSrc)
                imageView.setImageResource(stub_id);
            else
                imageView.setBackgroundResource(stub_id);
            return true;
        }
    }

    private void queuePhoto(String url, ImageView imageView) {
        PhotoToLoad p = new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoader(p));
    }

    public Bitmap getBitmap(String url){
        try{
            File f = fileCache.getFile(url);

            //从sd卡中获取
            Bitmap b = decodeFile(f);
            if(b!=null){
                Log.d("ImageLoader","ImageLoader-->cache"+url);
                return b;
            }
            //从网络上获取
            Bitmap bitmap;
            Log.d("ImageLoader","ImageLoader-->download"+url+"test");
            HttpUtil.CopyStream(url,f);
            bitmap = decodeFile(f);
           // bitmap = onDecodeFile(f);
            return bitmap;
        }catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public Bitmap onDecodeFile(File f) {
        try {
            //BitmapFactory.Options opts = new BitmapFactory.Options();
            //opts.inSampleSize = 2;
            FileInputStream  is = new FileInputStream(f);
            return BitmapFactory.decodeFileDescriptor(is.getFD());
            //return BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解码图像用来减少内存消耗
     *
     * @param file
     * @return
     */
    public Bitmap decodeFile(File file){
        try{
            //解码图片大小
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(file),null,options);
            //找到正确的刻度值，应为2的幂
            final int REQUIRED_SIZE = 70;
            int width_tmp = options.outWidth,height_tmp = options.outHeight;
            int scale = 1;
            while (true){
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(file), null, o2);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 任务队列
     *
     * @author Scorpio.Liu
     *
     */
    private class PhotoToLoad {
        public String url;
        public ImageView imageView;

        public PhotoToLoad(String u, ImageView i) {
            url = u;
            imageView = i;
        }
    }

    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;

        PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            Bitmap bmp = getBitmap(photoToLoad.url);
           // memoryCache.put(photoToLoad.url, bmp);
            lruCache.put(photoToLoad.url,bmp);
            if (imageViewReused(photoToLoad))
                return;
            BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
            Activity a = (Activity) photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
        }
    }
    boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = imageViews.get(photoToLoad.imageView);
        if (tag == null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }


    /**
     * 显示位图在UI线程
     *
     * @author Scorpio.Liu
     *
     */
    class BitmapDisplayer implements Runnable{
        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap b,PhotoToLoad p){
            bitmap  = b;
            photoToLoad = p;
        }
        @Override
        public void run() {
            if(imageViewReused(photoToLoad))
                return ;
            if(bitmap != null){
                if(isSrc)
                    photoToLoad.imageView.setImageBitmap(bitmap);
                else
                    photoToLoad.imageView.setBackgroundDrawable(new BitmapDrawable(bitmap));
            }else{
                if(isSrc){
                    photoToLoad.imageView.setImageResource(stub_id);
                }else
                    photoToLoad.imageView.setBackgroundResource(stub_id);
            }
        }
    }

    public void clearCache() {
        //memoryCache.clear();
        fileCache.clear();
    }


}
