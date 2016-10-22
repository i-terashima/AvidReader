package com.gashfara.it.avidreader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class Fragment_stock_fromCamera extends Fragment {

    private String id;
    private Uri imgUri;
    Item_library record = new Item_library();

    public Fragment_stock_fromCamera() {
    }

    public static Fragment_stock_fromCamera newInstance(Uri outputFileUri, Item_library currentRecords, String id) {
        Bundle args = new Bundle();
        args.putString("imgUri", outputFileUri.toString());
        args.putString("id", id);
        args.putSerializable("record", currentRecords);

        Fragment_stock_fromCamera fragment = new Fragment_stock_fromCamera();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("test_log", "Fragment_stock_fromCamera");
        View view = inflater.inflate(R.layout.fragment_stock_fromcamera, container, false);

        imgUri = Uri.parse(getArguments().getString("imgUri"));
        id = getArguments().getString("id");
        record = (Item_library) getArguments().getSerializable("record");

        ((ImageView) view.findViewById(R.id.image_view1)).setImageURI(imgUri);
        final EditText stock_tag = (EditText) view.findViewById(R.id.stock_tag);
        final EditText stock_memo = (EditText) view.findViewById(R.id.stock_memo);
        final EditText stock_page = (EditText) view.findViewById(R.id.stock_page);

        Button postBtn = (Button) view.findViewById(R.id.ocr_button);
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Item_stockInLibrary stock_record = new Item_stockInLibrary();
                stock_record.setStock_tag(stock_tag.getText().toString());
                stock_record.setStock_memo(stock_memo.getText().toString());
                stock_record.setStock_page(Integer.parseInt(stock_page.getText().toString()));

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4; // 1 - means max size. 4 - means maxsize/4 size. Don't use value <4, because you need more memory in the heap to store your data.
                Bitmap bitmap = BitmapFactory.decodeFile(imgUri.getPath(), options);
                new TessOCRAsyncTask(getActivity().getApplicationContext(),bitmap,id,record, stock_record).execute();;

                Fragment fragment = new Container_top();
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }
        });
        return view;
    }


}
