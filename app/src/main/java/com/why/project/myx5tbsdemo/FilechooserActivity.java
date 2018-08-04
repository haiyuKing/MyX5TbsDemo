package com.why.project.myx5tbsdemo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.tencent.smtt.sdk.ValueCallback;
import com.why.project.myx5tbsdemo.customwebview.utils.GetPathFromUri4kitkat;
import com.why.project.myx5tbsdemo.customwebview.utils.WebviewGlobals;
import com.why.project.myx5tbsdemo.customwebview.x5webview.X5WebView;
import com.why.project.myx5tbsdemo.customwebview.x5webview.X5WebViewJSInterface;

import java.io.File;

/**
 * Created by HaiyuKing
 * Used 用于展示在web端<input type=text>的标签被选择之后，文件选择器的制作和生成
 */

public class FilechooserActivity extends AppCompatActivity {
	private static final String TAG = FilechooserActivity.class.getSimpleName();

	//内容显示区域
	private FrameLayout center_layout;
	private X5WebView mX5WebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_x5webview);

		initViews();
		initDatas();
		initEvents();
	}

	@Override
	public void onDestroy() {
		if (mX5WebView != null) {
			mX5WebView.removeAllViews();
			mX5WebView.destroy();
		}

		super.onDestroy();
	}


	private void initViews() {
		//内容显示区域
		center_layout = (FrameLayout) findViewById(R.id.center_layout);

		mX5WebView = new X5WebView(this, null);
		center_layout.addView(mX5WebView, new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT));
	}

	private void initDatas() {
		mX5WebView.setCanBackPreviousPage(true, FilechooserActivity.this);//设置可返回上一页

		mX5WebView.loadLocalUrl("demo.html");
	}

	private void initEvents() {

	}

	/**
	 * 截取返回软键事件【在activity中写，不能在自定义的X5Webview中】
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((keyCode == KeyEvent.KEYCODE_BACK) && mX5WebView.canGoBack()) {
				mX5WebView.goBack();
				return true;
			} else {
				finish();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		Log.w(TAG, "{onActivityResult}resultCode="+resultCode);
		Log.w(TAG, "{onActivityResult}requestCode="+requestCode);

		if (resultCode == Activity.RESULT_OK) {
			//webview界面调用打开本地文件管理器选择文件的回调
			if (requestCode == WebviewGlobals.CHOOSE_FILE_REQUEST_CODE ) {
				Uri result = data == null ? null : data.getData();
				Log.w(TAG,"{onActivityResult}文件路径地址：" + result.toString());

				//如果mUploadMessage或者mUploadCallbackAboveL不为空，代表是触发input[type]类型的标签
				if (null != mX5WebView.getX5WebChromeClient().getmUploadMessage() || null != mX5WebView.getX5WebChromeClient().getmUploadCallbackAboveL()) {
					if (mX5WebView.getX5WebChromeClient().getmUploadCallbackAboveL() != null) {
						onActivityResultAboveL(requestCode, data);//5.0++
					} else if (mX5WebView.getX5WebChromeClient().getmUploadMessage() != null) {
						mX5WebView.getX5WebChromeClient().getmUploadMessage().onReceiveValue(result);//将文件路径返回去，填充到input中
						mX5WebView.getX5WebChromeClient().setmUploadMessage(null);
					}
				}else{
					//此处代码是处理通过js方法触发的情况
					Log.w(TAG,"{onActivityResult}文件路径地址(js)：" + result.toString());
					String filePath = GetPathFromUri4kitkat.getPath(FilechooserActivity.this,Uri.parse(result.toString()));

					//修改网页输入框文本【无法通过evaluateJavascript方式执行js方法，需要特殊处理】
					setUrlPathInput(mX5WebView,"打开本地相册：" + filePath);
				}
			}
			//因为拍照指定了路径，所以data值为null
			if(requestCode == WebviewGlobals.CAMERA_REQUEST_CODE){
				File pictureFile = new File(X5WebViewJSInterface.mCurrentPhotoPath);

				Uri uri = Uri.fromFile(pictureFile);
				Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
				intent.setData(uri);
				FilechooserActivity.this.sendBroadcast(intent);  // 这里我们发送广播让MediaScanner 扫描我们制定的文件
				// 这样在系统的相册中我们就可以找到我们拍摄的照片了【但是这样一来，就会执行MediaScanner服务中onLoadFinished方法，所以需要注意】

				//拍照
//				String fileName = FileUtils.getFileName(X5WebViewJSInterface.mCurrentPhotoPath);
				Log.e(TAG,"WebViewJSInterface.mCurrentPhotoPath="+X5WebViewJSInterface.mCurrentPhotoPath);

				//修改网页输入框文本【无法通过evaluateJavascript方式执行js方法，需要特殊处理】
				setUrlPathInput(mX5WebView,"打开相机：" + X5WebViewJSInterface.mCurrentPhotoPath);
			}

			//录音
			if(requestCode == WebviewGlobals.RECORD_REQUEST_CODE){
				Uri result = data == null ? null : data.getData();
				Log.w(TAG,"录音文件路径地址：" + result.toString());//录音文件路径地址：content://media/external/audio/media/111

				String filePath = GetPathFromUri4kitkat.getPath(FilechooserActivity.this,Uri.parse(result.toString()));
				Log.w(TAG,"录音文件路径地址：" + filePath);

				//修改网页输入框文本【无法通过evaluateJavascript方式执行js方法，需要特殊处理】
				setUrlPathInput(mX5WebView,"打开录音：" + filePath);
			}
		} else if(resultCode == RESULT_CANCELED){//resultCode == RESULT_CANCELED 解决不选择文件，直接返回后无法再次点击的问题
			if (mX5WebView.getX5WebChromeClient().getmUploadMessage() != null) {
				mX5WebView.getX5WebChromeClient().getmUploadMessage().onReceiveValue(null);
				mX5WebView.getX5WebChromeClient().setmUploadMessage(null);
			}
			if (mX5WebView.getX5WebChromeClient().getmUploadCallbackAboveL() != null) {
				mX5WebView.getX5WebChromeClient().getmUploadCallbackAboveL().onReceiveValue(null);
				mX5WebView.getX5WebChromeClient().setmUploadCallbackAboveL(null);
			}
		}
	}

	//5.0以上版本，由于api不一样，要单独处理
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private void onActivityResultAboveL(int requestCode, Intent data) {

		if (mX5WebView.getX5WebChromeClient().getmUploadCallbackAboveL() == null) {
			return;
		}
		Uri result = null;
		if (requestCode == WebviewGlobals.CHOOSE_FILE_REQUEST_CODE) {//打开本地文件管理器选择图片
			result = data.getData();
		} else if (requestCode == WebviewGlobals.CAMERA_REQUEST_CODE) {//调用相机拍照
			File pictureFile = new File(X5WebViewJSInterface.mCurrentPhotoPath);

			Uri uri = Uri.fromFile(pictureFile);
			Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			intent.setData(uri);
			FilechooserActivity.this.sendBroadcast(intent);  // 这里我们发送广播让MediaScanner 扫描我们制定的文件
			// 这样在系统的相册中我们就可以找到我们拍摄的照片了【但是这样一来，就会执行MediaScanner服务中onLoadFinished方法，所以需要注意】

			result = Uri.fromFile(pictureFile);
		}
		Log.w(TAG,"{onActivityResultAboveL}文件路径地址："+result.toString());
		mX5WebView.getX5WebChromeClient().getmUploadCallbackAboveL().onReceiveValue(new Uri[]{result});//将文件路径返回去，填充到input中
		mX5WebView.getX5WebChromeClient().setmUploadCallbackAboveL(null);
		return;
	}

	//设置网页上的文件路径输入框文本
	private void setUrlPathInput(X5WebView webView, String urlPath) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			webView.evaluateJavascript("setInputText('"+ urlPath +"')", new ValueCallback<String>() {
				@Override
				public void onReceiveValue(String value) {
					Log.i(TAG, "onReceiveValue value=" + value);
				}});
		}else{
			Toast.makeText(FilechooserActivity.this,"当前版本号小于19，无法支持evaluateJavascript，需要使用第三方库JSBridge", Toast.LENGTH_SHORT).show();
		}
	}
}
