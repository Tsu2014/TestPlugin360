package com.tsu.plugin_core.proxy;

import android.util.Log;

public class ProxyTest implements IProxy{
    private final String TAG = "ProxyTest";
    @Override
    public void getLog(String messager) {
        Log.d(TAG , messager);
    }
}
