package com.example.ewang.helloworld.model.client;

import android.graphics.Typeface;

/**
 * Created by ewang on 2018/6/8.
 */

public class CustomTypeFace {

    private String name;

    private Typeface typeface;

    public CustomTypeFace(String name, Typeface typeface) {
        this.name = name;
        this.typeface = typeface;
    }

    public String getName() {
        return name;
    }

    public Typeface getTypeface() {
        return typeface;
    }
}
