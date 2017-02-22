package io.sugo.pio.common.utils;

import com.metamx.common.logger.Logger;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class HttpClientUtil {

    private static final Logger logger = new Logger(HttpClientUtil.class);

    private static final MediaType DEFAULT_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

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

}
