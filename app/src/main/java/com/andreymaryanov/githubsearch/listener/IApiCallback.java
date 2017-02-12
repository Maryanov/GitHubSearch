package com.andreymaryanov.githubsearch.listener;


/**
 * Created by Andrey on 08.02.2017.
 */

public interface IApiCallback<T> {
    void onSuccess(T data);
    void onError(Throwable t);
}
