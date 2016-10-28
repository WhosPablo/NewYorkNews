package com.whospablo.newyorknews.models;

import org.parceler.Parcel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Parcel
public class Filters {
    public static final String SORT = "sort";
    public static final String QUERY = "q";
    public static final String NEWS_DESK = "fq";
    public static final String BEGIN_DATE = "begin_date";
    public static final String END_DATE = "end_date";
    public static final String SORT_BY_NEWEST = "newest";
    public static final String SORT_BY_OLDEST = "oldest";


    public String query;
    public String begin_date;
    public String end_date;
    public String sort;
    public List<String> news_desk_values;

    public Filters(){
        sort = SORT_BY_NEWEST;
        begin_date = "15000101";
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        end_date = format.format(new Date());
        news_desk_values = new ArrayList<>();
    }

    public String getNewsDeskValuesForParams(){
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for(String s:news_desk_values){
            sb.append("\"");
            sb.append(s);
            sb.append("\"");
        }
        sb.append(")");

        return sb.toString();
    }


}
