package org.ionc.wallet.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 交易记录
 */
@Entity
public class TxRecordBean implements Parcelable {
    @Id(autoincrement = true)
    private Long id;
    private String block;
    private String txHash;
    private String txFee;
    private String from;
    private String to;
    private String value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getTxFee() {
        return txFee;
    }

    public void setTxFee(String txFee) {
        this.txFee = txFee;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.block);
        dest.writeString(this.txHash);
        dest.writeString(this.txFee);
        dest.writeString(this.from);
        dest.writeString(this.to);
        dest.writeString(this.value);
    }

    public TxRecordBean() {
    }

    protected TxRecordBean(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.block = in.readString();
        this.txHash = in.readString();
        this.txFee = in.readString();
        this.from = in.readString();
        this.to = in.readString();
        this.value = in.readString();
    }

    @Generated(hash = 1109720762)
    public TxRecordBean(Long id, String block, String txHash, String txFee, String from,
            String to, String value) {
        this.id = id;
        this.block = block;
        this.txHash = txHash;
        this.txFee = txFee;
        this.from = from;
        this.to = to;
        this.value = value;
    }

    public static final Creator<TxRecordBean> CREATOR = new Creator<TxRecordBean>() {
        @Override
        public TxRecordBean createFromParcel(Parcel source) {
            return new TxRecordBean(source);
        }

        @Override
        public TxRecordBean[] newArray(int size) {
            return new TxRecordBean[size];
        }
    };
}
