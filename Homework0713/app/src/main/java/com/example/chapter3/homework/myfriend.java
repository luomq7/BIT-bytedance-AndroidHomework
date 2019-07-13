package com.example.chapter3.homework;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.chapter3.homework.ui.myfriend.MyfriendFragment;

public class myfriend extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myfriend_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MyfriendFragment.newInstance())
                    .commitNow();
        }
    }
}
