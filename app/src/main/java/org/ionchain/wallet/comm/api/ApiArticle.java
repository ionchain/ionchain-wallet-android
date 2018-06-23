package org.ionchain.wallet.comm.api;

import org.ionchain.wallet.comm.api.conf.ApiConfig;
import org.ionchain.wallet.comm.api.constant.ApiConstant;
import org.ionchain.wallet.comm.api.request.ViewParm;

import java.util.HashMap;

public class ApiArticle extends BaseApi {

    /**
     * 咨询列表
     * <p>
     * "userId":1,
     * "pageNo":1,
     * "pageSize":2
     */
    public static void getArticle(String userId, String pageNo, String pageSize, ViewParm viewParm) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("userId", userId);
//        map.put("articleId", "1");
        map.put("pageNo", pageNo);
        map.put("pageSize", pageSize);
        String url = ApiConfig.API_BASE_URL + ApiConstant.ApiUri.URI_ATRICLE_ALL.getDesc();
        postJson(url, map, viewParm);
    }

    /**
     * 观看咨询计数+1
     * <p>
     * "userId":1,
     * "pageNo":1,
     * "pageSize":2
     */
    public static void viewArticle(String articleId, ViewParm viewParm) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("articleId", articleId);
        String url = ApiConfig.API_BASE_URL + ApiConstant.ApiUri.URI_ATRICLE_VIEW.getDesc();
        postJson(url, map, viewParm);
    }

    /**
     * 点赞咨询+1
     * <p>
     * "articleId":1,
     * "userId":1
     */
    public static void praiseArticle(String userId, String articleId, ViewParm viewParm) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("userId", userId);
        map.put("articleId", articleId);
        String url = ApiConfig.API_BASE_URL + ApiConstant.ApiUri.URI_ATRICLE_PRAISE.getDesc();
        postJson(url, map, viewParm);
    }

}
