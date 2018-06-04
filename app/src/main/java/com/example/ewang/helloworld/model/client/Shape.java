package com.example.ewang.helloworld.model.client;

/**
 * Created by ewang on 2018/6/4.
 */

public class Shape {

    private int shapeId;

    private String name;

    private int shapeImage;

    public Shape(int shapeId, String name, int shapeImage) {
        this.name = name;
        this.shapeId = shapeId;
        this.shapeImage = shapeImage;
    }

    public String getName() {
        return name;
    }

    public int getShapeId() {
        return shapeId;
    }

    public int getShapeImage() {
        return shapeImage;
    }
}
