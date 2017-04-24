package io.sugo.pio.operator;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.time.FastDateFormat;

import java.util.Date;

/**
 */
public class OperatorLog {

    public static final String INFO = "INFO";
    public static final String WARN = "WARN";
    public static final String ERROR = "ERROR";

    public static final FastDateFormat FDF = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss,sss");

    public OperatorLog(String message, String level) {
        this.message = message;
        this.level = level;
        this.datetime = FDF.format(new Date());
    }

    private String message;

    private String level;

    private String datetime;

    @Override
    @JsonProperty("message")
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(datetime).append("] ")
                .append(level).append(" ")
                .append(message);

        return sb.toString();
    }

    @JsonProperty("level")
    public String getLevel() {
        return level;
    }
}
