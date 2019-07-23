package com.example.chanyue.utils;

import com.example.chanyue.bean.FeedResponse;
import com.example.chanyue.bean.PostVideoResponse;
import com.example.chanyue.network.IMiniDouyinService;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Xavier.S
 * @date 2019.01.15 13:27
 */
public class NetworkUtils {

    public static String getResponseWithHttpURLConnection(String url) {
        String result = null;
        InputStream in = null;
        HttpURLConnection urlConnection = null;
        try {
            URL netUrl = new URL(url);
            urlConnection = (HttpURLConnection) netUrl.openConnection();
            urlConnection.setRequestMethod("GET");
            in = new BufferedInputStream(urlConnection.getInputStream());
            result = readStream(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private static String readStream(final InputStream inputStream) {
        final Scanner scanner = new Scanner(inputStream);
        scanner.useDelimiter("\\A");
        final String data = scanner.next();
        return data;
    }

    private static String readStreamBuffer(InputStream in) {
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String s;
            while ((s = reader.readLine()) != null) {
                result.append(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    public static Call<FeedResponse> getResponseWithRetrofitAsync1() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://test.androidcamp.bytedance.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(IMiniDouyinService.class).fetchFeed();
    }

    public static Call<PostVideoResponse> getResponseWithRetrofitAsyncOfPostVideoResponse(String studentId, String userName, MultipartBody.Part image, MultipartBody.Part video){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(IMiniDouyinService.Host)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(IMiniDouyinService.class).createVideo(studentId,userName,image,video);
    }

//    public static Call<PostVideoResponse> getResponseWithRetrofitAsyncOfPostVideoResponseChanyue(String studentId, String userName,MultipartBody.Part video){
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(IMiniDouyinService.Host)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        return retrofit.create(IMiniDouyinService.class).createVideo(studentId,userName,video);
//    }

    public static Call<FeedResponse> getResponseWithRetrofitAsyncOffeedResponse(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(IMiniDouyinService.Host)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(IMiniDouyinService.class).fetchFeed();
    }
}
