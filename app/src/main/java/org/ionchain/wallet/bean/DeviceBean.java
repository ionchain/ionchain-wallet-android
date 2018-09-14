package org.ionchain.wallet.bean;

import java.io.Serializable;

/**
 * USER: binny
 * DATE: 2018/9/13
 * 描述: 设备信息
 */
public class DeviceBean implements Serializable {

    /**
     * success : 0
     * message : ok
     * data : {"id":3,"name":"共享单车","cksn":"4eeb01d54c4c11a1480cdabd1497a943","system":"Android","created_at":"2018-09-13","image_url":""}
     */

    private int success;
    private String message;
    private DataBean data;

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean implements Serializable {
        /**
         * id : 3
         * name : 共享单车
         * cksn : 4eeb01d54c4c11a1480cdabd1497a943
         * system : Android
         * created_at : 2018-09-13
         * image_url :
         */

        private int id;
        private String name;
        private String cksn;
        private String system;
        private String created_at;
        private String image_url;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCksn() {
            return cksn;
        }

        public void setCksn(String cksn) {
            this.cksn = cksn;
        }

        public String getSystem() {
            return system;
        }

        public void setSystem(String system) {
            this.system = system;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }
    }
}
