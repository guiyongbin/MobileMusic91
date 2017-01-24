package com.itcast.mobileplayer91.bean;

import android.database.Cursor;
import android.provider.MediaStore;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Ding on 2016/10/10.
 */
public class VideoItem implements Serializable{

    private String title;
    private int duration;
    private long size;
    private String path;

    // 解析 cursor 当前行的数据
    public static VideoItem parserCursor(Cursor cursor){
        VideoItem  item = new VideoItem();
        // 健壮性检查
        if (cursor==null||cursor.getCount()==0){
            return item;
        }

        item.title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
        item.duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
        item.size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
        item.path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));

        return item;
    }

    public static ArrayList<VideoItem> parserListFromCursor(Cursor cursor) {
        ArrayList<VideoItem> videoItems = new ArrayList<>();
        // 健壮性检查
        if (cursor==null||cursor.getCount()==0){
            return videoItems;
        }

        cursor.moveToPosition(-1);// 移动到列表最前面再解析
        while (cursor.moveToNext()){
            VideoItem videoItem = parserCursor(cursor);
            videoItems.add(videoItem);
        }

        return videoItems;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "VideoItem{" +
                "title='" + title + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", path='" + path + '\'' +
                '}';
    }
}
