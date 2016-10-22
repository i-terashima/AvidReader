package com.gashfara.it.avidreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiBucket;
import com.kii.cloud.storage.KiiObject;
import com.kii.cloud.storage.callback.KiiObjectCallBack;
import com.kii.cloud.storage.exception.CloudExecutionException;

import java.util.List;

public class TessOCRAsyncTask
        extends AsyncTask<Object, Object, String> {

    Context context;
    Bitmap bitmap;
    String id;
    Item_library record = new Item_library();
    Item_stockInLibrary stock_record = new Item_stockInLibrary();
    private TessBaseAPI tessBaseApi;
    private static final String lang = "jpn";
    private static final String DATA_PATH = Environment.getExternalStorageDirectory() + "/Tess-two/";


    public TessOCRAsyncTask(Context context, Bitmap bitmap, String id, Item_library record, Item_stockInLibrary stock_record) {
        Log.d("test_log", "TessOCRAsyncTask");
        this.context = context;
        this.bitmap = bitmap;
        this.id = id;
        this.record = record;
        this.stock_record = stock_record;
    }

    @Override
    protected String doInBackground(Object... arg0) {
        Log.d("test_log", "Start uploadFile");
        try {
            tessBaseApi = new TessBaseAPI();
        } catch (Exception e) {
            Log.e("test_log", e.getMessage());
            if (tessBaseApi == null) {
                Log.e("test_log", "TessBaseAPI is null. TessFactory not returning tess object.");
            }
        }
        tessBaseApi.init(DATA_PATH, lang);

        tessBaseApi.setImage(bitmap);
        String extractedText = "empty result";
        try {
            extractedText = tessBaseApi.getUTF8Text();
        } catch (Exception e) {
            Log.e("test_log", "Error in recognizing text.");
        }
        tessBaseApi.end();
        Log.d("test_log", extractedText);
        return extractedText;
    }

    @Override
    protected void onPostExecute(String result) {

        stock_record.setStock_quote(result);
        List<Item_stockInLibrary> stocks = record.getStocks();
        stocks.add(stock_record);
        record.setStocks(stocks);

        Gson gson_library = new Gson();
        String json_library = gson_library.toJson(record);
        KiiBucket bucket_library = Kii.user().bucket("library");
        KiiObject object_library = bucket_library.object(id);
        object_library.set("book", json_library);

        //データをKiiCloudに保存
        object_library.save(new KiiObjectCallBack() {
            //保存結果が帰ってくるコールバック関数。自動的に呼び出される。
            @Override
            public void onSaveCompleted(int token, KiiObject object, Exception exception) {
                //エラーがないとき
                if (exception == null) {
                    Toast.makeText(context, "解析を完了しました", Toast.LENGTH_SHORT).show();
                } else {
                    if (exception instanceof CloudExecutionException)
                        Toast.makeText(context, KiiCloudUtil.generateAlertMessage((CloudExecutionException) exception), Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(context, exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        Item_stock stock = new Item_stock();
        stock.setStock_quote(stock_record.getStock_quote());
        stock.setStock_tag(stock_record.getStock_tag());
        stock.setStock_memo(stock_record.getStock_memo());
        stock.setStock_page(stock_record.getStock_page());
        stock.setAuthor(record.getAuthor());
        stock.setIsbn(record.getIsbn());
        stock.setPublisher(record.getPublisher());
        stock.setTitle(record.getTitle());

        Gson gson_stock = new Gson();
        String json_stock = gson_stock.toJson(stock);
        KiiBucket bucket_stock = Kii.user().bucket("stocks");
        KiiObject object_stock = bucket_stock.object();
        object_stock.set("stock", json_stock);

        //データをKiiCloudに保存
        object_stock.save(new KiiObjectCallBack() {
            //保存結果が帰ってくるコールバック関数。自動的に呼び出される。
            @Override
            public void onSaveCompleted(int token, KiiObject object, Exception exception) {
                //エラーがないとき
                if (exception == null) {
                    Toast.makeText(context, "解析を完了しました", Toast.LENGTH_SHORT).show();
                } else {
                    if (exception instanceof CloudExecutionException)
                        Toast.makeText(context, KiiCloudUtil.generateAlertMessage((CloudExecutionException) exception), Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(context, exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}