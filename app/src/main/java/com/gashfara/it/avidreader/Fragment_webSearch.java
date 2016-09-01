package com.gashfara.it.avidreader;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private List<MessageRecord> messageRecords;
    private ArrayAdapter<MessageRecord> adapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        messageRecords = new ArrayList();

        fetch();
    }

    private class ViewHolder {
        NetworkImageView imageListUrl;
        TextView commentListText;
    }

    private void fetch() {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                "http://gashfara.com/test/json.txt",
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
        VolleyApplication.getInstance().getRequestQueue().add(request);

    }


    private List<MessageRecord> parse(JSONObject json) throws JSONException {
        ArrayList<MessageRecord> records = new ArrayList<MessageRecord>();
        JSONArray jsonMessages = json.getJSONArray("messages");
        for (int i = 0; i < jsonMessages.length(); i++) {
            JSONObject jsonMessage = jsonMessages.getJSONObject(i);
            String title = jsonMessage.getString("comment");
            String url = jsonMessage.getString("imageUrl");
            MessageRecord record = new MessageRecord(url, title);
            records.add(record);
        }

        return records;
    }

    private class ListAdapter extends ArrayAdapter<MessageRecord> {
        private LayoutInflater mInflater;
        private ImageLoader mImageLoader;

        public ListAdapter(Context context, List<MessageRecord> objects) {
            super(context, 0, objects);
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mImageLoader = new ImageLoader(VolleyApplication.getInstance().getRequestQueue(), new BitmapLruCache());
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.message_item, parent, false);
                holder = new ViewHolder();
                holder.imageListUrl = (NetworkImageView) convertView.findViewById(R.id.image1);
                holder.commentListText = (TextView) convertView.findViewById(R.id.text1);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final MessageRecord imageRecord = getItem(position);
            holder.imageListUrl.setImageUrl(imageRecord.getImageUrl(), mImageLoader);
            holder.commentListText.setText(imageRecord.getComment());

            return convertView;
        }

        public void setMessageRecords(List<MessageRecord> objects) {
            //ArrayAdapterを空にする。
            clear();
            //テータの数だけMessageRecordを追加します。
            for (MessageRecord object : objects) {
                add(object);
            }
            //データの変更を通知します。
            notifyDataSetChanged();
        }
    }
}