package org.ionc.wallet.sdk.transaction;

import org.ionc.wallet.sdk.bean.WalletBean;

import java.math.BigInteger;

/**
 * describe:
 * 交易参数的封装
 *
 * @author xubinbin
 * @date 2019/03/26
 */
public class Transaction {
    /**
     * 转出钱包
     */
    private WalletBean walletBeanTX;
    /**
     * 转入地址
     */
    private String toAddress;
    /**
     * gas价格
     */
    private BigInteger gasPrice;
    /**
     * 转账金额
     */
    private String txValue;



}
