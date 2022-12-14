package com.laba.user.data;

import android.util.Log;

import androidx.annotation.Nullable;

import java.util.HashMap;

public class DebugHashMap<K, V> extends HashMap<K, V> {

    private static final String TAG = "DebugHashMap";

    @Nullable
    @Override
    public V put(K key, V value) {
        Log.e(TAG, "put: [" + key + ", " + value + "]");
        return super.put(key, value);
    }
}
