package com.arbo.hero.network;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.arbo.hero.javabean.ChampionListBean;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtil {


    public static String getResponseStr(String path, Map<String,String> parameters)
    {
        StringBuffer stringBuffer = new StringBuffer();
        URL url;
        try {
            if(parameters != null && !parameters.isEmpty()){
                for (Map.Entry<String,String> entry:parameters.entrySet()){
                    //完成转码操作
                    stringBuffer.append(entry.getKey()).append("=")
                            .append(URLEncoder.encode(entry.getValue(),"UTF-8")).append("&");
                }
                stringBuffer.deleteCharAt(stringBuffer.length()-1);
            }
            url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(3000);
            urlConnection.setReadTimeout(3000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true); // 表示从服务器获取数据
            urlConnection.setDoOutput(true);// 表示向服务器写入数据

            // 获得上传信息的字节大小以及长度
            byte[] mydata = stringBuffer.toString().getBytes();
            // 表示设置请求体的类型是文本类型
            urlConnection.setRequestProperty("Connect-Type","application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("Content-Length",String.valueOf(mydata.length));

            //获得输出流，向服务器输出数据
            OutputStream outputStream = urlConnection.getOutputStream();
            outputStream.write(mydata,0,mydata.length);
            outputStream.close();
            int responseCode = urlConnection.getResponseCode();
            if(responseCode == 200){
                return changeInputStream(urlConnection.getInputStream());
            }
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (ProtocolException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    private static String changeInputStream(InputStream inputStream)
    {
        //把输入流转换成输出流
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        String result = "";
        if(inputStream != null){
            try {
                while((len = inputStream.read(data)) != -1){
                    outputStream.write(data,0,len);
                }
                result = new String(outputStream.toByteArray(),"UTF-8");
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static InputStream getInputStream(String path)
    {
        URL url;
        final OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
        try {
            url = new URL(path);
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                Log.d("ImageLoader2","ImageLoader-->download_Suecced");
                return response.body().byteStream();
            }
        }catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static void getResponse(final String path, final Handler handler, final int type)
    {
        final OkHttpClient client = new OkHttpClient();
        final Thread thread = new Thread(){
            @Override
            public void run(){
                try {
                    URL url = new URL(path);
                    Request request = new Request.Builder().url(url).build();
                    Response response = client.newCall(request).execute();
                    if(response.isSuccessful()){
                        String tempResponse = response.body().string();
                        Log.i("getResponse",tempResponse);
                        Message message = handler.obtainMessage();
                        if(type == 1){
                            ChampionListBean championListBean = JSON.parseObject(tempResponse,ChampionListBean.class);
                            List<ChampionListBean.AllBean> allBeen;
                            allBeen = championListBean.getAll();
                            message.obj = allBeen;
                            message.what = 3;
                        }else if(type == 2){
                            message.obj = tempResponse;
                            message.what = Constant.MSG_WHAT_CHAMP_Str;
                        }
                        handler.sendMessage(message);
                    }else{
                        throw new IOException("Unexpected code " + response);
                    }
                }catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        };
        thread.start();
    }

    public static String inputStream2String (InputStream in) throws IOException   {

        StringBuffer out = new StringBuffer();
        InputStreamReader isr = new InputStreamReader(in,"UTF-8");  //
        char[]  b = new char[8192];//避免采用byte[]导致截取了部分文字，从而出现乱码
        int n;
        while ((n = isr.read(b)) != -1){
            out.append(new String(b,0,n));
        }
        Log.i("String的长度",new Integer(out.length()).toString());
        return  out.toString();
    }

    public static byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = inStream.read(buffer)) != -1) {
            outSteam.write(buffer, 0, len);

        }
        outSteam.close();
        inStream.close();
        return outSteam.toByteArray();
    }

    public static void CopyStream(String url, File f) {
        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        try {
            inputStream = getInputStream(url);
            byte[] data = new byte[1024];
            int len = 0;
            fileOutputStream = new FileOutputStream(f);
            while ((len = inputStream.read(data)) != -1) {
                fileOutputStream.write(data, 0, len);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

