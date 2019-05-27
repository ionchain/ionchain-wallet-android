package org.ionc.wallet.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 新版本数据表
 */
@Entity
public class WalletBeanNew implements Parcelable {

    @Id(autoincrement = true)
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
    private String public_key;
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
    private Integer mIconIndex;

    /**
     * 助记词,用户备份后,置空
     */
    private String mnemonic;


    /**
     * 是否被选中，在支付的时候
     */
    private boolean chosen;


    /**
     * 是否是要展示的主钱包,删除主钱包,则钱包列表中的第一个钱包作为主钱包,如果没有,则删除后跳转到创建钱包界面
     */
    private boolean isMainWallet;


    /**
     * RMB价格 缓存
     */
    private String rmb;

    /**
     *  是否是轻钱包
     */
    private boolean light;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.privateKey);
        dest.writeString(this.name);
        dest.writeString(this.address);
        dest.writeString(this.public_key);
        dest.writeString(this.balance);
        dest.writeString(this.keystore);
        dest.writeString(this.password);
        dest.writeValue(this.mIconIndex);
        dest.writeString(this.mnemonic);
        dest.writeByte(this.chosen ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isMainWallet ? (byte) 1 : (byte) 0);
        dest.writeString(this.rmb);
        dest.writeByte(this.light ? (byte) 1 : (byte) 0);
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

    public String getPublic_key() {
        return this.public_key;
    }

    public void setPublic_key(String public_key) {
        this.public_key = public_key;
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

    public Integer getMIconIndex() {
        return this.mIconIndex;
    }

    public void setMIconIndex(Integer mIconIndex) {
        this.mIconIndex = mIconIndex;
    }

    public String getMnemonic() {
        return this.mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public boolean getChosen() {
        return this.chosen;
    }

    public void setChosen(boolean chosen) {
        this.chosen = chosen;
    }

    public boolean getIsMainWallet() {
        return this.isMainWallet;
    }

    public void setIsMainWallet(boolean isMainWallet) {
        this.isMainWallet = isMainWallet;
    }

    public String getRmb() {
        return this.rmb;
    }

    public void setRmb(String rmb) {
        this.rmb = rmb;
    }

    public boolean getLight() {
        return this.light;
    }

    public void setLight(boolean light) {
        this.light = light;
    }

    public WalletBeanNew() {
    }

    protected WalletBeanNew(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.privateKey = in.readString();
        this.name = in.readString();
        this.address = in.readString();
        this.public_key = in.readString();
        this.balance = in.readString();
        this.keystore = in.readString();
        this.password = in.readString();
        this.mIconIndex = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mnemonic = in.readString();
        this.chosen = in.readByte() != 0;
        this.isMainWallet = in.readByte() != 0;
        this.rmb = in.readString();
        this.light = in.readByte() != 0;
    }

    @Generated(hash = 199591911)
    public WalletBeanNew(Long id, String privateKey, String name, String address,
            String public_key, String balance, String keystore, String password,
            Integer mIconIndex, String mnemonic, boolean chosen, boolean isMainWallet,
            String rmb, boolean light) {
        this.id = id;
        this.privateKey = privateKey;
        this.name = name;
        this.address = address;
        this.public_key = public_key;
        this.balance = balance;
        this.keystore = keystore;
        this.password = password;
        this.mIconIndex = mIconIndex;
        this.mnemonic = mnemonic;
        this.chosen = chosen;
        this.isMainWallet = isMainWallet;
        this.rmb = rmb;
        this.light = light;
    }

    public static final Creator<WalletBeanNew> CREATOR = new Creator<WalletBeanNew>() {
        @Override
        public WalletBeanNew createFromParcel(Parcel source) {
            return new WalletBeanNew(source);
        }

        @Override
        public WalletBeanNew[] newArray(int size) {
            return new WalletBeanNew[size];
        }
    };
}
