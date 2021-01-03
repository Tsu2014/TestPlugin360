package com.tsu.plugin_core;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

public class LoadUtil {

    private String apkPath = "/sdcard/pl.apk";
    private PackageInfo packageInfo;

    public void loadClass(Context context , String apkPath) {
        this.apkPath = apkPath;
        if (context == null) {
            return;
        }
        try {
            //setp 1 . get dexElements of main
            PathClassLoader classLoader = (PathClassLoader) context.getClassLoader();
            //get BaseDexClassLoader
            Class<?> baseDexClassLoaderClazz = Class.forName("dalvik.system.BaseDexClassLoader");
            //get pathList of BaseDexClassLoader
            Field pathListField = baseDexClassLoaderClazz.getDeclaredField("pathList");
            pathListField.setAccessible(true);
            //get the value of pathList in main ClassLoader
            Object pathListValue = pathListField.get(classLoader);

            //get dexElements in the pathList
            Field dexElementsField = pathListValue.getClass().getDeclaredField("dexElements");
            dexElementsField.setAccessible(true);
            //get value of dexElements in main classLoader
            Object dexElementsValue = dexElementsField.get(pathListValue);


            //step 2 . load plugin , then get dexElements by plugin's classLoader
            DexClassLoader dexClassLoader = new DexClassLoader(apkPath, context.getCacheDir().getAbsolutePath(), null, context.getClassLoader());
            //get plugin's pathList
            Object pluginPathListValue = pathListField.get(dexClassLoader);
            //get plugin's dexElements
            Object pluginDexElementsValue = dexElementsField.get(pluginPathListValue);

            //step 3 . to array
            //get the length by two array
            int currLength = Array.getLength(dexElementsValue);
            int pluginLength = Array.getLength(pluginDexElementsValue);
            int newLength = currLength + pluginLength;

            //get the type of Array
            Class<?> componentType = dexElementsValue.getClass().getComponentType();
            //create new Array
            Object newArray = Array.newInstance(componentType, newLength);
            System.arraycopy(dexElementsValue, 0, newArray, 0, currLength);
            System.arraycopy(pluginDexElementsValue, 0, newArray, currLength, pluginLength);


            //set new Array to main ClassLoader
            dexElementsField.set(pathListValue, newArray);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * get plugin resources
     * create object of Resources , manage the Resources of plugin
     *
     * @return
     */
    public Resources loadPluginResource(Context context) {
        AssetManager assets = null;
        Resources resources = null;
        try {
            //get plugin packageInfo class
            //get PackageManager
            PackageManager packageManager = context.getPackageManager();
            packageInfo = packageManager.getPackageArchiveInfo(apkPath , PackageManager.GET_ACTIVITIES);

            assets = AssetManager.class.newInstance();
            //through fanshe ,get addAssetPath
            Method addAssetPath = assets.getClass().getDeclaredMethod("addAssetPath", String.class);
            addAssetPath.setAccessible(true);
            addAssetPath.invoke(assets, apkPath);
            resources = new Resources(assets, context.getResources().getDisplayMetrics(), context.getResources().getConfiguration());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return resources;
    }

    public PackageInfo getPackageInfo(){
        return this.packageInfo;
    }

}
