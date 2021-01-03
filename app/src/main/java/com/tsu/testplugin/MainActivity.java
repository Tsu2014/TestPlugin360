package com.tsu.testplugin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.tsu.plugin_core.HookUtil;
import com.tsu.plugin_core.LoadUtil;
import com.tsu.plugin_core.ProxyActivity;
import com.tsu.plugin_core.proxy.IProxy;
import com.tsu.plugin_core.proxy.InvocationHandler;
import com.tsu.plugin_core.proxy.ProxyTest;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private final String TAG = getClass().getCanonicalName();

    @BindView(R.id.main_textView1)
    TextView textView1;
    @BindView(R.id.main_button1)
    Button button1;
    @BindView(R.id.main_button2)
    Button button2;
    @BindView(R.id.main_button3)
    Button button3;
    @BindView(R.id.main_button4)
    Button button4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //LoadUtil.loadClass(this);
        //ClassLoader classLoader = getClassLoader();
        //textView1.setText(classLoader.getClass().getName());
    }

    @OnClick(R.id.main_button1)
    void onAction1(){
        textView1.setText("Action1 : "+System.currentTimeMillis());
        getPluginClass();
    }

    @OnClick(R.id.main_button2)
    void onAction2(){
        textView1.setText("Action2 : "+System.currentTimeMillis());
        //getPluginClass1();
        //proxyTest();
        //doHook();
        //Intent intent = new Intent(MainActivity.this , TestActivity.class);
        //intent.putExtra("className" , "test");
        //startActivity(intent);
        jumpPlugin("/sdcard/pl.apk");
    }

    @OnClick(R.id.main_button3)
    void onAction3(){
        textView1.setText("Action3 : "+System.currentTimeMillis());

    }

    @OnClick(R.id.main_button4)
    void onAction4(){
        textView1.setText("Action4 : "+System.currentTimeMillis());
    }

    private void getPluginClass(){
        try{
            Class<?> aClass = getClassLoader().loadClass("com.tsu.pluginapk.Test");
            Method getToast = aClass.getDeclaredMethod("getToast" , Context.class);
            getToast.setAccessible(true);
            getToast.invoke(aClass.newInstance() , getApplicationContext());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void getPluginClass1(){
        try{
            Class<?> aClass = getClassLoader().loadClass("com.tsu.testplugin.Test2");
            Method getToast = aClass.getDeclaredMethod("getToast" , Context.class);
            getToast.setAccessible(true);
            getToast.invoke(aClass.newInstance() , getApplicationContext());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void startPlugin(String className){
        if(className==null){
            return ;
        }
        try{
            //startActivity how to do?
            //step 1 . AMS check the Activity has regist manifest?
            //step 2 . AMS send message to ActivityThread create class and start lifecycle.
            Class<?> aClass = getClassLoader().loadClass(className);
            startActivity(new Intent(this , aClass));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void jumpPlugin(String akpPath){
        LoadUtil loadUtil = new LoadUtil();
        loadUtil.loadClass(getApplicationContext() , akpPath);
        Resources resources = loadUtil.loadPluginResource(getApplicationContext());
        MyApplication myApplication =(MyApplication)getApplication();
        myApplication.setResources(resources);

        //jump first Activity
        ActivityInfo[] activities = loadUtil.getPackageInfo().activities;
        String activityName = activities[0].name;
        startPlugin(activityName);
    }

    private void proxyTest(){
        ProxyTest proxyTest = new ProxyTest();
        IProxy proxy = (IProxy) Proxy.newProxyInstance(getClassLoader() , new Class[]{IProxy.class} , new InvocationHandler(proxyTest));
        proxy.getLog("I'm in MainActivity");
    }

    private void doHook(){
        try {
            new HookUtil(this.getApplicationContext() , ProxyActivity.class).hookStartActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}