package com.itcast.mobileplayer91.adapter;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Audio.Media;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itcast.mobileplayer91.R;
import com.itcast.mobileplayer91.utils.StringUtils;

/**
 * Created by Ding on 2016/10/10.
 */
public class AudioListAdapter extends CursorAdapter {

    public AudioListAdapter(Context context, Cursor c) {
        super(context, c);
    }

    public AudioListAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    public AudioListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    // 创建新的View，并初始化ViewHolder
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = View.inflate(context, R.layout.audio_item,null);
        ViewHolder holder = new ViewHolder();

        holder.tv_title = (TextView) view.findViewById(R.id.audio_item_tv_title);
        holder.tv_artist = (TextView) view.findViewById(R.id.audio_item_tv_artist);

        view.setTag(holder);
        return view;
    }

    @Override
    // 填充界面。cursor已经被移动到要获取数据的行
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        // 获取数据
        String title = cursor.getString(cursor.getColumnIndex(Media.DISPLAY_NAME));
        title = StringUtils.formatTitle(title);
        String artist = cursor.getString(cursor.getColumnIndex(Media.ARTIST));

        // 填充内容
        holder.tv_title.setText(title);
        holder.tv_artist.setText(artist);
    }

    private class ViewHolder{
        TextView tv_title,tv_artist;
    }
}
