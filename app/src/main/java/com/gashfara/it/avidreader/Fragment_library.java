package com.gashfara.it.avidreader;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Fragment_library extends ListFragment {

    private List<Item_library> list;
    private ArrayAdapter<Item_library> adapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        list = new ArrayList();
        for (int i = 0; i < 30; i++) {
            Item_library itemlibrary = new Item_library();
            itemlibrary.title = "Title" + (i + 1);
            itemlibrary.author = "著者名：" + (i + 1);
            itemlibrary.publisher = "出版社：" + (i + 1);
            itemlibrary.status = "読了済/未読/購入予定" + (i + 1);
            list.add(itemlibrary);
        }
        adapter = new ListAdapter(getActivity(), list);
        setListAdapter(adapter);
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
                holder.titleListText = (TextView) convertView.findViewById(R.id.title_library);
                holder.publisherListText = (TextView) convertView.findViewById(R.id.publisher_library);
                holder.authorListText = (TextView) convertView.findViewById(R.id.author_library);
                holder.statusListText = (TextView) convertView.findViewById(R.id.status_library);
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

    private class ViewHolder {
        TextView titleListText;
        TextView publisherListText;
        TextView authorListText;
        TextView statusListText;
    }
}
