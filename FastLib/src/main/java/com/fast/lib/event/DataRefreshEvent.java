package com.fast.lib.event;


import com.fast.lib.comm.LibComm;

/**
 * Created by siberiawolf on 17/6/26.
 */

public class DataRefreshEvent extends AppEvent<Object> {

    public DataRefreshEvent(){
        this(null);
    }

    public DataRefreshEvent(Object data){
        this(LibComm.data_refresh_type,data);
    }

    public DataRefreshEvent(int code, Object data) {
        super(code, data);
    }
}
