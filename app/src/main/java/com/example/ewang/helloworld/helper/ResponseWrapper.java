package com.example.ewang.helloworld.helper;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ewang on 2018/4/22.
 */

public class ResponseWrapper {

    private final boolean success;

    private final int status;

    private final String errMessage;

    private final Map<String, Object> data;

    @JsonCreator
    public ResponseWrapper(@JsonProperty("success") boolean success,
                           @JsonProperty("status") int status,
                           @JsonProperty("message") String errMessage,
                           @JsonProperty("data") Map<String, Object> data) {
        this.success = success;
        this.status = status;
        this.errMessage = errMessage;
        this.data = data;
    }

    public ResponseWrapper(int status, String errMessage) {
        this.status = status;
        this.success = false;
        this.errMessage = errMessage;
        this.data = new HashMap<>();
    }

    public <T> ResponseWrapper addObject(T value, String key) {
        if (key != null) {
            data.put(key, value);
        }
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getStatus() {
        return status;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public Map<String, Object> getData() {
        return data;
    }
}
