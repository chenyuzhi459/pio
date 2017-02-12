package io.sugo.pio.recommend.bean;

import com.google.common.base.Preconditions;
import org.joda.time.DateTime;

import java.io.Serializable;

public class RecInstanceCriteria implements Serializable {
    private String name;
    private String owner;
    private DateTime createTimeStart;
    private DateTime createTimeEnd;
    private Boolean enabled = false;
    private PageInfo pageInfo;

//    @JsonCreator
//    public RecInstanceCriteria(
//            @JsonProperty("name") String name,
//            @JsonProperty("owner") String owner,
//            @JsonProperty("createTime") DateTime createTime,
//            @JsonProperty("enabled") Boolean enabled
//    ) {
//        this.name = name;
//        this.owner = owner;
//        this.createTime = createTime;
//        this.enabled = enabled;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public DateTime getCreateTimeStart() {
        return createTimeStart;
    }

    public void setCreateTimeStart(DateTime createTimeStart) {
        this.createTimeStart = createTimeStart;
    }

    public DateTime getCreateTimeEnd() {
        return createTimeEnd;
    }

    public void setCreateTimeEnd(DateTime createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public void check() {
        if(this.createTimeStart != null && this.createTimeEnd != null){
            Preconditions.checkArgument(createTimeStart.isBefore(createTimeEnd), "start time is not before end time");
        }
    }
}
