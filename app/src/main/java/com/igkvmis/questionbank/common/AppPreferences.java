package com.igkvmis.questionbank.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class AppPreferences {


    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    private static final String APP_SHARED_PREFS = AppPreferences.class.getSimpleName(); //  Name of the file -.xml
    private SharedPreferences _sharedPrefs;
    private SharedPreferences.Editor _prefsEditor;

    private static final String USER_ID = "user_id";
    private static final String USER_TYPE = "user_type";

    private static final String KRY_THEME_STYLE = "theme_style";


    @SuppressLint("CommitPrefEdits")
    public AppPreferences(Context context) {
        this._sharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        this._prefsEditor = _sharedPrefs.edit();

    }

    public void createLogin(String id, String type) {
        _prefsEditor.putString(USER_ID, id);
        _prefsEditor.putString(USER_TYPE, type);
        _prefsEditor.putBoolean(KEY_IS_LOGGED_IN, true);
        _prefsEditor.apply();
    }

    public boolean isLoggedIn() {
        return _sharedPrefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void clearSession() {
        _prefsEditor.clear();
        _prefsEditor.apply();
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> profile = new HashMap<>();
        profile.put("id", _sharedPrefs.getString(USER_ID, null));
        profile.put("type", _sharedPrefs.getString(USER_TYPE, null));
        return profile;
    }
}
