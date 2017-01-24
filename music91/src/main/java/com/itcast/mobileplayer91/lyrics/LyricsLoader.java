package com.itcast.mobileplayer91.lyrics;

import android.os.Environment;

import java.io.File;

/**
 * Created by Ding on 2016/10/14.
 */
public class LyricsLoader {

    private static final File ROOT = new File(Environment.getExternalStorageDirectory(),"Download/audio");

    // 根据指定的歌曲名，加载到对应的歌词文件
    public static File loadFile(String title){
        File file = new File(ROOT,title+".lrc");
        if (file.exists()){
            return  file;
        }

        file = new File(ROOT, title+".txt");
        if (file.exists()){
            return file;
        }

        // 到专门存放歌词的文件夹查找
        //...

        // 连接服务器下载歌词文件
        // ...

        return null;
    }
}
