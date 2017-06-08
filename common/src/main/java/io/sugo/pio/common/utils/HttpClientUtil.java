package io.sugo.pio.common.utils;

import com.metamx.common.logger.Logger;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class HttpClientUtil {

    private static final Logger logger = new Logger(HttpClientUtil.class);

    private static final MediaType DEFAULT_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType TEXT_PLAIN_TYPE = MediaType.parse("text/plain; charset=utf-8");

    public static String get(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        Response response = client.newCall(request).execute();
        String result = response.body().string();

        return result;
    }

    public static String post(String url, String jsonData) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(DEFAULT_MEDIA_TYPE, jsonData);
        Request request = new Request.Builder().url(url).post(body).build();

        Response response = client.newCall(request).execute();
        String result = response.body().string();

        return result;
    }

    public static InputStream streamPost(String url, String jsonData) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(DEFAULT_MEDIA_TYPE, jsonData);
        Request request = new Request.Builder().url(url).post(body).build();

        Response response = client.newCall(request).execute();
        return response.body().byteStream();
    }

    public static String getWithTimeout(String url, Integer connectTimeout, Integer readTimeout) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder().url(url).build();

        Response response = client.newCall(request).execute();
        String result = response.body().string();

        return result;
    }

    public static OkHttpClient clientWithTimeout(Integer connectTimeout, Integer readTimeout) {
        return new OkHttpClient.Builder()
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .build();
    }

    public static RequestBody requestBody(String text, String typeDesc) {
        MediaType mediaType = MediaType.parse(typeDesc);
        return RequestBody.create(mediaType, text);
    }

    public static RequestBody requestBody(File file, String typeDesc) {
        MediaType mediaType = MediaType.parse(typeDesc);
        return RequestBody.create(mediaType, file);
    }

    public static String get(String url, OkHttpClient client) throws IOException {
        Request request = new Request.Builder().url(url).get().build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public static String post(String url, OkHttpClient client, RequestBody requestBody) throws IOException {
        Request request = new Request.Builder().url(url).post(requestBody).build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public static String delete(String url, OkHttpClient client) throws IOException {
        Request request = new Request.Builder().url(url).delete().build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

}
