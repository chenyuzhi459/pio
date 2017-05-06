package io.sugo.pio.engine.demo.data;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.metamx.common.logger.Logger;
import io.sugo.pio.common.utils.HttpClientUtil;
import io.sugo.pio.engine.common.data.UserExtensionRepository;
import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.input.Event;
import io.sugo.pio.engine.data.output.Repository;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by penghuan on 2017/3/30.
 */
public class UserExtBatchEventHose implements BatchEventHose {
    private static final Logger log = new Logger(UserExtBatchEventHose.class);
    private final String queryTarget;
    private final String url;
    private final Repository repository;
    private JavaRDD<Event> _rdd = null;

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

    public UserExtBatchEventHose(String queryTarget, String url, Repository repository) {
        this.queryTarget = queryTarget;
        this.url = url;
        this.repository = repository;
    }

    @Override
    public JavaRDD<Event> find(JavaSparkContext sc) {
        if (_rdd != null) {
            return _rdd;
        }

        JavaRDD<Event> rddCandidate = findCandidate(sc);
        JavaRDD<Event> rddTarget = findTarget(sc);
        Long userNumTarget = rddTarget.count();
//        if (userNumTarget == 0) {
//            throw new TargetEmptyRuntimeException("User group Targe empty");
//        }

        // 把 target 的用户 从 candidate 中去除
        JavaRDD<Event> rddCandidateClear = clearCandidate(sc, rddCandidate, rddTarget);
        Long userNumCandidate = rddCandidateClear.count();
        log.info(String.format("target:[%d],candidate:[%d]", userNumTarget, userNumCandidate));
        if (userNumCandidate == 0) {
            String predictFile = ((UserExtensionRepository)repository).getPredictDistanceFileName();
            repository.create(predictFile);
            throw new CandidateClearEmptyRuntimeException();
        }

//        // 打印出来看看
//        for (Event event: rddTarget.collect()) {
//            Map<String, Object> data = event.getProperties();
//            String label = (String) data.get("label");
//            String key = (String) data.get("key");
//            List<Object> values = (List<Object>) data.get("values");
//            System.out.println(label + " " + key + " " + values.toString());
//        }

        _rdd = rddTarget.union(rddCandidateClear);
//        _rdd = rddTarget.union(rddCandidateClear).persist(StorageLevel.MEMORY_AND_DISK());
        return _rdd;
    }

    @Override
    public JavaRDD<Event> find(JavaSparkContext sc, long starttime, long endTime) {
        return find(sc);
    }

    public JavaRDD<Event> findTarget(JavaSparkContext sc) {
        UserExtJsonQueryArgs queryArgs = JSON.parseObject(this.queryTarget, UserExtJsonQueryArgs.class);
        // TODO: 目标用户群的数限制在 1000000, 这个需要调整
        queryArgs.dimensionExtraSettings.get(0).setLimit(1000000);
        List<HashMap<String, Object>> resultSet = null;
        try {
            filePath = "/Users/penghuan/Tmp/sugo/user_target.json";
            resultSet = queryData(sc, queryArgs);
            queryArgs.saveValueName((UserExtensionRepository) repository);
        } catch (QeuryEmptyRuntimeException e) {
            throw new TargetEmptyRuntimeException();
        }
        return sc.parallelize(resultSet).map(
                new UserExtBatchEventHose.MapStringToEventFunc("target", queryArgs.getKeyName(), queryArgs.getValueNameList())
        );
    }

    public JavaRDD<Event> findCandidate(JavaSparkContext sc) {
        UserExtJsonQueryArgs queryArgs = JSON.parseObject(this.queryTarget, UserExtJsonQueryArgs.class);
        ((UserExtensionRepository)repository).setMaxUser(queryArgs.getMaxUser());
        // TODO: 候选用户群的数限制在 100000, 这个需要调整
        queryArgs.setFiltersToNull();
        queryArgs.dimensionExtraSettings.get(0).setLimit(1000000);
        List<HashMap<String, Object>> resultSet = null;
        try {
            filePath = "/Users/penghuan/Tmp/sugo/user_candidate.json";
            resultSet = queryData(sc, queryArgs);
        } catch (QeuryEmptyRuntimeException e) {
            throw new CandidateEmptyRuntimeException();
        }
        return sc.parallelize(resultSet).map(
                new UserExtBatchEventHose.MapStringToEventFunc("candidate", queryArgs.getKeyName(), queryArgs.getValueNameList())
        );
    }

    public JavaRDD<Event> clearCandidate(JavaSparkContext sc, JavaRDD<Event> rddCandidate, JavaRDD<Event> rddTarget) {
        JavaPairRDD<String, Event> rddTargetPair = rddTarget.mapToPair(new EventToPairFunc());
        return rddCandidate.mapToPair(new EventToPairFunc())
                .subtractByKey(rddTargetPair)
                .map(new PairToEventFunc());
    }

    private List<HashMap<String, Object>> queryData(JavaSparkContext sc, UserExtJsonQueryArgs queryArgs) {
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
            throw new QeuryEmptyRuntimeException();
        }
        List<UserExtJsonQueryResult> result = JSON.parseArray(data, UserExtJsonQueryResult.class);
        if (result.size() == 0) {
            log.error("request empty");
            throw new QeuryEmptyRuntimeException();
        }
        List<HashMap<String, Object>> resultSet = result.get(0).resultSet;
        if (resultSet.size() == 0) {
            log.error("request empty");
            throw new QeuryEmptyRuntimeException();
        }

        // 找出暂时不符合计算格式的维度
        HashMap<String, Boolean> stringValueName = findStringValueName(resultSet.get(0));
        if (stringValueName.size() == resultSet.get(0).size()) {
            throw new HasStringRuntimeException();
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
//        if (host != null && !host.isEmpty()) {
//            request = host + "/api/query-druid?qs=" + URLEncoder.encode(query);
//        } else {
//            request = url + URLEncoder.encode(query);
//        }

        if (url == null || url.isEmpty()) {
            request = host + "/api/query-druid?qs=" + URLEncoder.encode(query);
        } else {
            request = url + URLEncoder.encode(query);
        }
        log.info(String.format("queryArgs:%s", query));
        log.info(String.format("request %s", request));
        String result = null;
//        result = HttpClientUtil.get(request);
        try {
            result = HttpClientUtil.getWithTimeout(request, 10, 60);
        } catch (IOException e) {
            result = null;
            e.printStackTrace();
            throw new QueryIORuntimeException("query data time out");
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

    private static class MapStringToEventFunc implements Function<HashMap<String, Object>, Event> {
        private String label;
        private String keyName;
        private List<String> valueNameList;

        MapStringToEventFunc(String label, String keyName, List<String> valueNameList) {
            this.label = label;
            this.keyName = keyName;
            this.valueNameList = valueNameList;
        }

        @Override
        public Event call(HashMap<String, Object> data) throws Exception {
            Map<String, Object> map = Maps.newHashMap();
            map.put("label", label);
            map.put("key", data.get(keyName));
            List<Object> values = new ArrayList<Object>();
            for (String valueName: valueNameList) {
                values.add(data.get(valueName));
            }
            map.put("values", values);
            return new Event(System.currentTimeMillis(), map);
        }
    }

    private static class EventToPairFunc implements PairFunction<Event, String, Event> {

        @Override
        public Tuple2<String, Event> call(Event event) throws Exception {
            return new Tuple2<String, Event>(event.getProperties().get("key").toString(), event);
        }
    }

    private static class PairToEventFunc implements Function<Tuple2<String, Event>, Event> {

        @Override
        public Event call(Tuple2<String, Event> tuple) throws Exception {
            return tuple._2;
        }
    }

}
