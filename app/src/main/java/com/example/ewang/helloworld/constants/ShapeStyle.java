package com.example.ewang.helloworld.constants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ewang on 2018/6/4.
 */

public enum ShapeStyle {

    PAINT_FILL(0),
    PAINT_STROKE(1);

    private final int value;

    private static Map<Integer, ShapeStyle> valuesMap;

    static {
        valuesMap = new HashMap<Integer, ShapeStyle>();
        for (ShapeStyle t : values()) {
            ShapeStyle exist = valuesMap.put(t.value, t);
            if (exist != null) {
                throw new IllegalStateException("value冲突: " + exist + " " + t);
            }
        }
        valuesMap = Collections.unmodifiableMap(valuesMap);
    }

    ShapeStyle(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ShapeStyle fromValue(int value) {
        return valuesMap.get(value);
    }
}
