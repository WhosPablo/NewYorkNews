package com.whospablo.newyorknews;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.whospablo.newyorknews.adapters.NewsAdapter;
import com.whospablo.newyorknews.models.NewsArticle;
import com.whospablo.newyorknews.services.NewsArticleClient;
import com.whospablo.newyorknews.services.NewsArticleClientParams;
import com.whospablo.newyorknews.util.EmptyRecyclerView;
import com.whospablo.newyorknews.util.EndlessRecyclerViewScrollListener;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsActivity extends AppCompatActivity{

    private static final String LIST = "list";
    private static final String EXPANDED = "expanded";

    @BindView(R.id.activity_top_news_root) CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.app_bar) AppBarLayout mAppBarLayout;
    @BindView(R.id.news_recycler_view) EmptyRecyclerView mNewsArticlesRV;
    @BindView(R.id.toolbar) Toolbar mToolbar;

    ArrayList<NewsArticle> mNewsArticles;
    NewsArticleClient mNewsArticleClient;
    NewsArticleClientParams mCurrentSettings;

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putParcelable(LIST, Parcels.wrap(mNewsArticles));
        state.putBoolean(EXPANDED,
                (mAppBarLayout.getHeight() - mAppBarLayout.getBottom()) == 0);
    }

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_top_news);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        mCurrentSettings = new NewsArticleClientParams();


        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mNewsArticlesRV.setHasFixedSize(true);
        mNewsArticlesRV.setEmptyView(findViewById(R.id.content_empty));
        mNewsArticlesRV.setLayoutManager(layoutManager);
        mNewsArticlesRV.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                fetchMoreNewsArticlesAsync(page);
            }
        });

        if(state !=null && state.containsKey(LIST) && state.containsKey(EXPANDED)){
            mAppBarLayout.setExpanded(state.getBoolean(EXPANDED));
            mNewsArticles = Parcels.unwrap(state.getParcelable(LIST));
            mNewsArticlesRV.setAdapter(new NewsAdapter(this, mNewsArticles));
            Log.d("DEBUG", mNewsArticles.toString());
        } else {
            mNewsArticles = new ArrayList<>();
            mNewsArticlesRV.setAdapter(new NewsAdapter(this, mNewsArticles));
            fetchNewsArticlesAsync(null);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_top_news, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final MenuItem filterItem = menu.findItem(R.id.action_filter);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                mAppBarLayout.setExpanded(false);
                filterItem.setVisible(true);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                filterItem.setVisible(false);
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mNewsArticles.clear();
                mCurrentSettings.query = query;
                fetchNewsArticlesAsync(mCurrentSettings);
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_filter) {


            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void fetchNewsArticlesAsyncSuccess(ArrayList<NewsArticle> newsArticles) {
        mNewsArticles.addAll(newsArticles);
        mNewsArticlesRV.getAdapter().notifyDataSetChanged();
    }


    public void fetchNewsArticlesAsync(final NewsArticleClientParams newsArticleClientParams){
        if(mNewsArticleClient == null)
            mNewsArticleClient = new NewsArticleClient();
        mNewsArticleClient.getNewsArticles(newsArticleClientParams, new NewsArticleClient.ResponseHandler() {
            @Override
            public void onSuccess(ArrayList<NewsArticle> newsArticles) {
                fetchNewsArticlesAsyncSuccess(newsArticles);
            }

            @Override
            public void onFailure(int statusCode, String response) {
                Snackbar
                        .make(mCoordinatorLayout, "Unable to load more articles!", Snackbar.LENGTH_INDEFINITE)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                fetchNewsArticlesAsync(newsArticleClientParams);
                            }
                        })
                        .show();
            }
        });

    }
    public void fetchMoreNewsArticlesAsync(final int page){
        if(mNewsArticleClient == null)
            fetchNewsArticlesAsync(null);
        else
            mNewsArticleClient.getMoreNewsArticles(page, new NewsArticleClient.ResponseHandler() {
                @Override
                public void onSuccess(ArrayList<NewsArticle> newsArticles) {
                    fetchNewsArticlesAsyncSuccess(newsArticles);
                }

                @Override
                public void onFailure(int statusCode, String response) {
                    Snackbar
                            .make(mCoordinatorLayout, "Unable to load more articles!", Snackbar.LENGTH_INDEFINITE)
                            .setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    fetchMoreNewsArticlesAsync(page);
                                }
                            })
                            .show();
                }
            });
    }


}
