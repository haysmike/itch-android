package io.alpacafarm.itch;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ImgurRestClient {
    private static final String BASE_URL = "https://api.imgur.com/3/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    static {
        client.addHeader("Authorization", "Client-ID " + Secrets.IMGUR_CLIENT_ID);
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
