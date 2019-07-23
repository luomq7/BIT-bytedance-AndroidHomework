package com.example.chanyue.network;


import com.example.chanyue.bean.FeedResponse;
import com.example.chanyue.bean.PostVideoResponse;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * @author Xavier.S
 * @date 2019.01.17 20:38
 */
public interface IMiniDouyinService {
    String Host = "http://test.androidcamp.bytedance.com/";

    //上传视频
    @Multipart
    @POST("mini_douyin/invoke/video")
    Call<PostVideoResponse> createVideo(
            @Query("student_id") String studentId,
            @Query("user_name") String userName,
            @Part MultipartBody.Part image,
            @Part MultipartBody.Part video);

    //获取视频流
    @GET("/mini_douyin/invoke/video")
    Call<FeedResponse> fetchFeed();
}

