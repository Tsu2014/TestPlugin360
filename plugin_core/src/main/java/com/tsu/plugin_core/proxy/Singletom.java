package com.tsu.plugin_core.proxy;

public abstract class Singletom<T> {

    private T mInstance;
    protected abstract T create();

    public final T get(){
        synchronized (this){
            if(mInstance == null){
                mInstance = create();
            }
            return mInstance;
        }
    }

}
