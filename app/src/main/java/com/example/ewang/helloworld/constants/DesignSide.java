package com.example.ewang.helloworld.constants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ewang on 2018/6/20.
 */

public enum DesignSide {
    FRONT(0),
    BACK(1),
    LEFT(2),
    RIGHT(3);

    private final int value;

    private static Map<Integer, DesignSide> valuesMap;

    static {
        valuesMap = new HashMap<>();
        for (DesignSide t : values()) {
            DesignSide exist = valuesMap.put(t.getValue(), t);
            if (exist != null) {
                throw new IllegalStateException("value冲突: " + exist + " " + t);
            }
        }
        valuesMap = Collections.unmodifiableMap(valuesMap);
    }

    DesignSide(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static DesignSide fromValue(Integer value) {
        return valuesMap.get(value);
    }

}
