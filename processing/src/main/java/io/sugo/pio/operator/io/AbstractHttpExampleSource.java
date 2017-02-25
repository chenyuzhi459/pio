package io.sugo.pio.operator.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import io.sugo.pio.common.utils.HttpClientUtil;
import io.sugo.pio.operator.OperatorException;

import java.io.IOException;
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

    protected boolean isValidUrl(String url) {
        return !Strings.isNullOrEmpty(url) && urlPattern.matcher(url).matches();
    }
}
