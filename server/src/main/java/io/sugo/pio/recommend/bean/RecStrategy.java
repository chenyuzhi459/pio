package io.sugo.pio.recommend.bean;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import io.sugo.pio.server.utils.StringUtil;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;

public class RecStrategy implements Serializable {
    @JsonProperty
    private String id;
    @JsonProperty
    private String name;
    @JsonProperty
    private List<String> types;
    @JsonProperty
    private String orderField;
    @JsonProperty
    private Boolean asc = false;
    @JsonProperty
    private Integer percent;
    @JsonProperty
    private int startPos;
    @JsonProperty
    private int endPos;
    @JsonProperty
    private DateTime createTime;

    @JsonCreator
    public RecStrategy(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("types") List<String> types,
            @JsonProperty("orderField") String orderField,
            @JsonProperty("asc") Boolean asc,
            @JsonProperty("percent") Integer percent
    ) {
        Preconditions.checkNotNull(id, "Must specify uuid");
        Preconditions.checkNotNull(name, "Must specify name");
        Preconditions.checkArgument(types != null && types.isEmpty(), "Must specify types");
        Preconditions.checkNotNull(orderField, "Must specify orderField");
        Preconditions.checkNotNull(percent, "Must specify orderField");
        this.id = id;
        this.name = name;
        this.types = types;
        this.orderField = orderField;
        this.asc = asc == null ? false : asc;
        this.percent = percent;
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

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public String getOrderField() {
        return orderField;
    }

    public void setOrderField(String orderField) {
        this.orderField = orderField;
    }

    public Boolean getAsc() {
        return asc;
    }

    public void setAsc(Boolean asc) {
        this.asc = asc;
    }

    public Integer getPercent() {
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

    public int getStartPos() {
        return startPos;
    }

    public void setStartPos(int startPos) {
        this.startPos = startPos;
    }

    public int getEndPos() {
        return endPos;
    }

    public void setEndPos(int endPos) {
        this.endPos = endPos;
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

    public boolean match(int index) {
        if (index < startPos || index > endPos) {
            return false;
        }
        return true;
    }
}
