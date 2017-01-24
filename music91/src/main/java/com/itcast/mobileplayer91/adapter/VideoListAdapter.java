package com.itcast.mobileplayer91.adapter;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Video.Media;
import android.support.v4.widget.CursorAdapter;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itcast.mobileplayer91.R;
import com.itcast.mobileplayer91.utils.StringUtils;

/**
 * Created by Ding on 2016/10/10.
 */
public class VideoListAdapter extends CursorAdapter {

    public VideoListAdapter(Context context, Cursor c) {
        super(context, c);
    }

    public VideoListAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    public VideoListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    // 创建新的View，并初始化ViewHolder
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = View.inflate(context, R.layout.video_item,null);
        ViewHolder holder = new ViewHolder();

        holder.tv_title = (TextView) view.findViewById(R.id.video_item_tv_title);
        holder.tv_duration = (TextView) view.findViewById(R.id.video_item_tv_duration);
        holder.tv_size = (TextView) view.findViewById(R.id.video_item_tv_size);

        view.setTag(holder);
        return view;
    }

    @Override
    // 填充界面。cursor已经被移动到要获取数据的行
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        // 获取数据
        String title = cursor.getString(cursor.getColumnIndex(Media.TITLE));
        int duration = cursor.getInt(cursor.getColumnIndex(Media.DURATION));
        long size = cursor.getLong(cursor.getColumnIndex(Media.SIZE));

        // 填充内容
        holder.tv_title.setText(title);
        holder.tv_duration.setText(StringUtils.formatTime(duration));
        holder.tv_size.setText(Formatter.formatFileSize(context,size));;

    }

    private class ViewHolder{
        TextView tv_title,tv_duration,tv_size;
    }
}
