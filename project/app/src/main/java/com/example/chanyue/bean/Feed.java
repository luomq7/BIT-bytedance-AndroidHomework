package com.example.chanyue.bean;
import com.google.gson.annotations.SerializedName;

public class Feed {

    /*
        student_id
        user_name
        image_url
        video_url
         */
//    @SerializedName("student_id") private String student_id;
//    @SerializedName("user_name") private String user_name;
//    @SerializedName("image_url") private String image_url;
//    @SerializedName("video_url") private String video_url;

    @SerializedName("student_id") private String student_id;
    @SerializedName("user_name") private String user_name;
    @SerializedName("image_url") private String image_url;
    @SerializedName("video_url") private String video_url;
    @SerializedName("id") private String id;
    @SerializedName("image_w") private int image_w;
    @SerializedName("image_h") private int image_h;
    @SerializedName("createdAt") private String createdAt;
    @SerializedName("updatedAt") private String updatedAt;
    @SerializedName("v") private int v;




    public String getStudentId() {
        return student_id;
    }
    public void setStudentId(String student_id) {
        this.student_id = student_id;
    }

    public String getUserName() {
        return user_name;
    }
    public void setUserName(String user_name) {
        this.user_name = user_name;
    }

    public String getImageUrl() {
        return image_url;
    }
    public void setImageUrl(String image_url) {
        this.image_url = image_url;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getVideoUrl() {
        return video_url;
    }
    public void setVideoUrl(String video_url) {
        this.video_url = video_url;
    }

    public int getImage_w() {
        return image_w;
    }
    public void setImage_w(int image_w) {
        this.image_w = image_w;
    }

    public int getImage_h() {
        return image_h;
    }
    public void setImage_h(int image_h) {
        this.image_h = image_h;
    }

    public String getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getV() {
        return v;
    }
    public void setV(int v) {
        this.v = v;
    }

    @Override public String toString(){
        return "Value{" +
                "student_id=" + student_id +
                ",user_name=" + user_name +
                ",image_url=" + image_url +
                ",video_url=" + video_url +
                "}";
    }
}
