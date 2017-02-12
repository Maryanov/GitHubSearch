package com.andreymaryanov.githubsearch.api;

import com.andreymaryanov.githubsearch.listener.IApiCallback;
import com.andreymaryanov.githubsearch.model.Feed;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Andrey on 08.02.2017.
 */

public class ApiService {
    public static IApiClient getApiClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiContants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient.Builder().build())
                .build();

        return retrofit.create(IApiClient.class);
    }

    public void getSearchResult(String textRequest, String sort, String order, int page, int length, final IApiCallback<Feed> listener) {

        getApiClient().getTextSearch(textRequest, sort, order, page, length)
                .enqueue(new Callback<Feed>() {
            @Override
            public void onResponse(Call<Feed> call, Response<Feed> response) {
                if (response.body() == null)
                    return;
                listener.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<Feed> call, Throwable t) {
                listener.onError(t);
            }
        });
    }
}
