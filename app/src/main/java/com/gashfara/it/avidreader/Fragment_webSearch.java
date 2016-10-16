package com.gashfara.it.avidreader;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Fragment_webSearch extends ListFragment {

    public Fragment_webSearch() {
    }

    public static Fragment_webSearch newInstance() {
        Fragment_webSearch fragment = new Fragment_webSearch();
        return fragment;
    }

    private List<Item_library> messageRecords;
    private ArrayAdapter<Item_library> adapter;
    private String searchText;

    public static Fragment_webSearch newInstance(String searchText) {
        Bundle args = new Bundle();
        args.putString("searchText", searchText);
        Fragment_webSearch fragment = new Fragment_webSearch();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d("test_log", "Fragment_webSearch");
        super.onActivityCreated(savedInstanceState);

        messageRecords = new ArrayList();
        String mSearchText = getArguments().getString("searchText", searchText);

        adapter = new ListAdapter(getActivity(), messageRecords);
        setListAdapter(adapter);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentFragment, View arg1,
                                    int position, long arg3) {
                final String[] items = {"読了済", "読書中", "購入予定"};
                int defaultItem = 0;
                final List<Integer> checkedItems = new ArrayList<>();
                checkedItems.add(defaultItem);
                new AlertDialog.Builder(getActivity())
                        .setTitle("書庫に登録")
                        .setSingleChoiceItems(items, defaultItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                checkedItems.clear();
                                checkedItems.add(which);
                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!checkedItems.isEmpty()) {
                                    Log.d("checkedItem:", "" + checkedItems.get(0));
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

    }

    private class ViewHolder {
        NetworkImageView imageListUrl;
        TextView titleListText;
        TextView publisherListText;
        TextView authorListText;
    }

    private void fetch() {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                "https://www.googleapis.com/books/v1/volumes?q=" + getArguments().getString("searchText", searchText) + "&maxResults=40",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            messageRecords.clear();
                            messageRecords = parse(jsonObject);

                            adapter = new ListAdapter(getActivity(), messageRecords);
                            setListAdapter(adapter);

                            adapter.notifyDataSetChanged();

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

    private class ListAdapter extends ArrayAdapter<Item_library> {
        private LayoutInflater mInflater;
        private ImageLoader mImageLoader;

        public ListAdapter(Context context, List<Item_library> objects) {
            super(context, 0, objects);
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mImageLoader = new ImageLoader(MyApplication.getInstance().getRequestQueue(), new BitmapLruCache());
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.card_websearch, parent, false);
                holder = new ViewHolder();
                holder.imageListUrl = (NetworkImageView) convertView.findViewById(R.id.image_websearch);
                holder.titleListText = (TextView) convertView.findViewById(R.id.title_websearch);
                holder.publisherListText = (TextView) convertView.findViewById(R.id.publisher_websearch);
                holder.authorListText = (TextView) convertView.findViewById(R.id.author_websearch);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final Item_library imageRecord = getItem(position);
            holder.imageListUrl.setImageUrl(imageRecord.getImageUrl(), mImageLoader);
            holder.titleListText.setText(imageRecord.getTitle());
            holder.publisherListText.setText(imageRecord.getPublisher());
            holder.authorListText.setText(imageRecord.getAuthor());
            final String purchaseUrl = imageRecord.getPurchaseUrl();

            return convertView;
        }
    }
}