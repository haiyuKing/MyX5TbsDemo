package com.why.project.myx5tbsdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.why.project.myx5tbsdemo.customwebview.x5webview.X5WebView;

/**
 * Created by HaiyuKing
 * Used 用于演示X5webview实现视频的全屏播放功能 其中注意 X5的默认全屏方式 与 android 系统的全屏方式
 */

public class FullScreenActivity extends AppCompatActivity {
	private static final String TAG = FullScreenActivity.class.getSimpleName();

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
		mX5WebView.setCanBackPreviousPage(true, FullScreenActivity.this);//设置可返回上一页

		enablePageVideoFunc();//设置视频播放样式

		mX5WebView.loadUrl("http://live.hkstv.hk.lxdns.com/live/hks/playlist.m3u8");
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

	//恢复webkit初始状态
	private void disableX5FullscreenFunc() {
		if (mX5WebView.getX5WebViewExtension() != null) {
			Toast.makeText(this, "恢复webkit初始状态", Toast.LENGTH_LONG).show();
			Bundle data = new Bundle();
			data.putBoolean("standardFullScreen", true);// true表示标准全屏，会调起onShowCustomView()，false表示X5全屏；不设置默认false，
			data.putBoolean("supportLiteWnd", false);// false：关闭小窗；true：开启小窗；不设置默认true，
			data.putInt("DefaultVideoScreen", 2);// 1：以页面内开始播放，2：以全屏开始播放；不设置默认：1

			mX5WebView.getX5WebViewExtension().invokeMiscMethod("setVideoParams", data);
			//设置WebView是否通过手势触发播放媒体，默认是true，需要手势触发。
			mX5WebView.getSettings().setMediaPlaybackRequiresUserGesture(false);
			//接口禁止(直接或反射)调用，避免视频画面无法显示
			mX5WebView.setDrawingCacheEnabled(true);
		}
	}

	//开启X5全屏播放模式
	private void enableX5FullscreenFunc() {

		if (mX5WebView.getX5WebViewExtension() != null) {
			Toast.makeText(this, "开启X5全屏播放模式", Toast.LENGTH_LONG).show();
			Bundle data = new Bundle();

			data.putBoolean("standardFullScreen", false);// true表示标准全屏，false表示X5全屏；不设置默认false，

			data.putBoolean("supportLiteWnd", false);// false：关闭小窗；true：开启小窗；不设置默认true，

			data.putInt("DefaultVideoScreen", 2);// 1：以页面内开始播放，2：以全屏开始播放；不设置默认：1

			mX5WebView.getX5WebViewExtension().invokeMiscMethod("setVideoParams", data);
			//设置WebView是否通过手势触发播放媒体，默认是true，需要手势触发。
			mX5WebView.getSettings().setMediaPlaybackRequiresUserGesture(false);
			//接口禁止(直接或反射)调用，避免视频画面无法显示
			mX5WebView.setDrawingCacheEnabled(true);
		}
	}

	//开启小窗模式
	private void enableLiteWndFunc() {
		if (mX5WebView.getX5WebViewExtension() != null) {
			Toast.makeText(this, "开启小窗模式", Toast.LENGTH_LONG).show();
			Bundle data = new Bundle();

			data.putBoolean("standardFullScreen", false);// true表示标准全屏，会调起onShowCustomView()，false表示X5全屏；不设置默认false，

			data.putBoolean("supportLiteWnd", true);// false：关闭小窗；true：开启小窗；不设置默认true，

			data.putInt("DefaultVideoScreen", 2);// 1：以页面内开始播放，2：以全屏开始播放；不设置默认：1

			mX5WebView.getX5WebViewExtension().invokeMiscMethod("setVideoParams", data);
			//设置WebView是否通过手势触发播放媒体，默认是true，需要手势触发。
			mX5WebView.getSettings().setMediaPlaybackRequiresUserGesture(false);
			//接口禁止(直接或反射)调用，避免视频画面无法显示
			mX5WebView.setDrawingCacheEnabled(true);
		}
	}

	//页面内全屏播放模式
	private void enablePageVideoFunc() {
		if (mX5WebView.getX5WebViewExtension() != null) {
			Toast.makeText(this, "页面内全屏播放模式", Toast.LENGTH_LONG).show();
			Bundle data = new Bundle();

			data.putBoolean("standardFullScreen", false);// true表示标准全屏，会调起onShowCustomView()，false表示X5全屏；不设置默认false，

			data.putBoolean("supportLiteWnd", false);// false：关闭小窗；true：开启小窗；不设置默认true，

			data.putInt("DefaultVideoScreen", 1);// 1：以页面内开始播放，2：以全屏开始播放；不设置默认：1

			mX5WebView.getX5WebViewExtension().invokeMiscMethod("setVideoParams", data);
			//设置WebView是否通过手势触发播放媒体，默认是true，需要手势触发。
			mX5WebView.getSettings().setMediaPlaybackRequiresUserGesture(false);
			//接口禁止(直接或反射)调用，避免视频画面无法显示
			mX5WebView.setDrawingCacheEnabled(true);
		}
	}

}
