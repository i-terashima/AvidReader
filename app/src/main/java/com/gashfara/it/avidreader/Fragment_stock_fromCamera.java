package com.gashfara.it.avidreader;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;

public class Fragment_stock_fromCamera extends Fragment {

    private String mImagePath = null;
    private static Bitmap bmUpload;
    private ImageView mImageView;
    private String result;
    private Uri imgUri;

    public Fragment_stock_fromCamera() {
    }

    public static Fragment_stock_fromCamera newInstance(String result, Uri imgUri) {
        Bundle args = new Bundle();
        args.putString("result", result);
        args.putString("imgUri", imgUri.toString());

        Fragment_stock_fromCamera fragment = new Fragment_stock_fromCamera();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("test_log", "Fragment_stock_fromCamera");
        View view = inflater.inflate(R.layout.fragment_stock_fromcamera, container, false);

        result = getArguments().getString("result");
        imgUri = Uri.parse(getArguments().getString("imgUri"));
        Log.d("log_test",result);
        Log.d("log_test",imgUri.toString());

        ((ImageView) view.findViewById(R.id.image_view1)).setImageURI(imgUri);
        ((TextView) view.findViewById(R.id.stock_quote)).setText(result);
        Button postBtn = (Button) view.findViewById(R.id.post_button);
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mImagePath = getFilePath();
//                new TessOCRAsyncTask(getActivity().getApplicationContext(),mImagePath);
//                new TessOCRAsyncTask(getActivity().getApplicationContext(),bmUpload).execute();


                Fragment fragment = new Container_top();
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }
        });
        return view;
    }

    private String getFilePath() {
        String filePath = null;
        FileOutputStream fos = null;
        try {
            //ビットマップを取得
            //一時保存するディレクトリ。
            String cacheDir = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + File.separator + "AvidReader";
            //ディレクトリ作成
            File createDir = new File(cacheDir);
            if (!createDir.exists()) {
                createDir.mkdir();
            }
            //一時ファイル名を作成。毎回上書き
            filePath = cacheDir + File.separator + "upload.jpg";
            File file = new File(filePath);
            //ビットマップをjpgに変換して一時的に保存する。
            fos = new FileOutputStream(file);
            bmUpload.compress(Bitmap.CompressFormat.JPEG, 95, fos);
            fos.flush();
            fos.getFD().sync();
        } catch (Exception e) {
            filePath = null;
        } finally {//かならず最後に実行する処理
            if (fos != null) {
                try {
                    //ファイルを閉じる
                    fos.close();
                } catch (Exception e) {
                    // Nothing to do
                }
            }
        }
        return filePath;
    }
}
