package com.alphaae.android.audiotest.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.alphaae.android.audiotest.model.Music;
import com.alphaae.android.audiotest.R;
import com.alphaae.android.audiotest.base.BaseService;
import com.alphaae.android.audiotest.collector.ServiceCollector;
import com.alphaae.android.audiotest.helper.MusicListHelper;

import java.io.IOException;

public class PlayAudioService extends BaseService {

    NotificationManager notificationManager;
    NotificationCompat.Builder notification;
    RemoteViews remoteViews;

    public static final String PERENCES_PLAYINDEX = "PERENCES_PLAYINDEX";

    public static final int NOTIFICATION_PLAYMENU_ID = 233;
    public static final String EXTRA_OPERATING = "eo";
    public static final String EXTRA_OPERATING_INDEX = "eoi";
    public static final int EXTRA_EXIT = -1;
    public static final int EXTRA_START = 1;
    public static final int EXTRA_PAUSE = 2;
    public static final int EXTRA_RESET = 3;
    public static final int EXTRA_STOP = 4;
    public static final int EXTRA_NEXT = 5;
    public static final int EXTRA_ACTION = 6;
    public static final int EXTRA_TO = 7;

    public static final int VIEW_PLAYLIST = 11;

    private SharedPreferences sharedPreferences;
    private OperatingBroadcastReceiver broadcastReceiver;
    public static MusicListHelper musicListHelper;
    private MediaPlayer mediaPlayer = new MediaPlayer();

    private boolean isHaveMusic = false;


    public static void startAction(Context mContext) {
        if (ServiceCollector.isFinishing(PlayAudioService.class)) {
            Intent intent = new Intent(mContext, PlayAudioService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mContext.startForegroundService(intent);
            } else {
                mContext.startService(intent);
            }
        }
    }

    public static void intentAction(Context mContext, int extra, int index) {
        Intent intent = new Intent("com.alphaae.android.audiotest.OPERATING");
        intent.putExtra(EXTRA_OPERATING, extra);
        intent.putExtra(EXTRA_OPERATING_INDEX, index);
        mContext.sendBroadcast(intent);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    public PlayAudioService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);

        broadcastReceiver = new OperatingBroadcastReceiver();
        registerReceiver(broadcastReceiver, new IntentFilter("com.alphaae.android.audiotest.OPERATING"));

        initNotificaation();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NOTIFICATION_PLAYMENU_ID, notification.build());
        } else {
            notifyNotification();
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                nextMediaPlay();
            }
        });
        musicListHelper = new MusicListHelper(this);
        initMediaPlay(musicListHelper.getMusic(sharedPreferences.getInt(PERENCES_PLAYINDEX, 0)));

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sharedPreferences.edit().putInt(PERENCES_PLAYINDEX, musicListHelper.getIndex()).apply();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                .cancel(NOTIFICATION_PLAYMENU_ID);
        destroyMediaPlay();
        mediaPlayer.release();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    private void initNotificaation() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("playmenu", "PlayMenu", NotificationManager.IMPORTANCE_HIGH);
            channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            channel.enableLights(false);
            channel.enableVibration(false);
            channel.setSound(null, null);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intentPlayList = new Intent("com.alphaae.android.audiotest.OPERATING");
        intentPlayList.putExtra(EXTRA_OPERATING, VIEW_PLAYLIST);
        PendingIntent pendingIntentPlayList = PendingIntent.getBroadcast(this, VIEW_PLAYLIST, intentPlayList, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent intentAction = new Intent("com.alphaae.android.audiotest.OPERATING");
        intentAction.putExtra(EXTRA_OPERATING, EXTRA_ACTION);
        PendingIntent pendingIntentAction = PendingIntent.getBroadcast(this, EXTRA_ACTION, intentAction, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent intentNext = new Intent("com.alphaae.android.audiotest.OPERATING");
        intentNext.putExtra(EXTRA_OPERATING, EXTRA_NEXT);
        PendingIntent pendingIntentNext = PendingIntent.getBroadcast(this, EXTRA_NEXT, intentNext, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent intentExit = new Intent("com.alphaae.android.audiotest.OPERATING");
        intentExit.putExtra(EXTRA_OPERATING, EXTRA_EXIT);
        PendingIntent pendingIntentExit = PendingIntent.getBroadcast(this, EXTRA_EXIT, intentExit, PendingIntent.FLAG_CANCEL_CURRENT);

        remoteViews = new RemoteViews(getPackageName(), R.layout.notification_playmenu);
        remoteViews.setImageViewResource(R.id.img_head, R.mipmap.not_default);
        remoteViews.setTextViewText(R.id.text_title, "暂无歌曲");
        remoteViews.setTextViewText(R.id.text_content, "");
        remoteViews.setOnClickPendingIntent(R.id.btn_action, pendingIntentAction);
        remoteViews.setImageViewResource(R.id.btn_action, R.mipmap.not_play);
        remoteViews.setOnClickPendingIntent(R.id.btn_next, pendingIntentNext);
        remoteViews.setImageViewResource(R.id.btn_next, R.mipmap.not_next);
        remoteViews.setOnClickPendingIntent(R.id.btn_exit, pendingIntentExit);
        remoteViews.setImageViewResource(R.id.btn_exit, R.mipmap.not_exit);

        notification = new NotificationCompat.Builder(this, "playmenu")
                .setSmallIcon(R.mipmap.not_play)
                .setContentTitle("")
                .setContentText("")
                .setCustomContentView(remoteViews)
                .setOngoing(true)
                .setContentIntent(pendingIntentPlayList)
                .setDefaults(Notification.DEFAULT_LIGHTS);
    }

    private void notifyNotification() {
        notificationManager.notify(NOTIFICATION_PLAYMENU_ID, notification.build());
    }

    private void initMediaPlay(Music music) {
        try {
            if (music == null) {
                isHaveMusic = false;
                Toast.makeText(this, "歌曲不存在", Toast.LENGTH_SHORT).show();
                return;
            }
            isHaveMusic = true;
            remoteViews.setImageViewBitmap(R.id.img_head, musicListHelper.getAlbumArt(music.getAlbumId()));
            remoteViews.setTextViewText(R.id.text_title, music.getTitle());
            remoteViews.setTextViewText(R.id.text_content, music.getArtist());

            mediaPlayer.setDataSource(music.getUrl());
            mediaPlayer.prepare();
            notifyNotification();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void destroyMediaPlay() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.reset();
    }

    private void toMediaPlay(int index) {
        setMediaPlay(musicListHelper.getMusic(index));
    }

    private void nextMediaPlay() {
        setMediaPlay(musicListHelper.nextMusic());
    }

    private void setMediaPlay(Music music) {
        if (isHaveMusic) {
            destroyMediaPlay();
            remoteViews.setImageViewResource(R.id.btn_action, R.mipmap.not_pause);
            initMediaPlay(music);
            mediaPlayer.start();
            if (ViewPlayListService.recyclerPlayList != null) {
                ViewPlayListService.adapter.notifyItemChanged(musicListHelper.getPreviousIndex());
                ViewPlayListService.adapter.notifyItemChanged(musicListHelper.getIndex());
            }
        }
    }

    private void actionMediaPlay() {
        if (isHaveMusic) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                remoteViews.setImageViewResource(R.id.btn_action, R.mipmap.not_play);
            } else {
                mediaPlayer.start();
                remoteViews.setImageViewResource(R.id.btn_action, R.mipmap.not_pause);
            }
            notifyNotification();
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    class OperatingBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int operating = intent.getIntExtra(EXTRA_OPERATING, 0);
            switch (operating) {
                case EXTRA_EXIT:
                    ServiceCollector.stopSelfAll();
                    break;
                case EXTRA_RESET:
                    mediaPlayer.reset();
                    break;
                case EXTRA_STOP:
                    mediaPlayer.stop();
                    break;
                case EXTRA_NEXT:
                    nextMediaPlay();
                    break;
                case EXTRA_ACTION:
                    actionMediaPlay();
                    break;
                case EXTRA_TO:
                    toMediaPlay(intent.getIntExtra(EXTRA_OPERATING_INDEX, 0));
                    break;
                case VIEW_PLAYLIST:
                    ViewPlayListService.actionStart(PlayAudioService.this);
                    break;
            }
        }
    }
}
