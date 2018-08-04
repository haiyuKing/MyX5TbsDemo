package com.why.project.myx5tbsdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import com.why.project.myx5tbsdemo.customwebview.x5webview.X5WebView;

/**
 * Created by HaiyuKing
 * Used 用于展现打开普通网页；
 */

public class BrowserActivity extends AppCompatActivity {
	private static final String TAG = BrowserActivity.class.getSimpleName();

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
		mX5WebView.setCanBackPreviousPage(true, BrowserActivity.this);//设置可返回上一页

		mX5WebView.loadUrl("http://www.baidu.com");
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
}
