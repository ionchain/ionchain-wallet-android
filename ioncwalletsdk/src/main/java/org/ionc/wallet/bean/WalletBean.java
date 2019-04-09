package org.ionc.wallet.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

@Entity
public class WalletBean implements Serializable {


    private static final long serialVersionUID = 8908676465356334947L;
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
     * 证书密码,这个数据库不保存
     * )
     */
    private String password;

    /**
     * 钱包icon
     */
    private int mIconIdex;

    /**
     * 助记词,用户备份后,置空
     */
    private String mnemonic;


    /**
     * 是否被选中，在支付的时候
     */
    private boolean choosen;


    /**
     * 是否是要展示的主钱包,删除主钱包,则钱包列表中的第一个钱包作为主钱包,如果没有,则删除后跳转到创建钱包界面
     */
    private boolean isMainWallet;


    @Generated(hash = 1550670516)
    public WalletBean(Long id, String privateKey, String name, String address,
            String publickey, String balance, String keystore, String password,
            int mIconIdex, String mnemonic, boolean choosen, boolean isMainWallet) {
        this.id = id;
        this.privateKey = privateKey;
        this.name = name;
        this.address = address;
        this.publickey = publickey;
        this.balance = balance;
        this.keystore = keystore;
        this.password = password;
        this.mIconIdex = mIconIdex;
        this.mnemonic = mnemonic;
        this.choosen = choosen;
        this.isMainWallet = isMainWallet;
    }


    @Generated(hash = 1814219826)
    public WalletBean() {
    }


    public Long getId() {
        return this.id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getPrivateKey() {
        return this.privateKey;
    }


    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }


    public String getName() {
        return this.name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getAddress() {
        return this.address;
    }


    public void setAddress(String address) {
        this.address = address;
    }


    public String getPublickey() {
        return this.publickey;
    }


    public void setPublickey(String publickey) {
        this.publickey = publickey;
    }


    public String getBalance() {
        return this.balance;
    }


    public void setBalance(String balance) {
        this.balance = balance;
    }


    public String getKeystore() {
        return this.keystore;
    }


    public void setKeystore(String keystore) {
        this.keystore = keystore;
    }


    public String getPassword() {
        return this.password;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public int getMIconIdex() {
        return this.mIconIdex;
    }


    public void setMIconIdex(int mIconIdex) {
        this.mIconIdex = mIconIdex;
    }


    public String getMnemonic() {
        return this.mnemonic;
    }


    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }


    public boolean getChoosen() {
        return this.choosen;
    }


    public void setChoosen(boolean choosen) {
        this.choosen = choosen;
    }


    public boolean getIsMainWallet() {
        return this.isMainWallet;
    }


    public void setIsMainWallet(boolean isMainWallet) {
        this.isMainWallet = isMainWallet;
    }

}
