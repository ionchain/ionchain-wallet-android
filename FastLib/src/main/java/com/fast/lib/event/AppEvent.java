package com.fast.lib.event;

/**
 * Created by siberiawolf on 17/6/16.
 */

public class AppEvent<T> {

    private int code;
    private T data;

    public AppEvent(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
