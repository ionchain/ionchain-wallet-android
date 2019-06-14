package org.ionc.wallet.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.web3j.protocol.core.methods.response.Transaction;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 交易记录
 */
@Entity
public class TxRecordBean extends Transaction implements Parcelable {
    @Id(autoincrement = true)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
    }

    public TxRecordBean() {
    }

    protected TxRecordBean(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
    }

    @Generated(hash = 1049305498)
    public TxRecordBean(Long id) {
        this.id = id;
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
