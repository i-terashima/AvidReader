package com.gashfara.it.avidreader;

import android.accounts.AccountManager;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.callback.KiiSocialCallBack;
import com.kii.cloud.storage.callback.KiiUserCallBack;
import com.kii.cloud.storage.exception.CloudExecutionException;
import com.kii.cloud.storage.social.KiiSocialConnect;
import com.kii.cloud.storage.social.connector.KiiSocialNetworkConnector;

import java.io.IOException;


public class Fragment_User extends Fragment implements GoogleApiClient.OnConnectionFailedListener {
    private CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;
    private static final int ACCOUNT_REQUEST_CODE = 10000;

    public Fragment_User() {
    }

    public static Fragment_User newInstance() {
        Fragment_User fragment = new Fragment_User();
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences pref = getActivity().getSharedPreferences(getString(R.string.save_data_name), Context.MODE_PRIVATE);
        String token = pref.getString(getString(R.string.save_token), "");//保存されていない時は""
        if (token == "") {
            Fragment_User.newInstance();
        } else {
            try {
                KiiUser.loginWithToken(callback, token);
            } catch (Exception e) {
                Fragment_User.newInstance();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        super.onCreate(savedInstanceState);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
//                .requestScopes(new Scope(/* whatever scope you're needing */))
                .requestId()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity() /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        FacebookSdk.sdkInitialize(getActivity());
        callbackManager = CallbackManager.Factory.create();

        Kii.initialize(getActivity(), "a92e72bc", "57375ca4aae90f7a09e022042fff2390", Kii.Site.JP);

        SignInButton googleSignInButton = (SignInButton) view.findViewById(R.id.googleLoginButton);
        googleSignInButton.setSize(SignInButton.SIZE_STANDARD);
        googleSignInButton.setScopes(gso.getScopeArray());

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent accountChooserIntent =
                        AccountPicker.newChooseAccountIntent(null, null,
                                new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, true, getString(R.string.please_select_account), null,
                                null, null);
                startActivityForResult(accountChooserIntent, ACCOUNT_REQUEST_CODE);
            }
        });

        LoginButton fbLoginButton = (LoginButton) view.findViewById(R.id.facebookLoginButton);
        fbLoginButton.setReadPermissions("email");
        fbLoginButton.setFragment(this);
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.v("FB", "loginSuccess");
                Bundle options = new Bundle();
                String accessToken = loginResult.getAccessToken().getToken();
                options.putString("accessToken", accessToken);
                options.putParcelable("provider", KiiSocialNetworkConnector.Provider.FACEBOOK);
                KiiSocialNetworkConnector conn = (KiiSocialNetworkConnector) Kii.socialConnect(KiiSocialConnect.SocialNetwork.SOCIALNETWORK_CONNECTOR);
                conn.logIn(getActivity(), options, new KiiSocialCallBack() {
                    @Override
                    public void onLoginCompleted(KiiSocialConnect.SocialNetwork network, KiiUser user, Exception exception) {
                        if (exception != null) {
                            if (exception instanceof CloudExecutionException)
                                showAlert(R.string.operation_failed, KiiCloudUtil.generateAlertMessage((CloudExecutionException) exception), null);
                            else
                                showAlert(R.string.operation_failed, exception.getLocalizedMessage(), null);
                            return;
                        }
                        SharedPreferences pref = getActivity().getSharedPreferences(getString(R.string.save_data_name), Context.MODE_PRIVATE);
                        pref.edit().putString(getString(R.string.save_token), user.getAccessToken()).apply();

                        Fragment fragment = new Container_top();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == KiiSocialNetworkConnector.REQUEST_CODE) {
            Kii.socialConnect(KiiSocialConnect.SocialNetwork.SOCIALNETWORK_CONNECTOR)
                    .respondAuthOnActivityResult(resultCode, resultCode, data);
        } else if (requestCode == ACCOUNT_REQUEST_CODE) {
            Kii.socialConnect(KiiSocialConnect.SocialNetwork.SOCIALNETWORK_CONNECTOR)
                    .respondAuthOnActivityResult(resultCode, resultCode, data);

            String mEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            getGoogleToken(mEmail);
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void getGoogleToken(String account) {
        AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... accounts) {
                String scopes = "oauth2:https://www.googleapis.com/auth/userinfo.profile";
                String token = null;
                try {
                    token = GoogleAuthUtil.getToken(getActivity(), accounts[0], scopes);
                    return token;
                } catch (IOException e) {

                } catch (UserRecoverableAuthException e) {
                    startActivityForResult(e.getIntent(), ACCOUNT_REQUEST_CODE);
                } catch (GoogleAuthException e) {

                }
                return token;
            }

            @Override
            protected void onPostExecute(String token) {
                super.onPostExecute(token);
                Bundle options = new Bundle();
                options.putParcelable("provider", KiiSocialNetworkConnector.Provider.GOOGLEPLUS);
                options.putString("accessToken", token);
                KiiSocialConnect conn = Kii.socialConnect(KiiSocialConnect.SocialNetwork.SOCIALNETWORK_CONNECTOR);

                conn.logIn(getActivity(), options, new KiiSocialCallBack() {
                    @Override
                    public void onLoginCompleted(KiiSocialConnect.SocialNetwork network, KiiUser user, Exception exception) {
                        if (exception != null) {
                            if (exception instanceof CloudExecutionException)
                                showAlert(R.string.operation_failed, KiiCloudUtil.generateAlertMessage((CloudExecutionException) exception), null);
                            else
                                showAlert(R.string.operation_failed, exception.getLocalizedMessage(), null);
                            return;
                        }
                        SharedPreferences pref = getActivity().getSharedPreferences(getString(R.string.save_data_name), Context.MODE_PRIVATE);
                        pref.edit().putString(getString(R.string.save_token), user.getAccessToken()).apply();

                        Fragment fragment = new Container_top();
                        FragmentManager manager = getActivity().getSupportFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();
                        transaction.replace(R.id.fragment_container, fragment, "home");
                        transaction.commit();
                    }
                });
            }
        };
        task.execute(account);
    }

    void showAlert(int titleId, String message, AlertDialogFragment.AlertDialogListener listener) {
        DialogFragment newFragment = AlertDialogFragment.newInstance(titleId, message, listener);
        newFragment.show(getActivity().getFragmentManager(), "dialog");
    }

    KiiUserCallBack callback = new KiiUserCallBack() {
        //ログインが完了した時に自動的に呼び出される。自動ログインの時も呼び出される
        @Override
        public void onLoginCompleted(int token, KiiUser user, Exception e) {
            // setFragmentProgress(View.INVISIBLE);
            if (e == null) {
                SharedPreferences pref = getActivity().getSharedPreferences(getString(R.string.save_data_name), Context.MODE_PRIVATE);
                pref.edit().putString(getString(R.string.save_token), user.getAccessToken()).apply();

                Fragment fragment = new Container_top();
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
    };

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}