package com.naveejr.chatclient;

import android.app.Application;

import com.naveejr.chatclient.util.AppSettings;

public class ChatClientApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppSettings.initialize(ChatClientApplication.this);
    }
}
