package com.cogwerks.potato.utils;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by annie on 3/17/18.
 */

public class NetworkUtils {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType BINARY = MediaType.parse("application/octet-stream; charset=binary");

    private static final OkHttpClient mHTTPClient = new OkHttpClient();

    public static String doHTTPGet(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = mHTTPClient.newCall(request).execute();

        try {
            return response.body().string();
        } finally {
            response.close();
        }
    }

    public static String doHTTPPost(String url) throws IOException {
        RequestBody body = RequestBody.create(JSON, "{\"url\":\"https://upload.wikimedia.org/wikipedia/commons/1/12/Broadway_and_Times_Square_by_night.jpg\"}");
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = mHTTPClient.newCall(request).execute();

        try {
            return response.body().string();
        } finally {
            response.close();
        }
    }
}
