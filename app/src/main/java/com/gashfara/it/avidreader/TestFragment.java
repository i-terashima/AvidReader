package com.gashfara.it.avidreader;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class TestFragment extends Fragment {

    public TestFragment() {
    }

    public static TestFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt("page", page);
        TestFragment fragment = new TestFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int page = getArguments().getInt("page", 0);
        View view = inflater.inflate(R.layout.testpage, container, false);
        ((TextView) view.findViewById(R.id.page_text)).setText("Page " + page);
        //adで追加ここから
        //adのViewを探す
        AdView mAdView = (AdView) view.findViewById(R.id.adView);
        //広告を取得するクラスを作成
        AdRequest adRequest = new AdRequest.Builder().build();
        //広告を表示
        mAdView.loadAd(adRequest);
        //adで追加ここまで

        return view;
    }
}