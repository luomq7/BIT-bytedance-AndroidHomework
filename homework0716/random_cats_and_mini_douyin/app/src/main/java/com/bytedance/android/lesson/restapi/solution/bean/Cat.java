package com.bytedance.android.lesson.restapi.solution.bean;

import com.google.gson.annotations.SerializedName;

/**
 * @author Xavier.S
 * @date 2019.01.17 18:08
 */
public class Cat {

    // TODO-C1 (1) Implement your Cat Bean here according to the response json
    /*
    *[{"breeds":[],"id":"d7q","url":"https://cdn2.thecatapi.com/images/d7q.jpg","width":612,"height":612}]
    *
     */
    /*
     *"breeds":[],
     * "id":"d7q",
     * "url":"https://cdn2.thecatapi.com/images/d7q.jpg",
     * "width":612,
     * "height":612
     */

    @SerializedName("id") private String id;
    @SerializedName("url") private String url;

    public String getId(){return id;}

    public  void  setId(String id){this.id = id;}

    public  String getUrl(){return  url;}

    public void setUrl(String url) {this.url = url;}

    @Override public String toString(){
        return "Cat{" +
                "id=" + id +
                ",url='" + url +
                '}';
    }
}
