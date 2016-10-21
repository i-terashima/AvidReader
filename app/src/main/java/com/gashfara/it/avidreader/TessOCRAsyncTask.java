package com.gashfara.it.avidreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

public class TessOCRAsyncTask
        extends AsyncTask<Object, Object, String> {

    Context context;

    Bitmap bitmap;

    public TessOCRAsyncTask(Context context, Bitmap bitmap) {
        Log.d("test_log", "TessOCRAsyncTask");
//        this.mImagePath = mImagePath;
        this.context = context;
        this.bitmap = bitmap;
    }

    @Override
    protected String doInBackground(Object... arg0) {
        Log.d("test_log", "Start uploadFile");
        try {
            Log.d("log_test", "x");
//            mTessOCR = new MyTessOCR(context);
        } catch (Exception e) {
            Log.d("test_log_e", e.getMessage());
        }
        String temp = null;

        Log.d("test_log", "Exit uploadFile");
        return temp;
    }

    @Override
    protected void onPostExecute(String result) {

    }
}