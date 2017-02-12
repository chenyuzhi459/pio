package io.sugo.pio.recommend.bean;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import io.sugo.pio.server.utils.StringUtil;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RecInstance implements Serializable {
    @JsonProperty
    private String id;
    @JsonProperty
    private String name;
    @JsonProperty
    private int num = 10;
    @JsonProperty
    private String owner;
    @JsonProperty
    private DateTime createTime;
    @JsonProperty
    private DateTime updateTime;
    @JsonProperty
    private Boolean enabled = false;

    private Map<String, RecStrategy> recStrategys;

    public RecInstance(){

    }

    @JsonCreator
    public RecInstance(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("num") int num,
            @JsonProperty("owner") String owner,
            @JsonProperty("enabled") Boolean enabled
    ) {
        Preconditions.checkNotNull(id, "Must specify uuid");
        Preconditions.checkNotNull(name, "Must specify name");
        Preconditions.checkNotNull(num, "Must specify num");
        this.id = id;
        this.name = name;
        this.num = num;
        this.owner = owner;
        this.enabled = enabled == null ? false : enabled;
    }

    public String getId() {
        if (StringUtil.isEmpty(id)) {
            id = StringUtil.generateUid();
        }
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public DateTime getCreateTime() {
        if (createTime == null) {
            createTime = new DateTime();
        }
        return createTime;
    }

    public void setCreateTime(DateTime createTime) {
        this.createTime = createTime;
    }

    public DateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(DateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Map<String, RecStrategy> getRecStrategys() {
        return recStrategys;
    }

    public void setRecStrategys(Map<String, RecStrategy> recStrategys) {
        this.recStrategys = recStrategys;
    }

    public void addRecStrategy(RecStrategy recStrategy){
        if(this.recStrategys == null){
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
