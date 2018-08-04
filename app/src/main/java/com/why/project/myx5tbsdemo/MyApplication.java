package com.why.project.myx5tbsdemo;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.tencent.smtt.sdk.QbSdk;

/**
 * Created by HaiyuKing
 * Used 在Application里面初始化X5内核
 */

public class MyApplication extends Application {

	/**系统上下文*/
	private static Context mAppContext;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mAppContext = getApplicationContext();

		//配置腾讯浏览服务
		initQbSdk();

	}

	/**获取系统上下文*/
	public static Context getAppContext()
	{
		return mAppContext;
	}

	private void initQbSdk() {
		//搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
		QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
			@Override
			public void onViewInitFinished(boolean arg0) {
				// TODO Auto-generated method stub
				//x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
				Log.d("app", " onViewInitFinished is " + arg0);
			}
			@Override
			public void onCoreInitFinished() {
				// TODO Auto-generated method stub
			}
		};
		//x5内核初始化接口
		QbSdk.initX5Environment(mAppContext,  cb);
	}
}
