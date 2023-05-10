package cn.moozlee.fraudetection.entity;

import java.io.Serializable;
import java.util.HashMap;

public class Result extends HashMap<String, Object> implements Serializable {
    private static final long serialVersionUID = -8713837118340960775L;

    public Result() {
        put("code", null);
        put("message", null);
        put("data", null);
    }

    public Result code(Integer code) {
        put("code", code);
        return this;
    }

    public Result message(String message) {
        put("message", message);
        return this;
    }

    public Result data(Object data) {
        put("data", data);
        return this;
    }

    @Override
    public Result put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
