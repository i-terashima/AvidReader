package com.gashfara.it.avidreader;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.kii.cloud.storage.Kii;

import java.util.HashMap;

public class MyApplication extends Application {
    private static MyApplication sInstance;
    private RequestQueue mRequestQueue;

    //GrowthHackで追加ここから
    //トラッキングIDを設定
    static final String PROPERTY_ID = "UA-85752957-1";
    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
    }
    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();
    //参考サイトのまま：http://qiita.com/chonbo2525/items/bbc55d728f8e1b8dca39
    public synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID)
                    : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(R.xml.global_tracker)
                    : analytics.newTracker(R.xml.ecommerce_tracker);
            t.enableAdvertisingIdCollection(true);
            mTrackers.put(trackerId, t);

        }
        return mTrackers.get(trackerId);
    }
    //GrowthHackで追加ここまで
    @Override
    public void onCreate() {
        super.onCreate();

        mRequestQueue = Volley.newRequestQueue(this);
        sInstance = this;

        Kii.initialize(getApplicationContext(), "a92e72bc", "57375ca4aae90f7a09e022042fff2390", Kii.Site.JP);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

//        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
//                getApplicationContext(),
//                "ap-northeast-1:b331f4b6-7041-423e-8cf6-5a4174ce49ed", // Identity Pool ID
//                Regions.AP_NORTHEAST_1 // Region
//        );
//        CognitoSyncManager syncClient = new CognitoSyncManager(
//                getApplicationContext(),
//                Regions.AP_NORTHEAST_1, // Region
//                credentialsProvider);
//
//        AmazonS3 s3 = new AmazonS3Client(credentialsProvider);

//    // Create a record in a dataset and synchronize with the server
//        Dataset dataset = syncClient.openOrCreateDataset("myDataset");
//        dataset.put("myKey", "myValue");
//        dataset.synchronize(new DefaultSyncCallback() {
//            @Override
//            public void onSuccess(Dataset dataset, List newRecords) {
//                //Your handler code here
//            }
//        });
    }

    public synchronized static MyApplication getInstance() {
        return sInstance;
    }

    //通信クラスを返す関数
    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

}
