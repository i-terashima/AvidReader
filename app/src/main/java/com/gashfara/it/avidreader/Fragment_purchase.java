package com.gashfara.it.avidreader;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Fragment_purchase extends Fragment {

    public Fragment_purchase() {
    }

    public static Fragment_purchase newInstance() {
        Bundle args = new Bundle();
        Fragment_purchase fragment = new Fragment_purchase();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        String purchaseUrl = getArguments().getString("purchaseUrl");

        View view = inflater.inflate(R.layout.fragment_purchase, container, false);

//        WebViewを探す
        WebView webView = (WebView) view.findViewById(R.id.purchaseView);
        //デバッグログ
//        Log.d("get myurl", purchaseUrl);
        //ブラウザの機能をセットします。お約束。
        webView.setWebViewClient(new WebViewClient());
        //URLを表示します。
//        webView.loadUrl(purchaseUrl);
        webView.loadUrl("https://www.amazon.co.jp/gp/product/B01HHZDIWC/ref=s9_ri_gw_g74_i1_r?pf_rd_m=AN1VRQENFRJN5&pf_rd_s=&pf_rd_r=JRYVTTC357RTQCFPWX3N&pf_rd_t=36701&pf_rd_p=af1f6a92-57c5-4c51-adb8-be6c8e117649&pf_rd_i=desktop");

        return view;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_web, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

}