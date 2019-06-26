package org.ionchain.wallet.bean;

import java.io.Serializable;
import java.util.List;

/**
 * AUTHOR binny
 * <p>
 * TIME 2018/11/12 15:25
 */
public class TxRecordBeanTemp implements Serializable {

    /**
     * code : 0
     * msg : 操作成功
     * data : {"totalCount":4,"pageNumber":1,"pageSize":10,"data":[{"hash":"0x68b2132e7d025f98c8acd4fdae548e84428f3cb98a0531c7cf97efc5cd56e3a2","blockHash":"0xf293910f389b95e4f96a42b7b8e9cff25ae08db1545f10a3a32f5a0e92c97165","blockNumber":4305,"tx_from":"0xeb680f30715f347d4eb5cd03ac5eced297ac5046","tx_to":"0x018673c699738c569d88d31167be1d2cb97c443e","isContract":null,"value":"1000000000000000000","input":"0x","nonce":"2","transactionIndex":0,"gas":"4000000","gasPrice":"1000000000","v":"0x1b","s":"0x4b86970326718692bcf2b561f0030cc6d029e3d8167196d800ca91fffc06fd15","r":"0x8d74f11fda06559d5f70b3852152557cf650b36b47ec1887f97df5855457902a","etherValue":1,"txFee":"0.004","gasGasThousand":"4,000,000","gasGasPriceThousand":"1,000,000,000"},{"hash":"0xc6d0d37767f90c15d8065b85cd1a667e31274801efd2344b418b0a88e0d8b40f","blockHash":"0x3d491f0c9f5e5b5ce8fc6cebea85e1e780ca4994d5647e57beffa8c0cb213561","blockNumber":4269,"tx_from":"0x018673c699738c569d88d31167be1d2cb97c443e","tx_to":"0xeb680f30715f347d4eb5cd03ac5eced297ac5046","isContract":null,"value":"100000000000000000000","input":"0x","nonce":"0","transactionIndex":0,"gas":"30000","gasPrice":"1","v":"0x1c","s":"0x5fb2c0b56815736579176bd800dba6f7528bc41781fd32b82b003e7462652f3e","r":"0xf6b42e0ec7d45c02423a5fd6a00bed9dfa2a7acbbe6ce437f311e3c1ca24623","etherValue":100,"txFee":"0.00000000000003","gasGasThousand":"30,000","gasGasPriceThousand":"1"},{"hash":"0xdd2a5accda8fd45830a811c3a77ce8010d59d3d9a468460899fb94be9ca7718b","blockHash":"0x30537133010e2c8c93cf424601d41fc9a2527f0605a45d10a1fc643db18025cf","blockNumber":15,"tx_from":"0xeb680f30715f347d4eb5cd03ac5eced297ac5046","tx_to":"0x018673c699738c569d88d31167be1d2cb97c443e","isContract":null,"value":"100000000000000000000000","input":"0x","nonce":"1","transactionIndex":0,"gas":"4000000","gasPrice":"1000000000","v":"0x1b","s":"0x4d3cbe3a4e2ca7a784daeff2731dd4e0e095b89ad6e05ee1e6d359e78fd2cd5c","r":"0xaf75de77427564cb63ac37def4d297b3cb66fdcc74f3eb6369fa299faf5e39b7","etherValue":99999.99999999999,"txFee":"0.004","gasGasThousand":"4,000,000","gasGasPriceThousand":"1,000,000,000"},{"hash":"0x8f38990fde1b6d12a1dd119a168245cb7591821751535de8331f3496a880806a","blockHash":"0x5f40f199947701d810812d877ac0b400240dbcf8cb84ddad64666cba1de3c786","blockNumber":12,"tx_from":"0xeb680f30715f347d4eb5cd03ac5eced297ac5046","tx_to":"0x018673c699738c569d88d31167be1d2cb97c443e","isContract":null,"value":"1000000000","input":"0x","nonce":"0","transactionIndex":0,"gas":"4000000","gasPrice":"1000000000","v":"0x1b","s":"0x1165c387cb696ef7fb78e13b8c2f50d629c3b3a6ab0e39bc72989ef02b854f01","r":"0x56ef7d93304440a9f170e9e52a0d02d54fec81b8e48689b759188a4a089623f1","etherValue":1.0E-9,"txFee":"0.004","gasGasThousand":"4,000,000","gasGasPriceThousand":"1,000,000,000"}]}
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
         * totalCount : 4
         * pageNumber : 1
         * pageSize : 10
         * data : [{"hash":"0x68b2132e7d025f98c8acd4fdae548e84428f3cb98a0531c7cf97efc5cd56e3a2","blockHash":"0xf293910f389b95e4f96a42b7b8e9cff25ae08db1545f10a3a32f5a0e92c97165","blockNumber":4305,"tx_from":"0xeb680f30715f347d4eb5cd03ac5eced297ac5046","tx_to":"0x018673c699738c569d88d31167be1d2cb97c443e","isContract":null,"value":"1000000000000000000","input":"0x","nonce":"2","transactionIndex":0,"gas":"4000000","gasPrice":"1000000000","v":"0x1b","s":"0x4b86970326718692bcf2b561f0030cc6d029e3d8167196d800ca91fffc06fd15","r":"0x8d74f11fda06559d5f70b3852152557cf650b36b47ec1887f97df5855457902a","etherValue":1,"txFee":"0.004","gasGasThousand":"4,000,000","gasGasPriceThousand":"1,000,000,000"},{"hash":"0xc6d0d37767f90c15d8065b85cd1a667e31274801efd2344b418b0a88e0d8b40f","blockHash":"0x3d491f0c9f5e5b5ce8fc6cebea85e1e780ca4994d5647e57beffa8c0cb213561","blockNumber":4269,"tx_from":"0x018673c699738c569d88d31167be1d2cb97c443e","tx_to":"0xeb680f30715f347d4eb5cd03ac5eced297ac5046","isContract":null,"value":"100000000000000000000","input":"0x","nonce":"0","transactionIndex":0,"gas":"30000","gasPrice":"1","v":"0x1c","s":"0x5fb2c0b56815736579176bd800dba6f7528bc41781fd32b82b003e7462652f3e","r":"0xf6b42e0ec7d45c02423a5fd6a00bed9dfa2a7acbbe6ce437f311e3c1ca24623","etherValue":100,"txFee":"0.00000000000003","gasGasThousand":"30,000","gasGasPriceThousand":"1"},{"hash":"0xdd2a5accda8fd45830a811c3a77ce8010d59d3d9a468460899fb94be9ca7718b","blockHash":"0x30537133010e2c8c93cf424601d41fc9a2527f0605a45d10a1fc643db18025cf","blockNumber":15,"tx_from":"0xeb680f30715f347d4eb5cd03ac5eced297ac5046","tx_to":"0x018673c699738c569d88d31167be1d2cb97c443e","isContract":null,"value":"100000000000000000000000","input":"0x","nonce":"1","transactionIndex":0,"gas":"4000000","gasPrice":"1000000000","v":"0x1b","s":"0x4d3cbe3a4e2ca7a784daeff2731dd4e0e095b89ad6e05ee1e6d359e78fd2cd5c","r":"0xaf75de77427564cb63ac37def4d297b3cb66fdcc74f3eb6369fa299faf5e39b7","etherValue":99999.99999999999,"txFee":"0.004","gasGasThousand":"4,000,000","gasGasPriceThousand":"1,000,000,000"},{"hash":"0x8f38990fde1b6d12a1dd119a168245cb7591821751535de8331f3496a880806a","blockHash":"0x5f40f199947701d810812d877ac0b400240dbcf8cb84ddad64666cba1de3c786","blockNumber":12,"tx_from":"0xeb680f30715f347d4eb5cd03ac5eced297ac5046","tx_to":"0x018673c699738c569d88d31167be1d2cb97c443e","isContract":null,"value":"1000000000","input":"0x","nonce":"0","transactionIndex":0,"gas":"4000000","gasPrice":"1000000000","v":"0x1b","s":"0x1165c387cb696ef7fb78e13b8c2f50d629c3b3a6ab0e39bc72989ef02b854f01","r":"0x56ef7d93304440a9f170e9e52a0d02d54fec81b8e48689b759188a4a089623f1","etherValue":1.0E-9,"txFee":"0.004","gasGasThousand":"4,000,000","gasGasPriceThousand":"1,000,000,000"}]
         */

        private int totalCount;
        private int pageNumber;
        private int pageSize;
        private List<ItemBean> data;

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public int getPageNumber() {
            return pageNumber;
        }

        public void setPageNumber(int pageNumber) {
            this.pageNumber = pageNumber;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public List<ItemBean> getData() {
            return data;
        }

        public void setData(List<ItemBean> data) {
            this.data = data;
        }

        public static class ItemBean implements Serializable {
            /**
             * hash : 0x68b2132e7d025f98c8acd4fdae548e84428f3cb98a0531c7cf97efc5cd56e3a2
             * blockHash : 0xf293910f389b95e4f96a42b7b8e9cff25ae08db1545f10a3a32f5a0e92c97165
             * blockNumber : 4305
             * tx_from : 0xeb680f30715f347d4eb5cd03ac5eced297ac5046
             * tx_to : 0x018673c699738c569d88d31167be1d2cb97c443e
             * isContract : null
             * value : 1000000000000000000
             * input : 0x
             * nonce : 2
             * transactionIndex : 0
             * gas : 4000000
             * gasPrice : 1000000000
             * v : 0x1b
             * s : 0x4b86970326718692bcf2b561f0030cc6d029e3d8167196d800ca91fffc06fd15
             * r : 0x8d74f11fda06559d5f70b3852152557cf650b36b47ec1887f97df5855457902a
             * etherValue : 1
             * txFee : 0.004
             * gasGasThousand : 4,000,000
             * gasGasPriceThousand : 1,000,000,000
             */

            private String hash;
            private String blockHash;
            private int blockNumber;
            private String tx_from;
            private String tx_to;
            private Object isContract;
            private String value;
            private String input;
            private String nonce;
            private int transactionIndex;
            private String gas;
            private String gasPrice;
            private String v;
            private String s;
            private String r;
            private double etherValue;
            private String txFee;
            private String gasGasThousand;
            private String gasGasPriceThousand;

            @Override
            public String toString() {
                return "ItemBean{" +
                        "hash='" + hash + '\'' +
                        ", blockHash='" + blockHash + '\'' +
                        ", blockNumber=" + blockNumber +
                        ", tx_from='" + tx_from + '\'' +
                        ", tx_to='" + tx_to + '\'' +
                        ", isContract=" + isContract +
                        ", value='" + value + '\'' +
                        ", input='" + input + '\'' +
                        ", nonce='" + nonce + '\'' +
                        ", transactionIndex=" + transactionIndex +
                        ", gas='" + gas + '\'' +
                        ", gasPrice='" + gasPrice + '\'' +
                        ", v='" + v + '\'' +
                        ", s='" + s + '\'' +
                        ", r='" + r + '\'' +
                        ", etherValue=" + etherValue +
                        ", txFee='" + txFee + '\'' +
                        ", gasGasThousand='" + gasGasThousand + '\'' +
                        ", gasGasPriceThousand='" + gasGasPriceThousand + '\'' +
                        '}';
            }

            public String getHash() {
                return hash;
            }

            public void setHash(String hash) {
                this.hash = hash;
            }

            public String getBlockHash() {
                return blockHash;
            }

            public void setBlockHash(String blockHash) {
                this.blockHash = blockHash;
            }

            public int getBlockNumber() {
                return blockNumber;
            }

            public void setBlockNumber(int blockNumber) {
                this.blockNumber = blockNumber;
            }

            public String getTx_from() {
                return tx_from;
            }

            public void setTx_from(String tx_from) {
                this.tx_from = tx_from;
            }

            public String getTx_to() {
                return tx_to;
            }

            public void setTx_to(String tx_to) {
                this.tx_to = tx_to;
            }

            public Object getIsContract() {
                return isContract;
            }

            public void setIsContract(Object isContract) {
                this.isContract = isContract;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }

            public String getInput() {
                return input;
            }

            public void setInput(String input) {
                this.input = input;
            }

            public String getNonce() {
                return nonce;
            }

            public void setNonce(String nonce) {
                this.nonce = nonce;
            }

            public int getTransactionIndex() {
                return transactionIndex;
            }

            public void setTransactionIndex(int transactionIndex) {
                this.transactionIndex = transactionIndex;
            }

            public String getGas() {
                return gas;
            }

            public void setGas(String gas) {
                this.gas = gas;
            }

            public String getGasPrice() {
                return gasPrice;
            }

            public void setGasPrice(String gasPrice) {
                this.gasPrice = gasPrice;
            }

            public String getV() {
                return v;
            }

            public void setV(String v) {
                this.v = v;
            }

            public String getS() {
                return s;
            }

            public void setS(String s) {
                this.s = s;
            }

            public String getR() {
                return r;
            }

            public void setR(String r) {
                this.r = r;
            }

            public double getEtherValue() {
                return etherValue;
            }

            public void setEtherValue(int etherValue) {
                this.etherValue = etherValue;
            }

            public String getTxFee() {
                return txFee;
            }

            public void setTxFee(String txFee) {
                this.txFee = txFee;
            }

            public String getGasGasThousand() {
                return gasGasThousand;
            }

            public void setGasGasThousand(String gasGasThousand) {
                this.gasGasThousand = gasGasThousand;
            }

            public String getGasGasPriceThousand() {
                return gasGasPriceThousand;
            }

            public void setGasGasPriceThousand(String gasGasPriceThousand) {
                this.gasGasPriceThousand = gasGasPriceThousand;
            }
        }
    }
}
