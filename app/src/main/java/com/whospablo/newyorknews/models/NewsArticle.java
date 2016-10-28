package com.whospablo.newyorknews.models;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by pablo_arango on 10/25/16.
 */

@Parcel
public class NewsArticle {
    String headline;
    String summary;
    String webUrl;
    String imgUrl;

    public NewsArticle(){}

    public NewsArticle(String headline, String summary, String webUrl, String imgUrl) {
        this.headline = headline;
        this.summary = summary;
        this.webUrl = webUrl;
        this.imgUrl = imgUrl;
    }

    static NewsArticle fromJson(JSONObject jsonObject) {
        NewsArticle na = new NewsArticle();
        // Deserialize json into object fields
        try {
            na.headline = jsonObject.getJSONObject("headline").getString("main");
            na.summary = jsonObject.getString("snippet");
            na.webUrl = jsonObject.getString("web_url");

            JSONArray multimedia = jsonObject.getJSONArray("multimedia");
            if(multimedia.length()>0)
                na.imgUrl = multimedia.getJSONObject(multimedia.length()-1).getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        // Return new object
        return na;
    }

    public static ArrayList<NewsArticle> fromJSONArray(JSONArray array){
        ArrayList<NewsArticle> results = new ArrayList<>();

        for(int x = 0; x < array.length(); x++){
            try {
                results.add(NewsArticle.fromJson(array.getJSONObject(x)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return results;
    }

    public String getHeadline() {
        return headline;
    }

    public String getSummary() {
        return summary;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getImgUrl() {
        if(imgUrl!=null)
            return "http://www.nytimes.com/"+imgUrl;
        else
            return null;
    }
}
