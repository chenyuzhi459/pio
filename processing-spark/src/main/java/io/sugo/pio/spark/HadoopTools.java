package io.sugo.pio.spark;

import io.sugo.pio.spark.connections.HadoopConnectionEntry;
import org.apache.commons.lang.RandomStringUtils;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 */
public class HadoopTools {
    private static Object randomLock = new Object();
    private static String lastRandomString = null;

    public static String getRandomString() {
        String randomString = String.valueOf(System.currentTimeMillis());
        synchronized(randomLock) {
            if(lastRandomString != null && lastRandomString.toLowerCase().startsWith(randomString.toLowerCase())) {
                lastRandomString = lastRandomString + "a";
            } else {
                lastRandomString = randomString;
            }

            return lastRandomString + "_" + RandomStringUtils.randomAlphanumeric(7).toLowerCase();
        }
    }

    public static String getNonEmptyUsername(HadoopConnectionEntry hadoopConnectionEntry) {
        String hadoopUsername = hadoopConnectionEntry.getHadoopUsername();
        return hadoopUsername != null && !hadoopUsername.isEmpty()? hadoopUsername:System.getProperty("user.name");
    }

    public static String getNormalizedOSUsername() {
        String user = System.getProperty("user.name", "UnknownUser");
        String nfdNormalizedString = Normalizer.normalize(user, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("").replaceAll("[^A-Za-z0-9]", "").toLowerCase(Locale.ENGLISH);
    }
}
