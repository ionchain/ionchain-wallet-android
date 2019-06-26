package org.ionc.wallet.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class TxRecordBeanInHelper {
    @Id(autoincrement = true)
    private Long id;
    private Long indexMaxForIn;
    private String toAddress;

    @Generated(hash = 1355299878)
    public TxRecordBeanInHelper(Long id, Long indexMaxForIn, String toAddress) {
        this.id = id;
        this.indexMaxForIn = indexMaxForIn;
        this.toAddress = toAddress;
    }

    @Generated(hash = 1383857116)
    public TxRecordBeanInHelper() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIndexMaxForIn() {
        return indexMaxForIn;
    }

    public void setIndexMaxForIn(Long indexMaxForIn) {
        this.indexMaxForIn = indexMaxForIn;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    @Override
    public String toString() {
        return "TxRecordBeanInHelper{" +
                "id=" + id +
                ", indexMaxForIn=" + indexMaxForIn +
                ", toAddress='" + toAddress + '\'' +
                '}';
    }
}
