package org.ionc.wallet.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

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
     * 美元价格 缓存
     */
    private String us;
    /**
     * 韩元价格 缓存
     */
    private String krw;
    /**
     * 印尼盾价格 缓存
     */
    private String idr;

    /**
     *  是否是轻钱包,本钱包默认的就是轻钱包
     */
    private boolean light = true;

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

    public String getPublic_key() {
        return public_key;
    }

    public void setPublic_key(String public_key) {
        this.public_key = public_key;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getIconIndex() {
        return mIconIndex;
    }

    public void setIconIndex(Integer iconIndex) {
        mIconIndex = iconIndex;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public boolean isChosen() {
        return chosen;
    }

    public void setChosen(boolean chosen) {
        this.chosen = chosen;
    }

    public boolean isMainWallet() {
        return isMainWallet;
    }

    public void setMainWallet(boolean mainWallet) {
        isMainWallet = mainWallet;
    }

    public String getRmb() {
        return rmb;
    }

    public void setRmb(String rmb) {
        this.rmb = rmb;
    }

    public String getUs() {
        return us;
    }

    public void setUs(String us) {
        this.us = us;
    }

    public String getKrw() {
        return krw;
    }

    public void setKrw(String krw) {
        this.krw = krw;
    }

    public String getIdr() {
        return idr;
    }

    public void setIdr(String idr) {
        this.idr = idr;
    }

    public boolean isLight() {
        return light;
    }

    public void setLight(boolean light) {
        this.light = light;
    }

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
        dest.writeString(this.us);
        dest.writeString(this.krw);
        dest.writeString(this.idr);
        dest.writeByte(this.light ? (byte) 1 : (byte) 0);
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
        this.us = in.readString();
        this.krw = in.readString();
        this.idr = in.readString();
        this.light = in.readByte() != 0;
    }

    @Generated(hash = 387588)
    public WalletBeanNew(Long id, String privateKey, String name, String address,
            String public_key, String balance, String keystore, String password,
            Integer mIconIndex, String mnemonic, boolean chosen, boolean isMainWallet,
            String rmb, String us, String krw, String idr, boolean light) {
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
        this.us = us;
        this.krw = krw;
        this.idr = idr;
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

    @Override
    public String toString() {
        return "WalletBeanNew{" +
                "id=" + id +
                ", privateKey='" + privateKey + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", public_key='" + public_key + '\'' +
                ", balance='" + balance + '\'' +
                ", keystore='" + keystore + '\'' +
                ", password='" + password + '\'' +
                ", mIconIndex=" + mIconIndex +
                ", mnemonic='" + mnemonic + '\'' +
                ", chosen=" + chosen +
                ", isMainWallet=" + isMainWallet +
                ", rmb='" + rmb + '\'' +
                ", us='" + us + '\'' +
                ", krw='" + krw + '\'' +
                ", idr='" + idr + '\'' +
                ", light=" + light +
                '}';
    }

    public Integer getMIconIndex() {
        return this.mIconIndex;
    }

    public void setMIconIndex(Integer mIconIndex) {
        this.mIconIndex = mIconIndex;
    }

    public boolean getChosen() {
        return this.chosen;
    }

    public boolean getIsMainWallet() {
        return this.isMainWallet;
    }

    public void setIsMainWallet(boolean isMainWallet) {
        this.isMainWallet = isMainWallet;
    }

    public boolean getLight() {
        return this.light;
    }
}
