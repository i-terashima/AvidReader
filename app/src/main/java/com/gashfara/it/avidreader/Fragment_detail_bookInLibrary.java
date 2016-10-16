package com.gashfara.it.avidreader;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class Fragment_detail_bookInLibrary extends Fragment {

    Item_library book= new Item_library();
    private List<Item_stockInLibrary> stocks;
    private ArrayAdapter<Item_stockInLibrary> adapter;

    public Fragment_detail_bookInLibrary() {
    }

    public static Fragment_detail_bookInLibrary newInstance(Item_library book) {
        Log.d("test_log", "Fragment_detail_bookInLibrary");
        Bundle args = new Bundle();
        args.putSerializable("book", book);

        Fragment_detail_bookInLibrary fragment = new Fragment_detail_bookInLibrary();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_bookinlibrary, container, false);

        book = (Item_library) getArguments().getSerializable("book");
        Log.d("test_log", "1");

        ((TextView) view.findViewById(R.id.book_title)).setText(book.getTitle());
        ((TextView) view.findViewById(R.id.book_author)).setText(book.getAuthor());
        ((TextView) view.findViewById(R.id.book_publisher)).setText(book.getPublisher());
        ((TextView) view.findViewById(R.id.book_status)).setText(book.getStatus());

        ListView listView = (ListView) view.findViewById(R.id.stock_list);
        Log.d("test_log", "2");
        stocks = book.getStocks();




        Log.d("test_log", "3");
//        adapter = new ListAdapter(getActivity(), stocks);
//        adapter = new ListAdapter(getActivity(), listdata);
        Log.d("test_log", "4");
        listView.setAdapter(adapter);
        Log.d("test_log", "5");
        return view;
    }

    private class ViewHolder {
        TextView stock_title;
        TextView stock_page;
        TextView stock_quote;
        TextView stock_memo;
    }

    private class ListAdapter extends ArrayAdapter<Item_stockInLibrary> {
        private LayoutInflater mInflater;

        public ListAdapter(Context context, List<Item_stockInLibrary> objects) {
            super(context, 0, objects);
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.card_stock_in_library, parent, false);
                holder = new ViewHolder();
                holder.stock_title = (TextView) convertView.findViewById(R.id.stock_title);
                holder.stock_page = (TextView) convertView.findViewById(R.id.stock_page);
                holder.stock_quote = (TextView) convertView.findViewById(R.id.stock_quote);
                holder.stock_memo = (TextView) convertView.findViewById(R.id.stock_memo);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Item_stockInLibrary record = stocks.get(position);
            holder.stock_title.setText(record.getStock_title());
            holder.stock_page.setText("p." + record.getStock_page());
            holder.stock_quote.setText(record.getStock_quote());
            holder.stock_memo.setText(record.getStock_memo());

            return convertView;
        }
    }
}