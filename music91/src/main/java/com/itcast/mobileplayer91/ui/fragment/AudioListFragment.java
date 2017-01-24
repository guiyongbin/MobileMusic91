package com.itcast.mobileplayer91.ui.fragment;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore.Audio.Media;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.itcast.mobileplayer91.R;
import com.itcast.mobileplayer91.adapter.AudioListAdapter;
import com.itcast.mobileplayer91.base.BaseFragment;
import com.itcast.mobileplayer91.bean.AudioItem;
import com.itcast.mobileplayer91.db.MyAsyncQueryHandler;
import com.itcast.mobileplayer91.ui.activity.AudioPlayerActivity;

import java.util.ArrayList;

/**
 * Created by Ding on 2016/10/10.
 */
public class AudioListFragment extends BaseFragment {

    private ListView listView;
    private AudioListAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_audio_list;
    }

    @Override
    protected void initView() {
        listView = (ListView) findViewById(R.id.listview);
    }

    @Override
    protected void initListener() {

        mAdapter = new AudioListAdapter(getActivity(), null);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new OnAudioItemClickListener());
    }

    @Override
    protected void initData() {
        // 获取音频数据
        ContentResolver resolver = getActivity().getContentResolver();
//        Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI, new String[]{Media.DATA, Media.DISPLAY_NAME, Media.ARTIST, Media._ID}, null, null, null);
//        CursorUtils.printCursor(cursor);
//        mAdapter.swapCursor(cursor);
        // 子线程查询数据库
        AsyncQueryHandler asyncQueryHandler = new MyAsyncQueryHandler(resolver);
        asyncQueryHandler.startQuery(0, mAdapter,Media.EXTERNAL_CONTENT_URI, new String[]{Media.DATA, Media.DISPLAY_NAME, Media.ARTIST, Media._ID}, null, null, null);
    }

    @Override
    protected void processClick(View v) {

    }

    private class OnAudioItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // 获取数据
            Cursor cursor = (Cursor) mAdapter.getItem(position);
            ArrayList<AudioItem> audioItems = AudioItem.parserListFromCursor(cursor);

            // 跳转到播放界面
            Intent intent = new Intent(getActivity(), AudioPlayerActivity.class);
            intent.putExtra("audioItems",audioItems);
            intent.putExtra("position",position);
            startActivity(intent);
        }
    }
}
