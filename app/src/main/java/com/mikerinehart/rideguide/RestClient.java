package com.mikerinehart.rideguide;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by Mike on 12/28/2014.
 */
public class RestClient {

    private static final String BASE_URL = "http://mikerinehart.me/api/v1/";
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler rh)
    {
        client.get(getAbsoluteUrl(url), params, rh);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler rh)
    {
        client.post(getAbsoluteUrl(url), params, rh);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
