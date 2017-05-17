package io.sugo.engine.server;

/**
 * Created by penghuan on 2017/4/25.
 */
public class Constants {

    // user extension
    public static final String USER_EXT_HTTP_GET = "http://192.168.0.212:8030/api/query-druid?qs=";
    public static final String USER_EXT_QUERY_TARGET = "{\"druid_datasource_id\": \"SJ9o92XGl\",\"since\":\"2017-03-20 23:59:59\",\"until\":\"2017-03-23 23:59:59\",\"relativeTime\": \"-3 days\",\"timezone\":\"Asia/Shanghai\",\"dimensions\":[\"UserID\"],\"metrics\":[\"wuxianjiRT_total\",\"wuxianjiRT_ryQXN3adl\",\"fh\",\"wuxianjiRT_BJi2qiIje\",\"test9\"],\"granularity\":\"P1D\",\"dimensionExtraSettings\":[{\"limit\":100,\"sortDirect\":\"desc\",\"sortCol\":\"wuxianjiRT_total\"}],\"groupByAlgorithm\":\"groupBy\",\"dataConfig\":{\"hostAndPorts\":\"192.168.0.212:6379\",\"clusterMode\":false,\"type\":\"redis\"},\"groupId\":\"SJiypXrsx\"}";
    public static final String USER_EXT_QUERY_CANDIDATE = "{\"druid_datasource_id\": \"SJ9o92XGl\",\"since\":\"2017-03-20 23:59:59\",\"until\":\"2017-03-23 23:59:59\",\"relativeTime\": \"-3 days\",\"timezone\":\"Asia/Shanghai\",\"dimensions\":[\"UserID\"],\"metrics\":[\"wuxianjiRT_total\",\"wuxianjiRT_ryQXN3adl\",\"fh\",\"wuxianjiRT_BJi2qiIje\",\"test9\"],\"granularity\":\"P1D\",\"dimensionExtraSettings\":[{\"limit\":100000,\"sortDirect\":\"desc\",\"sortCol\":\"wuxianjiRT_total\"}],\"groupByAlgorithm\":\"groupBy\",\"dataConfig\":{\"hostAndPorts\":\"192.168.0.212:6379\",\"clusterMode\":false,\"type\":\"redis\"}}";
    public static final String USER_EXT_MODEL_DISTANCE = "model_distance";
    public static final String USER_EXT_MODEL_FEATURE = "model_feature";
    public static final String USER_EXT_FEATURE_FILE = "user_extension_feature.txt";
    public static final String USER_EXT_VECTOR_FILE = "user_extension_vector.txt";
    public static final String USER_EXT_DESIGN_FILE = "user_extension_design.txt";
    public static final String USER_EXT_VALUE_NAME_FILE = "user_extension_value_name.txt";
}
