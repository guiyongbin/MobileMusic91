package com.itcast.mobileplayer91.bean;

import android.database.Cursor;
import android.provider.MediaStore.Audio.Media;

import com.itcast.mobileplayer91.utils.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Ding on 2016/10/10.
 */
public class AudioItem implements Serializable {

    private String title;
    private String artist;
    private String path;

    // 解析 cursor 当前行的数据
    public static AudioItem parserCursor(Cursor cursor) {
        AudioItem item = new AudioItem();
        // 健壮性检查
        if (cursor == null || cursor.getCount() == 0) {
            return item;
        }

        item.title = cursor.getString(cursor.getColumnIndex(Media.DISPLAY_NAME));
        item.title = StringUtils.formatTitle(item.title);
        item.artist = cursor.getString(cursor.getColumnIndex(Media.ARTIST));
        item.path = cursor.getString(cursor.getColumnIndex(Media.DATA));

        return item;
    }

    public static ArrayList<AudioItem> parserListFromCursor(Cursor cursor) {
        ArrayList<AudioItem> audioItems = new ArrayList<>();
        // 健壮性检查
        if (cursor == null || cursor.getCount() == 0) {
            return audioItems;
        }

        cursor.moveToPosition(-1);// 移动到列表最前面再解析
        while (cursor.moveToNext()) {
            AudioItem videoItem = parserCursor(cursor);
            audioItems.add(videoItem);
        }

        return audioItems;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "AudioItem{" +
                "title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
