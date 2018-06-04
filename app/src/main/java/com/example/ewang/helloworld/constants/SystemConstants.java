package com.example.ewang.helloworld.constants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ewang on 2018/5/5.
 */

public enum SystemConstants {
    ServerIP("10.150.4.125"),
    CharsetName("utf-8"),
    DefaultBasicUrl("http://" + ServerIP.getValue() + ":8080");


    private final String value;

    private static Map<String, SystemConstants> valuesMap;

    static {
        valuesMap = new HashMap<>();
        for (SystemConstants t : values()) {
            SystemConstants exist = valuesMap.put(t.value, t);
            if (exist != null) {
                throw new IllegalStateException("value冲突: " + exist + " " + t);
            }
        }
        valuesMap = Collections.unmodifiableMap(valuesMap);
    }

    SystemConstants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static SystemConstants fromValue(String value) {
        return valuesMap.get(value);
    }
}
