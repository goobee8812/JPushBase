package com.example.goobee_yuer.jpushbase.jiguangpush;

import android.app.Application;
import android.content.Context;


import com.example.goobee_yuer.jpushbase.tools.Logger;

import cn.jpush.android.api.JPushInterface;

/**
 * For developer startup JPush SDK
 * 
 * 一般建议在自定义 Application 类里初始化。也可以在主 Activity 里。
 */
public class ExampleApplication extends Application {
    private static final String TAG = "JIGUANG-Example";
    private static Context context;
    @Override
    public void onCreate() {    	     
    	 Logger.d(TAG, "[ExampleApplication] onCreate");
         super.onCreate();
         JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
         JPushInterface.init(this);     		// 初始化 JPush
        context = getApplicationContext();
        //初始化日志记录
//        CrashHandler.getInstance().init(this);
        //初始化数据库
//        org.litepal.LitePal.initialize(context);
        //初始化下载库
//        Aria.init(this);
    }
    public static Context getContext(){
        return context;
    }
}
