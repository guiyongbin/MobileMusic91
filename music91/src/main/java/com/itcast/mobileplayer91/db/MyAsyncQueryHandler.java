package com.itcast.mobileplayer91.db;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;

import com.itcast.mobileplayer91.utils.LogUtils;

/**
 * Created by Ding on 2016/10/10.
 */
public class MyAsyncQueryHandler extends AsyncQueryHandler {

    private static final String TAG = "MyAsyncQueryHandler";

    public MyAsyncQueryHandler(ContentResolver cr) {
        super(cr);
    }

    @Override
    // 当子线程查询结束之后，会调用这个方法将 cursor传递过来
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        LogUtils.e(TAG,"MyAsyncQueryHandler.onQueryComplete,");
        CursorAdapter adapter = (CursorAdapter) cookie;
        adapter.swapCursor(cursor);
    }
}
