package com.why.project.myx5tbsdemo.customwebview.x5webview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.webkit.JavascriptInterface;

import com.why.project.myx5tbsdemo.customwebview.utils.WebviewGlobals;

import java.io.File;

/**
 * Created by HaiyuKing
 * Used Java和Java script交互的工具类
 */

public class X5WebViewJSInterface {

    private static X5WebViewJSInterface instance;
    /**
     * 依赖的窗口
     */
    private Context context;
    private X5WebView x5WebView;

    public static String mCurrentPhotoPath = null;//拍照存储的路径,例如：/storage/emulated/0/Pictures/20170608104809.jpg

    public static X5WebViewJSInterface getInstance(Context context, X5WebView x5WebView) {
        if (instance == null) {
            instance = new X5WebViewJSInterface();
        }
        instance.context = context;
        instance.x5WebView = x5WebView;
        return instance;
    }

    /**
     * 打开相机拍照的Intent【js调用的方法必须添加@JavascriptInterface】
     */
    @JavascriptInterface
    public void takePicture() {
        //调用系统拍照
        //调用系统拍照，保存到系统图库
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            //解决buildsdk>=24,调用Uri.fromFile时报错的问题
            // https://blog.csdn.net/qq_34709056/article/details/77968456
            //https://blog.csdn.net/qq_34709056/article/details/78528507
            mCurrentPhotoPath = Environment.getExternalStorageDirectory() + File.separator + "Pictures" + File.separator + "JPEG_" + System.currentTimeMillis() +".jpg";
            File file = new File(mCurrentPhotoPath);
            Uri photoFile = null;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                String authority = context.getApplicationInfo().packageName + ".provider";
                photoFile = FileProvider.getUriForFile(context.getApplicationContext(), authority, file);
            } else {
                photoFile = Uri.fromFile(file);
            }
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFile);
            //启动拍照的窗体。并注册 回调处理
            ((Activity) context).startActivityForResult(takePictureIntent, WebviewGlobals.CAMERA_REQUEST_CODE);
        }

    }

    /**
     * 打开本地相册选择图片的Intent
     */
    @JavascriptInterface
    public void choosePic() {
        Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        String IMAGE_UNSPECIFIED = "image/*";
        innerIntent.setType(IMAGE_UNSPECIFIED); // 查看类型
        Intent wrapperIntent = Intent.createChooser(innerIntent, "Image Browser");
        ((Activity) context).startActivityForResult(wrapperIntent, WebviewGlobals.CHOOSE_FILE_REQUEST_CODE);
    }

    /**
     * 打开文件管理器选择文件的Intent
     */
    @JavascriptInterface
    public void chooseFile() {
        Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        String IMAGE_UNSPECIFIED = "*/*";
        innerIntent.setType(IMAGE_UNSPECIFIED); // 查看类型
        Intent wrapperIntent = Intent.createChooser(innerIntent, "File Browser");
        ((Activity) context).startActivityForResult(wrapperIntent, WebviewGlobals.CHOOSE_FILE_REQUEST_CODE);
    }

    /**
     * 打开录音机录音的Intent【js调用的方法必须添加@JavascriptInterface】
     */
    @JavascriptInterface
    public void openRecord() {
        //这个方案不好用
        /*Intent openRecordIntent = new Intent(Intent.ACTION_GET_CONTENT);
        if (openRecordIntent.resolveActivity(context.getPackageManager()) != null) {
            openRecordIntent.setType("audio/amr");//String AUDIO_AMR = "audio/amr";
            openRecordIntent.setClassName("com.android.soundrecorder",
                    "com.android.soundrecorder.SoundRecorder");
            ((Activity) context).startActivityForResult(openRecordIntent, WebviewGlobals.RECORD_REQUEST_CODE);
        }*/
        //这个方案可以用
        Intent openRecordIntent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        ((Activity) context).startActivityForResult(openRecordIntent, WebviewGlobals.RECORD_REQUEST_CODE);
    }

}
