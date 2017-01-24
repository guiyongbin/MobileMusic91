package com.itcast.mobileplayer91.ui.activity;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.itcast.mobileplayer91.R;
import com.itcast.mobileplayer91.adapter.MainPagerAdapter;
import com.itcast.mobileplayer91.base.BaseActivity;
import com.itcast.mobileplayer91.ui.fragment.AudioListFragment;
import com.itcast.mobileplayer91.ui.fragment.VideoListFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private TextView tv_video;
    private TextView tv_audio;
    private View indicate;
    private ViewPager viewPager;
    private List<Fragment> fragmentList;
    private MainPagerAdapter mAdapter;

    /**
     * 返回当前 Activity 使用的布局id
     */
    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    /**
     * 只允许执行 findViewById 操作
     */
    @Override
    protected void initView() {
        tv_video = (TextView) findViewById(R.id.main_tv_video);
        tv_audio = (TextView) findViewById(R.id.main_tv_audio);
        indicate = findViewById(R.id.main_indicate);
        viewPager = (ViewPager) findViewById(R.id.main_viewpager);
    }

    /**
     * 执行注册监听、Adapter、广播，等解耦的操作
     */
    @Override
    protected void initListener() {
        tv_video.setOnClickListener(this);
        tv_audio.setOnClickListener(this);

        fragmentList = new ArrayList<>();
        mAdapter = new MainPagerAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(mAdapter);

        viewPager.setOnPageChangeListener(new OnMainPageChangeListener());
    }

    /**
     * 获取数据，初始化界面
     */
    @Override
    protected void initData() {
        fragmentList.add(new VideoListFragment());
        fragmentList.add(new AudioListFragment());
        mAdapter.notifyDataSetChanged();

        // 高亮视频标签
        updateTabs(0);

        // 初始化指示器宽度
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        indicate.getLayoutParams().width = screenWidth / fragmentList.size();
        indicate.requestLayout();
    }

    /**
     * 在 BaseActiviy 没有处理的点击事件，都在这个方法处理
     *
     * @param v
     */
    @Override
    protected void processClick(View v) {
        switch (v.getId()){
            case R.id.main_tv_video:
                viewPager.setCurrentItem(0);
                break;
            case R.id.main_tv_audio:
                viewPager.setCurrentItem(1);
                break;
        }
    }

    // 根据选择的position位置，修改所有标题的高亮和缩放状态
    private void updateTabs(int position) {
        updateTab(position, 0, tv_video);
        updateTab(position, 1, tv_audio);
    }

    // 根据 position 是否等于要比较的 position ，修改tab 的颜色和大小
    private void updateTab(int position, int tabPosition, TextView tab) {
        if (position == tabPosition){
            tab.setTextColor(getResources().getColor(R.color.green));
            ViewCompat.animate(tab).scaleX(1.2f).scaleY(1.2f).setDuration(500).start();
        }else {
            tab.setTextColor(getResources().getColor(R.color.halfwhite));
            ViewCompat.animate(tab).scaleX(0.9f).scaleY(0.9f).setDuration(500).start();
        }
    }

    private class OnMainPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        // 在界面滚动时被调用
        // position 当前选中的页面
        // positionOffset 当前页面的偏移百分比
        // positionOffsetPixels 当前页面偏移的像素值
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            logE("OnMainPageChangeListener.onPageScrolled,position="+position+";positionOffset="+positionOffset+":positionOffsetPixels="+positionOffsetPixels);
//            指示器移动到的位置 = 起始位置 + 偏移位置
//            起始位置 = position * 指示器宽度
//            偏移位置 = 屏幕偏移的百分比 * 指示器宽度

            // 偏移位置
            float offsetX = positionOffset * indicate.getWidth();
            // 起始位置
            int startX = position * indicate.getWidth();
            // 指示器移动到的位置
            float translationX = startX + offsetX;

            // 移动指示器
            ViewCompat.setTranslationX(indicate, translationX);
        }

        @Override
        // 当选中的界面发生变化时被调用
        public void onPageSelected(int position) {
//            logE("OnMainPageChangeListener.onPageSelected,position="+position);

            // 将被选中的标题修改为高亮颜色并放大，没选中的则变暗并缩小
            updateTabs(position);
        }

        @Override
        // 当界面滚动状态改变时被调用
        public void onPageScrollStateChanged(int state) {
//            logE("OnMainPageChangeListener.onPageScrollStateChanged,state="+state);
        }
    }
}
