package org.ionc.wallet.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class TxRecordBeanAllHelper {
    @Id(autoincrement = true)
    private Long id;
    private Long indexMaxForAll;
    private String publicKey;

    @Generated(hash = 2134229363)
    public TxRecordBeanAllHelper(Long id, Long indexMaxForAll, String publicKey) {
        this.id = id;
        this.indexMaxForAll = indexMaxForAll;
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

    public Long getIndexMaxForAll() {
        return indexMaxForAll;
    }

    public void setIndexMaxForAll(Long indexMaxForAll) {
        this.indexMaxForAll = indexMaxForAll;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
