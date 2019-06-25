package org.ionc.wallet.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class TxRecordBeanAllHelper {
    @Id(autoincrement = true)
    private Long id;
    private Long indexMax;
    private String publicKey;

    @Generated(hash = 1285865585)
    public TxRecordBeanAllHelper(Long id, Long indexMax, String publicKey) {
        this.id = id;
        this.indexMax = indexMax;
        this.publicKey = publicKey;
    }

    @Generated(hash = 2104225130)
    public TxRecordBeanAllHelper() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIndexMax() {
        return indexMax;
    }

    public void setIndexMax(Long indexMax) {
        this.indexMax = indexMax;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
