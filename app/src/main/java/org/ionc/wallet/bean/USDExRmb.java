package org.ionc.wallet.bean;

import java.io.Serializable;

public class USDExRmb {


    /**
     * code : 0
     * msg : 操作成功
     * data : {"USD":1,"CNY":6.924092,"KRW":1184.032628,"IDR":14306.145641}
     * ext : null
     */

    private int code;
    private String msg;
    private DataBean data;
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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public Object getExt() {
        return ext;
    }

    public void setExt(Object ext) {
        this.ext = ext;
    }

    public static class DataBean implements Serializable {
        /**
         * USD : 1
         * CNY : 6.924092
         * KRW : 1184.032628
         * IDR : 14306.145641
         */

        private int USD;
        private double CNY;
        private double KRW;
        private double IDR;

        public int getUSD() {
            return USD;
        }

        public void setUSD(int USD) {
            this.USD = USD;
        }

        public double getCNY() {
            return CNY;
        }

        public void setCNY(double CNY) {
            this.CNY = CNY;
        }

        public double getKRW() {
            return KRW;
        }

        public void setKRW(double KRW) {
            this.KRW = KRW;
        }

        public double getIDR() {
            return IDR;
        }

        public void setIDR(double IDR) {
            this.IDR = IDR;
        }
    }
}
