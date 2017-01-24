package com.itcast.mobileplayer91.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.itcast.mobileplayer91.R;
import com.itcast.mobileplayer91.bean.AudioItem;
import com.itcast.mobileplayer91.ui.activity.AudioPlayerActivity;
import com.itcast.mobileplayer91.utils.LogUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Ding on 2016/10/13.
 */
public class AudioService extends Service {

    private static final String TAG = "AudioService";

    public static final int PLAYMODE_ALLREPEAT = 0;
    public static final int PLAYMODE_SINGLEREPEAT = 1;
    public static final int PLAYMODE_RANDOM = 2;
    private int mPlayMode = PLAYMODE_ALLREPEAT;

    private static final int NOTIFY_PRE = 0;
    private static final int NOTIFY_NEXT = 1;
    private static final int NOTIFY_CONTENT = 2;
    private static final String NOTIFY_KEY = "notify_type";

    private AudioBinder mAudioBinder;
    private ArrayList<AudioItem> audioItems;
    private int position = -1;

    @Override
    public void onCreate() {
        super.onCreate();
        mAudioBinder = new AudioBinder();

        SharedPreferences preferences = getSharedPreferences("config", MODE_PRIVATE);
        mPlayMode = preferences.getInt("playmode", PLAYMODE_ALLREPEAT);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mAudioBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int notifyType = intent.getIntExtra(NOTIFY_KEY, -1);
        if (notifyType!=-1){
            // 从通知栏打开的
            switch (notifyType){
                case NOTIFY_PRE:
                    mAudioBinder.playPre();
                    break;
                case NOTIFY_NEXT:
                    mAudioBinder.playNext();
                    break;
                case NOTIFY_CONTENT:
                    notifyUI();
                    break;
            }
        }else{
            // 从播放界面打开服务
            int position = intent.getIntExtra("position", -1);
            if (position != this.position){
                // 不同的歌曲
                this.position = position;
                audioItems = (ArrayList<AudioItem>) intent.getSerializableExtra("audioItems");

                // 播放选中的音乐
                mAudioBinder.playItem();
            }else{
                // 重复的歌曲
                notifyUI();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public class AudioBinder extends Binder {

        private class OnAudioPreparedListener implements MediaPlayer.OnPreparedListener {
            @Override
            // 资源准备完毕
            public void onPrepared(MediaPlayer mp) {
                // 开始播放
                mediaPlayer.start();

                // 显示通知
                showNotification();

                // 通知界面更新
                notifyUI();
            }
        }

        private class OnAudioCompletionListener implements MediaPlayer.OnCompletionListener {
            @Override
            // 播放结束
            public void onCompletion(MediaPlayer mp) {
                autoPlayNext();
            }
        }

        private MediaPlayer mediaPlayer;

        // 播放当前 position 指定的歌曲
        public void playItem(){

            // 验证数据可用性
            if (audioItems.size() == 0 || position == -1) {
                return ;
            }

            AudioItem audioItem = audioItems.get(position);
            LogUtils.e(TAG, "AudioService.onStartCommand,item=" + audioItem);

            // 播放音乐
            try {
                if (mediaPlayer!=null){
                    mediaPlayer.reset();
                }else {
                    mediaPlayer = new MediaPlayer();
                }

                mediaPlayer.setDataSource(audioItem.getPath());
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnCompletionListener(new OnAudioCompletionListener());
                mediaPlayer.setOnPreparedListener(new OnAudioPreparedListener());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 切换播放状态
        public void switchPasueStatus() {
            if (mediaPlayer.isPlaying()){
                // 正在播放
                mediaPlayer.pause();
                cancelNotification();
            }else{
                // 暂停状态
                mediaPlayer.start();
                showNotification();
            }
        }

        // 如果返回 true 则说明歌曲正在播放
        public boolean isPlaying(){
            return mediaPlayer.isPlaying();
        }

        // 返回当前歌曲的总时长
        public int getDuration(){
            return mediaPlayer.getDuration();
        }

        // 返回当前歌曲的播放位置
        public int getCurrentPosition(){
            return mediaPlayer.getCurrentPosition();
        }

        // 跳转到指定毫秒值处播放
        public void seekTo(int msec){
            mediaPlayer.seekTo(msec);
        }

        // 播放上一曲
        public void playPre(){
            if (position!=0){
                position--;
                playItem();
            }else{
                Toast.makeText(AudioService.this, "已经是第一首了", Toast.LENGTH_SHORT).show();
            }
        }

        // 播放下一曲
        public void playNext(){
            if (position!=audioItems.size()-1){
                position++;
                playItem();
            }else{
                Toast.makeText(AudioService.this, "已经是最后一首了", Toast.LENGTH_SHORT).show();
            }
        }

        // 依次切换 列表循环、单曲循环、随机播放
        public void switchPlayMode(){
            switch (mPlayMode){
                case PLAYMODE_ALLREPEAT:
                    mPlayMode = PLAYMODE_SINGLEREPEAT;
                    break;
                case PLAYMODE_SINGLEREPEAT:
                    mPlayMode = PLAYMODE_RANDOM;
                    break;
                case PLAYMODE_RANDOM:
                    mPlayMode = PLAYMODE_ALLREPEAT;
                    break;
            }

            LogUtils.e(TAG,"AudioBinder.switchPlayMode,playmode="+mPlayMode);
            // 保存播放模式到配置文件
            SharedPreferences preferences = getSharedPreferences("config", MODE_PRIVATE);
            preferences.edit().putInt("playmode",mPlayMode).commit();
        }

        /** 返回当前使用的播放模式。 */
        public int getPlayMode(){
            return mPlayMode;
        }

        /**根据当前的播放模式，自动切换下一首歌*/
        private void autoPlayNext() {
            switch (mPlayMode){
                case PLAYMODE_ALLREPEAT:
                    // 列表循环，自动查找下一首歌播放。如果已经是最后一首歌，则回到第一首歌
                    if (position!=audioItems.size()-1){
                        position++;
                    }else{
                        position = 0;
                    }
                    break;
                case PLAYMODE_SINGLEREPEAT:
                    // 保持当前位置，重新播放即可
                    break;
                case PLAYMODE_RANDOM:
                    position = new Random().nextInt(audioItems.size());
                    break;
            }

            playItem();
        }
    }

    // 通知界面更新
    private void notifyUI() {
        // 获取当前播放的歌曲
        AudioItem audioItem = audioItems.get(position);

        // 通知界面更新
        Intent intent = new Intent("com.itheima.onPrepared");
        intent.putExtra("audioItem",audioItem);
        sendBroadcast(intent);
    }


    // 取消通知
    private void cancelNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(0);
    }

    // 显示通知
    private void showNotification() {
        // 创建通知对象
        Notification notification;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            // 小于Android 3.0的版本
            notification = getNotificationByOldApi();
        } else {
            // Notification notification = getNotificationByNewApi();
            notification = getCustomNotificationByNewApi();
        }

        // 显示通知
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0, notification);
    }

    // 使用新API来生成一个自定义布局的通知
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private Notification getCustomNotificationByNewApi() {
        Notification.Builder builder = new Notification.Builder(this);// 在 Android 3.0 以后才可用
        builder.setSmallIcon(R.mipmap.icon)
                .setTicker("正在播放："+getCurrentItem().getTitle())
                .setContent(getRemoteViews())
                .setOngoing(true);

        return builder.getNotification();
    }

    // 使用新 API 来生成通知对象
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private Notification getNotificationByNewApi() {
        Notification.Builder builder = new Notification.Builder(this);// 在 Android 3.0 以后才可用
        builder.setSmallIcon(R.mipmap.icon)
                .setTicker("正在播放："+getCurrentItem().getTitle())
                .setWhen(System.currentTimeMillis())
                .setContentTitle(getCurrentItem().getTitle())
                .setContentText(getCurrentItem().getArtist())
                .setContentIntent(getContentIntent())
                .setAutoCancel(true)// 点击后自动隐藏通知
                .setOngoing(true);

        return builder.getNotification();
    }

    @NonNull
    // 使用旧的API生成一个通知对象
    private Notification getNotificationByOldApi() {
        Notification notification = new Notification(R.mipmap.icon,"正在播放:"+getCurrentItem().getTitle(),System.currentTimeMillis());
        notification.setLatestEventInfo(this,getCurrentItem().getTitle(),getCurrentItem().getArtist(),getContentIntent());// 该方法在 Android6.0 被删除
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        return notification;
    }

    // 返回自定义通知使用的布局
    private RemoteViews getRemoteViews() {
        RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.audio_notify);

        // 设置文本
        remoteViews.setTextViewText(R.id.audio_notify_tv_title,getCurrentItem().getTitle());
        remoteViews.setTextViewText(R.id.audio_notify_tv_artist,getCurrentItem().getArtist());

        // 设置点击监听
        remoteViews.setOnClickPendingIntent(R.id.audio_notify_iv_pre,getPreIntent());
        remoteViews.setOnClickPendingIntent(R.id.audio_notify_iv_next,getNextIntent());
        remoteViews.setOnClickPendingIntent(R.id.audio_notify_layout,getContentIntent());

        return remoteViews;
    }

    // 返回上一曲的点击响应
    private PendingIntent getPreIntent() {
        Intent intent = new Intent(this,AudioService.class);
        intent.putExtra(NOTIFY_KEY, NOTIFY_PRE);

        return PendingIntent.getService(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
    }

    // 返回下一曲的点击响应
    private PendingIntent getNextIntent() {
        Intent intent = new Intent(this,AudioService.class);
        intent.putExtra(NOTIFY_KEY, NOTIFY_NEXT);

        return PendingIntent.getService(this,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
    }

    // 返回正文被点击时的响应intent
    private PendingIntent getContentIntent() {
        Intent intent = new Intent(this,AudioPlayerActivity.class);
        intent.putExtra(NOTIFY_KEY,NOTIFY_CONTENT);

        return PendingIntent.getActivity(this,2,intent,PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private AudioItem getCurrentItem(){
        return audioItems.get(position);
    }
}
