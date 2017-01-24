package com.itcast.mobileplayer91.ui.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.itcast.mobileplayer91.R;
import com.itcast.mobileplayer91.base.BaseActivity;
import com.itcast.mobileplayer91.bean.AudioItem;
import com.itcast.mobileplayer91.lyrics.LyricsLoader;
import com.itcast.mobileplayer91.lyrics.LyricsView;
import com.itcast.mobileplayer91.service.AudioService;
import com.itcast.mobileplayer91.utils.StringUtils;

import java.io.File;

public class AudioPlayerActivity extends BaseActivity {

    private static final int MSG_UPDATE_POSITION = 0;
    private static final int MSG_ROLLING_LYRICS = 1;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_UPDATE_POSITION:
                    startUpdatePosition();
                    break;
                case MSG_ROLLING_LYRICS:
                    startRollingLyrics();
                    break;
            }
        }
    };

    private AudioServiceConnection conn;
    private AudioService.AudioBinder mAudioBinder;
    private ImageView iv_pause;
    private AudioReceiver audioReceiver;
    private TextView tv_title;
    private TextView tv_artist;
    private ImageView iv_wave;
    private TextView tv_position;
    private SeekBar sk_position;
    private ImageView iv_pre;
    private ImageView iv_next;
    private ImageView iv_playmode;
    private LyricsView lyricsView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_audio_player;
    }

    @Override
    protected void initView() {
        // 顶部面板
        tv_title = (TextView) findViewById(R.id.audio_tv_title);

        // 中间部分
        tv_artist = (TextView) findViewById(R.id.audio_tv_artist);
        iv_wave = (ImageView) findViewById(R.id.audio_iv_wave);
        lyricsView = (LyricsView) findViewById(R.id.audio_lyricsview);

        // 底部部分
        tv_position = (TextView) findViewById(R.id.audio_tv_position);
        sk_position = (SeekBar) findViewById(R.id.audio_sk_position);
        iv_pause = (ImageView) findViewById(R.id.audio_iv_pause);
        iv_pre = (ImageView) findViewById(R.id.audio_iv_pre);
        iv_next = (ImageView) findViewById(R.id.audio_iv_next);
        iv_playmode = (ImageView) findViewById(R.id.audio_iv_playmode);
    }

    @Override
    protected void initListener() {
        iv_pause.setOnClickListener(this);
        iv_pre.setOnClickListener(this);
        iv_next.setOnClickListener(this);
        iv_playmode.setOnClickListener(this);

        sk_position.setOnSeekBarChangeListener(new OnAudioSeekBarChangeListener());

        // 注册广播
        IntentFilter filter = new IntentFilter("com.itheima.onPrepared");
        audioReceiver = new AudioReceiver();
        registerReceiver(audioReceiver, filter);
    }

    @Override
    protected void initData() {
        // 将数据转交给后台服务
        Intent intent = new Intent(getIntent());
        intent.setClass(this, AudioService.class);
        startService(intent);
        conn = new AudioServiceConnection();
        bindService(intent, conn, BIND_AUTO_CREATE);

        // 开启示波器动画
        AnimationDrawable wave_anim = (AnimationDrawable) iv_wave.getDrawable();
        wave_anim.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
        unregisterReceiver(audioReceiver);
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void processClick(View v) {
        switch (v.getId()){
            case R.id.audio_iv_pause:
                switchPauseStatus();
                break;
            case R.id.audio_iv_pre:
                playPre();
                break;
            case R.id.audio_iv_next:
                playNext();
                break;
            case R.id.audio_iv_playmode:
                switchPlayMode();
                break;
        }
    }

    // 依次切换播放模式
    private void switchPlayMode() {
        mAudioBinder.switchPlayMode();

        updatePlayModeBtn();
    }

    // 根据当前的播放模式，切换使用的图片
    private void updatePlayModeBtn() {
        switch (mAudioBinder.getPlayMode()){
            case AudioService.PLAYMODE_ALLREPEAT:
                iv_playmode.setImageResource(R.drawable.audio_playmode_allrepeat_selector);
                break;
            case AudioService.PLAYMODE_SINGLEREPEAT:
                iv_playmode.setImageResource(R.drawable.audio_playmode_singlerepeat_selector);
                break;
            case AudioService.PLAYMODE_RANDOM:
                iv_playmode.setImageResource(R.drawable.audio_playmode_random_selector);
                break;
        }
    }

    // 播放上一曲
    private void playPre() {
        mAudioBinder.playPre();
    }

    // 播放下一曲
    private void playNext() {
        mAudioBinder.playNext();
    }

    // 切换播放状态
    private void switchPauseStatus() {
        mAudioBinder.switchPasueStatus();

        updatePauseBtn();
    }

    // 更新暂停按钮的图片
    private void updatePauseBtn() {
        if (mAudioBinder.isPlaying()){
            // 正在播放
            iv_pause.setImageResource(R.drawable.audio_pause_selector);
        }else{
            // 暂停状态
            iv_pause.setImageResource(R.drawable.audio_play_selector);
        }
    }

    // 更新播放进度，并稍后再次更新
    private void startUpdatePosition() {
        int duration = mAudioBinder.getDuration();
        int position = mAudioBinder.getCurrentPosition();
        CharSequence duraionStr = StringUtils.formatTime(duration);
        CharSequence positionStr = StringUtils.formatTime(position);
        tv_position.setText(positionStr +" / "+duraionStr);

        sk_position.setMax(duration);
        sk_position.setProgress(position);

        // 发送延迟消息
        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_POSITION,500);
    }

    // 使用当前播放进度更新歌词高亮行，并稍后再次更新
    private void startRollingLyrics() {
        lyricsView.computeCenterIndex(mAudioBinder.getCurrentPosition(),mAudioBinder.getDuration());

        // 发送消息，再次滚动歌词
        mHandler.sendEmptyMessage(MSG_ROLLING_LYRICS);
    }

    private class AudioReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("com.itheima.onPrepared".equals(action)){
                // 音乐准备完毕，开始播放
                updatePauseBtn();

                // 获取正在播放的歌曲
                AudioItem audioItem = (AudioItem) intent.getSerializableExtra("audioItem");
                logE("AudioReceiver.onReceive,item="+audioItem);

                // 更新歌曲信息
                tv_title.setText(audioItem.getTitle());
                tv_artist.setText(audioItem.getArtist());

                // 开启进度更新
                startUpdatePosition();

                // 更新播放模式
                updatePlayModeBtn();

                // 开始滚动歌词
//                File lyricsFile = new File(Environment.getExternalStorageDirectory(),"Download/audio/"+audioItem.getTitle()+".lrc");
                File lyricsFile = LyricsLoader.loadFile(audioItem.getTitle());

                lyricsView.setLyricsFile(lyricsFile);
                startRollingLyrics();
            }
        }
    }

    private class AudioServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mAudioBinder = (AudioService.AudioBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    private class OnAudioSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            // 不是用户发起的变化，则不处理
            if (!fromUser){
                return;
            }

            // 跳转播放进度
            mAudioBinder.seekTo(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}
