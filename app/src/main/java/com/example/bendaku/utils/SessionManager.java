package com.example.bendaku.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.bendaku.model.User;
import com.google.gson.Gson;

public class SessionManager {
    private static final String PREF_NAME = "BendaKuSession";
    private static final String KEY_USER = "user";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_JWT_TOKEN = "jwt_token";
    private static final String KEY_USER_ID = "user_id";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;
    private Gson gson;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
        gson = new Gson();
    }

    public void createSession(User user) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER, gson.toJson(user));
        editor.commit();
    }

    public void saveJwtToken(String jwt, Integer userId) {
        editor.putString(KEY_JWT_TOKEN, jwt);
        if (userId != null) {
            editor.putInt(KEY_USER_ID, userId);
        }
        editor.commit();
    }

    public String getJwtToken() {
        return pref.getString(KEY_JWT_TOKEN, null);
    }

    public Integer getUserId() {
        if (pref.contains(KEY_USER_ID)) {
            return pref.getInt(KEY_USER_ID, -1);
        }
        return null;
    }

    public User getUser() {
        String userJson = pref.getString(KEY_USER, null);
        if (userJson != null) {
            return gson.fromJson(userJson, User.class);
        }
        return null;
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false) && getJwtToken() != null;
    }

    public void logout() {
        editor.clear();
        editor.commit();
    }
}
