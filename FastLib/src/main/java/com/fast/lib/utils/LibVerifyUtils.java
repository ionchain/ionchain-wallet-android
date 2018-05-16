package com.fast.lib.utils;

import android.text.TextUtils;

import java.util.regex.Pattern;

/**
 * Created by siberiawolf on 17/6/15.
 */

public class LibVerifyUtils {

    /**
     * 验证手机号码（支持国际格式，+86135xxxx...（中国内地），+00852137xxxx...（中国香港））
     *
     * @param telNumber 移动、联通、电信运营商的号码段
     * <p>移动的号段：134(0-8)、135、136、137、138、139、147、150、151、152、157、158、159、187、188</p>
     * <p>联通的号段：130、131、132、155、156、185、186</p>
     * <p>电信的号段：133、153、180、189</p>
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkMobileNumber(String telNumber) {
        if (!TextUtils.isEmpty(telNumber)) {
            String regex = "(\\+\\d+)?1[34578]\\d{9}$";
            return Pattern.matches(regex, telNumber);
        }

        return false;
    }

    // 判断邮箱地址是否有效
    public static boolean checkEmailAddress(String emailAddress) {

        if (!TextUtils.isEmpty(emailAddress)) {
            String regex = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
            return Pattern.matches(regex, emailAddress);
        }

        return false;
    }

    // 判断内容是否由字母，数字，下划线组成
    public static boolean checkContent(String content) {

        if (!TextUtils.isEmpty(content)) {
            String regex = "^[\\w\\u4e00-\\u9fa5]+$";
            return Pattern.matches(regex, content);
        }

        return false;
    }

    // 判断身份证号码是否有效
    public static boolean checkIdCard(String idCard) {
        return IdcardUtils.validateCard(idCard);
    }


    /**
     * 手机号码，中间4位星号替换
     *
     * @param phone 手机号
     * @return 星号替换的手机号
     */
    public static String phoneNoHide(String phone) {
        // 括号表示组，被替换的部分$n表示第n组的内容
        // 正则表达式中，替换字符串，括号的意思是分组，在replace()方法中，
        // 参数二中可以使用$n(n为数字)来依次引用模式串中用括号定义的字串。
        // "(\d{3})\d{4}(\d{4})", "$1****$2"的这个意思就是用括号，
        // 分为(前3个数字)中间4个数字(最后4个数字)替换为(第一组数值，保持不变$1)(中间为*)(第二组数值，保持不变$2)
        return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    /**
     * 银行卡号，保留最后4位，其他星号替换
     *
     * @param cardId 卡号
     * @return 星号替换的银行卡号
     */
    public static String cardIdHide(String cardId) {
        return cardId.replaceAll("\\d{15}(\\d{3})", "**** **** **** **** $1");
    }

    /**
     * 身份证号，中间10位星号替换
     *
     * @param id 身份证号
     * @return 星号替换的身份证号
     */
    public static String idHide(String id) {
        return id.replaceAll("(\\d{4})\\d{10}(\\d{4})", "$1** **** ****$2");
    }

    /**
     * 简单验证用户名（字母、数字、下划线）
     *
     * @param username 用户名
     * @return 是否为用户名
     */

    public static boolean checkUserName(String username) {
        Pattern pattern = Pattern.compile("^\\w+$");
        return pattern.matcher(username).find();

    }
    /**
     * 简单验证昵称（字母、数字、汉字、下划线）
     *
     * @param nickname 昵称
     * @return 是否为昵称
     */

    public static boolean checkNickName(String nickname) {
        Pattern pattern = Pattern.compile("\\w\\u4e00-\\u9fa5]+");
        return pattern.matcher(nickname).find();

    }
    /**
     * 简单验证密码（6-16位、字母、数字、字符）
     *
     * @param password 密码
     * @return 是否为密码
     */

    public static boolean checkPassword(String password) {
        Pattern pattern = Pattern.compile("^(\\w|[`~!@#$%^&*()+=|{}':;',\\\\\\\\[\\\\\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]){6,16}$");
        return pattern.matcher(password).find();

    }
    /**
     * 是否为车牌号（川A88888）
     *
     * @param vehicleNo 车牌号
     * @return 是否为车牌号
     */

    public static boolean checkVehicleNo(String vehicleNo) {
        Pattern pattern = Pattern.compile("^[\u4e00-\u9fa5]{1}[a-zA-Z]{1}[a-zA-Z_0-9]{5}$");
        return pattern.matcher(vehicleNo).find();

    }

    /**
     * 验证中文
     *
     * @param chinese 中文字符
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkChinese(String chinese) {
        String regex = "^[\u4E00-\u9FA5]+$";
        return Pattern.matches(regex, chinese);
    }

    /**
     * 验证日期（年月日）
     *
     * @param birthday 日期，格式：1992-09-03，或1992.09.03
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkBirthday(String birthday) {
        String regex = "[1-9]{4}([-./])\\d{1,2}\\1\\d{1,2}";
        return Pattern.matches(regex, birthday);
    }

    /**
     * 验证URL地址
     *
     * @param url 格式：http://blog.csdn.net:80/xyang81/article/details/7705960? 或 http://www.csdn.net:80
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkURL(String url) {
        String regex = "(https?://(w{3}\\.)?)?\\w+\\.\\w+(\\.[a-zA-Z]+)*(:\\d{1,5})?(/\\w*)*(\\??(.+=.*)?(&.+=.*)?)?";
        return Pattern.matches(regex, url);
    }

    /**
     * 匹配中国邮政编码
     *
     * @param postcode 邮政编码
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkPostcode(String postcode) {
        String regex = "[1-9]\\d{5}";
        return Pattern.matches(regex, postcode);
    }



}
