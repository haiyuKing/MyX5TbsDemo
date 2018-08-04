package com.why.project.myx5tbsdemo.customwebview.x5webview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.webkit.WebStorage;

import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;


/**
 * Used 主要处理解析，渲染网页等浏览器做的事情。帮助WebView处理各种通知、请求事件：比如页面加载的开始、结束、失败时的对话框提示
 */
public class X5WebViewClient extends WebViewClient {

	private static final String TAG = X5WebViewClient.class.getSimpleName();

	/**依赖的窗口*/
	private Context context;
	private X5WebView x5WebView;

	/**进度加载对话框*/
	X5WebViewProgressDialog progressDialog;

	private boolean blockLoadingNetworkImage=false;//WebView 图片延迟加载【暂时用不到】

	private boolean needClearHistory = false;//是否需要清除历史记录

	public X5WebViewClient(Context context, X5WebView x5WebView) {
		this.context = context;
		this.x5WebView = x5WebView;
	}

	/**
	 * 重写此方法表明点击网页内的链接由自己处理，而不是新开Android的系统browser中响应该链接。
	 */
	@Override
	public boolean shouldOverrideUrlLoading(WebView webView, String url) {
		Log.e(TAG,"{shouldOverrideUrlLoading}url="+url);
		if(url.startsWith("tel:")) {//拨打电话
			Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
			context.startActivity(intent);
			return true;
		}else{
			//view.loadUrl(url);//根据传入的参数再去加载新的网页【注意，需要注释掉】
			return false;//表示当前的webview可以处理打开新网页的请求，不用借助系统浏览器,【false 显示frameset, true 不显示Frameset】
		}
	}

	//是否在webview内加载页面【高版本写法】
	/**
	 * return true 表示当前url即使是重定向url也不会再执行（除了在return true之前使用webview.loadUrl(url)除外，因为这个会重新加载）
	 return false  表示由系统执行url，直到不再执行此方法，即加载完重定向的ur（即具体的url，不再有重定向）

	 * @param view
	 * @param request
	 * @return
	 */
//	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
//	@Override
	public boolean shouldOverrideUrlLoading(android.webkit.WebView view, android.webkit.WebResourceRequest request) {
		String url = "";
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			url = request.getUrl().toString();
		} else {
			url = request.toString();
		}
		Log.e(TAG,"{shouldOverrideUrlLoading}request.toString()="+url);

		if(url.startsWith("tel:")) {//拨打电话
			Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
			context.startActivity(intent);
			return true;
		}else{
			//view.loadUrl(url);//根据传入的参数再去加载新的网页【注意，需要注释掉】
			return false;//表示当前的webview可以处理打开新网页的请求，不用借助系统浏览器
		}
	}

	/**
	 * 网页加载开始时调用，显示加载提示旋转进度条
	 */
	@Override
	public void onPageStarted(WebView webView, String url, Bitmap bitmap) {
		super.onPageStarted(webView, url, bitmap);
		Log.e(TAG,"{onPageStarted}url="+url);
		showProgressDialog();
	}

	/**
	 * 网页加载完成时调用，比如：隐藏加载提示旋转进度条*/
	@Override
	public void onPageFinished(WebView webView, String url) {
		super.onPageFinished(webView, url);
		Log.e(TAG,"{onPageFinished}url="+url);
		dismissProgressDialog();
	}

	/**
	 * 网页加载失败时调用，隐藏加载提示旋转进度条
	 * 捕获的是 文件找不到，网络连不上，服务器找不到等问题
	 */
	@Override
	public void onReceivedError(WebView webView, int errorCode,
								String description, String failingUrl) {
		super.onReceivedError(webView, errorCode, description, failingUrl);
		Log.e(TAG,"{onReceivedError}failingUrl="+failingUrl);

		dismissProgressDialog();
		//x5WebView.loadLocalUrl("404.html");//404界面，会自动使用X5内核自带的
	}

	/**
	 * 直接捕获到404
	 */
	@Override
	public void onReceivedHttpError(WebView webView, WebResourceRequest webResourceRequest, WebResourceResponse webResourceResponse) {
		super.onReceivedHttpError(webView, webResourceRequest, webResourceResponse);
		String url = "";
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			url = webResourceRequest.getUrl().toString();
		} else {
			url = webResourceRequest.toString();
		}
		Log.e(TAG,"{onReceivedHttpError}url="+url);
		//x5WebView.loadLocalUrl("404.html");//404界面，会自动使用X5内核自带的
	}

	/**
	 * 显示进度加载对话框
	 * param msg 显示内容
	 */
	public void showProgressDialog() {
		try {
			if (progressDialog == null) {
				progressDialog = new X5WebViewProgressDialog(context);
			}
			progressDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 隐藏进度加载对话框
	 */
	public void dismissProgressDialog() {
		try {
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*=================================根据需要清除历史记录=================================*/
	@Override
	public void doUpdateVisitedHistory(WebView webView, String url, boolean isReload) {
		super.doUpdateVisitedHistory(webView, url, isReload);
		Log.w(TAG, "{doUpdateVisitedHistory}needClearHistory="+needClearHistory);
		if(needClearHistory){
			webView.clearHistory();//清除历史记录
			needClearHistory = false;
		}
	}

	public void setNeedClearHistory(boolean needClearHistory) {
		this.needClearHistory = needClearHistory;
	}

	/**扩充数据库的容量*/
	public void onExceededDatabaseQuota(String url, String databaseIdentifier, long currentQuota, long estimatedSize, long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater) {
		quotaUpdater.updateQuota(estimatedSize * 2);
	}
}
