package org.ionchain.wallet.comm.api;

import com.fast.lib.logger.Logger;
import com.fast.lib.okhttp.ResponseBean;
import com.fast.lib.okhttp.callback.ResultCallback;
import com.fast.lib.okhttp.request.OkHttpRequest;

import org.ionchain.wallet.comm.api.conf.ApiConfig;
import org.ionchain.wallet.comm.api.constant.ApiConstant;
import org.ionchain.wallet.comm.api.request.ViewParm;
import org.ionchain.wallet.comm.constants.Comm;
import org.ionchain.wallet.ui.comm.BaseActivity;

import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;

import okhttp3.Request;

public class ApiLogin extends BaseApi {

    /**
     * 登录
     *
     * @param tel
     * @param password
     * @param viewParm
     */
    public static void login(String tel, String password, ViewParm viewParm) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("tel", tel);
        map.put("password", password);
        String url = ApiConfig.API_BASE_URL + ApiConstant.ApiUri.URI_LOGIN.getDesc();
        postJson(url, map, viewParm);

    }

    /**
     * 获取短信验证码
     *
     * @param tel

     * @param viewParm
     */
    public static void sendSmsCode(String tel,ViewParm viewParm) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("tel", tel);
        String url = ApiConfig.API_BASE_URL + ApiConstant.ApiUri.URI_SMS_CODE.getDesc();
        postJson(url, map, viewParm);
    }

    /**
     * 注册
     *
     * @param tel
     * @param smsCode
     * @param password
     * @param inviteCode
     * @param viewParm
     */
    public static void register(String tel, String smsCode, String password, String inviteCode, ViewParm viewParm) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("tel", tel);
        map.put("smsCode", smsCode);
        map.put("password", password);
        map.put("inviteCode", inviteCode);
        String url = ApiConfig.API_BASE_URL + ApiConstant.ApiUri.URI_REG.getDesc();
        postJson(url, map, viewParm);
    }

    /**
     * 修改密码
     * "smsCode":"5400",
     * "tel":"18621870243",
     * "newpassword":"123456AaB"
     */
    public  static void updatePassWord(String tel, String smsCode, String password, ViewParm viewParm) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("tel", tel);
        map.put("smsCode", smsCode);
        map.put("newpassword", password);
        String url = ApiConfig.API_BASE_URL + ApiConstant.ApiUri.URI_EDIT_PASS.getDesc();
        postJson(url, map, viewParm);
    }

    public void getSysInfo( ViewParm viewParm){
        HashMap<String, String> map = new HashMap<String, String>();
        String url = ApiConfig.API_BASE_URL + ApiConstant.ApiUri.URI_SYS_INFO.getDesc();
        postJson(url, map, viewParm);
    }

    public void getSysInfoSyn(){

    }
}
