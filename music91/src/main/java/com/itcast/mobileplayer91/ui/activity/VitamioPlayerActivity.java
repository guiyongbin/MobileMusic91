//package com.itcast.mobileplayer91.ui.activity;
//
//import android.app.AlertDialog;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.media.AudioManager;
//import android.net.Uri;
//import android.os.BatteryManager;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v4.view.ViewCompat;
//import android.view.GestureDetector;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewTreeObserver;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ProgressBar;
//import android.widget.SeekBar;
//import android.widget.TextView;
//
//import com.itcast.mobileplayer91.R;
//import com.itcast.mobileplayer91.base.BaseActivity;
//import com.itcast.mobileplayer91.bean.VideoItem;
//import com.itcast.mobileplayer91.utils.StringUtils;
//
//import java.util.ArrayList;
//
//import io.vov.vitamio.MediaPlayer;
//import io.vov.vitamio.Vitamio;
//import io.vov.vitamio.widget.VideoView;
//
//public class VitamioPlayerActivity extends BaseActivity {
//
//    private static final int MSG_UPDATE_SYSTEM_TIME = 0;
//    private static final int MSG_UPDATE_POSITION = 1;
//    private static final int MSG_HIDE_CONTROLLOR = 2;
//
//    private Handler mHandler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what){
//                case MSG_UPDATE_SYSTEM_TIME:
//                    startUpdateSystemTime();
//                    break;
//                case MSG_UPDATE_POSITION:
//                    startUpdatePosition();
//                    break;
//                case MSG_HIDE_CONTROLLOR:
//                    hideControllor();
//                    break;
//            }
//        }
//    };
//
//    private VideoView videoView;
//    private ImageView iv_pause;
//    private TextView tv_title;
//    private VideoReceiver videoReceiver;
//    private ImageView iv_battery;
//    private TextView tv_system_time;
//    private ImageView iv_mute;
//    private SeekBar sk_volume;
//    private AudioManager mAudioManager;
//    private int mCurrentVolume;
//    private float mStartY;
//    private int mStartVolume;
//    private View alpha_cover;
//    private float mStartAlpha;
//    private TextView tv_position;
//    private SeekBar sk_position;
//    private TextView tv_duration;
//    private ImageView iv_pre;
//    private ImageView iv_next;
//    private ArrayList<VideoItem> mVideoItems;
//    private int mPosition;
//    private LinearLayout ll_top;
//    private LinearLayout ll_bottom;
//    private GestureDetector gestureDetector;
//
//    // 如果为true，则说明面板是显示状态
//    private boolean isContollorShowing = false;
//    private ImageView iv_fullscreen;
//    private LinearLayout ll_loading_cover;
//    private ProgressBar pb_buffering;
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_vitamio_player;
//    }
//
//    @Override
//    protected void initView() {
//        videoView = (VideoView) findViewById(R.id.videoview);
//        alpha_cover = findViewById(R.id.video_alpha_cover);
//        ll_top = (LinearLayout) findViewById(R.id.video_ll_top);
//        ll_bottom = (LinearLayout) findViewById(R.id.video_ll_bottom);
//        ll_loading_cover = (LinearLayout) findViewById(R.id.video_ll_loading_cover);
//        pb_buffering = (ProgressBar) findViewById(R.id.video_pb_buffering);
//
//        // 顶部面板
//        tv_title = (TextView) findViewById(R.id.video_tv_title);
//        iv_battery = (ImageView) findViewById(R.id.video_iv_battery);
//        tv_system_time = (TextView) findViewById(R.id.video_tv_system_time);
//        iv_mute = (ImageView) findViewById(R.id.video_iv_mute);
//        sk_volume = (SeekBar) findViewById(R.id.video_sk_volume);
//
//        // 底部面板
//        iv_pause = (ImageView) findViewById(R.id.video_iv_pause);
//        tv_position = (TextView) findViewById(R.id.video_tv_position);
//        sk_position = (SeekBar) findViewById(R.id.video_sk_position);
//        tv_duration = (TextView) findViewById(R.id.video_tv_duration);
//        iv_pre = (ImageView) findViewById(R.id.video_iv_pre);
//        iv_next = (ImageView) findViewById(R.id.video_iv_next);
//        iv_fullscreen = (ImageView) findViewById(R.id.video_iv_fullscreen);
//    }
//
//    @Override
//    protected void initListener() {
//
//        // 顶部面板
//        iv_mute.setOnClickListener(this);
//
//        // 底部面板
//        iv_pause.setOnClickListener(this);
//        iv_pre.setOnClickListener(this);
//        iv_next.setOnClickListener(this);
//        iv_fullscreen.setOnClickListener(this);
//
//        // 进度条监听
//        OnVideoSeekBarChangeListener onSeekBarChangeListener = new OnVideoSeekBarChangeListener();
//        sk_volume.setOnSeekBarChangeListener(onSeekBarChangeListener);
//        sk_position.setOnSeekBarChangeListener(onSeekBarChangeListener);
//
//        // 注册手势监听
//        gestureDetector = new GestureDetector(this, new OnVideoGestureListener());
//
//        // 视频相关的监听
//        videoView.setOnPreparedListener(new OnVideoPreparedListener());
//        videoView.setOnCompletionListener(new OnVideoCompletionListener());
//        videoView.setOnBufferingUpdateListener(new OnVideoBufferingUpdateListener());
//        videoView.setOnInfoListener(new OnVideoInfoListener());
//        videoView.setOnErrorListener(new OnVideoErrorListener());
//
//        // 注册广播
//        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
//        videoReceiver = new VideoReceiver();
//        registerReceiver(videoReceiver, filter);
//    }
//
//    @Override
//    protected void initData() {
//
//        // 初始化C库
//        Vitamio.isInitialized(this);
//
//        // 获取数据
//        Uri uri = getIntent().getData();
//        if (uri!=null){
//            // 从外部打开
//            videoView.setVideoURI(uri);
//            iv_pre.setEnabled(false);
//            iv_next.setEnabled(false);
//            // file:///storage/emulated/0/Download/video/oppo - 2.mp4
//            // schema://host:port/path
//            tv_title.setText(uri.getPath());
//        }else {
//            // 从应用内打开
//            mVideoItems = (ArrayList<VideoItem>) getIntent().getSerializableExtra("videoItems");
//            mPosition = getIntent().getIntExtra("position",-1);
//
//            // 可用性验证
//            if (mVideoItems.size()==0|| mPosition ==-1){
//                return;
//            }
//
//            // 播放视频
//            playItem();
//        }
//
//        // 开是更新系统时间
//        startUpdateSystemTime();
//
//        // 获取系统音量
//        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
//        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
//        sk_volume.setMax(maxVolume);
//        int currentVolume = getCurrentVolume();
//        logE("VideoPlayerActivity.initData,maxVolume="+maxVolume+";currentVoume="+currentVolume);
//        sk_volume.setProgress(currentVolume);
//
//        // 修改透明遮罩为完全透明
//        ViewCompat.setAlpha(alpha_cover,0);
//
//        // 隐藏控制面板
//        initHideControllor();
//    }
//
//    // 初始化界面时隐藏控制面板
//    private void initHideControllor() {
//        // 使用 measure 来获取控件的高度
//        ll_top.measure(0,0);
//        ViewCompat.animate(ll_top).translationY(-ll_top.getMeasuredHeight()).setDuration(2000).start();
////        logE("VideoPlayerActivity.initHideControllor,topH="+ll_top.getHeight()+";mH="+ll_top.getMeasuredHeight());
//
//        // 使用布局监听来获取控件高度
//        ll_bottom.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                ll_bottom.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//
//                logE("VideoPlayerActivity.onGlobalLayout,bottomH="+ll_bottom.getHeight());
//                ViewCompat.animate(ll_bottom).translationY(ll_bottom.getHeight()).setDuration(2000).start();
//            }
//        });
//
//        isContollorShowing = false;
//    }
//
//    // 获取当前  mPosition 指定的视频，并开始播放
//    private void playItem() {
//        // 获取要播放的视频
//        VideoItem videoItem = mVideoItems.get(mPosition);
////        VideoItem videoItem = (VideoItem) getIntent().getSerializableExtra("videoItem");
//        logE("VideoPlayerActivity.initData,item="+videoItem);
//
//        // 播放视频
//        videoView.setVideoPath(videoItem.getPath());
//
//        // 更新标题
//        tv_title.setText(videoItem.getTitle());
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        unregisterReceiver(videoReceiver);
//        mHandler.removeCallbacksAndMessages(null);
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//
//        // 由手势分析器，先分析触摸事件
//        gestureDetector.onTouchEvent(event);
//
////        最终音量 = 起始音量 + 变化音量
////        变化音量 = 划过屏幕的百分比 * 最大音量
////        划过屏幕的百分比 = 划过屏幕的距离 / 屏幕的高度
////        划过屏幕的距离 = 手指的当前位置 - 手指的起始位置
//        switch (event.getAction()){
//            case MotionEvent.ACTION_DOWN:
//                // 手指的起始位置
//                mStartY = event.getY();
//                // 起始音量
//                mStartVolume = getCurrentVolume();
//                // 起始透明度
//                mStartAlpha = ViewCompat.getAlpha(alpha_cover);
//                break;
//            case MotionEvent.ACTION_MOVE:
//                // 手指的当前位置
//                float currentY = event.getY();
//                // 划过屏幕的距离
//                float distance = currentY - mStartY;
//                // 屏幕的高度0
//                int halfScreenH = getWindowManager().getDefaultDisplay().getHeight() / 2;
//                int halfScreenW = getWindowManager().getDefaultDisplay().getWidth() / 2;
//                // 划过屏幕的百分比
//                float movePercent = distance / halfScreenH;
//
//                if (event.getX()< halfScreenW) {
//                    // 左侧,修改亮度
//                    moveAlpha(movePercent);
//                }else {
//                    // 右侧，修改音量
//                    moveVolume(movePercent);
//                }
//                break;
//        }
//        return true;
//    }
//
//    // 根据手指划过屏幕的百分比，修改屏幕亮度
//    private void moveAlpha(float movePercent) {
//        // 最终透明度 = 起始透明度 + 划过屏幕的百分比
//        float finalAlpha = mStartAlpha + movePercent;
////        logE("VideoPlayerActivity.moveAlpha,mStartAlpha="+mStartAlpha+":movePercent="+movePercent+";finalAlpha="+finalAlpha);
//        if (finalAlpha>=0&&finalAlpha<=1){
//            // 只有在 0-1范围内才允许修改透明度
//            ViewCompat.setAlpha(alpha_cover,finalAlpha);
//        }
//    }
//
//    // 根据手指划过屏幕的百分比修改系统音量
//    private void moveVolume(float movePercent) {
//        // 变化音量
//        float offsetVolume = movePercent * sk_volume.getMax();
//        // 最终音量
//        int finalVolume = (int) (mStartVolume + offsetVolume);
//
//        // 更新音量
//        setVolume(finalVolume);
//    }
//
//    @Override
//    protected void processClick(View v) {
//        switch (v.getId()){
//            case R.id.video_iv_pause:
//                switchPauseStatus();
//                break;
//            case R.id.video_iv_mute:
//                switchMuteStatus();
//                break;
//            case R.id.video_iv_pre:
//                playPre();
//                break;
//            case R.id.video_iv_next:
//                playNext();
//                break;
//            case R.id.video_iv_fullscreen:
//                switchFullScreen();
//                break;
//        }
//    }
//
//    // 切换全屏状态
//    private void switchFullScreen() {
//        videoView.switchFullScreen();
//
//        updateFullScreenBtn();
//    }
//
//    // 更新全屏按钮使用的图片
//    private void updateFullScreenBtn() {
//        if (videoView.isFullScreen()){
//            // 全屏
//            iv_fullscreen.setImageResource(R.drawable.video_defaultscreen_selector);
//        }else{
//            // 非全屏
//            iv_fullscreen.setImageResource(R.drawable.video_fullscreen_selector);
//        }
//    }
//
//    // 播放上一曲
//    private void playPre() {
//        if (mPosition!=0){
//            mPosition--;
//            playItem();
//        }
//
//        updatePreAndNext();
//    }
//
//    // 播放下一曲
//    private void playNext() {
//        if (mPosition!=mVideoItems.size()-1){
//            mPosition++;
//            playItem();
//        }
//
//        updatePreAndNext();
//    }
//
//    // 更新上一曲和下一曲的可用性
//    private void updatePreAndNext() {
//        iv_pre.setEnabled(mPosition!=0);
//        iv_next.setEnabled(mPosition!=mVideoItems.size()-1);
//    }
//
//    // 如果当前音量不为0，则记录当前音量，并将音量置为0；否则将音量还原为之前的音量
//    private void switchMuteStatus() {
//        if (getCurrentVolume() !=0){
//            // 非静音状态
//            mCurrentVolume = getCurrentVolume();
//            setVolume(0);
//        }else{
//            // 静音状态
//            setVolume(mCurrentVolume);
//        }
//    }
//
//    // 更新音量
//    private void setVolume(int volume) {
//        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,volume,0);
//        sk_volume.setProgress(volume);
//    }
//
//    // 获取音量
//    private int getCurrentVolume() {
//        return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//    }
//
//    // 更新系统时间，并隔一段时间后再次更新
//    private void startUpdateSystemTime() {
////        logE("VideoPlayerActivity.startUpdateSystemTime,time="+System.currentTimeMillis());
//        tv_system_time.setText(StringUtils.formatSystemTime());
//
//        // 发送延迟消息
//        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_SYSTEM_TIME,500);
//    }
//
//    // 切换暂停/播放状态
//    private void switchPauseStatus() {
//        if (videoView.isPlaying()){
//            // 播放状态，需要暂停
//            videoView.pause();
//            mHandler.removeMessages(MSG_UPDATE_POSITION);
//        }else {
//            // 暂停状态，需要开启播放
//            videoView.start();
//            startUpdatePosition();
//        }
//
//        updatePauseBtn();
//    }
//
//    // 更新暂停按钮使用的图片
//    private void updatePauseBtn() {
//        if (videoView.isPlaying()){
//            // 播放状态
//            iv_pause.setImageResource(R.drawable.video_pause_selector);
//        }else {
//            // 暂停状态
//            iv_pause.setImageResource(R.drawable.video_play_selector);
//        }
//    }
//
//    // 根据当前系统的电量，更新电池图片
//    private void updateBatteryBtn(int level) {
//        if (level < 10) {
//            iv_battery.setImageResource(R.mipmap.ic_battery_0);
//        } else if (level < 20) {
//            iv_battery.setImageResource(R.mipmap.ic_battery_10);
//        } else if (level < 40) {
//            iv_battery.setImageResource(R.mipmap.ic_battery_20);
//        } else if (level < 60) {
//            iv_battery.setImageResource(R.mipmap.ic_battery_40);
//        } else if (level < 80) {
//            iv_battery.setImageResource(R.mipmap.ic_battery_60);
//        } else if (level < 100) {
//            iv_battery.setImageResource(R.mipmap.ic_battery_80);
//        } else {
//            iv_battery.setImageResource(R.mipmap.ic_battery_100);
//        }
//    }
//
//    // 更新播放进度，并稍后再次更新
//    private void startUpdatePosition() {
//        int position = (int) videoView.getCurrentPosition();// 获取当前播放位置
//        updatePosition(position);
//
//        // 发送延迟消息
//        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_POSITION,500);
//    }
//
//    // 刚更新播放进度为position
//    private void updatePosition(int position) {
////        logE("VideoPlayerActivity.updatePosition,position="+position+";duration="+videoView.getDuration());
//        tv_position.setText(StringUtils.formatTime(position));
//        sk_position.setProgress(position);
//    }
//
//    // 显示控制面板
//    private void showContollor() {
//        ViewCompat.animate(ll_top).translationY(0).setDuration(500).start();
//        ViewCompat.animate(ll_bottom).translationY(0).setDuration(500).start();
//        isContollorShowing = true;
//    }
//
//    // 隐藏控制面板
//    private void hideControllor() {
//        ViewCompat.animate(ll_top).translationY(-ll_top.getHeight()).setDuration(500).start();
//        ViewCompat.animate(ll_bottom).translationY(ll_bottom.getHeight()).setDuration(500).start();
//        isContollorShowing = false;
//    }
//
//    private class OnVideoPreparedListener implements MediaPlayer.OnPreparedListener {
//        @Override
//        // 视频资源已经加载完毕
//        public void onPrepared(MediaPlayer mp) {
//
//            // 隐藏加载遮罩
//            ll_loading_cover.setVisibility(View.GONE);
//
//            // 开启播放
//            videoView.start();
////              videoView.setMediaController(new MediaController(this));
//
//            // 更新暂停按钮
//            updatePauseBtn();
//
//            // 初始化进度信息
//            int duration = (int) videoView.getDuration();// 获取总时长
//            tv_duration.setText(StringUtils.formatTime(duration));
//            sk_position.setMax(duration);
//
//            startUpdatePosition();
//
//        }
//    }
//
//    private class VideoReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (Intent.ACTION_BATTERY_CHANGED.equals(action)){
//                // 获取到电量变化
//                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
////                logE("VideoReceiver.onReceive,level="+level);
//                updateBatteryBtn(level);
//            }
//        }
//    }
//
//    private class OnVideoSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
//        @Override
//        // 当进度发生变化的时候被调用
//        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
////            logE("OnVideoSeekBarChangeListener.onProgressChanged,progress="+progress+";fromUser="+fromUser);
//
//            // 如果不是用户发起的变更，则不处理
//            if (!fromUser){
//                return;
//            }
//
//            switch (seekBar.getId()){
//                case R.id.video_sk_volume:
//                    // 修改系统音量
//                    // 参数二 要设置的音量大小
//                    // 参数三 如果为1 则会显示系统的音量控制条，如果为0则不显示
//                    setVolume(progress);
//
//                    break;
//                case R.id.video_sk_position:
//                    // 跳转播放进度
//                    videoView.seekTo(progress);
//                    break;
//            }
//        }
//
//        @Override
//        // 当用户按下的时候被调用
//        public void onStartTrackingTouch(SeekBar seekBar) {
//            logE("OnVideoSeekBarChangeListener.onStartTrackingTouch,");
//            mHandler.removeMessages(MSG_HIDE_CONTROLLOR);
//        }
//
//        @Override
//        // 当用户松手时被调用
//        public void onStopTrackingTouch(SeekBar seekBar) {
//            logE("OnVideoSeekBarChangeListener.onStopTrackingTouch,");
//            mHandler.sendEmptyMessageDelayed(MSG_HIDE_CONTROLLOR,5000);
//        }
//    }
//
//    private class OnVideoCompletionListener implements MediaPlayer.OnCompletionListener {
//        @Override
//        // 视频播放结束
//        public void onCompletion(MediaPlayer mp) {
//            // 由于低版本的MediaPlayer存在BUG，当视频播放结束后，有可能播放进度不完整，需要将进度强制的设置为总时长
//            mHandler.removeMessages(MSG_UPDATE_POSITION);
//            int duration = (int) videoView.getDuration();
//            updatePosition(duration);
//
//            // 更新暂停按钮
////            updatePauseBtn();
//            iv_pause.setImageResource(R.drawable.video_play_selector);
//        }
//    }
//
//    private class OnVideoGestureListener extends GestureDetector.SimpleOnGestureListener {
//
//        @Override
//        public boolean onSingleTapConfirmed(MotionEvent e) {
////            logE("OnVideoGestureListener.onSingleTapConfirmed,");
//            if (isContollorShowing){
//                // 显示状态，需要将面板隐藏
//                hideControllor();
//            }else{
//                // 隐藏状态,需要将面板显示出来
//                showContollor();
//
//                // 发送延迟消息，自动隐藏控制面板
//                mHandler.sendEmptyMessageDelayed(MSG_HIDE_CONTROLLOR, 5000);
//            }
//
//            return super.onSingleTapConfirmed(e);
//        }
//
//        @Override
//        public boolean onDoubleTap(MotionEvent e) {
////            logE("OnVideoGestureListener.onDoubleTap,");
//            switchFullScreen();
//            return super.onDoubleTap(e);
//        }
//
//        @Override
//        public void onLongPress(MotionEvent e) {
//            super.onLongPress(e);
//            switchPauseStatus();
//        }
//    }
//
//    private class OnVideoBufferingUpdateListener implements MediaPlayer.OnBufferingUpdateListener {
//        @Override
//        public void onBufferingUpdate(MediaPlayer mp, int percent) {
////            logE("OnVideoBufferingUpdateListener.onBufferingUpdate,percent="+percent);
//
//            float bufferPercent = percent / 100f;
//            int position = (int) (sk_position.getMax() * bufferPercent);
//            sk_position.setSecondaryProgress(position);
//        }
//    }
//
//    private class OnVideoInfoListener implements MediaPlayer.OnInfoListener {
//        @Override
//        // 播放过程中发生了一些状态变化
//        public boolean onInfo(MediaPlayer mp, int what, int extra) {
//            switch (what){
//                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
//                    // 播放中出现缓冲
//                    pb_buffering.setVisibility(View.VISIBLE);
//                    break;
//                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
//                    // 播放中缓冲结束
//                    pb_buffering.setVisibility(View.GONE);
//                    break;
//            }
//            return false;
//        }
//    }
//
//    private class OnVideoErrorListener implements MediaPlayer.OnErrorListener {
//        @Override
//        // 发生了不可修复的错误
//        public boolean onError(MediaPlayer mp, int what, int extra) {
//            AlertDialog .Builder builder = new AlertDialog.Builder(VitamioPlayerActivity.this);
//            builder.setTitle("警告")
//                    .setMessage("该视频无法播放")
//                    .setPositiveButton("退出界面", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            finish();
//                        }
//                    });
//
//            builder.show();
//            return false;
//        }
//    }
//}
