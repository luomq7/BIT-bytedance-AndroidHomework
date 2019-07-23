package com.example.chanyue.bean;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class FeedResponse {
    @SerializedName("feeds")
    private List<Feed> feeds;
    @SerializedName("success")
    private boolean success;
    @SerializedName("myfeeds")
    private List<Feed> myfeeds;


    public List<Feed> getFeeds() {
        System.out.println(feeds);
        return feeds;
    }

    public List<Feed> getMyfeeds() {
        myfeeds = new ArrayList<Feed>();
        for (int i = 0; i < feeds.size(); i++) {
            if (feeds.get(i).getStudentId().equals("1234509")) {
                myfeeds.add(feeds.get(i));
            }
        }
        return myfeeds;
    }

    public void setMyfeeds(List<Feed> myfeeds) {
        this.myfeeds = myfeeds;
    }

    public void setFeeds(List<Feed> feeds) {
        this.feeds = feeds;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}