package org.ionc.wallet.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class TxRecordBeanOutHelper {
    @Id(autoincrement = true)
    private Long id;
    private Long indexMaxForOut;
    private String fromAddress;

    @Generated(hash = 528160087)
    public TxRecordBeanOutHelper(Long id, Long indexMaxForOut, String fromAddress) {
        this.id = id;
        this.indexMaxForOut = indexMaxForOut;
        this.fromAddress = fromAddress;
    }

    @Generated(hash = 1718105565)
    public TxRecordBeanOutHelper() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIndexMaxForOut() {
        return indexMaxForOut;
    }

    public void setIndexMaxForOut(Long indexMaxForOut) {
        this.indexMaxForOut = indexMaxForOut;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    @Override
    public String toString() {
        return "TxRecordBeanOutHelper{" +
                "id=" + id +
                ", indexMaxForOut=" + indexMaxForOut +
                ", fromAddress='" + fromAddress + '\'' +
                '}';
    }
}
