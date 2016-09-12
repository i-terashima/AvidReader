package com.gashfara.it.avidreader;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

public class Fragment_login extends Fragment {

    private CallbackManager callbackManager;

    public Fragment_login() {
    }

    public static Fragment_login newInstance() {
        Fragment_login fragment = new Fragment_login();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        FacebookSdk.sdkInitialize(getActivity());

        Button loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("TEST", "LoginManager.getInstance() start");
                LoginManager.getInstance().logInWithReadPermissions(getTargetFragment(), Arrays.asList("public_profile", "email"));    //profileとemailの情報を取得
                Log.d("TEST", "LoginManager.getInstance() end");
            }
        });

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        //ログイン成功
                        Log.d("TEST", "success");
                    }

                    @Override
                    public void onCancel() {
                        //キャンセル
                        Log.d("TEST", "cancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        //ログイン失敗
                        Log.d("TEST", "error");
                        Log.e("TEST", exception.toString());
                    }
                });
        return view;
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        Fragment parent =  this.getParentFragment();
        if (parent != null) {
            parent.startActivityForResult(intent, requestCode);
        } else {
            super.startActivityForResult(intent, requestCode);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("TEST", "onActivityResult ABC");
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}