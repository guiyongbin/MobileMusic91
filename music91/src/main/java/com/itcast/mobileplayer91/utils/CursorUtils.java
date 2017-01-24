package com.itcast.mobileplayer91.utils;

import android.database.Cursor;

/**
 * Created by Ding on 2016/10/10.
 */
public class CursorUtils {

    private static final String TAG = "CursorUtils";

    // 打印 cursor 里的所有数据
    public static void printCursor(Cursor cursor){

        LogUtils.e(TAG,"CursorUtils.printCursor,查询到的数据个数为："+cursor.getCount());
        while (cursor.moveToNext()){
            LogUtils.e(TAG,"CursorUtils.printCursor,===================================");
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                LogUtils.e(TAG,"CursorUtils.printCursor,name="+cursor.getColumnName(i)+";value="+cursor.getString(i));
            }
        }
    }
}
