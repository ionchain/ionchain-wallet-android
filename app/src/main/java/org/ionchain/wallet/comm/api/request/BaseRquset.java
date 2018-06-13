package org.ionchain.wallet.comm.api.request;

import org.ionchain.wallet.comm.api.conf.ApiConfig;

import java.util.HashMap;

public class BaseRquset {

    private String appyKey;
    private String appSercert;
    private String baseUrl;
    private String postUri;

    public BaseRquset(String postUri) {

        this.appyKey = ApiConfig.API_APP_KEY;
        this.appSercert = ApiConfig.API_APP_SECRET;
        this.baseUrl = ApiConfig.API_BASE_URL;
        this.postUri = postUri;
    }

    public BaseRquset(String appyKey, String appSercert, String baseUrl, String postUri) {
        this.appyKey = appyKey;
        this.appSercert = appSercert;
        this.baseUrl = baseUrl;
        this.postUri = postUri;
    }

    public String getPostUri() {
        return postUri;
    }

    public void setPostUri(String postUri) {
        this.postUri = postUri;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getAppyKey() {
        return appyKey;
    }

    public void setAppyKey(String appyKey) {
        this.appyKey = appyKey;
    }

    public String getAppSercert() {
        return appSercert;
    }

    public void setAppSercert(String appSercert) {
        this.appSercert = appSercert;
    }

    /**
     * 基类方法返回post的url
     *
     * @return
     */
    public String getPostUrl() {
        return this.baseUrl + this.postUri;
    }

    /**
     * @return
     */
    public HashMap<String, String> getPostParm() {
        return null;
    }

    public void formatHashParm() {

    }

}
