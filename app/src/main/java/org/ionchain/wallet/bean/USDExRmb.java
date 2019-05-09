package org.ionchain.wallet.bean;

public class USDExRmb {

    /**
     * code : 0
     * msg : 操作成功
     * data : 6.778264 汇率
     * ext : null
     */

    private int code;
    private String msg;
    private double data;
    private Object ext;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public double getData() {
        return data;
    }

    public void setData(double data) {
        this.data = data;
    }

    public Object getExt() {
        return ext;
    }

    public void setExt(Object ext) {
        this.ext = ext;
    }
}
