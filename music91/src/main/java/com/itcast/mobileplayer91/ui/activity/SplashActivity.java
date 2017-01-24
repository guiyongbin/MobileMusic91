package com.itcast.mobileplayer91.ui.activity;

import android.content.Intent;
import android.os.Handler;
import android.view.View;

import com.itcast.mobileplayer91.R;
import com.itcast.mobileplayer91.base.BaseActivity;

public class SplashActivity extends BaseActivity {
    /**
     * 返回当前 Activity 使用的布局id
     */
    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    /**
     * 只允许执行 findViewById 操作
     */
    @Override
    protected void initView() {
    }

    /**
     * 执行注册监听、Adapter、广播，等解耦的操作
     */
    @Override
    protected void initListener() {
    }

    /**
     * 获取数据，初始化界面
     */
    @Override
    protected void initData() {
    }

    /**
     * 在 BaseActiviy 没有处理的点击事件，都在这个方法处理
     *
     * @param v
     */
    @Override
    protected void processClick(View v) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 延迟两秒之后跳转到主界面
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startMainActivity();
            }
        }, 2000);
    }

    private void startMainActivity() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);

        finish();
    }
}
