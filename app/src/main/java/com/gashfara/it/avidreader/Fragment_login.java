package com.gashfara.it.avidreader;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.callback.KiiUserCallBack;
import com.kii.cloud.storage.exception.CloudExecutionException;

public class Fragment_login extends Fragment {

    private EditText mUsernameField;
    private EditText mPasswordField;

    public Fragment_login() {
    }

    public static Fragment_login newInstance() {
        Bundle args = new Bundle();
        Fragment_login fragment = new Fragment_login();
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //自動ログインのため保存されているaccess tokenを読み出す。tokenがあればログインできる
        SharedPreferences pref = this.getActivity().getSharedPreferences(getString(R.string.save_data_name), Context.MODE_PRIVATE);
        String token = pref.getString(getString(R.string.save_token), "");//保存されていない時は""
        //tokenがないとき。
        if(token == "") {
            //画面を作る
            Fragment fragment = new Fragment_login();
            FragmentManager manager = this.getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.fragment_container, fragment, "home");
            transaction.commit();
        }else {
            //自動ログインをする。
            try {
                //KiiCloudのAccessTokenによるログイン処理。完了すると結果がcallback関数として実行される。
                KiiUser.loginWithToken(callback, token);
            } catch (Exception e) {
                //ダイアログを表示
                showAlert(R.string.operation_failed, e.getLocalizedMessage(), null);
                //画面を作る
                Fragment fragment = new Fragment_login();
                FragmentManager manager = this.getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.fragment_container, fragment, "home");
                transaction.commit();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        //EditTextのビューを探します
        mUsernameField = (EditText) view.findViewById(R.id.username_field);
        mPasswordField = (EditText) view.findViewById(R.id.password_field);
        //パスワードを隠す設定
        mPasswordField.setTransformationMethod(new PasswordTransformationMethod());
        //パスワードの入力文字を制限する。参考：http://techbooster.jpn.org/andriod/ui/3857/
        mPasswordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        //登録ボタン
        Button signupBtn = (Button) view.findViewById(R.id.signup_button);
        //ログインボタン
        Button loginBtn = (Button) view.findViewById(R.id.login_button);
        //ログインボタンをクリックした時の処理を設定
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ログイン処理
                onLoginButtonClicked(v);
            }
        });
        //登録ボタンをクリックした時の処理を設定
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //登録処理
                onSignupButtonClicked(v);
            }
        });
        return view;
    }

    //ログイン処理：参考　http://documentation.kii.com/ja/guides/android/managing-users/sign-in/
    public void onLoginButtonClicked(View v) {
        //IMEを閉じる
        InputMethodManager imm = (InputMethodManager) this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        //入力文字を得る
        String username = mUsernameField.getText().toString();
        String password = mPasswordField.getText().toString();
        try {
            //KiiCloudのログイン処理。完了すると結果がcallback関数として実行される。
            KiiUser.logIn(callback, username, password);
        } catch (Exception e) {
            //ダイアログを表示
            showAlert(R.string.operation_failed, e.getLocalizedMessage(), null);
        }
    }

    //ダイアログを表示する
    void showAlert(int titleId, String message, AlertDialogFragment.AlertDialogListener listener) {
        DialogFragment newFragment = AlertDialogFragment.newInstance(titleId, message, listener);
//        newFragment.show(getFragmentManager(), "dialog");
    }

    //登録処理
    public void onSignupButtonClicked(View v) {
        //IMEを閉じる
        InputMethodManager imm = (InputMethodManager) this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

        //入力文字を得る
        String username = mUsernameField.getText().toString();
        String password = mPasswordField.getText().toString();
        try {
            //KiiCloudのユーザ登録処理
            KiiUser user = KiiUser.createWithUsername(username);
            user.register(callback, password);
        } catch (Exception e) {
            showAlert(R.string.operation_failed, e.getLocalizedMessage(), null);
        }
    }

    //新規登録、ログインの時に呼び出されるコールバック関数
    KiiUserCallBack callback = new KiiUserCallBack() {
        //ログインが完了した時に自動的に呼び出される。自動ログインの時も呼び出される
        @Override
        public void onLoginCompleted(int token, KiiUser user, Exception e) {
            // setFragmentProgress(View.INVISIBLE);
            if (e == null) {
                //自動ログインのためにSharedPreferenceに保存。アプリのストレージ。参考：http://qiita.com/Yuki_Yamada/items/f8ea90a7538234add288
                SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences(getString(R.string.save_data_name), Context.MODE_PRIVATE);
                pref.edit().putString(getString(R.string.save_token), user.getAccessToken()).apply();

                Fragment fragment = new Fragment_home();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.fragment_container, fragment, "home");
                transaction.commit();

            } else {
                //eがKiiCloud特有のクラスを継承している時
                if (e instanceof CloudExecutionException)
                    //KiiCloud特有のエラーメッセージを表示。フォーマットが違う
                    showAlert(R.string.operation_failed, KiiCloudUtil.generateAlertMessage((CloudExecutionException) e), null);
                else
                    //一般的なエラーを表示
                    showAlert(R.string.operation_failed, e.getLocalizedMessage(), null);
            }
        }

        //新規登録の時に自動的に呼び出される
        @Override
        public void onRegisterCompleted(int token, KiiUser user, Exception e) {
            if (e == null) {
                //自動ログインのためにSharedPreferenceに保存。アプリのストレージ。参考：http://qiita.com/Yuki_Yamada/items/f8ea90a7538234add288
                SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences(getString(R.string.save_data_name), Context.MODE_PRIVATE);
                pref.edit().putString(getString(R.string.save_token), user.getAccessToken()).apply();

                Fragment fragment = new Fragment_home();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.fragment_container, fragment, "home");
                transaction.commit();

            } else {
                //eがKiiCloud特有のクラスを継承している時
                if (e instanceof CloudExecutionException)
                    //KiiCloud特有のエラーメッセージを表示
                    showAlert(R.string.operation_failed, KiiCloudUtil.generateAlertMessage((CloudExecutionException) e), null);
                else
                    //一般的なエラーを表示
                    showAlert(R.string.operation_failed, e.getLocalizedMessage(), null);
            }
        }
    };
}