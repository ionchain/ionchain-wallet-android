package org.ionchain.wallet.comm.api.request;

import org.ionchain.wallet.comm.api.constant.ApiConstant;

public class WalletCreateRquest extends BaseRquset {

    private String name;
    private String password;
    private String privateKey;

    public WalletCreateRquest(){
        super(ApiConstant.ApiUri.URI_LOGIN.getDesc());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

}
