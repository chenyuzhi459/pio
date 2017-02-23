package io.sugo.pio.parameter;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A parameter type for linkage categories.
 * It get values through request a http url when observed others changed
 */
public class ParameterTypeLinkageHttpCategory extends ParameterTypeStringCategory {

    public static final String REQUEST_METHOD_GET = "get";

    public static final String REQUEST_METHOD_POST = "post";

    @JsonProperty
    private String observeKey = null;

    @JsonProperty
    private String requestUrl = null;

    @JsonProperty
    private String requestMethod = null;

    @JsonProperty
    private String[] categoryValues = new String[0];

    public ParameterTypeLinkageHttpCategory(String key, String observeKey, String description, String[] categories,
                                            String[] categoryValues, String requestUrl) {
        this(key, observeKey, description, categories, categoryValues, requestUrl,REQUEST_METHOD_GET);
    }

    public ParameterTypeLinkageHttpCategory(String key, String observeKey, String description, String[] categories,
                                            String[] categoryValues, String requestUrl, String requestMethod) {
        this(key, observeKey, description, categories, categoryValues, requestUrl, requestMethod, null);
    }

    public ParameterTypeLinkageHttpCategory(String key, String observeKey, String description, String[] categories,
                                            String[] categoryValues, String requestUrl, String requestMethod,
                                            String defaultValue) {
        this(key, observeKey, description, categories, categoryValues, requestUrl, requestMethod, defaultValue, true);
    }

    public ParameterTypeLinkageHttpCategory(String key, String observeKey, String description, String[] categories,
                                            String[] categoryValues, String requestUrl, String requestMethod,
                                            String defaultValue, boolean editable) {
        super(key, description, categories, defaultValue, editable);
        this.observeKey = observeKey;
        this.categoryValues = categoryValues;
        this.requestUrl = requestUrl;
        this.requestMethod = requestMethod;
        setOptional(defaultValue != null);
    }
}
