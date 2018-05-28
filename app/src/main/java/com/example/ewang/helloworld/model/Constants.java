package com.example.ewang.helloworld.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ewang on 2018/5/5.
 */

public enum Constants {
    ServerIP("10.151.38.74"),
    CharsetName("utf-8"),
    DefaultBasicUrl("http://" + ServerIP.getValue() + ":8080");


    private final String value;

    private static Map<String, Constants> valuesMap;

    static {
        valuesMap = new HashMap<>();
        for (Constants t : values()) {
            Constants exist = valuesMap.put(t.value, t);
            if (exist != null) {
                throw new IllegalStateException("value冲突: " + exist + " " + t);
            }
        }
        valuesMap = Collections.unmodifiableMap(valuesMap);
    }

    Constants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Constants fromValue(String value) {
        return valuesMap.get(value);
    }
}
