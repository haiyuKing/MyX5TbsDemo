package com.why.project.myx5tbsdemo.customwebview.x5webview;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;


/**
 * Used 处理解析，渲染网页等浏览器做的事情。辅助WebView处理Javascript的对话框，网站图标，网站title，加载进度等
 */
public class X5WebChromeClient extends WebChromeClient {

	private static final String TAG = X5WebChromeClient.class.getSimpleName();

	/**依赖的窗口*/
	private Context context;
	private X5WebView x5WebView;

	public X5WebChromeClient(Context context, X5WebView x5WebView) {
		this.context = context;
		this.x5WebView = x5WebView;
	}

	//更改加载进度值
	@Override
	public void onProgressChanged(WebView view, int newProgress) {
		// TODO Auto-generated method stub
		super.onProgressChanged(view, newProgress);
	}

	/*=========================================实现webview打开文件管理器功能==============================================*/
	/**
	 * HTML界面：
	 * <input accept="image/*" capture="camera" id="imgFile" name="imgFile" type="file">
	 * <input type="file" capture="camera" accept="* /*" name="image">
	 *  */

	/**
	 * 重写WebChromeClient 的openFileChooser方法
	 * 这里有个漏洞，4.4.x的由于系统内核发生了改变，没法调用以上方法，现在仍然找不到解决办法，唯一的方法就是4.4直接使用手机浏览器打开，这个是可以的。
	 * */

	private android.webkit.ValueCallback<Uri> mUploadMessage;//5.0--版本用到的
	private android.webkit.ValueCallback<Uri[]> mUploadCallbackAboveL;//5.0++版本用到的

	//3.0--
	public void openFileChooser(android.webkit.ValueCallback<Uri> uploadMsg) {
		openFileChooserImpl(uploadMsg);
	}
	//3.0++
	public void openFileChooser(android.webkit.ValueCallback<Uri> uploadMsg, String acceptType) {
		openFileChooserImpl(uploadMsg);
	}
	//4.4--(4.4.2特殊，不执行该方法)
	public void openFileChooser(android.webkit.ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
		openFileChooserImpl(uploadMsg);
	}

	//5.0++
	@Override
	public boolean onShowFileChooser(WebView webView,
									 ValueCallback<Uri[]> filePathCallback,
									 FileChooserParams fileChooserParams) {
		openFileChooserImplForAndroid5(filePathCallback);
		return true;
	}
	//5.0--的调用
	private void openFileChooserImpl(android.webkit.ValueCallback<Uri> uploadMsg) {
		mUploadMessage = uploadMsg;
		dispatchTakePictureIntent();
	}
	//5.0++的调用
	private void openFileChooserImplForAndroid5(android.webkit.ValueCallback<Uri[]> uploadMsg) {
		mUploadCallbackAboveL = uploadMsg;
		dispatchTakePictureIntent();
	}

	//拍照或者打开文件管理器
	private void dispatchTakePictureIntent() {
		if(mUploadMessage!=null){
			Log.w(TAG,"mUploadMessage.toString()="+mUploadMessage.toString());
		}
		if(mUploadCallbackAboveL!=null) {
			Log.w(TAG, "mUploadCallbackAboveL.toString()=" + mUploadCallbackAboveL.toString());
		}
		X5WebViewJSInterface.getInstance(context,x5WebView).chooseFile();
	}

	public android.webkit.ValueCallback<Uri> getmUploadMessage() {
		return mUploadMessage;
	}

	public android.webkit.ValueCallback<Uri[]> getmUploadCallbackAboveL() {
		return mUploadCallbackAboveL;
	}

	public void setmUploadMessage(android.webkit.ValueCallback<Uri> mUploadMessage) {
		this.mUploadMessage = mUploadMessage;
	}

	public void setmUploadCallbackAboveL(android.webkit.ValueCallback<Uri[]> mUploadCallbackAboveL) {
		this.mUploadCallbackAboveL = mUploadCallbackAboveL;
	}


}
