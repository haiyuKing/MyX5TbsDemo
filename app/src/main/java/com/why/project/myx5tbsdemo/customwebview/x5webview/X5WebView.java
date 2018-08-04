package com.why.project.myx5tbsdemo.customwebview.x5webview;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;

import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebSettings.LayoutAlgorithm;
import com.tencent.smtt.sdk.WebView;

public class X5WebView extends WebView {

	private static final String TAG = X5WebView.class.getSimpleName();
	/**网页编码*/
	private static final String ENCODENAME = "utf-8";
	/**上下文*/
	private Context mContext;

	private  X5WebChromeClient x5WebChromeClient;
	private  X5WebViewClient x5WebViewClient;

	/**是否可以返回到上一页(true:可以，默认；false：不可以)*/
	private boolean canBackPreviousPage = false;
	/**当前Webview所处的上下文（默认大家使用的是DialogFragment）*/
	private DialogFragment mDialog;
	private Activity mActivity;
	private Fragment mFragment;

	/*
     * 在Code中new实例化一个ew会调用第一个构造函数
     * 如果在xml中定义会调用第二个构造函数
     * 而第三个函数系统是不调用的，要由View（我们自定义的或系统预定义的View）显式调用，一般用于自定义属性的相关操作
     * */
	/**在Java代码中new实例化的时候调用*/
	public X5WebView(Context context) {
		super(context);
		mContext = context;
		initWebViewSettings();
	}
	/**在xml布局文件中定义的时候调用*/
	public X5WebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initWebViewSettings();
	}

	private void initWebViewSettings(){
		// settings 的设计
		WebSettings webSetting = this.getSettings();

		/*=============================JS的相关设置===========================================*/
		webSetting.setJavaScriptEnabled(true);//设置WebView是否允许执行JavaScript脚本，默认false，不允许。
		webSetting.setJavaScriptCanOpenWindowsAutomatically(true);//让JavaScript自动打开窗口，默认false。适用于JavaScript方法window.open()。

		/*=============================缓存机制的相关设置===========================================*/
		webSetting.setAllowFileAccess(true);//是否允许访问文件，默认允许。注意，这里只是允许或禁止对文件系统的访问，Assets 和 resources 文件使用file:///android_asset和file:///android_res仍是可访问的。
		webSetting.setAppCacheEnabled(true);//应用缓存API是否可用，默认值false, 结合setAppCachePath(String)使用。
		webSetting.setAppCacheMaxSize(Long.MAX_VALUE);//设置app缓存容量
		webSetting.setAppCachePath(mContext.getApplicationContext().getDir("appcache", 0).getPath());//设置缓存路径
		Log.e(TAG,"{webview设置缓存路径==}"+mContext.getApplicationContext().getDir("appcache", 0).getPath());
//		webSetting.setDatabaseEnabled(true);//数据库存储API是否可用，默认值false。
//		webSetting.setDatabasePath(mContext.getApplicationContext().getDir("databases", 0).getPath());
		webSetting.setDomStorageEnabled(true);// 使用localStorage则必须打开, 支持文件存储
		webSetting.setGeolocationEnabled(true);
		webSetting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据
		webSetting.setGeolocationDatabasePath(mContext.getApplicationContext().getDir("geolocation", 0)
				.getPath());

		/*==============================webview页面自适应屏幕的相关设置===========================================*/
		webSetting.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);// 排版适应屏幕 设置布局，会引起WebView的重新布局（relayout）,默认值NARROW_COLUMNS
		webSetting.setSupportZoom(true);//是否应该支持使用其屏幕缩放控件和手势缩放,默认值true
		webSetting.setBuiltInZoomControls(true);//设置触摸可缩放  ，默认值为false。
		webSetting.setUseWideViewPort(true);// 设置此属性，可任意比例缩放。
		// webSetting.setLoadWithOverviewMode(true);//是否允许WebView度超出以概览的方式载入页面，默认false。即缩小内容以适应屏幕宽度

		webSetting.setSupportMultipleWindows(false);//设置WebView是否支持多窗口。如果设置为true，主程序要实现onCreateWindow(WebView, boolean, boolean, Message)，默认false。

		webSetting.setDefaultTextEncodingName(ENCODENAME);//设置网页默认编码

		// webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
		webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
//		webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
		webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
		// 设置缓存模式【我们可以在有网的情况下将缓存模式改为websetting.setCacheMode(WebSettings.LOAD_DEFAULT);当没有网络时则设置为 LOAD_CACHE_ELSE_NETWORK】
//		if(HttpUtil.isNetworkAvailable(mContext)){
//			webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
//		}else{
//			webSetting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//第一次加载之后便在本地缓存，如果没网络就加载缓存，
//		}

		// this.getSettingsExtension().setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);//extension

		//在接入SDK后，需要放到创建X5的WebView之后（也就是X5内核加载完成）进行；否则，cookie的相关操作只能影响系统内核。
//		CookieSyncManager.createInstance(mContext);
//		CookieSyncManager.getInstance().sync();

		//使用WebChormClient的特性处理html页面
		x5WebChromeClient = new X5WebChromeClient(getContext(),this);
		this.setWebChromeClient(x5WebChromeClient);
		//使用WebViewClient的特性处理html页面
		x5WebViewClient = new X5WebViewClient(getContext(),this);
		this.setWebViewClient(x5WebViewClient);

		//实现html文件中可以调用java方法
		addJavascriptInterface(X5WebViewJSInterface.getInstance(mContext,this), "androidMethod");
	}



	/**加载远程网页
	 * @param webUrl - 远程网页链接地址：比如http://m.baidu.com/*/
	public void loadWebUrl(String webUrl){
		this.loadUrl(webUrl);
	}

	/**加载本地assets目录下的网页
	 * @param localUrl - assets目录下的路径：比如www/login.html*/
	public void loadLocalUrl(final String localUrl){
		this.loadUrl("file:///android_asset/"+localUrl);
	}

	/**设置是否直接退出还是返回上一页面【根据实际情况，可以再返回当前webview的载体（activity或者DialogFragment）进行处理】*/
	public void setCanBackPreviousPage(boolean canBackPreviousPage,DialogFragment dialog) {
		this.canBackPreviousPage = canBackPreviousPage;
		this.mDialog = dialog;
	}

	public void setCanBackPreviousPage(boolean canBackPreviousPage,Activity activtiy) {
		this.canBackPreviousPage = canBackPreviousPage;
		this.mActivity = activtiy;
	}

	public void setCanBackPreviousPage(boolean canBackPreviousPage,Fragment fragment) {
		this.canBackPreviousPage = canBackPreviousPage;
		this.mFragment = fragment;
	}

	/**按返回键时， 是不退出当前界面而是返回上一浏览页面还是直接退出当前界面*/
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.e(TAG,"{onKeyDown}canBackPreviousPage="+canBackPreviousPage);
		if(canBackPreviousPage){
			if ((keyCode == KeyEvent.KEYCODE_BACK) && canGoBack()) {
				goBack();
				return true;
			}else if((keyCode == KeyEvent.KEYCODE_BACK) && !canGoBack()){
				//当没有上一页可返回的时候
				//此处执行DialogFragment关闭或者Activity关闭.....
				if(mDialog != null){
					mDialog.dismiss();
				}
				if(mActivity != null || mFragment != null){
					return  false;//将操作返回到activity处理
				}
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	public X5WebChromeClient getX5WebChromeClient() {
		return x5WebChromeClient;
	}

	public X5WebViewClient getX5WebViewClient() {
		return x5WebViewClient;
	}

}
