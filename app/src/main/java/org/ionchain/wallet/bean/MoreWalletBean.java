package org.ionchain.wallet.bean;

import java.io.Serializable;
import java.util.List;

/**
 * USER: binny
 * DATE: 2018/9/13
 * 描述:
 */
public class MoreWalletBean implements Serializable{
    private List<WalletBean> mWallets;

    public List<WalletBean> getWallets() {
        return mWallets;
    }

    public void setWallets(List<WalletBean> wallets) {
        mWallets = wallets;
    }
}