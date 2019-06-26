package org.ionc.wallet.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class CurrentPageNum {
    @Id(autoincrement = true)
    private Long id;
    private String numAll;
    private String numOut;
    private String numIn;
    private String address;

    @Generated(hash = 567406188)
    public CurrentPageNum(Long id, String numAll, String numOut, String numIn,
            String address) {
        this.id = id;
        this.numAll = numAll;
        this.numOut = numOut;
        this.numIn = numIn;
        this.address = address;
    }

    @Generated(hash = 1261456018)
    public CurrentPageNum() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumAll() {
        return numAll;
    }

    public void setNumAll(String numAll) {
        this.numAll = numAll;
    }

    public String getNumOut() {
        return numOut;
    }

    public void setNumOut(String numOut) {
        this.numOut = numOut;
    }

    public String getNumIn() {
        return numIn;
    }

    public void setNumIn(String numIn) {
        this.numIn = numIn;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "CurrentPageNum{" +
                "id=" + id +
                ", numAll='" + numAll + '\'' +
                ", numOut='" + numOut + '\'' +
                ", numIn='" + numIn + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
