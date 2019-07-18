package com.bytedance.videoplayer;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private SurfaceView surfaceView;
    private MediaPlayer player;
    private SurfaceHolder holder;
    private DisplayMetrics displayMetrics;
    private SeekBar seekBar;

    private boolean flag;
    private int currentPosition;
    private boolean button_play_value;

    private TextView time_start,time_sum;



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        surfaceView = findViewById(R.id.surfaceView);
        player = new MediaPlayer();
        seekBar = findViewById(R.id.seekBar);
        time_start = findViewById(R.id.time_start);
        time_sum = findViewById(R.id.time_sum);

        try {
            player.setDataSource(getResources().openRawResourceFd(R.raw.bytedance));
            holder = surfaceView.getHolder();
            holder.addCallback(new PlayerCallBack());
            player.prepare();
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    // 自动播放
                    player.start();
                    player.setLooping(true);

                    final int max = player.getDuration();
                    seekBar.setMax(max);
                    player.seekTo(currentPosition);
                    //设置时间
//                    time_start.setText(calculateTime(player.getCurrentPosition()/1000));
//                    time_sum.setText(calculateTime(player.getDuration()/1000));

                    new Thread(){
                        public void run(){
                            flag = true;
                            while (flag){
                                int progress = player.getCurrentPosition();
//                                time_start.setText(calculateTime(player.getCurrentPosition()));
                                seekBar.setProgress(progress);
                                try {
                                    Thread.sleep(500);
                                }catch (InterruptedException e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    }.start();

                }
            });
            player.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                @Override
                public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                    changeVideoSize();
                }
            });
            player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                    System.out.println(percent);
                }
            });
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    //设置时间
                    time_start.setText(calculateTime(player.getCurrentPosition()/1000));
                    time_sum.setText(calculateTime(player.getDuration()/1000));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if(player != null){
                        player.seekTo(seekBar.getProgress());
                        time_start.setText(calculateTime(player.getCurrentPosition()));
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        //设置时间
        time_start.setText(calculateTime(player.getCurrentPosition()/1000));
        time_sum.setText(calculateTime(player.getDuration()/1000));


        button_play_value = true;
        findViewById(R.id.buttonPlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button_play_value == false) {
                    player.start();
                    button_play_value = true;
                }
                else {
                    player.pause();
                    button_play_value = false;
                }
            }
        });

        findViewById(R.id.screenChange).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE){
                    MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    findViewById(R.id.btn_back).setVisibility(View.GONE);
//                    System.out.println("转变为竖屏，返回button设为INVISIBLE");
                }
                else {
                    MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    findViewById(R.id.btn_back).setVisibility(View.VISIBLE);
//                    System.out.println("转变为全屏，返回button设为VISIBLE");

                    findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                            findViewById(R.id.btn_back).setVisibility(View.GONE);
//                            System.out.println("转变为非全屏，返回button设置为INVISIBLE");
                        }
                    });
                }
                changeVideoSize();
            }
        });
    }

    public void changeVideoSize() {
        int videoWidth = player.getVideoWidth();
        int videoHeight = player.getVideoHeight();

        displayMetrics = new DisplayMetrics();
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
            max = Math.max((float) videoWidth / (float) surfaceWidth, (float) videoHeight / (float) (surfaceHeight - 350 ));
        }

        layoutParams.width = (int) Math.ceil((float) videoWidth / max);
        layoutParams.height = (int) Math.ceil((float) videoHeight / max);

//        System.out.println("全屏状态的视频宽：" + layoutParams.width + ", 高："+ layoutParams.height);
//        System.out.println("全屏状态的屏幕宽：" + surfaceWidth + ", 高："+ surfaceHeight);
//        System.out.println("全屏状态的原始视频宽：" + videoWidth + ", 高："+ videoHeight);
        surfaceView.setLayoutParams(layoutParams);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.stop();
            player.release();
            flag = false;
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
            if (player != null && player.isPlaying()){
                currentPosition = player.getCurrentPosition();
                time_start.setText(calculateTime(currentPosition));
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        changeVideoSize();
    }

    public String calculateTime(int time){
        int minute;
        int second;
        if(time > 60){
            minute = time / 60;
            second = time % 60;
            //分钟再0~9
            if(minute >= 0 && minute < 10){
                //判断秒
                if(second >= 0 && second < 10){
                    return "0"+minute+":"+"0"+second;
                }else {
                    return "0"+minute+":"+second;
                }
            }else {
                //分钟大于10再判断秒
                if(second >= 0 && second < 10){
                    return minute+":"+"0"+second;
                }else {
                    return minute+":"+second;
                }
            }
        }else if(time < 60){
            second = time;
            if(second >= 0 && second < 10){
                return "00:"+"0"+second;
            }else {
                return "00:"+ second;
            }
        }
        return null;
    }
}
