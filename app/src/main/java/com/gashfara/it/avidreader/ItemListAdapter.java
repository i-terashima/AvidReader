package com.gashfara.it.avidreader;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

public class ItemListAdapter extends ArrayAdapter<Item_library> {
    private ImageLoader mImageLoader;

    public ItemListAdapter(Context context) {
        super(context, R.layout.card_websearch);
        Log.d("test_log", "ItemListAdapter");
        mImageLoader = new ImageLoader(MyApplication.getInstance().getRequestQueue(), new BitmapLruCache());
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.card_websearch, parent, false);
            NetworkImageView imageListUrl = (NetworkImageView) convertView.findViewById(R.id.image_websearch);
            TextView titleListText = (TextView) convertView.findViewById(R.id.title_websearch);
            TextView publisherListText = (TextView) convertView.findViewById(R.id.publisher_websearch);
            TextView authorListText = (TextView) convertView.findViewById(R.id.author_websearch);

            final Item_library imageRecord = getItem(position);
            imageListUrl.setImageUrl(imageRecord.getImageUrl(), mImageLoader);
            titleListText.setText(imageRecord.getTitle());
            publisherListText.setText(imageRecord.getPublisher());
            authorListText.setText(imageRecord.getAuthor());
            final String purchaseUrl = imageRecord.getPurchaseUrl();
        }
        return convertView;
    }


    //データをセットしなおす関数
    public void setItem_Library(List<Item_library> objects) {
        //ArrayAdapterを空にする。
        Log.d("test_log", "通過2");
        clear();
        //テータの数だけMessageRecordを追加します。
        for (Item_library object : objects) {
            add(object);
        }
        //データの変更を通知します。

        notifyDataSetChanged();
    }
}