package com.example.ewang.helloworld.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by ewang on 2018/4/20.
 */

public class User {
    private final long id;

    private final String account;

    private final Image image;

    private final String username;

    @JsonCreator
    public User(@JsonProperty("id") long id,
                @JsonProperty("account") String account,
                @JsonProperty("username") String username,
                @JsonProperty("image") Image image) {
        this.id = id;
        this.account = account;
        this.image = image;
        this.username = username;
    }

    public long getId() {
        return id;
    }

    public String getAccount() {
        return account;
    }

    public String getUsername() {
        return username == null ? "Customer" : username;
    }

    public Image getImage() {
        return image;
    }
}
