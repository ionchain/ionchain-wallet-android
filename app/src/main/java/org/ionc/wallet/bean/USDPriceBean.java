package org.ionc.wallet.bean;

import java.io.Serializable;
import java.util.List;

public class USDPriceBean implements Serializable{

    /**
     * code : 0
     * msg : 操作成功
     * data : {"blockNumber":188487,"exchangeMarket":[{"name":"CMC","url":"https://coinmarketcap.com/currencies/ionchain/"}],"marketinfo":{"price":0.00510047302951,"volume_24h":706040.929370844,"percent_change_1h":-0.932033,"percent_change_24h":-2.31309,"percent_change_7d":10.6083,"market_cap":605614.4677579852,"last_updated":"2019-05-08T09:02:11.000Z"}}
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
         * blockNumber : 188487
         * exchangeMarket : [{"name":"CMC","url":"https://coinmarketcap.com/currencies/ionchain/"}]
         * marketinfo : {"price":0.00510047302951,"volume_24h":706040.929370844,"percent_change_1h":-0.932033,"percent_change_24h":-2.31309,"percent_change_7d":10.6083,"market_cap":605614.4677579852,"last_updated":"2019-05-08T09:02:11.000Z"}
         */

        private int blockNumber;
        private MarketinfoBean marketinfo;
        private List<ExchangeMarketBean> exchangeMarket;

        public int getBlockNumber() {
            return blockNumber;
        }

        public void setBlockNumber(int blockNumber) {
            this.blockNumber = blockNumber;
        }

        public MarketinfoBean getMarketinfo() {
            return marketinfo;
        }

        public void setMarketinfo(MarketinfoBean marketinfo) {
            this.marketinfo = marketinfo;
        }

        public List<ExchangeMarketBean> getExchangeMarket() {
            return exchangeMarket;
        }

        public void setExchangeMarket(List<ExchangeMarketBean> exchangeMarket) {
            this.exchangeMarket = exchangeMarket;
        }

        public static class MarketinfoBean implements Serializable {
            /**
             * price : 0.00510047302951  离子币美元价格
             * volume_24h : 706040.929370844
             * percent_change_1h : -0.932033
             * percent_change_24h : -2.31309
             * percent_change_7d : 10.6083
             * market_cap : 605614.4677579852
             * last_updated : 2019-05-08T09:02:11.000Z
             */

            private double price;
            private double volume_24h;
            private double percent_change_1h;
            private double percent_change_24h;
            private double percent_change_7d;
            private double market_cap;
            private String last_updated;

            public double getPrice() {
                return price;
            }

            public void setPrice(double price) {
                this.price = price;
            }

            public double getVolume_24h() {
                return volume_24h;
            }

            public void setVolume_24h(double volume_24h) {
                this.volume_24h = volume_24h;
            }

            public double getPercent_change_1h() {
                return percent_change_1h;
            }

            public void setPercent_change_1h(double percent_change_1h) {
                this.percent_change_1h = percent_change_1h;
            }

            public double getPercent_change_24h() {
                return percent_change_24h;
            }

            public void setPercent_change_24h(double percent_change_24h) {
                this.percent_change_24h = percent_change_24h;
            }

            public double getPercent_change_7d() {
                return percent_change_7d;
            }

            public void setPercent_change_7d(double percent_change_7d) {
                this.percent_change_7d = percent_change_7d;
            }

            public double getMarket_cap() {
                return market_cap;
            }

            public void setMarket_cap(double market_cap) {
                this.market_cap = market_cap;
            }

            public String getLast_updated() {
                return last_updated;
            }

            public void setLast_updated(String last_updated) {
                this.last_updated = last_updated;
            }
        }

        public static class ExchangeMarketBean implements Serializable {
            /**
             * name : CMC
             * url : https://coinmarketcap.com/currencies/ionchain/
             */

            private String name;
            private String url;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }
    }
}
