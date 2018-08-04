package com.why.project.myx5tbsdemo.customwebview.x5webview;


import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import com.why.project.myx5tbsdemo.R;


/**
 * @Created HaiyuKing
 * @Used  网页加载时的进度对话框
 */
public class X5WebViewProgressDialog extends AlertDialog {

	/**对话框依赖的窗口*/
	private Context context;

	/**
	 * 使用固定样式*/
	public X5WebViewProgressDialog(Context context) {
		this(context, R.style.x5webview_loading_style);
	}
	/**
	 * 使用指定样式*/
	public X5WebViewProgressDialog(Context context, int theme) {
		super(context, theme);

		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.x5webview_dialog_webviewprogress);//引用布局文件

		//设置为false，按对话框以外的地方不起作用
		setCanceledOnTouchOutside(true);
		//设置为false，按返回键不能退出
		setCancelable(true);

	}
}
