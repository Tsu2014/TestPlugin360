package com.tsu.pluginapk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.tsu.plugin_core.BaseActivity;

public class PluginMainActivity extends BaseActivity {

    private final static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plugin_main);

        ClassLoader classLoader = getClassLoader();
        Log.d(TAG , "MainActivity onCreate classLoader : "+classLoader.getClass().getName());
    }
}