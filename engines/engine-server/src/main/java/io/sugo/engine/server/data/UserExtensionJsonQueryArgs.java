package io.sugo.engine.server.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by penghuan on 2017/4/25.
 */
public class UserExtensionJsonQueryArgs {

    public String host;
    public String druid_datasource_id;
    public String since;
    public String until;
    public String relativeTime;
    public String timezone;
    public String granularity;
    public String groupByAlgorithm;
    public String alg;
    public Integer maxUser;
    public List<String> dimensions = new ArrayList<String>();
    public List<String> metrics = new ArrayList<String>();
    public List<QueryArgsDimensionExtraSettings> dimensionExtraSettings = new ArrayList<QueryArgsDimensionExtraSettings>();
    public List<QueryArgsFilters> filters;
    public List<String> valueNameList = new ArrayList<String>();
    public String dataSource;
    // 有些维度暂时不符合计算格式, 需要忽略掉
    private Map<String, Boolean> ignoreValueNameList = new HashMap<String, Boolean>();

    public static class QueryArgsDimensionExtraSettings {
        public Integer limit;
        public String sortDirect;
        public String sortCol;

        public void setLimit(Integer value) {
            limit = value;
        }
    }

    public static class QueryArgsDataConfig {
        public String hostAndPorts;
        public Boolean clusterMode;
        public String type;
    }

    public static class QueryArgsFilters {
        public String col;
        public String op;
        public String eq;

        public void setOp(String op) {
            this.op = op;
        }
    }

//    public void setGroupId(String value) {
//        groupId = value;
//    }

    public void setFiltersToNull() {
        filters = null;
    }

    public String getKeyName() {
        return dimensions.get(0);
    }

    public void addIgnoreValueName(String name) {
        ignoreValueNameList.put(name, true);
    }

    public List<String> getValueNameList() {
        List<String> valueNameList = new ArrayList<String>();
        for (String name: metrics) {
            if (!ignoreValueNameList.containsKey(name)) {
                valueNameList.add(name);
            }
        }
        return valueNameList;
    }

    public Integer getMaxUser() {
        return maxUser;
    }

    public String getHost() {
        return host;
    }

//    public void saveValueName(UserExtensionRepository repository) {
//        repository.delete(repository.getRecordValueNameFileName());
//        repository.create(repository.getRecordValueNameFileName());
//        try {
//            FileWriter writer = new FileWriter(repository.getRecordValueNameFile());
//            List<String> valueNameList = getValueNameList();
//            for (int i = 0; i < valueNameList.size(); i++) {
//                writer.write(String.format("%d:%s\n", i, valueNameList.get(i)));
//            }
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new RuntimeException(String.format("Write to %s failed", repository.getRecordValueNameFile()));
//        }
//    }

    public Boolean readFromLocal() {
        if (dataSource != null && dataSource.equals("local")) {
            return true;
        } else {
            return false;
        }
    }
}
