package com.tsu.testplugin;

import android.app.Application;
import android.content.res.Resources;

import com.tsu.plugin_core.HookUtil;
import com.tsu.plugin_core.LoadUtil;
import com.tsu.plugin_core.ProxyActivity;

public class MyApplication extends Application {

    private Resources resources;

    @Override
    public void onCreate() {
        super.onCreate();
        HookUtil hookUtil = new HookUtil(this , ProxyActivity.class);
        try{
            hookUtil.hookStartActivity();
            hookUtil.hookLaunchActivity();

            //loding plugin getResource
            //resources = LoadUtil.loadPluginResource(this);
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public Resources getResources() {
        //if plugin loaded resource ,return resource of plugin , else return main resources
        return resources == null?super.getResources():resources;
    }

    public void setResources(Resources resources){
        this.resources = resources;
    }
}
