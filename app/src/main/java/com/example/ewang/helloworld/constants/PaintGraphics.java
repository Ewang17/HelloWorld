package com.example.ewang.helloworld.constants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ewang on 2018/6/4.
 */

public enum PaintGraphics {

    DRAW_LINE(0),
    DRAW_CIRCLE(1),
    DRAW_RECTANGLE(2),
    DRAW_ARROW(3),
    DRAW_TRIANGLE(4);

    private final int value;

    private static Map<Integer, PaintGraphics> valuesMap;

    static {
        valuesMap = new HashMap<Integer, PaintGraphics>();
        for (PaintGraphics t : values()) {
            PaintGraphics exist = valuesMap.put(t.value, t);
            if (exist != null) {
                throw new IllegalStateException("value冲突: " + exist + " " + t);
            }
        }
        valuesMap = Collections.unmodifiableMap(valuesMap);
    }

    PaintGraphics(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static PaintGraphics fromValue(int value) {
        return valuesMap.get(value);
    }
}
