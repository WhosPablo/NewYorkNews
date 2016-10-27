package com.whospablo.newyorknews.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.whospablo.newyorknews.models.NewsArticle;
import com.whospablo.newyorknews.R;

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
        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private static final int VIEW_TYPE_EMPTY_LIST_PLACEHOLDER = 0;
    private static final int VIEW_TYPE_OBJECT_VIEW = 1;

    List<NewsArticle> news;
    Context context;

    public NewsAdapter(Context context, List<NewsArticle> news) {
        this.news = news;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = null;
        switch(viewType) {
            case VIEW_TYPE_EMPTY_LIST_PLACEHOLDER:
                itemView = LayoutInflater.
                        from(parent.getContext()).
                        inflate(R.layout.content_empty, parent, false);
                StaggeredGridLayoutManager.LayoutParams layoutParams = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                layoutParams.setFullSpan(true);
                itemView.setLayoutParams(layoutParams);
                break;
            case VIEW_TYPE_OBJECT_VIEW:
                itemView = LayoutInflater.
                        from(parent.getContext()).
                        inflate(R.layout.card_news_article, parent, false);
                break;
            default:
                itemView = LayoutInflater.
                        from(parent.getContext()).
                        inflate(R.layout.card_news_article, parent, false);
                break;
        }

        return new ViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        if (news.isEmpty()) {
            return VIEW_TYPE_EMPTY_LIST_PLACEHOLDER;
        } else {
            return VIEW_TYPE_OBJECT_VIEW;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NewsArticle newsArticle = news.get(position);

        holder.headline.setText(newsArticle.getHeadline());
        holder.summary.setText(newsArticle.getSummary());

    }

    @Override
    public int getItemCount() {
        return news.size();
    }




}
