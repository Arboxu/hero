package com.arbo.hero.network;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by Administrator on 2016/8/10.
 */
public class FileCache {
    private File cacheDir;
    public FileCache(Context context){
        //找一个用来缓存图片的路径
       // if(isExternalStorageWritable())
        if(Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)){
            cacheDir = new File(Environment.getExternalStorageDirectory(),"YXXT");
        }
        else
            cacheDir = context.getCacheDir();
        if(!cacheDir.exists())
            cacheDir.mkdirs();
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static File getFilePath(String filePath,
                                   String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath +"/"+ fileName);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return file;
    }

    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Exception e) {

        }
    }


    public File getFile(String url)
    {
        String filename = String.valueOf(url.hashCode());
        File f = getFilePath(cacheDir.getPath(),filename);  //先创建路径，然后创建文件名
       // File f = new File(cacheDir,filename);
        Log.d("getFile:","文件路径为："+f.getPath());
        return f;
    }

    public void clear(){
        File[] files = cacheDir.listFiles();
        if(files == null)
            return ;
        for(File f:files)
                f.delete();
    }
}
