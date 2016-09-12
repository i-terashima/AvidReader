package com.gashfara.it.avidreader;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.callback.KiiSocialCallBack;
import com.kii.cloud.storage.social.KiiSocialConnect;
import com.kii.cloud.storage.social.connector.KiiSocialNetworkConnector;


public class UserActivity extends ActionBarActivity {
    private CallbackManager callbackManager;
    private TextView textView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_user);

        Kii.initialize(getApplicationContext(), "a92e72bc", "57375ca4aae90f7a09e022042fff2390", Kii.Site.JP);

        textView = (TextView) findViewById(R.id.textView);

        LoginButton fbLoginButton = (LoginButton) findViewById(R.id.facebookLoginButton);
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.v("FB", "loginSuccess");
                Bundle options = new Bundle();
                String accessToken = loginResult.getAccessToken().getToken();
                options.putString("accessToken", accessToken);
                options.putParcelable("provider", KiiSocialNetworkConnector.Provider.FACEBOOK);
                KiiSocialNetworkConnector conn = (KiiSocialNetworkConnector) Kii.socialConnect(KiiSocialConnect.SocialNetwork.SOCIALNETWORK_CONNECTOR);
                conn.logIn(UserActivity.this, options, new KiiSocialCallBack() {
                    @Override
                    public void onLoginCompleted(KiiSocialConnect.SocialNetwork network, KiiUser user, Exception exception) {
                        if (exception != null) {
                            textView.setText("Failed to Login to Kii! " + exception
                                    .getMessage());
                            return;
                        }
                        // Intent のインスタンスを取得する。getApplicationContext()で自分のコンテキストを取得。遷移先のアクティビティーを.classで指定
                        SharedPreferences pref = getSharedPreferences(getString(R.string.save_data_name), Context.MODE_PRIVATE);
                        pref.edit().putString(getString(R.string.save_token), user.getAccessToken()).apply();

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        // 遷移先の画面を呼び出す
                        startActivity(intent);
                        //戻れないようにActivityを終了します。
                        finish();
                    }
                });
            }

            @Override
            public void onCancel() {
                Log.v("FB", "Cancelled.");
                textView.setText("Facebook Login has been cancelled.");
            }

            @Override
            public void onError(FacebookException error) {
                Log.v("FB", "loginFailed");
                error.printStackTrace();
                textView.setText("Facebook Login has been failed: " + error.getMessage());
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Kii.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Kii.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == KiiSocialNetworkConnector.REQUEST_CODE) {
            Kii.socialConnect(KiiSocialConnect.SocialNetwork.SOCIALNETWORK_CONNECTOR)
                    .respondAuthOnActivityResult(requestCode, resultCode, data);
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}