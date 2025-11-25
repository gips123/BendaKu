package com.example.bendaku.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {
    private static final String PREFS_NAME = "bendaku_prefs";
    private static final String KEY_TOKEN = "jwt_token";
    private static SharedPreferences prefs;
    
    public static void init(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    public static void saveToken(String token) {
        if (prefs != null) {
            prefs.edit().putString(KEY_TOKEN, token).apply();
        }
    }
    
    public static String getToken() {
        if (prefs != null) {
            return prefs.getString(KEY_TOKEN, null);
        }
        return null;
    }
    
    public static void clearToken() {
        if (prefs != null) {
            prefs.edit().remove(KEY_TOKEN).apply();
        }
    }
    
    public static boolean isLoggedIn() {
        return getToken() != null && !getToken().isEmpty();
    }
}

