package com.gashfara.it.avidreader;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.kii.cloud.storage.Kii;

public class MyApplication extends Application {
    private static MyApplication sInstance;

    private RequestQueue mRequestQueue;
    @Override
    public void onCreate() {
        super.onCreate();
        mRequestQueue = Volley.newRequestQueue(this);
        sInstance = this;

        Kii.initialize(getApplicationContext(), "a92e72bc", "57375ca4aae90f7a09e022042fff2390", Kii.Site.JP);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }

    public synchronized static MyApplication getInstance() {
        return sInstance;
    }

    //通信クラスを返す関数
    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }
}
