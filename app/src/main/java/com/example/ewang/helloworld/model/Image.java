package com.example.ewang.helloworld.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by ewang on 2018/5/27.
 */

public class Image {

    private final long id;

    private final String path;

    private final String type;

    private final int height;

    private final int width;

    @JsonCreator
    public Image(@JsonProperty("id") long id,
                 @JsonProperty("path") String path,
                 @JsonProperty("type") String type,
                 @JsonProperty("height") int height,
                 @JsonProperty("width") int width) {
        this.id = id;
        this.path = path;
        this.type = type;
        this.height = height;
        this.width = width;
    }
}
