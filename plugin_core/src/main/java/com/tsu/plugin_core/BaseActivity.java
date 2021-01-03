package com.tsu.plugin_core;

import android.app.AppComponentFactory;
import android.content.res.Resources;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    @Override
    public Resources getResources() {

        //check plugin
        if(getApplication() != null && getApplication().getResources() != null){
            return getApplication().getResources();
        }
        //normal
        return super.getResources();
    }
}
