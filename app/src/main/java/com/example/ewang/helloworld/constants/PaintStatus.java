package com.example.ewang.helloworld.constants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ewang on 2018/6/4.
 */

public enum PaintStatus {

    IN_PENCIL(0),
    IN_ERASER(1),
    IN_SHAPE(2);

    private final int value;

    private static Map<Integer, PaintStatus> valuesMap;

    static {
        valuesMap = new HashMap<Integer, PaintStatus>();
        for (PaintStatus t : values()) {
            PaintStatus exist = valuesMap.put(t.value, t);
            if (exist != null) {
                throw new IllegalStateException("value冲突: " + exist + " " + t);
            }
        }
        valuesMap = Collections.unmodifiableMap(valuesMap);
    }

    PaintStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static PaintStatus fromValue(int value) {
        return valuesMap.get(value);
    }
}
