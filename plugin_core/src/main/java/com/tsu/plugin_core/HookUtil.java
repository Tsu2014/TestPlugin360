package com.tsu.plugin_core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * start activity that didn't regist by hook
 * 1 . Reflect AMS instance ,then create object of Dynamic proxy
 */
public class HookUtil {
    private static final String TAG = "HookUtil";
    public static final String EXTRA_ORIGIN_INTENT = "EXTRA_ORIGIN_INTENT";
    private Context context;

    //the proxy of Activity
    private Class<? extends Activity> mProxyActivityClass;

    public HookUtil(Context context, Class<? extends Activity> mProxyActivityClass) {
        this.context = context;
        this.mProxyActivityClass = mProxyActivityClass;
    }

    /**
     * Hook AMS
     * 1 . get AMS instance
     * 2 . create proxy objcet of the AMS
     * 3 . intercept the method of startActivity
     */
    public void hookStartActivity() throws Exception {
        //first get ActivityManagerNative object
        Class<?> amnClass = Class.forName("android.app.ActivityManagerNative");

        //get ActivityManagerNative Default variable
        Field getDefault = amnClass.getDeclaredField("gDefault");
        getDefault.setAccessible(true);
        //get Default static variable value
        Object getDefaultValue = getDefault.get(null);

        //get object of the SingleTon class
        Class<?> singleTonClass = Class.forName("android.util.Singleton");
        //get mInstance variable
        Field mInstance = singleTonClass.getDeclaredField("mInstance");
        mInstance.setAccessible(true);
        //get AMS
        Object amsObject = mInstance.get(getDefaultValue);


        //create proxy object of AMS
        //get interface Class
        Class<?> IActivityManagerClass = Class.forName("android.app.IActivityManager");
        Object amsProxy = Proxy.newProxyInstance(HookUtil.class.getClassLoader(), new Class[]{IActivityManagerClass}, new StartActivityInvocationHandler(amsObject));

        //set proxy to ams by fanshe
        mInstance.set(getDefaultValue, amsProxy);
    }

    public class StartActivityInvocationHandler implements InvocationHandler {
        //proxy object
        private Object ams;

        public StartActivityInvocationHandler(Object ams) {
            this.ams = ams;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            //Log.d(TAG , "method name is : "+method.getName());
            if (method.getName().equals("startActivity")) {
                Log.d(TAG, "invoke startActivity");
                processStartActivity(proxy, method, args);
            }


            return method.invoke(this.ams, args);
        }
    }

    private void processStartActivity(Object proxy, Method method, Object[] args) {
        Intent oldIntent = null;
        int position = 0;
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof Intent) {
                position = i;
                oldIntent = (Intent) args[i];
                break;
            }
        }

        if (oldIntent == null) {
            return;
        }

        Intent newIntent = new Intent(context, mProxyActivityClass);
        newIntent.putExtra(EXTRA_ORIGIN_INTENT, oldIntent);
        args[position] = newIntent;
    }

    public void hookLaunchActivity() throws Exception {
        Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
        //get instance of this class that CurrentActivityThread;
        Field sCurrentActivityThread = activityThreadClass.getDeclaredField("sCurrentActivityThread");
        sCurrentActivityThread.setAccessible(true);
        //get instance of ActivityThread
        Object activityThreadValue = sCurrentActivityThread.get(null);

        //get mH variable
        Field mHField = activityThreadClass.getDeclaredField("mH");
        mHField.setAccessible(true);
        //get value
        Object mHValue = mHField.get(activityThreadValue);

        //get handler that ActivityThread send message
        Class<?> handlerClass = Class.forName("android.os.Handler");
        Field mCallBackField = handlerClass.getDeclaredField("mCallback");
        mCallBackField.setAccessible(true);
        //reSet
        mCallBackField.set(mHValue , new HandlerCallBack());

    }

    private class HandlerCallBack implements Handler.Callback {

        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == 100) {
                try {
                    handlerLaunchActivity(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        private void handlerLaunchActivity(Message message) throws Exception{
            //get AcitivtyClientRecord
            Object r = message.obj;     //just ActivityClientRecord
            Field intentField = r.getClass().getDeclaredField("intent");
            intentField.setAccessible(true);
            //get intent variable by ActivityClientRecord
            Intent newIntent = (Intent)intentField.get(r);

            //get real intent by newIntent
            Intent oldIntent = newIntent.getParcelableExtra(EXTRA_ORIGIN_INTENT);
            //sometime oldIntent is null such as on init activity
            if(oldIntent != null){
                //reSet oldIntent to r
                intentField.set(r , oldIntent);
            }
        }
    }
}
