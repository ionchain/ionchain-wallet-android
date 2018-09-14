package org.ionchain.wallet.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

@Entity
public class WalletBean implements Serializable {
    @Id
    private Long id;
    /**
     * 私钥 最重要的唯一凭证丢了 你就哭吧
     */
    private String privateKey;

    /**
     * 钱包名称 这个是一个本地化的名称 不跟着 证书 或者 私钥 走(删了app就没了 就这意思
     * )
     */
    private String name;
    /**
     * 钱包地址 用于查询余额交易等等
     * )
     */
    private String address;
    /**
     * 公钥 我也不知道干嘛的
     * )
     */
    private String publickey;
    /**
     * 余额 ionchain的 精确到0.0000
     * )
     */
    private String balance;
    /**
     * 证书的本地存放地址
     * )
     */
    private String keystore = "";
    /**
     * 证书密码
     * )
     */
    private String password;

    private int mIconIdex;

    public int getIconIdex() {
        return mIconIdex;
    }

    public void setIconIdex(int iconIdex) {
        mIconIdex = iconIdex;
    }

    @Generated(hash = 147693845)
    public WalletBean(Long id, String privateKey, String name, String address, String publickey,
            String balance, String keystore, String password, int mIconIdex) {
        this.id = id;
        this.privateKey = privateKey;
        this.name = name;
        this.address = address;
        this.publickey = publickey;
        this.balance = balance;
        this.keystore = keystore;
        this.password = password;
        this.mIconIdex = mIconIdex;
    }

    @Generated(hash = 1814219826)
    public WalletBean() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPublickey() {
        return publickey;
    }

    public void setPublickey(String publickey) {
        this.publickey = publickey;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getKeystore() {
        return keystore;
    }

    public void setKeystore(String keystore) {
        this.keystore = keystore;
    }

    public int getMIconIdex() {
        return this.mIconIdex;
    }

    public void setMIconIdex(int mIconIdex) {
        this.mIconIdex = mIconIdex;
    }


}