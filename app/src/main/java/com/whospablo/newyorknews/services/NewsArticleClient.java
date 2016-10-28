package com.whospablo.newyorknews.services;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.whospablo.newyorknews.models.NewsArticle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class NewsArticleClient {
    private static final String API_BASE_URL = "https://api.nytimes.com/svc/search/v2/";
    private static final String API_KEY = "060f62504b6944278a5ae93095fec036";
    private AsyncHttpClient client;
    private RequestParams currentNewsArticleClientParams;

    public interface ResponseHandler{
        void onSuccess(ArrayList<NewsArticle> newsArticles);
        void onFailure(int statusCode, String response);
    }

    public NewsArticleClient() {
        this.client = new AsyncHttpClient();
    }

    private String getApiUrl(String relativeUrl) {
        return API_BASE_URL + relativeUrl;
    }

    public void getNewsArticles(RequestParams newsArticleClientParams, ResponseHandler handler){
        currentNewsArticleClientParams = newsArticleClientParams == null? new RequestParams(): newsArticleClientParams;
        getNewsArticles(0, currentNewsArticleClientParams, handler);
    }

    public void getMoreNewsArticles(int page, ResponseHandler handler){
        getNewsArticles(page, currentNewsArticleClientParams, handler);
    }

    private void getNewsArticles(final int page, RequestParams requestParams, final ResponseHandler handler) {
        String url = getApiUrl("articlesearch.json");
        requestParams.put("api-key", API_KEY);
        requestParams.put("page", page);
        client.get(url, requestParams,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray newsArticlesJSONArray;
                try {
                    newsArticlesJSONArray = response.getJSONObject("response").getJSONArray("docs");
                    handler.onSuccess(NewsArticle.fromJSONArray(newsArticlesJSONArray));
                    Log.d("DEBUG", "fetchNewsArticlesAsync: loaded page " + response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                handler.onFailure(statusCode, responseString);
                Log.d("DEBUG", "fetchNewsArticlesAsync: failed with response String "+ responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                handler.onFailure(statusCode, response.toString());
                Log.d("DEBUG", "fetchNewsArticlesAsync: failed with response JSONObject "+ statusCode);
            }
        });

    }
}
