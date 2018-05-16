package com.fast.lib.okhttp;

import java.lang.reflect.Type;

/**
 * Created by siberiawolf on 15/12/9.
 */
public class ResponseBean {

    public int refreshType;
    public Object obj;
    public Type mType;

    @Override
    public String toString() {
        return "ResponseBean{" +
                "refreshType=" + refreshType +
                ", obj=" + obj.toString() +
                ", mType=" + mType +
                '}';
    }
}
