package org.ionchain.wallet.utils;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.regex.Pattern;

/**
 * Created by WINDOWS on 2016/12/29.
 */

public class CheckUtil {
    private static final Pattern dateFormat = Pattern.compile("(\\d+)-(\\d+)-(\\d+)");
    private static final Pattern monthFormat = Pattern.compile("(\\d+)-(\\d+)");

    public static boolean isUsername(String username) {
        String regEx = "[a-zA-Z][a-zA-Z0-9_]{3,15}";
        return !TextUtils.isEmpty(username) && !username.contains("51bel,bel,beier,zhongzilian,zzl,admin,test") && username.matches(regEx);
    }

    public static boolean isPassword(String pwd) {
        String regEx = "^(?![^a-zA-Z]+$)(?!\\D+$).{6,16}$";
        return !TextUtils.isEmpty(pwd) && pwd.matches(regEx);
    }

    public static boolean isMobileNO(String mobiles) {
        String telRegex = "^((13[0-9])|(15[^4,\\D])|(16[6])|(18[0-9])|(14[0-9])|(17[0-9])|(19[8,9]))\\d{8}$";
        return !TextUtils.isEmpty(mobiles) && mobiles.matches(telRegex);
    }

    public static boolean isBankAccount(String code){
        String bankRegex = "^[0-9]{16,19}$";
        return !TextUtils.isEmpty(code) && code.matches(bankRegex);
    }

//    public static boolean isOneMonth(String startTime, String endTime){
//        if (StringUtils.isSpace(startTime) || StringUtils.isSpace(endTime)){
//            MyToast.showToast("请选择开始时间和结束时间");
//            return false;
//        }
//        Matcher matcher = dateFormat.matcher(startTime);
//        Matcher m = dateFormat.matcher(endTime);
//        if (matcher.find() && m.find()){
//            String syear = matcher.group(1);
//            String smonth = matcher.group(2);
//            String sday = matcher.group(3);
//            String eyear = m.group(1);
//            String emonth = m.group(2);
//            String eday = m.group(3);
//            int startYear = Integer.parseInt(syear);
//            int startMonth = Integer.parseInt(smonth);
//            int startDay = Integer.parseInt(sday);
//            int endYear = Integer.parseInt(eyear);
//            int endMonth = Integer.parseInt(emonth);
//            int endDay = Integer.parseInt(eday);
//            Calendar startCalendar = Calendar.getInstance();
//            startCalendar.set(startYear,startMonth - 1,startDay);
//            Calendar endCalendar = Calendar.getInstance();
//            endCalendar.set(endYear,endMonth - 1,endDay);
//            Calendar current = Calendar.getInstance();
//            if (startCalendar.getTimeInMillis() > current.getTimeInMillis() ||
//                    endCalendar.getTimeInMillis() > current.getTimeInMillis()){
//                MyToast.showToast("查询时间不能大于当前时间");
//                return false;
//            }
//            if (endCalendar.getTimeInMillis() < startCalendar.getTimeInMillis()){
//                MyToast.showToast("结束时间不能小于开始时间");
//                return false;
//            }
//            startCalendar.add(Calendar.DAY_OF_MONTH,30);
//            if (endCalendar.getTimeInMillis() <= startCalendar.getTimeInMillis()){
//                return true;
//            }else {
//                MyToast.showToast("时间跨度超过一个月，请重新选择");
//            }
//        }else {
//            MyToast.showToast("请选择开始时间和结束时间");
//            return false;
//        }
//        return false;
//    }
//
//    public static boolean isSixMonth(String startTime, String endTime) {
//        if (StringUtils.isSpace(startTime) || StringUtils.isSpace(endTime)) {
//            MyToast.showToast("请选择开始时间和结束时间");
//            return false;
//        }
//        Matcher matcher = monthFormat.matcher(startTime);
//        Matcher m = monthFormat.matcher(endTime);
//        if (matcher.find() && m.find()) {
//            String syear = matcher.group(1);
//            String smonth = matcher.group(2);
//            String eyear = m.group(1);
//            String emonth = m.group(2);
//            int startYear = Integer.parseInt(syear);
//            int startMonth = Integer.parseInt(smonth);
//            int endYear = Integer.parseInt(eyear);
//            int endMonth = Integer.parseInt(emonth);
//            Calendar startCalendar = Calendar.getInstance();
//            startCalendar.set(startYear,startMonth - 1,1);
//            Calendar endCalendar = Calendar.getInstance();
//            endCalendar.set(endYear,endMonth - 1,1);
//            Calendar current = Calendar.getInstance();
//            /*if (startCalendar.getTimeInMillis() > current.getTimeInMillis() ||
//                    endCalendar.getTimeInMillis() > current.getTimeInMillis()){
//                MyToast.showToast("查询时间不能大于当前时间");
//                return false;
//            }*/
//            if (endCalendar.getTimeInMillis() < startCalendar.getTimeInMillis()){
//                MyToast.showToast("结束时间不能小于开始时间");
//                return false;
//            }
//            startCalendar.add(Calendar.DAY_OF_MONTH,180);
//            if (endCalendar.getTimeInMillis() <= startCalendar.getTimeInMillis()){
//                return true;
//            }else {
//                MyToast.showToast("时间跨度超过六个月，请重新选择");
//            }
//        }else {
//            MyToast.showToast("请选择开始时间和结束时间");
//            return false;
//        }
//        return false;
//    }

    /**
     * 限制金额输入
     * 保留小数点后两位
     * @param editText 需限制输入的控件
     */
    public static void inputAmount(final EditText editText){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s == null) return;
                if (s.toString().contains(".") && s.length() - 1 - s.toString().indexOf(".") > 2) {
                    //输入包含小数点且小数点不在倒数第三位上
                    s = s.toString().subSequence(0,
                            s.toString().indexOf(".") + 3);
                    editText.setText(s);
                    editText.setSelection(s.length());
                }
                if (s.toString().trim().equals(".")) {
                    //首先输入点时 首位+0
                    s = "0" + s;
                    editText.setText(s);
                    editText.setSelection(2);
                }
                if (s.toString().startsWith("0") && s.toString().trim().length() > 1
                        && !s.toString().substring(1, 2).equals(".")) {
                    //首位为0 后一位必须输入点
                    editText.setText(s.subSequence(0, 1));
                    editText.setSelection(1);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}
