package com.fast.lib.event;

/**
 * Created by siberiawolf on 17/6/26.
 */

public class CommonEvent extends AppEvent<Object> {
    public CommonEvent(int code, Object data) {
        super(code, data);
    }
}
