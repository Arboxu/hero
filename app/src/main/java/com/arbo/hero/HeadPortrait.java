package com.arbo.hero;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;

public class HeadPortrait extends AppCompatActivity {
    Uri imagePhotoUri = null;
    public static Uri imageCropUri = null;
    File tempFile;
    /* 头像文件 */
    private static final String IMAGE_FILE_NAME = "temp_head_image.jpg";
    private static final String DEST_FILE_NAME = "dest_head_image.jpg";
    float alpha = 1f;

//    /* 请求识别码 */
    private static final int PHOTO_REQUEST_CAREMA = 0x01;
    private static final int PHOTO_REQUEST_CUT = 0x02;
    private static final int PHOTO_REQUEST_GALLERY = 0x04;
    private static final int CODE_RESULT_REQUEST = 0xa2;

    private ImageView ibUserIcon = null;

    PopupWindow popupWindow;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    backgroundAlpha((float)msg.obj);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setheadportrait);
        setActionBar();
        ibUserIcon = (ImageView) findViewById(R.id.imageView);
        if(imageCropUri == null){
            File file  = new File(Environment.getExternalStorageDirectory(),
                    DEST_FILE_NAME);
            // 从文件中创建uri
            imageCropUri = Uri.fromFile(file);
        }else{
            ibUserIcon.setImageBitmap(decodeUriAsBitmap(this,imageCropUri));
        }
    }

    private void setActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.myactionbar);
        View view = actionBar.getCustomView();
        TextView tv = (TextView) view.findViewById(R.id.action_bar_title);
        tv.setText("设置头像");
        ImageButton back = (ImageButton) view.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backPressed();
            }
        });
        final ImageButton btnmenu= (ImageButton) view.findViewById(R.id.more);
        btnmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomwindow(btnmenu);
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        while(alpha>0.5f){
                            try {
                                Thread.sleep(4);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Log.d("HeadPortrait","alpha:"+alpha);
                            Message msg =mHandler.obtainMessage();
                            msg.what = 1;
                            alpha-=0.01f;
                            msg.obj =alpha ;
                            mHandler.sendMessage(msg);
                        }
                    }

                }).start();
            }
        });
    }

    void bottomwindow(View view) {
        if (popupWindow != null && popupWindow.isShowing()) {
            return;
        }
        LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.window_popup, null);
        popupWindow = new PopupWindow(layout,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        //点击空白处时，隐藏掉pop窗口
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setAnimationStyle(R.style.Popupwindow);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        popupWindow.showAtLocation(view, Gravity.LEFT | Gravity.BOTTOM, 0, -location[1]);
        setButtonListeners(layout);
        //添加pop窗口关闭事件
        popupWindow.setOnDismissListener(new poponDismissListener());
        backgroundAlpha(1f);
    }

    private void setButtonListeners(LinearLayout layout){
        Button camera = (Button) layout.findViewById(R.id.camera);
        Button gallery = (Button) layout.findViewById(R.id.gallery);
        Button cancel = (Button) layout.findViewById(R.id.cancel);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    choseHeadImageFromCameraCapture();
                    popupWindow.dismiss();
                }
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    choseHeadImageFromGallery();
                    popupWindow.dismiss();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });
    }


    /**
     * 提取保存裁剪之后的图片数据，并设置头像部分的View
     */
    private void setImageToHeadView(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            ibUserIcon.setImageBitmap(photo);
        }
    }

    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            // 有存储的SDCard
            return true;
        } else {
            return false;
        }
    }
    private void choseHeadImageFromCameraCapture(){
        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //将拍照结果保存至photo_file的Uri中，不保留在相册中
        if (hasSdcard()) {
            tempFile = new File(Environment.getExternalStorageDirectory(),
                    IMAGE_FILE_NAME);
            // 从文件中创建uri
            imagePhotoUri = Uri.fromFile(tempFile);
            intentCamera.putExtra(MediaStore.EXTRA_OUTPUT,imagePhotoUri);
        }

        startActivityForResult(intentCamera,PHOTO_REQUEST_CAREMA);
    }

        @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {

        // 用户没有进行有效的设置操作，返回
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getApplication(), "取消", Toast.LENGTH_LONG).show();
            return;
        }
        switch (requestCode) {
            case PHOTO_REQUEST_CAREMA:
                if (resultCode == RESULT_OK) {
                    //从相机拍摄保存的Uri中取出图片，调用系统剪裁工具
                    if (imagePhotoUri != null) {
                        cropImageUri(this, imagePhotoUri, imageCropUri, ibUserIcon.getWidth(), ibUserIcon.getHeight(), PHOTO_REQUEST_CUT);
                    } else {
                        Toast.makeText(getApplication(), "没有得到拍照图片", Toast.LENGTH_LONG).show();
                    }
                }
                else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(getApplication(), "取消拍照", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplication(), "拍照失败", Toast.LENGTH_LONG).show();
                }
                break;
            case PHOTO_REQUEST_CUT:
                if (resultCode == RESULT_OK) {
                    Bitmap bitmap = decodeUriAsBitmap(this,imageCropUri);
                    ibUserIcon.setImageBitmap(bitmap);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(getApplication(), "取消剪切图片", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getApplication(), "剪切失败", Toast.LENGTH_LONG).show();

                }
                break;
            case PHOTO_REQUEST_GALLERY:
                if (resultCode == RESULT_OK) {
                //从相册选取成功后，需要从Uri中拿出图片的绝对路径，再调用剪切   /*change data换成了intent*/
                Uri newUri = Uri.parse("file:///" + getPath(this, intent.getData()));
                if (newUri != null) {
                    cropImageUri(this, newUri, imageCropUri, ibUserIcon.getWidth(),
                            ibUserIcon.getHeight(), PHOTO_REQUEST_CUT);
                    } else {
                    Toast.makeText(getApplication(), "没有得到相册图片", Toast.LENGTH_LONG).show();
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(getApplication(), "从相册选取取消", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplication(), "从相册选取失败", Toast.LENGTH_LONG).show();
                }
                break;
            case CODE_RESULT_REQUEST:
                if (intent != null) {
                    setImageToHeadView(intent);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    private void backPressed(){
        Intent backIntent = new Intent(HeadPortrait.this,SearchActivity.class);
        backIntent.putExtra("CODE_RESULT_VAL",1);
        HeadPortrait.this.setResult(0x05, backIntent);
        HeadPortrait.this.finish();
    }

    @Override
    public void onBackPressed() {
        backPressed();
    }

    //调用系统的剪裁处理图片并保存至imageUri中
    public static void cropImageUri(Activity activity, Uri orgUri, Uri desUri, int width, int height, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(orgUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra("scale", true);

        //将剪切的图片保存到目标Uri中
        intent.putExtra(MediaStore.EXTRA_OUTPUT, desUri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        activity.startActivityForResult(intent, requestCode);
    }

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
     //从本地相册选取图片作为头像
    private void choseHeadImageFromGallery() {
        Intent intentFromGallery = new Intent();
        // 设置文件类型
        intentFromGallery.setType("image/*");
        intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intentFromGallery, PHOTO_REQUEST_GALLERY);
    }

    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"+ split[1];
                    }

                }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
                }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection,selectionArgs);
                }
            }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
            }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
            }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri,String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection,selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
                }
            } finally {
            if (cursor != null)
            cursor.close();
            }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }



    /**
     * 返回或者点击空白位置的时候将背景透明度改回来
     */
    class poponDismissListener implements PopupWindow.OnDismissListener{
        @Override
        public void onDismiss() {
            // TODO Auto-generated method stub
            new Thread(new Runnable(){
                @Override
                public void run() {
                    //此处while的条件alpha不能<= 否则会出现黑屏
                    while(alpha<1f){
                        try {
                            Thread.sleep(4);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.d("HeadPortrait","alpha:"+alpha);
                        Message msg =mHandler.obtainMessage();
                        msg.what = 1;
                        alpha+=0.01f;
                        msg.obj =alpha ;
                        mHandler.sendMessage(msg);
                    }
                }

            }).start();
        }

    }
    /**
     * 设置添加屏幕的背景透明度
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = getWindow().getAttributes();

        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }


}
