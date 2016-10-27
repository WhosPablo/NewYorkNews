package com.whospablo.newyorknews.services;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class NewsArticleClientParams {
    public String query;
    public String begin_date;
    public String end_date;
    public String sort;
    public List<String> news_desk_values;
}
