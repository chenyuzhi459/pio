package io.sugo.pio.recommend.bean;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.recommend.AlgorithmManager;
import io.sugo.pio.recommend.algorithm.AbstractAlgorithm;
import io.sugo.pio.server.utils.StringUtil;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.*;

public class RecStrategy implements Serializable {
    private String id;
    private String name;
    private List<String> types;
    private String orderField;
    private Boolean asc = false;
    private Integer percent;
    private int startPos;
    private int endPos;
    private DateTime createTime;
    private Integer num = 10;
    private Set<AbstractAlgorithm> algorithms;
    private Map<String, String> parmas;

    @JsonCreator
    public RecStrategy(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("types") List<String> types,
            @JsonProperty("orderField") String orderField,
            @JsonProperty("asc") Boolean asc,
            @JsonProperty("percent") Integer percent,
            @JsonProperty("params") Map<String, String> parmas
    ) {
        this.id = id;
        this.name = name;
        setTypes(types);
        this.orderField = orderField;
        this.asc = asc == null ? false : asc;
        this.percent = percent;
        this.parmas = parmas;
    }

    @JsonProperty
    public String getId() {
        if (StringUtil.isEmpty(id)) {
            id = StringUtil.generateUid();
        }
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
        if (algorithms != null && !algorithms.isEmpty()) {
            this.algorithms.clear();
        }
        for (String type : types) {
            addAlgorithm(AlgorithmManager.get(type));
        }
    }

    @JsonProperty
    public String getOrderField() {
        return orderField;
    }

    public void setOrderField(String orderField) {
        this.orderField = orderField;
    }

    @JsonProperty
    public Boolean getAsc() {
        return asc;
    }

    public void setAsc(Boolean asc) {
        this.asc = asc;
    }

    @JsonProperty
    public Integer getPercent() {
        if (percent == null) {
            return 0;
        }
        return percent;
    }

    public void setPercent(Integer percent) {
        this.percent = percent;
    }

    public void setPercent(Integer percent, int startPos) {
        this.percent = percent;
        this.startPos = startPos;
        this.endPos = startPos + percent - 1;
    }

    @JsonProperty
    public int getStartPos() {
        return startPos;
    }

    public void setStartPos(int startPos) {
        this.startPos = startPos;
    }

    @JsonProperty
    public int getEndPos() {
        return endPos;
    }

    public void setEndPos(int endPos) {
        this.endPos = endPos;
    }

    @JsonProperty
    public DateTime getCreateTime() {
        if (createTime == null) {
            createTime = new DateTime();
        }
        return createTime;
    }

    public void setCreateTime(DateTime createTime) {
        this.createTime = createTime;
    }

    public boolean match(int index) {
        if (index < startPos || index > endPos) {
            return false;
        }
        return true;
    }

    @JsonProperty
    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    @JsonProperty
    public Set<AbstractAlgorithm> getAlgorithms() {
        return algorithms;
    }

    public void setAlgorithms(Set<AbstractAlgorithm> algorithms) {
        this.algorithms = algorithms;
    }

    public void addAlgorithm(AbstractAlgorithm algorithm) {
        if (this.algorithms == null) {
            this.algorithms = new HashSet<>();
        }
        this.algorithms.add(algorithm);
    }

    public void addParams(String key, String value) {
        if(this.parmas == null){
            this.parmas = new HashMap<>();
        }
        this.parmas.put(key,value);
    }

    @JsonProperty
    public Map<String, String> getParmas() {
        return parmas;
    }

    public void setParmas(Map<String, String> parmas) {
        this.parmas = parmas;
    }
}