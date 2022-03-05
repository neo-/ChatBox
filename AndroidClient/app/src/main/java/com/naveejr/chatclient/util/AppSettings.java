package com.naveejr.chatclient.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.function.Predicate;

public class AppSettings {

    private static final String HOST_NAME = "KEY_HOSTNAME";

    private SharedPreferences sharedPreferences;

    private static AppSettings instance;

    private AppSettings() {

    }

    public static void initialize(Context context) {
        if (instance == null) {
            instance = new AppSettings();
            instance.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
    }

    public static String getHostname() {
        return instance.sharedPreferences.getString(HOST_NAME, "https://localhost");
    }

    public static void setHostname(String hostname){
        instance.sharedPreferences.edit().putString(HOST_NAME, hostname).apply();
    }

}
