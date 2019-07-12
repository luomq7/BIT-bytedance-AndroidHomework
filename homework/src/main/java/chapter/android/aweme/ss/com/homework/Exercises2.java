package chapter.android.aweme.ss.com.homework;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import chapter.android.aweme.ss.com.homework.widget.CircleImageView;

/**
 * 作业2：一个抖音笔试题：统计页面所有view的个数
 * Tips：ViewGroup有两个API
 * {@link android.view.ViewGroup #getChildAt(int) #getChildCount()}
 * 用一个TextView展示出来
 */
public class Exercises2 extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.im_list_item);
        View inflate = LayoutInflater.from(this).inflate(R.layout.im_list_item,null);
        int Count = getViewCount(inflate);
        Log.d("viewCount", "viewCount: " + Count);
//        System.out.println("viewCount = "+ Count);
        TextView viewCountCon = findViewById(R.id.viewCount);
        viewCountCon.setText("viewCount为" + Count);
    }

    public static int getViewCount(View view) {
        //getChildCount()方法仅返回其所包含的直接控件的数量
        //todo 补全你的代码
        int viewCount = 0;
        if(view == null){
            return 0;
        }
        viewCount++;
        if(view instanceof ViewGroup){
            for(int i =0;i<((ViewGroup) view).getChildCount();i++){
                View child = ((ViewGroup) view).getChildAt(i);
                if (child instanceof  ViewGroup){
                    viewCount += getViewCount(child);
                }
                else {
                    viewCount++;
                }
            }
        }

        return viewCount;
    }
}
