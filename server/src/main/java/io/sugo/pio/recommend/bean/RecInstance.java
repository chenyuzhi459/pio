package io.sugo.pio.recommend.bean;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.server.utils.StringUtil;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RecInstance implements Serializable {
    private String id;
    private String name;
    private Integer num = 10;
    private String owner;
    private DateTime createTime;
    private DateTime updateTime;
    private Boolean enabled = false;

    private Map<String, RecStrategy> recStrategys;

    public RecInstance() {

    }

    @JsonCreator
    public RecInstance(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("num") Integer num,
            @JsonProperty("owner") String owner,
            @JsonProperty("enabled") Boolean enabled
    ) {
        this.id = id;
        this.name = name;
        this.num = num;
        this.owner = owner;
        this.enabled = enabled == null ? false : enabled;
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
    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    @JsonProperty
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
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

    @JsonProperty
    public DateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(DateTime updateTime) {
        this.updateTime = updateTime;
    }

    @JsonProperty
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @JsonProperty
    public Map<String, RecStrategy> getRecStrategys() {
        return recStrategys;
    }

    public void setRecStrategys(Map<String, RecStrategy> recStrategys) {
        this.recStrategys = recStrategys;
    }

    public void addRecStrategy(RecStrategy recStrategy) {
        if (this.recStrategys == null) {
            this.recStrategys = new ConcurrentHashMap<>();
        }
        this.recStrategys.put(recStrategy.getId(), recStrategy);
    }

    public RecStrategy getRecStrategy(String strategyId) {
        return this.recStrategys.get(strategyId);
    }

    public RecStrategy deleteStrategy(String strategyId) {
        return this.recStrategys.remove(strategyId);
    }
}
