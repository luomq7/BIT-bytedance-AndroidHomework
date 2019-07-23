package com.example.chanyue;


import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chanyue.bean.Feed;
import com.example.chanyue.bean.FeedResponse;
import com.example.chanyue.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalFragment extends Fragment {


    public PersonalFragment() {
        // Required empty public constructor
    }


    private RecyclerView mRv;
    private List<Feed> mFeeds = new ArrayList<>();


    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_personal, container, false);
        mRv = view.findViewById(R.id.rv);


        mRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        MyfeedClassAdapter mAdapter = new MyfeedClassAdapter(view.getContext(), mFeeds);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(),3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup(){
            @Override public int getSpanSize(int position) {
                return 1;
            }
        });
        mRv.setLayoutManager(gridLayoutManager);

        mRv.setAdapter(mAdapter);

        return view;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //initRecyclerView();
        fetchFeed(mRv);

    }

    public void fetchFeed(View view) {

    /*
    TODO-C2 (9) Send Request to fetch feed
    if success, assign data to mFeeds and call mRv.getAdapter().notifyDataSetChanged()
    don't forget to call resetRefreshBtn() after response received
    */
        Call<FeedResponse> feedCall = NetworkUtils.getResponseWithRetrofitAsync1();
        feedCall.enqueue(new Callback<FeedResponse>() {
            @Override
            public void onResponse(Call<FeedResponse> call, Response<FeedResponse> response) {

                mFeeds.addAll(response.body().getMyfeeds());
                mRv.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<FeedResponse> call, Throwable t) {
                //Toast.makeText(getApplicationContext(),"FeedResponse",Toast.LENGTH_SHORT).show();
            }
        });

    }

}