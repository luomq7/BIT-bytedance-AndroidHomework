package com.example.chanyue;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;

import com.bumptech.glide.Glide;
import com.example.chanyue.bean.Feed;
import com.example.chanyue.bean.FeedResponse;
import com.example.chanyue.utils.NetworkUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Url;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private RecyclerView mRv;
    private List<Feed> mFeeds = new ArrayList<>();



    public HomeFragment() {
        // Required empty public constructor
    }



    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mRv=view.findViewById(R.id.rv);

        mRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false));
        FeedClassAdapter mAdapter = new FeedClassAdapter(view.getContext(), mFeeds);
        mRv.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchFeed(mRv);

    }


    public void fetchFeed(View view) {

        /*
        if success, assign data to mFeeds and call mRv.getAdapter().notifyDataSetChanged()
        don't forget to call resetRefreshBtn() after response received
        */
        Call<FeedResponse> feedCall = NetworkUtils.getResponseWithRetrofitAsync1();
        feedCall.enqueue(new Callback<FeedResponse>() {
            @Override
            public void onResponse(Call<FeedResponse> call, Response<FeedResponse> response) {
                mFeeds.addAll(response.body().getFeeds());
                mRv.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<FeedResponse> call, Throwable t) {
                //Toast.makeText(getApplicationContext(),"FeedResponse",Toast.LENGTH_SHORT).show();
            }
        });

    }
    public class PictureViewHolder extends RecyclerView.ViewHolder{
        private View mView;
        private ImageView imageView;
        private TextView textView;

        public PictureViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            initView();
        }

        private void initView() {
            imageView = mView.findViewById(R.id.image);
            textView = mView.findViewById(R.id.txt_id);
        }

    }


}
