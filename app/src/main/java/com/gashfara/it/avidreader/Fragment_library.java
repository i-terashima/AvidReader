package com.gashfara.it.avidreader;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.google.gson.Gson;
import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiObject;
import com.kii.cloud.storage.callback.KiiQueryCallBack;
import com.kii.cloud.storage.query.KiiQuery;
import com.kii.cloud.storage.query.KiiQueryResult;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Fragment_library extends ListFragment implements ActivityCompat.OnRequestPermissionsResultCallback {

    private ArrayList<Item_library> records = new ArrayList<Item_library>();
    private ArrayAdapter<Item_library> adapter;
    private static final int IMAGE_CHOOSER_RESULTCODE = 1;
    private Uri mImageUri;
    private String filename;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean hasNextPage = false;
    ArrayList<Item_library> currentRecords = null;

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
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int position, long arg3) {
                final String[] items = {"カメラから画像を取得", "ギャラリーから画像を取得"};
                new AlertDialog.Builder(getActivity())
                        .setTitle("文章をストック")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
//                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                                        startActivityForResult(Intent.createChooser(intent, "Camera"), IMAGE_CHOOSER_RESULTCODE);
                                        filename = System.currentTimeMillis() + ".jpg";
                                        //設定を保存するパラメータを作成
                                        ContentValues values = new ContentValues();
                                        values.put(MediaStore.Images.Media.TITLE, filename);//ファイル名
                                        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");//ファイルの種類
                                        mImageUri = getActivity().getApplicationContext().getContentResolver().insert(
                                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                                        Intent intent = new Intent();
                                        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//カメラ
                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);//画像の保存先
                                        startActivityForResult(intent, IMAGE_CHOOSER_RESULTCODE);
                                        break;
                                    case 1:
                                        break;
                                    case 2:
                                        break;
                                }
                                dialog.dismiss();           // item_which pressed
                            }
                        })
                        .setTitle("書庫から削除")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
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
                        Log.d("test_log", "size : " + objListsSize);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //他のインテントの実行結果と区別するためstartActivityで指定した定数IMAGE_CHOOSER_RESULTCODEと一致するか確認
        if (requestCode == IMAGE_CHOOSER_RESULTCODE) {
            //失敗の時
            if (resultCode != getActivity().RESULT_OK) {
                return;
            }
            Uri result;
            if (data != null) {
                result = data.getData();
            } else {
                result = mImageUri;
            }

            InputStream inputStream = null;
            try {
                inputStream = getActivity().getContentResolver().openInputStream(mImageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            BitmapFactory.Options imageOptions = new BitmapFactory.Options();
            imageOptions.inJustDecodeBounds = true;
            imageOptions.inPreferredConfig = Bitmap.Config.ALPHA_8;
            BitmapFactory.decodeStream(inputStream, null, imageOptions);
            Log.d("test_log", "Original Image Size: " + imageOptions.outWidth + " x " + imageOptions.outHeight);

            try {
                inputStream.close();
                inputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap bitmap;
            int imageSizeMax = 500;
            try {
                inputStream = getActivity().getContentResolver().openInputStream(mImageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            float imageScaleWidth = (float) imageOptions.outWidth / imageSizeMax;
            float imageScaleHeight = (float) imageOptions.outHeight / imageSizeMax;

            // もしも、縮小できるサイズならば、縮小して読み込む
            if (imageScaleWidth > 2 && imageScaleHeight > 2) {
                imageOptions.inJustDecodeBounds = false;
                // 縦横、小さい方に縮小するスケールを合わせる
                int imageScale = (int) Math.floor((imageScaleWidth > imageScaleHeight ? imageScaleHeight : imageScaleWidth));
                // inSampleSizeには2のべき上が入るべきなので、imageScaleに最も近く、かつそれ以下の2のべき上の数を探す
                for (int i = 2; i <= imageScale; i *= 2) {
                    imageOptions.inSampleSize = i;
                }
                Log.d("test_log", "Sample Size: 1/" + imageOptions.inSampleSize);
                bitmap = BitmapFactory.decodeStream(inputStream, null, imageOptions);
            } else {
                bitmap = BitmapFactory.decodeStream(inputStream);
            }
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            getActivity().getApplicationContext().getContentResolver().delete(result, null, null);

            Fragment_stock_fromCamera fragment = Fragment_stock_fromCamera.newInstance(bitmap);
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack("library").commit();
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
                        if(hasNextPage == true){
                            startAsyncNextQuery(result);
                        }
                        //得られたListをMessageRecordに設定する
                        KiiObject obj;
                        int objListsSize = objLists.size();

                        Log.d("test_log", "size" + objListsSize);

                        //得られたListをMessageRecordに設定する
                        for (int i = 0 ; i < objListsSize; i++) {
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
}
