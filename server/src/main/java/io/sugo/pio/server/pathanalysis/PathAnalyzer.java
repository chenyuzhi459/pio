package io.sugo.pio.server.pathanalysis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.metamx.common.logger.Logger;
import io.sugo.pio.common.utils.HttpClientUtil;
import io.sugo.pio.data.fetcher.DataFetcherConfig;
import io.sugo.pio.server.pathanalysis.model.AccessPath;
import io.sugo.pio.server.pathanalysis.model.AccessTree;
import io.sugo.pio.server.pathanalysis.model.PathNode;
import io.sugo.pio.server.pathanalysis.vo.PageAccessRecordVo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 */
public class PathAnalyzer {

    private static final Logger log = new Logger(PathAnalyzer.class);

    private static final ObjectMapper jsonMapper = new ObjectMapper();

    private static final JavaType javaType = jsonMapper.getTypeFactory().constructParametrizedType(List.class, ArrayList.class, DruidResult.class);

    private final String queryUrl;

    private final TreePlanter planter;

    @Inject
    public PathAnalyzer(DataFetcherConfig config, TreePlanter planter) {
        Preconditions.checkNotNull(config.getUrl(), "must specify parameter: pio.broker.data.fetcher.url");
        this.queryUrl = config.getUrl();
        this.planter = planter;
    }

    public AccessTree getAccessTree(String queryStr, String homePage, boolean reversed) {
        int depth = reversed ? PathAnalysisConstant.TREE_DEPTH_REVERSE : PathAnalysisConstant.TREE_DEPTH_NORMAL;

        log.info("Begin to fetch path analysis data...");
        long now = System.currentTimeMillis();

        List<PageAccessRecordVo> records = fetchData(queryStr);

        long after = System.currentTimeMillis();
        log.info("Fetch path analysis data ended, cost %d million seconds.", after - now);

        log.info("Begin to path analysis...");
        now = System.currentTimeMillis();

        records.sort(reversed ? PageAccessRecordVo.DESC_COMPARATOR : PageAccessRecordVo.ASC_COMPARATOR);

        if (records.size() > 0) {
            boolean startAnalysis = false;
            PageAccessRecordVo preRecord = null;
            AccessPath path = null;
            int layer = 0;
            Long preAccessTime = null;

            for (PageAccessRecordVo record : records) {
                if (record.getPageName().equals(homePage)) {
                    startAnalysis = true;
                }
                if (startAnalysis) {
                    if (path == null) {
                        path = new AccessPath();
                    }

                    if (sameSession(preRecord, record, homePage)) {
                        // Discard the nodes whose layer greater than depth
                        if (layer <= depth) {
                            if (!sameNode(preRecord, record)) {
                                PathNode node = new PathNode(record.getPageName(), ++layer);
                                node.setUserId(record.getUserId());
                                if (preAccessTime != null) {
                                    node.setStayTime(record.getAccessTime().getTime() - preAccessTime);
                                }
                                path.addNode(node);
                            }
                            preRecord = record;
                            preAccessTime = record.getAccessTime().getTime();
                        }
                    } else {
                        // Add path for growing tree
                        planter.addPath(path);

                        // Clear all variables to initiate status
                        startAnalysis = false;
                        preRecord = null;
                        path = null;
                        layer = 0;
                        preAccessTime = null;
                    }
                }
            }

            // The last path that is not cleaned
            if (path != null) {
                planter.addPath(path);
            }
        }

        after = System.currentTimeMillis();
        log.info("Path analysis ended, cost %d million seconds.", after - now);

        return planter.getRoot();
    }

    private List<PageAccessRecordVo> fetchData(String queryStr) {
        String resultStr = "";
        try {
            log.info("Begin to fetch path analysis data from url: [%s], params: [%s].", queryUrl, queryStr);
            resultStr = HttpClientUtil.post(queryUrl, queryStr);
        } catch (IOException e) {
            log.error("Query druid '%s' with parameter '%s' failed: ", queryUrl, queryStr);
        }

        final List<PageAccessRecordVo> recordList = new ArrayList<>();
        try {
            List<DruidResult> druidResult = jsonMapper.readValue(resultStr, javaType);
            if (druidResult.size() > 0 && druidResult.get(0).getResult() != null) {
                log.info("Fetch %d path analysis data from druid %s.", druidResult.get(0).getResult().getEvents().size(), queryUrl);
                druidResult.get(0).getResult().getEvents().forEach(events -> {
                    recordList.add(events.getEvent());
                });
            } else {
                log.info("Fetch empty path analysis data from druid %s.", queryUrl);
            }

        } catch (IOException e) {
            log.warn("Deserialize druid result to type [" + DruidResult.class.getName() +
                    "] list failed, details:" + e.getMessage());

            try {
                DruidError errorResult = jsonMapper.readValue(resultStr, DruidError.class);
                log.error("Fetch path analysis data from druid failed: %s", errorResult.getError());
            } catch (IOException e1) {
                log.warn("Deserialize druid error result to type [" + DruidError.class.getName() +
                        "] failed, details:" + e.getMessage());
            }
        }

        return recordList;
    }

    private boolean sameSession(PageAccessRecordVo preRecord, PageAccessRecordVo currentRecord, String firstPage) {
        // First page
        if (preRecord == null && currentRecord.getPageName().equals(firstPage)) {
            return true;
        }

        Date preTime = preRecord.getAccessTime();
        Date currentTime = currentRecord.getAccessTime();
        long interval = Math.abs(currentTime.getTime() - preTime.getTime());

        return preRecord.getSessionId().equals(currentRecord.getSessionId()) &&
                interval <= PathAnalysisConstant.SESSION_EXPIRED_TIME;
    }

    private boolean sameNode(PageAccessRecordVo preRecord, PageAccessRecordVo currentRecord) {
        if (preRecord == null) {
            return false;
        }

        return preRecord.getPageName().equals(currentRecord.getPageName());
    }

    private static class DruidResult {
        ResultDetail result;
        Date timestamp;

        public ResultDetail getResult() {
            return result;
        }

        public void setResult(ResultDetail result) {
            this.result = result;
        }

        public Date getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Date timestamp) {
            this.timestamp = timestamp;
        }

    }

    private static class ResultDetail {
        Map<String, Integer> pagingIdentifiers = Maps.newHashMap();
        private List<Events> events;

        public Map<String, Integer> getPagingIdentifiers() {
            return pagingIdentifiers;
        }

        public void setPagingIdentifiers(Map<String, Integer> pagingIdentifiers) {
            this.pagingIdentifiers = pagingIdentifiers;
        }

        public List<Events> getEvents() {
            return events;
        }

        public void setEvents(List<Events> events) {
            this.events = events;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Events {
        PageAccessRecordVo event;

        public PageAccessRecordVo getEvent() {
            return event;
        }

        public void setEvent(PageAccessRecordVo event) {
            this.event = event;
        }
    }

    private static class DruidError {
        String error;

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }
}
