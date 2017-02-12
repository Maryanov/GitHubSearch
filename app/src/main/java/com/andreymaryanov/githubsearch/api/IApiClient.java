package com.andreymaryanov.githubsearch.api;

import com.andreymaryanov.githubsearch.model.Feed;

import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Andrey on 08.02.2017.
 */

public interface IApiClient {

    @GET(ApiContants.SEARCH_REPOSITORIES)
    Call<Feed> getTextSearch(@Query("q") String q, @Query("sort") String sort, @Query("order") String order);

    @GET(ApiContants.SEARCH_REPOSITORIES)
    Call<Feed> getTextSearch(@Query("q") String q, @Query("sort") String sort, @Query("order") String order,
                             @Query("page") int page);

    @GET(ApiContants.SEARCH_REPOSITORIES)
    Call<Feed> getTextSearch(@Query("q") String q, @Query("sort") String sort, @Query("order") String order,
                             @Query("page") int page, @Query("per_page") int perPage);

}
