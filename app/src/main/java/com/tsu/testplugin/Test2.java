package com.tsu.testplugin;

import android.content.Context;
import android.widget.Toast;

public class Test2 {

    public void getToast(Context context){
        Toast.makeText(context , "I'm plugin's method ,I had be take " , Toast.LENGTH_SHORT).show();
    }

}
