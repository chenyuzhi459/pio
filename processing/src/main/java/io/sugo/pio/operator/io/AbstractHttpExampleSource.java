package io.sugo.pio.operator.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.net.MediaType;
import com.metamx.common.lifecycle.Lifecycle;
import com.metamx.http.client.HttpClient;
import com.metamx.http.client.HttpClientConfig;
import com.metamx.http.client.HttpClientInit;
import com.metamx.http.client.Request;
import com.metamx.http.client.response.InputStreamResponseHandler;
import io.sugo.pio.common.utils.HttpClientUtil;
import io.sugo.pio.operator.OperatorException;
import org.jboss.netty.handler.codec.http.HttpMethod;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.regex.Pattern;

public abstract class AbstractHttpExampleSource extends AbstractExampleSource {

    private static final Pattern urlPattern = Pattern.compile("^((ht)tps?):\\/\\/([\\w\\-]+(\\.[\\w\\-\\:]+)*\\/)*[\\w\\-]+(\\.[\\w\\-]+)*\\/?(\\?([\\w\\-\\.,@?^=%&:\\/~\\+#]*)+)?");

    public final ObjectMapper jsonMapper = new ObjectMapper();

    protected String httpGet(String url) {
        if (!isValidUrl(url)) {
            throw new OperatorException("Invalid request url:" + url);
        }

        String result;
        try {
            result = HttpClientUtil.get(url);
        } catch (IOException e) {
            throw new OperatorException("Http get failed, url: '" + url + "', reason: ", e, e);
        }

        return result;
    }

    protected <T> T httpGet(String url, Class<T> clazz) {
        String result = httpGet(url);
        return deserialize(result, clazz);
    }

    protected String httpPost(String url, String requestJson) {
        if (!isValidUrl(url)) {
            throw new OperatorException("Invalid request url:" + url);
        }

        String result;
        try {
            result = HttpClientUtil.post(url, requestJson);
        } catch (IOException e) {
            throw new OperatorException("Http post failed, url: '" + url + "', reason: ", e, e);
        }

        return result;
    }

    protected <T> T httpPost(String url, String requestJson, Class<T> clazz) {
        String result = httpPost(url, requestJson);
        return deserialize(result, clazz);
    }

    protected <T> T deserialize(String json, Class<T> clazz) {
        if (Objects.nonNull(json)) {
            ObjectReader reader = jsonMapper.readerFor(clazz);
            try {
                T instance = reader.readValue(json);
                return instance;
            } catch (IOException e) {
                return null;
            }
        }

        return null;
    }

    protected boolean isValidUrl(String url) {
        return !Strings.isNullOrEmpty(url) && urlPattern.matcher(url).matches();
    }

    public static void main(String[] args) {
        InputStreamResponseHandler RESPONSE_HANDLER = new InputStreamResponseHandler();
        ObjectMapper jsonMapper = new ObjectMapper();
//        com.metamx.http.client.HttpClient httpClient = new com.metamx.http.client.HttpClient();
        HttpClientConfig config = HttpClientConfig.builder().build();
        HttpClient httpClient = HttpClientInit.createClient(config, new Lifecycle());

        try {
            InputStream input = httpClient.go(new Request(
                    HttpMethod.GET,
                    new URL("http://192.168.0.212:8000/api/datasources/list")
            ).setContent(MediaType.JSON_UTF_8.toString(), jsonMapper.writeValueAsBytes("")),
                    RESPONSE_HANDLER).get();

            String result = jsonMapper.readValue(input, String.class);
            System.out.println(result);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }
}
