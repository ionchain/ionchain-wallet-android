package org.ionchain.wallet.model;

import java.io.Serializable;

public class UserModel implements Serializable {


    /**
     * userId : 26
     * userName : 15618273172
     * tel : 15618273172
     * inviteCode : EPX9NP
     * coin : 30
     */

    private int userId;
    private String userName;
    private String tel;
    private String inviteCode;
    private int coin;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }
}
