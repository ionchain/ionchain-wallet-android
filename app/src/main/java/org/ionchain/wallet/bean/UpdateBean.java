package org.ionchain.wallet.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by binny on 2018/11/29.
 */
public class UpdateBean implements Serializable {


    /**
     * code : 0
     * msg : 操作成功!
     * data : [{"id":"1a1f18d6faf0fasdfas","has_new_version":"1","must_update":null,"version_number":"1.2.2","version_code":109,"update_info":"1111","url":"1212121212fsadf"}]
     */

    private int code;
    private String msg;
    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean implements Serializable {
        /**
         * id : 1a1f18d6faf0fasdfas
         * has_new_version : 1
         * must_update : null
         * version_number : 1.2.2
         * version_code : 109
         * update_info : 1111
         * url : 1212121212fsadf
         */

        private String id;
        private String has_new_version;
        private Object must_update;
        private String version_number;
        private int version_code;
        private String update_info;
        private String url;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getHas_new_version() {
            return has_new_version;
        }

        public void setHas_new_version(String has_new_version) {
            this.has_new_version = has_new_version;
        }

        public Object getMust_update() {
            return must_update;
        }

        public void setMust_update(Object must_update) {
            this.must_update = must_update;
        }

        public String getVersion_number() {
            return version_number;
        }

        public void setVersion_number(String version_number) {
            this.version_number = version_number;
        }

        public int getVersion_code() {
            return version_code;
        }

        public void setVersion_code(int version_code) {
            this.version_code = version_code;
        }

        public String getUpdate_info() {
            return update_info;
        }

        public void setUpdate_info(String update_info) {
            this.update_info = update_info;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
