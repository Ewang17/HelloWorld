package com.example.ewang.helloworld.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by ewang on 2018/5/10.
 */

public class Session {

    private final User toUser;

    private String latestMessage;

    private final Date updateTime;

    private int unread;


    @JsonCreator
    public Session(@JsonProperty("toUser") User toUser,
                   @JsonProperty("latestMessage") String latestMessage,
                   @JsonProperty("updateTime") long updateTime,
                   @JsonProperty("unread") int unread) {
        this.toUser = toUser;
        this.latestMessage = latestMessage;
        this.updateTime = new Date(updateTime);
        this.unread = unread;
    }

    public User getToUser() {
        return toUser;
    }

    public String getLatestMessage() {
        return latestMessage;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public int getUnread() {
        return unread;
    }

    public void setUnread(int unread) {
        this.unread = unread;
    }

    public void setLatestMessage(String latestMessage) {
        this.latestMessage = latestMessage;
    }
}
