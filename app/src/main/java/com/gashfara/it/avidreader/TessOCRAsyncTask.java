package com.gashfara.it.avidreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

public class TessOCRAsyncTask
        extends AsyncTask<Object, Object, String> {

    Context context;
    String mImagePath;
    private MyTessOCR mTessOCR;
    Bitmap bitmap;

    public TessOCRAsyncTask(Context context, Bitmap bitmap) {
//        this.mImagePath = mImagePath;
        this.context = context;
        this.bitmap = bitmap;
    }

    @Override
    protected String doInBackground(Object... arg0) {
        Boolean ret = Boolean.FALSE;
        Log.d("test_log", "Start uploadFile");
        String temp = null;
        try {
            mTessOCR = new MyTessOCR(context);
            temp = mTessOCR.getOCRResult(bitmap);
            Log.d("log_test", temp);
        } catch (Exception ex) {
            Log.d("test_log", ex.getMessage());
        }
        Log.d("test_log", "Exit uploadFile");
        return temp;
    }

    @Override
    protected void onPostExecute(String result) {

    }
}