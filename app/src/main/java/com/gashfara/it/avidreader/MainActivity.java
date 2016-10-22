package com.gashfara.it.avidreader;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.crittercism.app.Crittercism;
import com.facebook.login.LoginManager;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kii.cloud.storage.KiiUser;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            String[] permissions = new String[] {
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
            ActivityCompat.requestPermissions(this, permissions, 1);
        } else {

        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            String[] permissions = new String[] {
                    android.Manifest.permission.CAMERA
            };
            ActivityCompat.requestPermissions(this, permissions, 1);
        } else {

        }
        //ad(クラッシュ)で追加ここから
        //Crittercismの初期化。IDの部分は自分のアプリのIDにしてください。
        Crittercism.initialize(getApplicationContext(), "e21bb2c57fd9450ca0217b6fd150aed600555300");

        KiiUser user = KiiUser.getCurrentUser();

        SharedPreferences pref = getSharedPreferences(getString(R.string.save_data_name), Context.MODE_PRIVATE);
        String token = pref.getString(getString(R.string.save_token), "");

        setContentView(R.layout.activity_main);

        if (user == null || token == "") {
            Fragment fragment = Fragment_User.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        } else {
            Fragment fragment = new Container_top();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //GrowthHackで追加ここから
        //GAのスクリーン名はアクティビティーの名前を送信します。
        Tracker t = ((MyApplication)getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
        t.setScreenName(this.getClass().getSimpleName());
        t.send(new HitBuilders.AppViewBuilder().build());
        //GrowthHackで追加ここまで
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.d("MainActivity", "onPageSelected() position=" + position);

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onBackPressed() {
        int backStackCnt = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackCnt != 0) {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.settings) {
            final String[] items = {getString(R.string.setting1), getString(R.string.setting2), getString(R.string.setting3)};
            new AlertDialog.Builder(this)
                    .setTitle(R.string.settings)
                    .setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    Fragment fragment = Container_webSearch.newInstance();
                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
                                    break;
                                case 1:
                                    break;
                                case 2:
                                    break;
                            }
                            dialog.dismiss();
                        }
                    })
                    .show();
            return true;
        }
        //ログアウト処理.KiiCloudにはログアウト機能はないのでAccesTokenを削除して対応。
        if (id == R.id.log_out) {
            //自動ログインのため保存されているaccess tokenを消す。
            SharedPreferences pref = getSharedPreferences(getString(R.string.save_data_name), Context.MODE_PRIVATE);
            pref.edit().clear().apply();

            LoginManager.getInstance().logOut();
            KiiUser.logOut();

            //ログイン画面に遷移
            // Intent のインスタンスを取得する。getApplicationContext()でViewの自分のアクティビティーのコンテキストを取得。遷移先のアクティビティーを.classで指定
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            // 遷移先の画面を呼び出す
            startActivity(intent);
            //戻れないようにActivityを終了します。
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}