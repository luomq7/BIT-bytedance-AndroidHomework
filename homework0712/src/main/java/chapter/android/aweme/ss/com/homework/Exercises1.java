package chapter.android.aweme.ss.com.homework;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * 作业1：
 * 打印出Activity屏幕切换 Activity会执行什么生命周期？
 * 切换屏幕前：onCreate-->onStart-->onResume
 * 切换屏幕后：onPause-->onStop-->onDestory-->onCreate-->onStart-->onResume
 */
public class Exercises1 extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_tips);
        Log.d("生命周期", "onCreate: ");
    }
    @Override
    public void onStart(){
        super.onStart();
        Log.d("生命周期", "onStart: ");
    }
    @Override
    public void onResume(){
        super.onResume();
        Log.d("生命周期", "onResume: ");
    }
    @Override
    public void onPause(){
        super.onPause();
        Log.d("生命周期", "onPause: ");
    }
    @Override
    public void onStop(){
        super.onStop();
        Log.d("生命周期", "onStop: ");
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d("生命周期", "onDestory: ");
    }

}
