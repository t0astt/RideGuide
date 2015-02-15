package com.mikerinehart.rideguide;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Mike on 12/28/2014.
 */
public class RestClient {

    private static final String BASE_URL = "http://mikerinehart.me/api/v1/";
    private static final String FB_GRAPH_URL = "https://graph.facebook.com/v2.2/";
    private static final String FB_GRAPH_ACCESS_TOKEN = "790452704337105|ZR4rIogqLQ67Fz__MetopcKZj_I";
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler rh) {
        client.get(getAbsoluteUrl(url), params, rh);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler rh) {
        client.post(getAbsoluteUrl(url), params, rh);
    }

    public static void fbGet(String url, RequestParams params, AsyncHttpResponseHandler rh) {
        client.get(getFbUrl(url), params, rh);
    }

    private static String getFbUrl(String relativeUrl) {
        String newUrl = "";
            newUrl = FB_GRAPH_URL + relativeUrl + "&access_token=" + URLEncoder.encode(FB_GRAPH_ACCESS_TOKEN);
        return newUrl;
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

}
