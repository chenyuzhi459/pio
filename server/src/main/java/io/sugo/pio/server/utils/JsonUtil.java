package io.sugo.pio.server.utils;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class JsonUtil {

    public static String getString(JSONObject jsonObject, String name) {
        try {
            String value = null;
            if (!jsonObject.isNull(name)) {
                value = jsonObject.getString(name);
            }
            return value;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getInt(JSONObject jsonObject, String name) {
        try {
            int value = 0;
            if (!jsonObject.isNull(name)) {
                value = jsonObject.getInt(name);
            }
            return value;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
