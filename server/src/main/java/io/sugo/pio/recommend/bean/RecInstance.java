package io.sugo.pio.recommend.bean;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;

public class RecInstance implements Serializable {
    @JsonProperty
    private String id;
    @JsonProperty
    private String name;
    @JsonProperty
    private int num;
    @JsonProperty
    private String owner;
    @JsonProperty
    private DateTime createTime;
    @JsonProperty
    private Boolean enabled = false;

    private List<RecStrategy> recStrategyList;

    @JsonCreator
    public RecInstance(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("num") int num,
            @JsonProperty("owner") String owner,
            @JsonProperty("enabled") Boolean enabled
    ) {
        Preconditions.checkNotNull(name, "Must specify name");
        Preconditions.checkNotNull(num, "Must specify num");
        this.id = id;
        this.name = name;
        this.num = num;
        this.owner = owner;
        this.enabled = enabled == null ? false : enabled;
    }

    public String getId() {
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
        return createTime;
    }

    public void setCreateTime(DateTime createTime) {
        this.createTime = createTime;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
