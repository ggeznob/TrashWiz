package com.example.trashwiz.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;

public class ShareUtils {
    public static final String KEY_INIT = "keyInit";

    public static void add(Context context, String key,String value) {
        context.getSharedPreferences("data", MODE_PRIVATE).edit().putString(key,value).commit();
    }

    public static String get(Context context, String key) {
        String s = context.getSharedPreferences("data", MODE_PRIVATE).getString(key, "");
        return  s;
    }
}
