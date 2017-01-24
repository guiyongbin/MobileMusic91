package com.itcast.mobileplayer91.utils;

import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ding on 2016/10/10.
 */
public class StringUtils {

    // 格式化时间，01：02：03 或者 02：03
    public static CharSequence formatTime(int duration){

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.add(Calendar.MILLISECOND,duration);
        int hour = calendar.get(Calendar.HOUR);


        if (hour < 1 ){
            // 不足一小时 02：03
            return DateFormat.format("mm:ss",duration);
        }else{
            // 超过一小时 01：02：03
            return DateFormat.format("hh:mm:ss",duration);
        }
    }

    // 将系统时间转换为 01:02:03
    public static String formatSystemTime(){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }

    // 将 beijingbeijing.mp3 --> beijinngbejing
    public static String formatTitle(String title){
        return title.substring(0,title.indexOf("."));
    }
}
