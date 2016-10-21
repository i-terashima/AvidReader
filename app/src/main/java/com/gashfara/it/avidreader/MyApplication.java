package com.gashfara.it.avidreader;

import android.app.Application;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.kii.cloud.storage.Kii;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class MyApplication extends Application {
    private static MyApplication sInstance;
    private RequestQueue mRequestQueue;
    String datapath;

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

        datapath = Environment.getExternalStorageDirectory() + "/Tess-two/";
        File dir = new File(datapath + "/tessdata/");
        File file_jpn = new File(datapath + "/tessdata/" + "jpn.traineddata");
        File file_eng = new File(datapath + "/tessdata/" + "eng.traineddata");
        if (!file_jpn.exists()) {
            Log.d("test_log", "download jpn.traineddata");
            dir.mkdirs();
            new HttpGetTask_jpn().execute();
        }
        if (!file_eng.exists()) {
            Log.d("test_log", "download eng.traineddata");
            dir.mkdirs();
            new HttpGetTask_eng().execute();
        }
    }

    public synchronized static MyApplication getInstance() {
        return sInstance;
    }

    //通信クラスを返す関数
    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public final class HttpGetTask_jpn extends AsyncTask<URL, Void, Boolean> {
        public HttpGetTask_jpn() {
        }
        @Override
        protected Boolean doInBackground(URL[] urls) {
            HttpURLConnection con = null;
            try {
                // アクセス先URL
                final URL url = new URL("https://github.com/tesseract-ocr/tessdata/raw/master/jpn.traineddata");

                // 出力ファイルフルパス
                final String filePath = datapath + "/tessdata/" + "jpn.traineddata";

                // ローカル処理
                // コネクション取得
                con = (HttpURLConnection) url.openConnection();
                con.connect();

                // HTTPレスポンスコード
                final int status = con.getResponseCode();
                if (status == HttpURLConnection.HTTP_OK) {
                    // 通信に成功した
                    // ファイルのダウンロード処理を実行
                    // 読み込み用ストリーム
                    final InputStream input = con.getInputStream();
                    final DataInputStream dataInput = new DataInputStream(input);
                    // 書き込み用ストリーム
                    final FileOutputStream fileOutput = new FileOutputStream(filePath);
                    final DataOutputStream dataOut = new DataOutputStream(fileOutput);
                    // 読み込みデータ単位
                    final byte[] buffer = new byte[4096];
                    // 読み込んだデータを一時的に格納しておく変数
                    int readByte = 0;

                    // ファイルを読み込む
                    while ((readByte = dataInput.read(buffer)) != -1) {
                        dataOut.write(buffer, 0, readByte);
                    }
                    // 各ストリームを閉じる
                    dataInput.close();
                    fileOutput.close();
                    dataInput.close();
                    input.close();
                    // 処理成功
                    return true;
                }

            } catch (IOException e1) {
                Log.d("test_log", "e1.printStackTrace();");
            } finally {
                if (con != null) {
                    // コネクションを切断
                    con.disconnect();
                }
            }
            return false;
        }
    }
    public final class HttpGetTask_eng extends AsyncTask<URL, Void, Boolean> {
        public HttpGetTask_eng() {
        }
        @Override
        protected Boolean doInBackground(URL[] urls) {
            HttpURLConnection con = null;
            try {
                // アクセス先URL
                final URL url = new URL("https://github.com/tesseract-ocr/tessdata/raw/master/eng.traineddata");
                // 出力ファイルフルパス
                final String filePath = datapath + "/tessdata/" + "eng.traineddata";

                // ローカル処理
                // コネクション取得
                con = (HttpURLConnection) url.openConnection();
                con.connect();

                // HTTPレスポンスコード
                final int status = con.getResponseCode();
                if (status == HttpURLConnection.HTTP_OK) {
                    // 通信に成功した
                    // ファイルのダウンロード処理を実行
                    // 読み込み用ストリーム
                    final InputStream input = con.getInputStream();
                    final DataInputStream dataInput = new DataInputStream(input);
                    // 書き込み用ストリーム
                    final FileOutputStream fileOutput = new FileOutputStream(filePath);
                    final DataOutputStream dataOut = new DataOutputStream(fileOutput);
                    // 読み込みデータ単位
                    final byte[] buffer = new byte[4096];
                    // 読み込んだデータを一時的に格納しておく変数
                    int readByte = 0;

                    // ファイルを読み込む
                    while((readByte = dataInput.read(buffer)) != -1) {
                        dataOut.write(buffer, 0, readByte);
                    }
                    // 各ストリームを閉じる
                    dataInput.close();
                    fileOutput.close();
                    dataInput.close();
                    input.close();
                    // 処理成功
                    return true;
                }

            } catch (IOException e1) {
                Log.d("test_log", "e1.printStackTrace();");
            } finally {
                if (con != null) {
                    // コネクションを切断
                    con.disconnect();
                }
            }
            return false;
        }
    }

}
