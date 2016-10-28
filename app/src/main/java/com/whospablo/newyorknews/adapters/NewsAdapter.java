package com.whospablo.newyorknews.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.whospablo.newyorknews.R;
import com.whospablo.newyorknews.models.NewsArticle;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by pablo_arango on 10/25/16.
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.card_headline) TextView headline;
        @BindView(R.id.card_summary) TextView summary;
        @BindView(R.id.card_news_article_img) ImageView image;
        @BindView(R.id.card_news_article) LinearLayout newsArticleLayout;
        @BindView(R.id.card_share_button) ImageButton shareButton;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface NewsArticleItemListener {
        void onClickNewsArticle(View v, NewsArticle t);
        void onClickShare(View v, NewsArticle t );
    }

    List<NewsArticle> news;
    Context context;
    NewsArticleItemListener mCallback;

    public NewsAdapter(Context context, List<NewsArticle> news, NewsArticleItemListener callback) {
        this.news = news;
        this.context = context;
        this.mCallback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.card_news_article, parent, false);

        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final NewsArticle newsArticle = news.get(position);

        holder.newsArticleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onClickNewsArticle(view, newsArticle);
            }
        });

        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onClickShare(view, newsArticle);
            }
        });

        holder.headline.setText(newsArticle.getHeadline());
        holder.summary.setText(newsArticle.getSummary());

        if(newsArticle.getImgUrl() != null) {
            holder.image.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(newsArticle.getImgUrl())
                    .into(holder.image);
        } else {
            holder.image.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return news.size();
    }




}
