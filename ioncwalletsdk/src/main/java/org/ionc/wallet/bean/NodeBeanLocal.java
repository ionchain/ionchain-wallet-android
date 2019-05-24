package org.ionc.wallet.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 离子链节点本地储存
 */
@Entity
public class NodeBeanLocal {
    @Id(autoincrement = true)
    private Long id;
    private String ioncNode;
    @Generated(hash = 1278564743)
    public NodeBeanLocal(Long id, String ioncNode) {
        this.id = id;
        this.ioncNode = ioncNode;
    }
    @Generated(hash = 1654852094)
    public NodeBeanLocal() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getIoncNode() {
        return this.ioncNode;
    }
    public void setIoncNode(String ioncNode) {
        this.ioncNode = ioncNode;
    }
}
