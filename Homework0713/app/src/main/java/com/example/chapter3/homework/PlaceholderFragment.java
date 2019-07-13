package com.example.chapter3.homework;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.airbnb.lottie.LottieAnimationView;

public class PlaceholderFragment extends Fragment {
private LottieAnimationView lottieView;
private ListView listView;
private AnimatorSet animatorSet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO ex3-3: 修改 fragment_placeholder，添加 loading 控件和列表视图控件
        View conView =inflater.inflate(R.layout.fragment_placeholder, container, false);
        lottieView = conView.findViewById(R.id.lottie_view);
        listView = conView.findViewById(R.id.list_item);
        String listItem[] = {"item1","item2","item3","item4","item5","item6","item7","item1","item2","item3","item4","item5","item6","item7"};

        //???为什么context要用getActivity()
        //getView()就是一个context，context是activity的子类
        listView.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,listItem));
        listView.setAlpha(0);

        return conView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getView().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 这里会在 5s 后执行
                // TODO ex3-4：实现动画，将 lottie 控件淡出，列表数据淡入
                ObjectAnimator animatorlistView = ObjectAnimator.ofFloat(listView,"alpha",0,1);
                animatorlistView.setRepeatCount(0);
                animatorlistView.setDuration(5000);

                ObjectAnimator animatorlottieView = ObjectAnimator.ofFloat(lottieView,"alpha",1,0);
                animatorlottieView.setRepeatCount(0);
                animatorlottieView.setDuration(5000);

                animatorSet = new AnimatorSet();
                animatorSet.playTogether(animatorlistView,animatorlottieView);
                animatorSet.start();

            }
        }, 5000);
    }
}
