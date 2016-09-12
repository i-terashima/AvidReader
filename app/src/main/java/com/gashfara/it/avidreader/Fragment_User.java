package com.gashfara.it.avidreader;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.callback.KiiSocialCallBack;
import com.kii.cloud.storage.callback.KiiUserCallBack;
import com.kii.cloud.storage.exception.CloudExecutionException;
import com.kii.cloud.storage.social.KiiSocialConnect;
import com.kii.cloud.storage.social.connector.KiiSocialNetworkConnector;


public class Fragment_User extends Fragment {
    private CallbackManager callbackManager;

    public Fragment_User() {
    }

    public static Fragment_User newInstance() {
        Fragment_User fragment = new Fragment_User();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences pref = getActivity().getSharedPreferences(getString(R.string.save_data_name), Context.MODE_PRIVATE);
        String token = pref.getString(getString(R.string.save_token), "");//保存されていない時は""
        if(token == "") {
            Fragment_User.newInstance();
        }else {
            try {
                KiiUser.loginWithToken(callback, token);
            } catch (Exception e) {
                //ダイアログを表示
                //画面を作る
                Fragment_User.newInstance();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity());
        callbackManager = CallbackManager.Factory.create();

        Kii.initialize(getActivity(), "a92e72bc", "57375ca4aae90f7a09e022042fff2390", Kii.Site.JP);

        LoginButton fbLoginButton = (LoginButton) view.findViewById(R.id.facebookLoginButton);
        fbLoginButton.setReadPermissions("email");
        fbLoginButton.setFragment(this);
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.v("FB", "loginSuccess");
                Toast.makeText(getActivity(), "Please wait a moment.", Toast.LENGTH_SHORT).show();
                Bundle options = new Bundle();
                String accessToken = loginResult.getAccessToken().getToken();
                options.putString("accessToken", accessToken);
                options.putParcelable("provider", KiiSocialNetworkConnector.Provider.FACEBOOK);
                KiiSocialNetworkConnector conn = (KiiSocialNetworkConnector) Kii.socialConnect(KiiSocialConnect.SocialNetwork.SOCIALNETWORK_CONNECTOR);
                conn.logIn(getActivity(), options, new KiiSocialCallBack() {
                    @Override
                    public void onLoginCompleted(KiiSocialConnect.SocialNetwork network, KiiUser user, Exception exception) {
                        if (exception != null) {
                            return;
                        }

                        SharedPreferences pref = getActivity().getSharedPreferences(getString(R.string.save_data_name), Context.MODE_PRIVATE);
                        pref.edit().putString(getString(R.string.save_token), user.getAccessToken()).apply();

                        Fragment fragment = new Fragment_home();
                        FragmentManager manager = getActivity().getSupportFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();
                        transaction.replace(R.id.fragment_container, fragment, "home");
                        transaction.commit();
                    }
                });
            }

            @Override
            public void onCancel() {
                Log.v("FB", "Cancelled.");
                Toast.makeText(getActivity(), "Facebook Login has been cancelled.", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onError(FacebookException error) {
                Log.v("FB", "loginFailed");
                error.printStackTrace();
                Toast.makeText(getActivity(), "Facebook Login has been failed.", Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Kii.onSaveInstanceState(outState);
    }

//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        Kii.onRestoreInstanceState(savedInstanceState);
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == KiiSocialNetworkConnector.REQUEST_CODE) {
            Kii.socialConnect(KiiSocialConnect.SocialNetwork.SOCIALNETWORK_CONNECTOR)
                    .respondAuthOnActivityResult(requestCode, resultCode, data);
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    void showAlert(int titleId, String message, AlertDialogFragment.AlertDialogListener listener ) {
        DialogFragment newFragment = AlertDialogFragment.newInstance(titleId, message, listener);
        newFragment.show(getActivity().getFragmentManager(), "dialog");
    }

    KiiUserCallBack callback = new KiiUserCallBack() {
        //ログインが完了した時に自動的に呼び出される。自動ログインの時も呼び出される
        @Override
        public void onLoginCompleted(int token, KiiUser user, Exception e) {
            // setFragmentProgress(View.INVISIBLE);
            if (e == null) {
                //自動ログインのためにSharedPreferenceに保存。アプリのストレージ。参考：http://qiita.com/Yuki_Yamada/items/f8ea90a7538234add288
                SharedPreferences pref = getActivity().getSharedPreferences(getString(R.string.save_data_name), Context.MODE_PRIVATE);
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
//        //新規登録の時に自動的に呼び出される
//        @Override
//        public void onRegisterCompleted(int token, KiiUser user, Exception e) {
//            if (e == null) {
//                //自動ログインのためにSharedPreferenceに保存。アプリのストレージ。参考：http://qiita.com/Yuki_Yamada/items/f8ea90a7538234add288
//                SharedPreferences pref = getSharedPreferences(getString(R.string.save_data_name), Context.MODE_PRIVATE);
//                pref.edit().putString(getString(R.string.save_token), user.getAccessToken()).apply();
//
//                // Intent のインスタンスを取得する。getApplicationContext()で自分のコンテキストを取得。遷移先のアクティビティーを.classで指定
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                // 遷移先の画面を呼び出す
//                startActivity(intent);
//                //戻れないようにActivityを終了します。
//                finish();
//            } else {
//                //eがKiiCloud特有のクラスを継承している時
//                if (e instanceof CloudExecutionException)
//                    //KiiCloud特有のエラーメッセージを表示
//                    showAlert(R.string.operation_failed, Util.generateAlertMessage((CloudExecutionException) e), null);
//                else
//                    //一般的なエラーを表示
//                    showAlert(R.string.operation_failed, e.getLocalizedMessage(), null);
//            }
//        }
    };

}