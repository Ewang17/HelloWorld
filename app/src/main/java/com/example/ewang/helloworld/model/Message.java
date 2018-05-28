package com.example.ewang.helloworld.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by ewang on 2018/5/28.
 */

public class Message {

    private final User user;

    private final User toUser;

    private final String content;

    private final Date createTime;

    @JsonCreator
    public Message(@JsonProperty("user") User user,
                   @JsonProperty("toUser") User toUser,
                   @JsonProperty("content") String content,
                   @JsonProperty("createTime") Date createTime) {
        this.user = user;
        this.toUser = toUser;
        this.content = content;
        this.createTime = createTime;
    }

    public User getUser() {
        return user;
    }

    public User getToUser() {
        return toUser;
    }

    public String getContent() {
        return content;
    }

    public Date getCreateTime() {
        return createTime;
    }
}
