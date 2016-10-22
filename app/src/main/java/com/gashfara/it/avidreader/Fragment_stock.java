package com.gashfara.it.avidreader;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
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

import java.util.ArrayList;
import java.util.List;

public class Fragment_stock extends ListFragment implements ActivityCompat.OnRequestPermissionsResultCallback {

    private ArrayList<Item_stock> records = new ArrayList<Item_stock>();
    private ArrayAdapter<Item_stock> adapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean hasNextPage = false;
    ArrayList<Item_stock> currentRecords = null;
    private  List<KiiObject> objLists;

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

    public Fragment_stock() {
    }

    public static Fragment_stock newInstance() {
        Fragment_stock fragment = new Fragment_stock();
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d("test_log", "Fragment_stock");
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
                String id = objLists.get(position).getString("_id");
//                Fragment fragment = Fragment_detail_bookInLibrary.newInstance(records.get(position), id);
//                getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();

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
                        final ArrayList<Item_stock> records = new ArrayList<Item_stock>();
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
                            Item_stock record = gson.fromJson(obj.getString("stock"), Item_stock.class);
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

    private class ListAdapter extends ArrayAdapter<Item_stock> {
        private LayoutInflater mInflater;

        public ListAdapter(Context context, List<Item_stock> objects) {
            super(context, 0, objects);
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.card_stock, parent, false);
                holder = new ViewHolder();
                holder.titleListText = (TextView) convertView.findViewById(R.id.title_stock);
                holder.pageListText = (TextView) convertView.findViewById(R.id.page_stock);
                holder.quoteListText = (TextView) convertView.findViewById(R.id.quote_stock);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final Item_stock item = getItem(position);
            holder.titleListText.setText(item.getTitle());
            holder.pageListText.setText(String.valueOf(item.getStock_page()));
            holder.quoteListText.setText(item.getStock_quote());

            return convertView;
        }
    }

    private void fetch() {
        //KiiCloudの検索条件を作成。検索条件は未設定。なので全件。
        KiiQuery query = new KiiQuery();
        //ソート条件を設定。日付の降順
        query.sortByDesc("_created");
        //バケットmessagesを検索する。最大200件
        Kii.user().bucket("stocks")
                .query(new KiiQueryCallBack<KiiObject>() {
                    //検索が完了した時
                    @Override
                    public void onQueryCompleted(int token, KiiQueryResult<KiiObject> result, Exception exception) {
                        if (exception != null) {
                            Log.d("test_log", exception.getLocalizedMessage());
                            return;
                        }
                        //検索結果をListで得る
                        objLists = result.getResult();
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
                            Item_stock record = gson.fromJson(obj.getString("stock"), Item_stock.class);
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
        TextView pageListText;
        TextView quoteListText;
    }
}
