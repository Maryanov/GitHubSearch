package com.andreymaryanov.githubsearch.model.exception;

/**
 * Created by Andrey on 07.02.2017.
 */

public class NoInternetConnectionException extends Exception {

    public NoInternetConnectionException(){
        super("InternetConnectionException");
    }
}
