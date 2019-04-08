package org.ionc.wallet.bean;

/**
 * describe:
 * 余额
 *
 * @author 596928539@qq.com
 * @date 2019/04/02
 */
public class BalanceBean {
    private String ionchain_balance;//离子链余额
    private String eth_balance;//以太坊余额

    public String getIonchain_balance() {
        return ionchain_balance;
    }

    public void setIonchain_balance(String ionchain_balance) {
        this.ionchain_balance = ionchain_balance;
    }

    public String getEth_balance() {
        return eth_balance;
    }

    public void setEth_balance(String eth_balance) {
        this.eth_balance = eth_balance;
    }

    @Override
    public String toString() {
        return "BalanceBean{" +
                "ionchain_balance='" + ionchain_balance + '\'' +
                ", eth_balance='" + eth_balance + '\'' +
                '}';
    }
}
