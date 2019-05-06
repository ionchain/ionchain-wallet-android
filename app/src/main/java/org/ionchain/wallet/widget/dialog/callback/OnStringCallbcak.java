package org.ionchain.wallet.widget.dialog.callback;

import android.support.v7.widget.AppCompatEditText;

import org.ionchain.wallet.widget.dialog.check.DialogCheckMnemonic;

import java.util.List;

/**
 * describe:
 *
 * @author 596928539@qq.com
 * @date 2019/04/09
 */
public interface OnStringCallbcak {
    /**
     * @param s 回传数据
     * @param editTextList
     * @param dialogCheckMnemonic
     */
    void onString(String[] s, List<AppCompatEditText> editTextList, DialogCheckMnemonic dialogCheckMnemonic);
}
