package org.ionchain.wallet.helper;

import android.app.Activity;

import java.util.Stack;

/**
 * Activity管理类 
 *  
 */  
public class ActivityHelper {
  
    private static Stack<Activity> activityStack;
  
    private static ActivityHelper instance;
  
    private ActivityHelper() {
    }  
  
    /** 
     * 单一实例 
     */  
    public static ActivityHelper getHelper() {
        if (instance == null) {  
            instance = new ActivityHelper();
        }  
        return instance;  
    }  
  
    /** 
     * 添加Activity到堆栈 
     */  
    public void addActivity(Activity activity) {  
        if (activityStack == null) {  
            activityStack = new Stack<Activity>();  
        }  
        activityStack.add(activity);  
    }  
  
    /** 
     * 获取当前Activity（堆栈中最后一个压入的） 
     */  
    public Activity currentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }  
  
    /** 
     * 结束当前Activity（堆栈中最后一个压入的） 
     */  
    public void finishActivity() {  
        Activity activity = activityStack.lastElement();  
        finishActivity(activity);  
    }  
  
    /** 
     * 结束指定的Activity 
     */  
    public void finishActivity(Activity activity) {  
        if (activity != null) {  
            activityStack.remove(activity);  
            activity.finish();  
            activity = null;  
        }  
    }  
  
    /** 
     * 结束指定类名的Activity 
     */  
    public void finishActivity(Class<?> cls) {  
        for (Activity activity : activityStack) {  
            if (activity.getClass().equals(cls)) {  
                finishActivity(activity);  
            }  
        }  
    }  
  
    /** 
     * 结束所有Activity 
     */  
    public void finishAllActivity() {  
        for (int i = 0, size = activityStack.size(); i < size; i++) {  
            if (null != activityStack.get(i)) {  
                activityStack.get(i).finish();  
            }  
        }  
        activityStack.clear();  
    }  
  
    /** 
     * 退出应用程序 
     */  
    @SuppressWarnings("deprecation")  
    public void AppExit() {
        try {  
            finishAllActivity();  
        } catch (Exception e) {
            e.printStackTrace();  
        }  
    }  
}
