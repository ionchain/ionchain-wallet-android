package org.ionc.wallet.utils;

import android.content.Context;
import android.os.Build;
import android.widget.Button;

import org.ionchain.wallet.R;

public class ViewUtils {
  public static void setBtnSelectedColor(Context context, Button button){

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          button.setBackground(context.getDrawable(R.drawable.common_btn_background_main_color) );
      }  else {
          button.setBackgroundResource(R.drawable.common_btn_background_main_color);
      }
  }

    public static void setBtnUnSelectedColor(Context context, Button button) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            button.setBackground(context.getDrawable(R.drawable.common_btn_background_white_color));
        } else {
            button.setBackgroundResource(R.drawable.common_btn_background_white_color);
        }
    }
}
