package io.sugo.pio.server.broker;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Item implements Serializable {
    @JsonProperty
    private String itemId;
    private Long click;
    private Long publishTime;

    public Item(String itemId) {
        this.itemId = itemId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public Long getClick() {
        return click;
    }

    public void setClick(Long click) {
        this.click = click;
    }

    public Long getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Long publishTime) {
        this.publishTime = publishTime;
    }

    @Override
    public int hashCode() {
        int result = itemId.hashCode();
        result = 31 * result + click.hashCode();
        result = 31 * result + publishTime.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Item that = (Item) o;
        if (itemId != null ? !itemId.equals(that.itemId) : that.itemId != null) {
            return false;
        }
        if (click != null ? !click.equals(that.click) : that.click != null) {
            return false;
        }
        if (publishTime != null ? !publishTime.equals(that.publishTime) : that.publishTime != null) {
            return false;
        }
        return true;
    }
}
