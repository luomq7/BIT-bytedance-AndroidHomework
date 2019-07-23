package com.example.chanyue;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.chanyue.bean.Like;

import java.io.IOException;

public class PlayVideo extends AppCompatActivity{

    private Uri mPlayVideo;
    private SurfaceView surfaceView;
    private MediaPlayer player;
    private SurfaceHolder holder;

    private Uri playVideoUri;

    private static final int PLAYVIDEO = 11;

    private boolean button_play_value;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);

        surfaceView = findViewById(R.id.view_play_video);
        player = new MediaPlayer();


//        try {
//            player.setDataSource(getResources().openRawResourceFd(R.raw.bytedance));
////            player.setDataSource(PlayVideo.this,playVideoUri);
//            holder = surfaceView.getHolder();
//            holder.addCallback(new PlayerCallBack());
//            player.prepare();
//            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mp) {
//                    // 自动播放
//                    player.start();
//                    player.setLooping(true);
//                }
//            });
//            player.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
//                @Override
//                public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
//                    changeVideoSize(mp);
//                }
//            });
//            player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
//                @Override
//                public void onBufferingUpdate(MediaPlayer mp, int percent) {
//                    System.out.println(percent);
//                }
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        try {
            Intent intent = getIntent();
            String videoUrl = intent.getStringExtra("VideoUrl");
            playVideoUri = Uri.parse(videoUrl);
            player.setDataSource(PlayVideo.this,playVideoUri);
            holder = surfaceView.getHolder();
            holder.addCallback(new PlayerCallBack());
            player.prepare();
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    // 自动播放
                    player.start();
                    player.setLooping(true);
                }
            });
            player.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                @Override
                public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                    changeVideoSize(mp);
                }
            });
            player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                    System.out.println(percent);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        button_play_value = true;
        surfaceView.setOnTouchListener(new MyClickListener(new MyClickListener.MyClickCallBack() {
            @Override
            public void oneClick() {
                if (button_play_value == false) {
                    player.start();
                    surfaceView.setBackgroundResource(R.color.colorNull);
                    button_play_value = true;
                    findViewById(R.id.stop_img).setVisibility(View.GONE);
                } else {
                    player.pause();
                    surfaceView.setBackgroundResource(R.color.colorBackground);
                    button_play_value = false;
                    findViewById(R.id.stop_img).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void doubleClick() {
//                Toast.makeText(PlayVideo.this, "我是双击哦！！！", Toast.LENGTH_SHORT).show();
                Like like = new Like(PlayVideo.this);
                like = findViewById(R.id.like);
                like.startAnimation();
            }
        }));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == RESULT_OK && null != intent) {
            playVideoUri = intent.getData();
//            playVideoUri = Uri.parse(url);
//            playVideoUri = intent.getData().
//            playVideoUri = intent.getVideoUrl();
//            playVideoUri = intent.getData()

            Log.d("获取视频数据", "mSelectedVideo = " + playVideoUri);
//            Uri mselectedVideoUri;
//            mselectedVideoUri = intent.getData();
//            videoView.setVideoURI(mselectedVideoUri);
//            videoView.start();
//            mBtn.setText(R.string.post_it);


        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.stop();
            player.release();
        }
    }

    private class PlayerCallBack implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            player.setDisplay(holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    }

    public void changeVideoSize(MediaPlayer mediaPlayer) {
        int surfaceWidth = surfaceView.getWidth();
        int surfaceHeight = surfaceView.getHeight();

        int videoWidth = mediaPlayer.getVideoWidth();
        int videoHeight = mediaPlayer.getVideoHeight();

        //根据视频尺寸去计算->视频可以在sufaceView中放大的最大倍数。
        float max;
        if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            //竖屏模式下按视频宽度计算放大倍数值
            max = Math.max((float) videoWidth / (float) surfaceWidth, (float) videoHeight / (float) surfaceHeight);
        } else {
            //横屏模式下按视频高度计算放大倍数值
            max = Math.max(((float) videoWidth / (float) surfaceHeight), (float) videoHeight / (float) surfaceWidth);
        }

        //视频宽高分别/最大倍数值 计算出放大后的视频尺寸
        videoWidth = (int) Math.ceil((float) videoWidth / max);
        videoHeight = (int) Math.ceil((float) videoHeight / max);

        //无法直接设置视频尺寸，将计算出的视频尺寸设置到surfaceView 让视频自动填充。
        surfaceView.setLayoutParams(new LinearLayout.LayoutParams(videoWidth, videoHeight));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        changeVideoSize();
    }

    public void changeVideoSize() {
        int videoWidth = player.getVideoWidth();
        int videoHeight = player.getVideoHeight();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int surfaceHeight = displayMetrics.heightPixels;
        int surfaceWidth = displayMetrics.widthPixels;

        ViewGroup.LayoutParams layoutParams = surfaceView.getLayoutParams();

        float max;
        if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            //竖屏模式下按视频宽度计算放大倍数值
            max = Math.max((float) videoWidth / (float) surfaceWidth, (float) videoHeight / (float) surfaceHeight);
        } else {
            //横屏状态
            max = Math.max((float) videoWidth / (float) surfaceWidth, (float) videoHeight / (float) (surfaceHeight - 250 ));
        }

        layoutParams.width = (int) Math.ceil((float) videoWidth / max);
        layoutParams.height = (int) Math.ceil((float) videoHeight / max);

        surfaceView.setLayoutParams(layoutParams);
    }
}
