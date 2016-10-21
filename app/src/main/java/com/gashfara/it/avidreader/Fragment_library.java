package com.gashfara.it.avidreader;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiObject;
import com.kii.cloud.storage.callback.KiiQueryCallBack;
import com.kii.cloud.storage.query.KiiQuery;
import com.kii.cloud.storage.query.KiiQueryResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Fragment_library extends ListFragment implements ActivityCompat.OnRequestPermissionsResultCallback {

    private ArrayList<Item_library> records = new ArrayList<Item_library>();
    private ArrayAdapter<Item_library> adapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean hasNextPage = false;
    ArrayList<Item_library> currentRecords = null;

    static final int PHOTO_REQUEST_CODE = 1;
    private TessBaseAPI tessBaseApi;
    Uri outputFileUri;
    private static final String lang = "jpn";
    String result = "empty";
    private static final String DATA_PATH = Environment.getExternalStorageDirectory() + "/Tess-two/";

    private void startAsyncNextQuery(KiiQueryResult<KiiObject> res) {
        isNextResultReady = false;
        AsyncNextQuery asyncNextQuery = new AsyncNextQuery(getActivity().getApplicationContext());
        asyncNextQuery.execute(res);
    }

    private boolean isNextResultReady = false;
    //直近のfetch()で、result.hasNext()の結果次のページがある場合、次のページの結果を保存している。new
    private KiiQueryResult<KiiObject> nextResult = null;

    public void setNextResult(KiiQueryResult<KiiObject> res) {
        // asyncNextQueryが終わったら呼ばれる
        isNextResultReady = true;
        nextResult = res;
    }

    public Fragment_library() {
    }

    public static Fragment_library newInstance() {
        Fragment_library fragment = new Fragment_library();
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d("test_log", "Fragment_library");
        super.onActivityCreated(savedInstanceState);

        // プログレスダイアログを用意する new
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("処理しています");
        progressDialog.setCancelable(false);

        // SwipeRefreshLayoutの設定 new
//        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
//        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
//        mSwipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.green, R.color.blue, R.color.yellow);

        fetch();

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentFragment, View arg1,
                                    int position, long arg3) {
                Fragment fragment = Fragment_detail_bookInLibrary.newInstance(records.get(position));
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();

            }
        });

        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view,
                                           int position, long id) {
                final String[] items = {"カメラから画像を取得", "ギャラリーから画像を取得"};
                new AlertDialog.Builder(getActivity())
                        .setTitle("文章をストック")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        startCameraActivity();
                                        break;
                                    case 1:
                                        break;
                                    case 2:
                                        break;
                                }
                                dialog.dismiss();           // item_which pressed
                            }
                        })
                        .show();
                return true;
            }
        });

        getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                ;
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount != 0 && totalItemCount == firstVisibleItem + visibleItemCount) {
                    // 最後尾までスクロールしたので、何かデータ取得する処理
                    if (hasNextPage == true && isNextResultReady == true) {
                        // ちょっとWait入れてからプログレスダイアログ表示
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //プログレスダイアログ表示
                                progressDialog.show();
                            }
                        }, 250);
                        //
                        hasNextPage = false;
                        isNextResultReady = false;
                        //空のMessageRecordデータの配列を作成
                        final ArrayList<Item_library> records = new ArrayList<Item_library>();
                        //検索結果をListで得る
                        List<KiiObject> objLists = nextResult.getResult();
                        //200件を超えている場合のページング処理のためhasNext()の結果を保存
                        hasNextPage = nextResult.hasNext();
                        if (hasNextPage == true) {
                            startAsyncNextQuery(nextResult);
                        }
                        //得られたListをMessageRecordに設定する
                        KiiObject obj;
                        int objListsSize = objLists.size();
                        for (int i = 0; i < objListsSize; i++) {
                            obj = objLists.get(i);
                            Gson gson = new Gson();
                            Item_library record = gson.fromJson(obj.getString("book"), Item_library.class);
                            //MessageRecordの配列に追加します。
                            records.add(record);
                        }
                        // ちょっとWait入れてからプログレスダイアログを閉じる
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //アダプターに更新版データをセットしなおす
                                adapter.notifyDataSetChanged();
                                currentRecords.addAll(records);
                                //プログレスダイアログ非表示
                                progressDialog.dismiss();
                            }
                        }, 1000);
                    }
                }
            }
        });
    }

    private class ListAdapter extends ArrayAdapter<Item_library> {
        private LayoutInflater mInflater;

        public ListAdapter(Context context, List<Item_library> objects) {
            super(context, 0, objects);
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.card_library, parent, false);
                holder = new ViewHolder();
                holder.titleListText = (TextView) convertView.findViewById(R.id.book_title);
                holder.publisherListText = (TextView) convertView.findViewById(R.id.book_publisher);
                holder.authorListText = (TextView) convertView.findViewById(R.id.book_author);
                holder.statusListText = (TextView) convertView.findViewById(R.id.book_status);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final Item_library item = getItem(position);
            holder.titleListText.setText(item.getTitle());
            holder.publisherListText.setText(item.getPublisher());
            holder.authorListText.setText(item.getAuthor());
            holder.statusListText.setText(item.getStatus());

            return convertView;
        }
    }

    private void fetch() {
        //KiiCloudの検索条件を作成。検索条件は未設定。なので全件。
        KiiQuery query = new KiiQuery();
        //ソート条件を設定。日付の降順
        query.sortByDesc("_created");
        //バケットmessagesを検索する。最大200件
        Kii.user().bucket("library")
                .query(new KiiQueryCallBack<KiiObject>() {
                    //検索が完了した時
                    @Override
                    public void onQueryCompleted(int token, KiiQueryResult<KiiObject> result, Exception exception) {
                        if (exception != null) {
                            Log.d("test_log", exception.getLocalizedMessage());
                            return;
                        }
                        //検索結果をListで得る
                        List<KiiObject> objLists = result.getResult();
                        //200件を超えている場合のページング処理のためhasNext()の結果を保存
                        hasNextPage = result.hasNext();
                        //
                        if (hasNextPage == true) {
                            startAsyncNextQuery(result);
                        }
                        //得られたListをMessageRecordに設定する
                        KiiObject obj;
                        int objListsSize = objLists.size();

                        //得られたListをMessageRecordに設定する
                        for (int i = 0; i < objListsSize; i++) {
                            obj = objLists.get(i);
                            Gson gson = new Gson();
                            Item_library record = gson.fromJson(obj.getString("book"), Item_library.class);
                            //MessageRecordの配列に追加します。
                            records.add(record);
                        }
                        adapter = new ListAdapter(getActivity(), records);
                        setListAdapter(adapter);
                        currentRecords = records;
                    }
                }, query);
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        //一覧のデータを作成して表示します。
//        fetch();
//    }

    private class AsyncNextQuery extends AsyncTask<KiiQueryResult<KiiObject>, Void, KiiQueryResult<KiiObject>> {
        private Context context;
        private KiiQueryResult<KiiObject> res;

        public AsyncNextQuery(Context context) {
            // 呼び出し元のアクティビティ
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected KiiQueryResult<KiiObject> doInBackground(KiiQueryResult<KiiObject>... kiiQueryResults) {
            try {
                res = kiiQueryResults[0].getNextQueryResult();

            } catch (Exception e) { //次が0件でもhasNext()がtrue返すこともあるらしいので注意。その場合例外になる
                //e.printStackTrace();//未処理
            }
            return res;
        }

        @Override
        protected void onPostExecute(KiiQueryResult<KiiObject> res) {
            setNextResult(res);
        }
    }

    private class ViewHolder {
        TextView titleListText;
        TextView publisherListText;
        TextView authorListText;
        TextView statusListText;
    }

    private void startCameraActivity() {
        try {
            String IMGS_PATH = Environment.getExternalStorageDirectory().toString() + "/Tess-two/imgs";
            prepareDirectory(IMGS_PATH);

            String img_path = IMGS_PATH + "/ocr.jpg";

            outputFileUri = Uri.fromFile(new File(img_path));

            final Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, PHOTO_REQUEST_CODE);
            }
        } catch (Exception e) {
            Log.e("test_log", e.getMessage());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        //making photo
        if (requestCode == PHOTO_REQUEST_CODE && resultCode == getActivity().RESULT_OK) {
            startOCR(outputFileUri);
        } else {
            Toast.makeText(getActivity(), "ERROR: Image was not obtained.", Toast.LENGTH_SHORT).show();
        }
    }

    private void prepareDirectory(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.e("test_log", "ERROR: Creation of directory " + path + " failed, check does Android Manifest have permission to write to external storage.");
            }
        } else {
            Log.i("test_log", "Created directory " + path);
        }
    }

    private void startOCR(Uri imgUri) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4; // 1 - means max size. 4 - means maxsize/4 size. Don't use value <4, because you need more memory in the heap to store your data.
            Bitmap bitmap = BitmapFactory.decodeFile(imgUri.getPath(), options);

            result = extractText(bitmap);
            Log.d("log_test", result);

        } catch (Exception e) {
            Log.e("test_log", e.getMessage());
        }
        Fragment fragment = Fragment_stock_fromCamera.newInstance(result, imgUri);
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    private String extractText(Bitmap bitmap) {
        try {
            tessBaseApi = new TessBaseAPI();
        } catch (Exception e) {
            Log.e("test_log", e.getMessage());
            if (tessBaseApi == null) {
                Log.e("test_log", "TessBaseAPI is null. TessFactory not returning tess object.");
            }
        }
        Log.d("test_log", "x");
        tessBaseApi.init(DATA_PATH, lang);
        Log.d("test_log", "xx");
//       //EXTRA SETTINGS
//        //For example if we only want to detect numbers
//        tessBaseApi.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "1234567890");
//
//        //blackList Example
//        tessBaseApi.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, "!@#$%^&*()_+=-qwertyuiop[]}{POIU" +
//                "YTRWQasdASDfghFGHjklJKLl;L:'\"\\|~`xcvXCVbnmBNM,./<>?");

        tessBaseApi.setImage(bitmap);
        String extractedText = "empty result";
        try {
            extractedText = tessBaseApi.getUTF8Text();
        } catch (Exception e) {
            Log.e("test_log", "Error in recognizing text.");
        }
        tessBaseApi.end();
        return extractedText;
    }
}
