package com.itcast.mobileplayer91.ui.fragment;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore.Video.Media;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.itcast.mobileplayer91.R;
import com.itcast.mobileplayer91.adapter.VideoListAdapter;
import com.itcast.mobileplayer91.base.BaseFragment;
import com.itcast.mobileplayer91.bean.VideoItem;
import com.itcast.mobileplayer91.db.MyAsyncQueryHandler;
import com.itcast.mobileplayer91.ui.activity.VideoPlayerActivity;

import java.util.ArrayList;

/**
 * Created by Ding on 2016/10/10.
 */
public class VideoListFragment extends BaseFragment {

    private ListView listView;
    private VideoListAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_video_list;
    }

    @Override
    protected void initView() {
        listView = (ListView) findViewById(R.id.listview);
    }

    @Override
    protected void initListener() {
        mAdapter = new VideoListAdapter(getActivity(), null);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new OnVideoItemClickListener());
    }

    @Override
    protected void initData() {
        // 查询视频数据
        ContentResolver resolver = getActivity().getContentResolver();
//        Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI, new String[]{Media._ID/*"uid as _id"*/, Media.DURATION, Media.TITLE, Media.SIZE, Media.DATA}, null, null, null);
//        CursorUtils.printCursor(cursor);
//        mAdapter.swapCursor(cursor);

        AsyncQueryHandler asyncQueryHandler = new MyAsyncQueryHandler(resolver);
        // token 可以用于区分不同类型的查询
        // cookie 要使用 cursor数据的对象
        asyncQueryHandler.startQuery( 0, mAdapter ,Media.EXTERNAL_CONTENT_URI, new String[]{Media._ID/*"uid as _id"*/, Media.DURATION, Media.TITLE, Media.SIZE, Media.DATA}, null, null, null);
    }

    @Override
    protected void processClick(View v) {

    }

    private class OnVideoItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // 获取被选中的视频数据
            Cursor cursor = (Cursor) mAdapter.getItem(position);
//            VideoItem item = VideoItem.parserCursor(cursor);
            ArrayList<VideoItem> videoItems = VideoItem.parserListFromCursor(cursor);
            logE("OnVideoItemClickListener.onItemClick,list="+videoItems);

            // 打开播放界面
            Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
//            Intent intent = new Intent(getActivity(), VitamioPlayerActivity.class);
            intent.putExtra("videoItems",videoItems);
            intent.putExtra("position",position);
            startActivity(intent);
        }
    }
}
