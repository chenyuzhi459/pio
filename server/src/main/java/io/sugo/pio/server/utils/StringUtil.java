package io.sugo.pio.server.utils;

import java.util.UUID;

public class StringUtil {

    public static String generateUid() {
        String uid = UUID.randomUUID().toString();
        return uid.replace("-", "");
    }

    public static boolean isEmpty(String str){
        return str == null || str.trim().length() == 0;
    }
    public static boolean isNotEmpty(String str){
        return !isEmpty(str);
    }

}
