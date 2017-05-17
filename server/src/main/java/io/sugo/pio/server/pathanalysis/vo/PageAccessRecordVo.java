package io.sugo.pio.server.pathanalysis.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Comparator;
import java.util.Date;

/**
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PageAccessRecordVo {

    public static final Comparator ASC_COMPARATOR = (Comparator<PageAccessRecordVo>) (o1, o2) -> {
        int sessionIdCompareResult = o1.getSessionId().compareTo(o2.getSessionId());
        if (sessionIdCompareResult == 0) {
            return o1.getAccessTime().compareTo(o2.getAccessTime());
        }

        return sessionIdCompareResult;
    };

    public static final Comparator DESC_COMPARATOR = ASC_COMPARATOR.reversed();

    private String pageName;

    private Date accessTime;

    private String sessionId;

    private String userId;

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public Date getAccessTime() {
        return accessTime;
    }

    public void setAccessTime(Date accessTime) {
        this.accessTime = accessTime;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
