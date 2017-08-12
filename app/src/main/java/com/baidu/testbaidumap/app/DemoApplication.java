package com.baidu.testbaidumap.app;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * @anthor ydl
 * @time 2017/8/9 15:28
 * TestBaiduMap 描述:
 */

public class DemoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(getApplicationContext());
    }
}
