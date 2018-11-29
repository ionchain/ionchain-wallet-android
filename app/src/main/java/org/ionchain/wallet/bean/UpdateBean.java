package org.ionchain.wallet.bean;

import java.io.Serializable;

/**
 * Created by binny on 2018/11/29.
 */
public class UpdateBean implements Serializable {


    /**
     * success : 0
     * message : ok
     * data : {"platform":"Android","version":"1.1.2","changelog":"初始化app","url":"http://www.baidu.com"}
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
         * platform : Android
         * version : 1.1.2
         * changelog : 初始化app
         * url : http://www.baidu.com
         */

        private String platform;
        private String version;
        private String changelog;
        private String url;

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getChangelog() {
            return changelog;
        }

        public void setChangelog(String changelog) {
            this.changelog = changelog;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
