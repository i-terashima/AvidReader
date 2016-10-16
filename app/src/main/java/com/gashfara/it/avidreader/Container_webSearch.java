package com.gashfara.it.avidreader;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiBucket;
import com.kii.cloud.storage.KiiObject;
import com.kii.cloud.storage.callback.KiiObjectCallBack;
import com.kii.cloud.storage.exception.CloudExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Container_webSearch extends Fragment {

    private ItemListAdapter mAdapter;
    private List<Item_library> messageRecords;

    public Container_webSearch() {
    }

    public static Container_webSearch newInstance() {
        Bundle args = new Bundle();
        Container_webSearch fragment = new Container_webSearch();
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("test_log", "Container_webSearch");
        View view = inflater.inflate(R.layout.container_websearch, container, false);

        // searchView
        android.support.v7.widget.SearchView searchText = (android.support.v7.widget.SearchView) view
                .findViewById(R.id.search_text);

        mAdapter = new ItemListAdapter(getActivity());
        ListView listView = (ListView) view.findViewById(R.id.search_list);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                final String[] items = {"読了済", "読書中", "購入予定"};
                new AlertDialog.Builder(getActivity())
                        .setTitle("書庫に登録")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                register_book(position, which);
                                dialog.dismiss();           // item_which pressed
                            }
                        })
                        .show();
            }
        });

        // Expand the SearchView with
        searchText.setIconified(false);

        searchText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String arg0) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String arg0) {
                // TODO Auto-generated method stub
                fetch(arg0);
                return false;
            }
        });
        return view;
    }

    private void fetch(String arg0) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                "https://www.googleapis.com/books/v1/volumes?q=" + arg0 + "&maxResults=40",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
//                            messageRecords.clear();
                            messageRecords = parse(jsonObject);
                            mAdapter.setItem_Library(messageRecords);
                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), "Unable to parse data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                //通信結果、エラーの時の処理クラスを作成。
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getActivity(), "Unable to fetch data: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        MyApplication.getInstance().getRequestQueue().add(request);
    }

    private List<Item_library> parse(JSONObject json) throws JSONException {
        ArrayList<Item_library> records = new ArrayList<Item_library>();
        JSONArray jsonMessages = json.getJSONArray("items");
        int length = json.getInt("totalItems");

        for (int i = 0; i < 10; i++) {
            JSONObject jsonMessage = jsonMessages.getJSONObject(i).getJSONObject("volumeInfo");

            String url = jsonMessage.getJSONObject("imageLinks").getString("smallThumbnail");
            String title = jsonMessage.getString("title");

            String author = jsonMessage.getJSONArray("authors").toString()
                    .replace("[", "").replace("]", "").replace("\"", "");

            String publisher = jsonMessage.getString("publisher");

            String purchaseUrl = "https://www.amazon.co.jp/gp/product/B01HHZDIWC/ref=s9_ri_gw_g74_i1_r?pf_rd_m=AN1VRQENFRJN5&pf_rd_s=&pf_rd_r=JRYVTTC357RTQCFPWX3N&pf_rd_t=36701&pf_rd_p=af1f6a92-57c5-4c51-adb8-be6c8e117649&pf_rd_i=desktop";

            Item_library record = new Item_library(url, title, author, publisher, purchaseUrl);
            records.add(record);
        }
        return records;
    }

    public void register_book(int position, int which) {
        //バケット名を設定。バケット＝DBのテーブルみたいなもの。Excelのシートみたいなもの。
        KiiBucket bucket = Kii.user().bucket("library");
        KiiObject object = bucket.object();

        Item_library record = messageRecords.get(position);

        switch (which) {
            case 0:
                record.setStatus("読了済");
                break;
            case 1:
                record.setStatus("読書中");
                break;
            case 2:
                record.setStatus("購入予定");
                break;
        }

        Gson gson = new Gson();
        String json = gson.toJson(record);

        object.set("book", json);

        //データをKiiCloudに保存
        object.save(new KiiObjectCallBack() {
            //保存結果が帰ってくるコールバック関数。自動的に呼び出される。
            @Override
            public void onSaveCompleted(int token, KiiObject object, Exception exception) {
                //エラーがないとき
                if (exception == null) {
                    Toast.makeText(getActivity(), "書庫に登録しました", Toast.LENGTH_SHORT).show();
                } else {
                    if (exception instanceof CloudExecutionException)
                        Toast.makeText(getActivity(), KiiCloudUtil.generateAlertMessage((CloudExecutionException) exception), Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(getActivity(), exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        //GAのスクリーン名はアクティビティーの名前を送信します。
        Tracker t = ((MyApplication) getActivity().getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
        t.setScreenName(this.getClass().getSimpleName());
        t.send(new HitBuilders.AppViewBuilder().build());
    }
}