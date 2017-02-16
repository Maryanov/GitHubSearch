package com.andreymaryanov.githubsearch;

import android.app.SearchManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.andreymaryanov.githubsearch.adapters.*;
import com.andreymaryanov.githubsearch.api.ApiService;
import com.andreymaryanov.githubsearch.listener.IApiCallback;
import com.andreymaryanov.githubsearch.model.Feed;

import static com.andreymaryanov.githubsearch.AppConstants.*;
import static com.andreymaryanov.githubsearch.api.ApiContants.*;

public class MainActivity extends AppCompatActivity {

    public RecyclerView mainListView=null;
    private String textQuery = STRING_EMPTY;
    private Integer pageSearch = FIRST_PAGE;
    private Feed listFeed = new Feed();
    private Boolean isCommandLoading=false;
    private Integer feedLoadCurrentCount=0;
    private int listVisibleThreshold = THRESHOLD_LOAD_LIST;
    private int listFirstVisibleItem=0;
    private int listVisibleItemCount=0;
    private int listTotalItemCount=0;
    private LinearLayoutManager layoutManager=null;
    private ApiService apiService = new ApiService();
    Handler handler = new Handler();
    Integer a=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainListView = (RecyclerView) findViewById(R.id.mainListView);

        layoutManager = new LinearLayoutManager(MainActivity.this);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(MainActivity.this, listFeed.getItems());
        layoutManager = new LinearLayoutManager(MainActivity.this);
        mainListView.setAdapter(adapter);
        mainListView.setLayoutManager(layoutManager);

        mainListView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                listVisibleItemCount = mainListView.getChildCount();
                listTotalItemCount = layoutManager.getItemCount();
                listFirstVisibleItem = layoutManager.findFirstVisibleItemPosition();

                if (isViewLastItem(newState)){
                    isCommandLoading = true;
                    gitHuBSearch(textQuery, pageSearch);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                runSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                textQuery = newText;
                if (isCommandLoading) return false;
                handler.postDelayed(new Runnable() {
                    public void run() {
                        runSearch(textQuery);
                    }
                }, 2000);
                isCommandLoading = true;
                return false;
            }
        });
        return true;
    }

    void runSearch(String text) {
        mainListView.setVisibility(View.VISIBLE);
        textQuery = text;
        pageSearch = FIRST_PAGE;
        listVisibleThreshold=THRESHOLD_LOAD_LIST;
        feedLoadCurrentCount=0;
        gitHuBSearch(textQuery, pageSearch);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void gitHuBSearch(String query, Integer page) {

        if (isConnectInternet()) {
            apiService.getSearchResult(query,
                                       SEARCH_PARAMETER_SORT,
                                       SEARCH_PARAMETER_ORDER,
                                       page,
                                       LOAD_PAGE_SAZE,
                                       new IApiCallback<Feed>() {
                @Override
                public void onSuccess(Feed data) {
                    if ((data != null)) {
                        feedLoadCurrentCount = feedLoadCurrentCount + data.getItems().size();
                        if (pageSearch.equals(FIRST_PAGE)) {
                            pageSearch++;
                            listFeed = new Feed();
                            listFeed = data;
                            RecyclerViewAdapter adapter = new RecyclerViewAdapter(MainActivity.this, listFeed.getItems());
                            mainListView.setAdapter(adapter);
                        } else /*if (isCommandLoading)*/ {
                            pageSearch++;
                            listVisibleThreshold += LOAD_PAGE_SAZE;
                            listFeed.addNewItems(data.getItems());
                            RecyclerViewAdapter adapter = new RecyclerViewAdapter(MainActivity.this, listFeed.getItems());
                            Parcelable recyclerViewState = mainListView.getLayoutManager().onSaveInstanceState();//save
                            mainListView.setAdapter(adapter);
                            mainListView.getLayoutManager().onRestoreInstanceState(recyclerViewState);//restore
                            //isCommandLoading = false;
                        }
                    }  isCommandLoading = false;
                }

                @Override
                public void onError(Throwable t) {
                    isCommandLoading = false;
                }
            });
        }
    }

    private boolean isConnectInternet(){

        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null) {
            Toast.makeText(this, MESSAGE_ERROR, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isViewLastItem(int newState){
        return ((!isCommandLoading) && newState == RecyclerView.SCROLL_STATE_IDLE &&
                (listFirstVisibleItem + listVisibleItemCount) >= (listVisibleThreshold) &&
                listTotalItemCount >= LOAD_PAGE_SAZE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
