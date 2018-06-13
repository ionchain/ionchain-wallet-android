package org.ionchain.wallet.ui.login.view;

import android.os.CountDownTimer;
import android.widget.TextView;


public class CountTimer extends CountDownTimer {

    private TextView btn;
    private int normalColor, timingColor;// 未计时的文字颜色，计时期间的文字颜色
    public static int TIME_REST;


    public CountTimer(TextView btn, long time) {
        super(time, 1000);
        this.btn = btn;
    }

    public CountTimer(TextView btn, int time, int normalColor, int timingColor) {
        super(time * 1000, 1000);
        this.btn = btn;
        this.normalColor = normalColor;
        this.timingColor = timingColor;
    }

    @Override
    public void onFinish() {
        if (timingColor > 0) {
            btn.setText("继续");
            btn.setEnabled(true);
        } else {
            btn.setText("");
        }

    }

    @Override
    public void onTick(long millisUntilFinished) {
        if (timingColor > 0) {
            btn.setText(millisUntilFinished / 1000 + "秒后重新获取");
            btn.setEnabled(false);
            TIME_REST = (int) (millisUntilFinished / 1000);
        } else {
            //long month = millisUntilFinished / 1000 / 60 / 60 / 24 / 30;
            long day = millisUntilFinished / (1000 * 60 * 60 * 24) % 30;//天
            long hour = millisUntilFinished / (1000 * 60) / 60 % 24;//时
            long minute = millisUntilFinished / (1000 * 60) % 60;//分
            long second = millisUntilFinished / 1000 % 60;//秒
            //Log.e("kmy", day + "---天" + hour + "---时" + minute + "----分" + second + "---秒");
            if (day != 0) {
                btn.setText("剩余时间:" + day + "天" + hour + "时" + minute + "分" + second + "秒");
            } else if (hour != 0) {
                btn.setText("剩余时间:" + hour + "时" + minute + "分" + second + "秒");
            } else if (minute != 0) {
                btn.setText("剩余时间:" + minute + "分" + second + "秒");
            } else if (second != 0) {
                btn.setText("剩余时间:" + second + "秒");
            }
        }

    }
}