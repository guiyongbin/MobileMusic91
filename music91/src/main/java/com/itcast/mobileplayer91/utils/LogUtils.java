package com.itcast.mobileplayer91.utils;

import android.util.Log;

/**
 * Created by Ding on 2016/10/10.
 */
public class LogUtils {

    private static final boolean ENABLE = true;

    /**打印debug等级的log*/
    public static void d(String tag,String msg){
        if (ENABLE){
            Log.d("itcast_"+tag,msg);
        }
    }

    /**打印error等级的log*/
    public static void e(String tag,String msg){
        if (ENABLE){
            Log.e("itcast_"+tag,msg);
        }
    }

    /**打印error等级的log*/
    public static void e(Class cls,String msg){
        if (ENABLE){
            Log.e("itcast_"+cls.getSimpleName(),msg);
        }
    }
}
