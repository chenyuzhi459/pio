package io.sugo.pio.operator;

import org.apache.commons.lang.time.FastDateFormat;

import java.util.Date;

/**
 */
public class OperatorLog {

    public static final String INFO = "INFO";
    public static final String WARN = "WARN";
    public static final String ERROR = "ERROR";

    private static final String DIV_LEFT_INFO = "<div>";
    private static final String DIV_LEFT_WARN = "<div>";
    private static final String DIV_LEFT_ERROR = "<div class=\"color-red\">";
    private static final String DIV_RIGHT = "</div>";

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
    public String toString() {
        String divLeft = INFO.equals(level) ? DIV_LEFT_INFO : WARN.equals(level) ? DIV_LEFT_WARN : DIV_LEFT_ERROR;

        StringBuilder sb = new StringBuilder(divLeft);
        sb.append("[").append(datetime).append("] ")
                .append(level).append(" ")
                .append(message).append(DIV_RIGHT);

        return sb.toString();
    }

}
