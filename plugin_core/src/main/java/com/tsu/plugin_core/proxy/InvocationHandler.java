package com.tsu.plugin_core.proxy;

import android.util.Log;

import java.lang.reflect.Method;

public class InvocationHandler implements java.lang.reflect.InvocationHandler {

    private static final String TAG = "InvocationHandler";
    private IProxy proxy;
    public InvocationHandler(IProxy proxy){
        this.proxy = proxy;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        for(Object object : args){
            Log.d(TAG , "object : "+object.toString());
        }

        String message = "this is InvocationHandler";
        args[0] = message;
        Object invoke = method.invoke(this.proxy , args);
        return invoke;
    }
}
