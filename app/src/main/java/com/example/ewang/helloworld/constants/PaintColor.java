package com.example.ewang.helloworld.constants;

import android.graphics.Color;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ewang on 2018/6/4.
 */

public enum PaintColor {

    Red(Color.RED, 0),
    Blue(Color.BLUE, 1),
    Green(Color.GREEN, 2),
    Yellow(Color.YELLOW, 3),
    Black(Color.BLACK, 4),
    White(Color.WHITE, 5),
    Gray(Color.GRAY, 6),
    Cyan(Color.CYAN, 7);

    private final int value;

    private final int index;

    private static Map<Integer, PaintColor> indexMap;

    static {
        indexMap = new HashMap<Integer, PaintColor>();
        for (PaintColor t : values()) {
            PaintColor exist = indexMap.put(t.index, t);
            if (exist != null) {
                throw new IllegalStateException("value冲突: " + exist + " " + t);
            }
        }
        indexMap = Collections.unmodifiableMap(indexMap);
    }

    PaintColor(int value, int index) {
        this.value = value;
        this.index = index;
    }

    public int getValue() {
        return value;
    }

    public int getIndex() {
        return index;
    }

    public static PaintColor fromIndex(int index) {
        return indexMap.get(index);
    }
}
