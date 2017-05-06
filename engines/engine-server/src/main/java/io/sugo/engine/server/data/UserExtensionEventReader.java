package io.sugo.engine.server.data;

import com.alibaba.fastjson.JSON;
import com.metamx.common.logger.Logger;
import io.sugo.pio.common.utils.HttpClientUtil;
import io.sugo.pio.engine.common.data.DataReader;
import io.sugo.pio.engine.common.data.Event;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by penghuan on 2017/4/26.
 */
public class UserExtensionEventReader implements DataReader<List<Event>, String> {
    private static final Logger log = new Logger(UserExtensionEventReader.class);
    private final String queryTarget;
    private final String url;

    // For test
    private String filePath = null;

    public static class QueryIORuntimeException extends RuntimeException {
        public QueryIORuntimeException() {
            super();
        }

        public QueryIORuntimeException(String message) {
            super(message);
        }
    }

    public static class QeuryEmptyRuntimeException extends RuntimeException {
        public QeuryEmptyRuntimeException() {
            super();
        }

        public QeuryEmptyRuntimeException(String message) {
            super(message);
        }
    }

    public static class TargetEmptyRuntimeException extends RuntimeException {
        public TargetEmptyRuntimeException() {
            super();
        }

        public TargetEmptyRuntimeException(String message) {
            super(message);
        }
    }

    public static class CandidateEmptyRuntimeException extends RuntimeException {
        public CandidateEmptyRuntimeException() {
            super();
        }

        public CandidateEmptyRuntimeException(String message) {
            super(message);
        }
    }

    public static class CandidateClearEmptyRuntimeException extends RuntimeException {
        public CandidateClearEmptyRuntimeException() {
            super();
        }

        public CandidateClearEmptyRuntimeException(String message) {
            super(message);
        }
    }

    public static class HasStringRuntimeException extends RuntimeException {
        public HasStringRuntimeException() {
            super();
        }

        public HasStringRuntimeException(String message) {
            super(message);
        }
    }

    public UserExtensionEventReader(String queryTarget, String url) {
        this.queryTarget = queryTarget;
        this.url = url;
    }

    private static class EventIterator implements Iterator<String> {
        private Iterator<Event> eventIter;
        public EventIterator(List<Event> repository) {
            this.eventIter = repository.iterator();
        }

        @Override
        public boolean hasNext() {
            return eventIter.hasNext();
        }

        @Override
        public String next() {
            return Event.serialize(eventIter.next());
        }
    }

    @Override
    public Iterator<List<Event>> getRepositoryIterator() {
        List<Event> candidateEventList = findCandidate();
        List<Event> targetEventList = findTarget();
        candidateEventList.removeAll(targetEventList);
        log.info(String.format("target:[%d],candidate:[%d]", targetEventList.size(), candidateEventList.size()));
        if (candidateEventList.size() == 0) {
            throw new CandidateClearEmptyRuntimeException();
        }
        List<List<Event>> list = new ArrayList<List<Event>>();
        list.add(candidateEventList);
        list.add(targetEventList);
        return list.iterator();
    }

    @Override
    public Iterator<String> getDataIterator(List<Event> repository) {
        return new EventIterator(repository);
    }

    @Override
    public void close() {}

    public List<Event> findTarget() {
        UserExtensionJsonQueryArgs queryArgs = JSON.parseObject(this.queryTarget, UserExtensionJsonQueryArgs.class);
        // TODO: 目标用户群的数限制在 1000000, 这个需要调整
        queryArgs.dimensionExtraSettings.get(0).setLimit(1000000);
        List<HashMap<String, Object>> resultSet = null;
        try {
            filePath = "/Users/penghuan/Tmp/sugo/user_target.json";
            resultSet = queryData(queryArgs);
        } catch (QeuryEmptyRuntimeException e) {
            throw new TargetEmptyRuntimeException();
        }
        List<Event> eventList = new ArrayList<>();
        MapStringToEventFunc mapStringToEventFunc = new MapStringToEventFunc("target", queryArgs.getKeyName(), queryArgs.getValueNameList());
        for (HashMap<String, Object> data: resultSet) {
            eventList.add(mapStringToEventFunc.call(data));
        }
        return eventList;
    }

    public List<Event> findCandidate() {
        UserExtensionJsonQueryArgs queryArgs = JSON.parseObject(this.queryTarget, UserExtensionJsonQueryArgs.class);
        // TODO: 候选用户群的数限制在 100000, 这个需要调整
        queryArgs.setFiltersToNull();
        queryArgs.dimensionExtraSettings.get(0).setLimit(1000000);
        List<HashMap<String, Object>> resultSet = null;
        try {
            filePath = "/Users/penghuan/Tmp/sugo/user_candidate.json";
            resultSet = queryData(queryArgs);
        } catch (QeuryEmptyRuntimeException e) {
            throw new CandidateEmptyRuntimeException();
        }
        List<Event> eventList = new ArrayList<>();
        MapStringToEventFunc mapStringToEventFunc = new MapStringToEventFunc("candidate", queryArgs.getKeyName(), queryArgs.getValueNameList());
        for (HashMap<String, Object> data: resultSet) {
            eventList.add(mapStringToEventFunc.call(data));
        }
        return eventList;
    }

    private List<HashMap<String, Object>> queryData(UserExtensionJsonQueryArgs queryArgs) {
        String host = queryArgs.getHost();
        String query = JSON.toJSONString(queryArgs);
        String data = null;
        if (queryArgs.readFromLocal()) {
            data = readFromFile(filePath);
        } else {
            data = readFromHttp(query, host);
        }
        if (data == null || data.isEmpty()) {
            log.error("request empty");
            throw new UserExtensionEventReader.QeuryEmptyRuntimeException();
        }
        List<UserExtensionJsonQueryResult> result = JSON.parseArray(data, UserExtensionJsonQueryResult.class);
        if (result.size() == 0) {
            log.error("request empty");
            throw new UserExtensionEventReader.QeuryEmptyRuntimeException();
        }
        List<HashMap<String, Object>> resultSet = result.get(0).resultSet;
        if (resultSet.size() == 0) {
            log.error("request empty");
            throw new UserExtensionEventReader.QeuryEmptyRuntimeException();
        }

        // 找出暂时不符合计算格式的维度
        HashMap<String, Boolean> stringValueName = findStringValueName(resultSet.get(0));
        if (stringValueName.size() == resultSet.get(0).size()) {
            throw new UserExtensionEventReader.HasStringRuntimeException();
        }
        for (String name: stringValueName.keySet()) {
            queryArgs.addIgnoreValueName(name);
        }
        return resultSet;
    }

    private HashMap<String, Boolean> findStringValueName(HashMap<String, Object> data) {
        HashMap<String, Boolean> stringValueName = new HashMap<String, Boolean>();
        for (String key: data.keySet()) {
            if (String.class.isInstance(data.get(key))) {
                stringValueName.put(key, true);
            }
        }
        return stringValueName;
    }

    private String readFromHttp(String query, String host) {
        String request = null;
        if (url == null || url.isEmpty()) {
            request = host + "/api/query-druid?qs=" + URLEncoder.encode(query);
        } else {
            request = url + URLEncoder.encode(query);
        }
        log.info(String.format("queryArgs:%s", query));
        log.info(String.format("request %s", request));
        String result = null;
        try {
            result = HttpClientUtil.getWithTimeout(request, 10, 60);
        } catch (IOException e) {
            result = null;
            e.printStackTrace();
            throw new UserExtensionEventReader.QueryIORuntimeException("query data time out");
        }

        return result;
    }

    private String readFromFile(String filepath) {
        String data = null;
        File file = new File(filepath);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            data = br.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    public static class UserExtensionEvent extends Event {

        public UserExtensionEvent() {}

        public UserExtensionEvent(long timestamp) {
            super(timestamp);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof  UserExtensionEvent)) {
                return false;
            }
            UserExtensionEvent userExtensionEvent = (UserExtensionEvent)obj;
            return this.getProperties().get("key").toString().equals(userExtensionEvent.getProperties().get("key").toString());
        }
    }

    private static class MapStringToEventFunc {
        private String label;
        private String keyName;
        private List<String> valueNameList;

        MapStringToEventFunc(String label, String keyName, List<String> valueNameList) {
            this.label = label;
            this.keyName = keyName;
            this.valueNameList = valueNameList;
        }

        public Event call(HashMap<String, Object> data) {
            UserExtensionEvent userExtensionEvent = new UserExtensionEvent(System.currentTimeMillis());
            userExtensionEvent.putProperties("label", label);
            userExtensionEvent.putProperties("key", data.get(keyName));
            List<Object> values = new ArrayList<Object>();
            for (String valueName: valueNameList) {
                values.add(data.get(valueName));
            }
            userExtensionEvent.putProperties("values", values);
            return userExtensionEvent;
        }
    }
}
