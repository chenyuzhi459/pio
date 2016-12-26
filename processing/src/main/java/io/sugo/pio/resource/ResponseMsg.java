package io.sugo.pio.resource;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 16-12-23.
 */
public class ResponseMsg implements Serializable {
    private Boolean success = true;
    private Map<String, Object> data = new HashMap<>();
    private Response.Status status;

    public ResponseMsg put(String key, Object value) {
        data.put(key, value);
        return this;
    }

    public ResponseMsg() {
    }

    public ResponseMsg ok(){
        this.success = true;
        return this;
    }

    public ResponseMsg error(String msg){
        this.success = false;
        put("msg", msg);
        return this;
    }

    @JsonProperty
    public Boolean getSuccess() {
        return success;
    }

    @JsonProperty
    public Map<String, Object> getData() {
        return data;
    }

    public ResponseMsg status(Response.Status status) {
        this.status = status;
        return this;
    }

    public Response.Status status() {
        return status;
    }
}
