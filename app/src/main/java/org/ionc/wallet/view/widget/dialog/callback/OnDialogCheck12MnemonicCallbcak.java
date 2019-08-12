package org.ionc.wallet.view.widget.dialog.callback;


import androidx.appcompat.widget.AppCompatEditText;

import org.ionc.wallet.view.widget.dialog.mnemonic.DialogCheckMnemonic;

import java.util.List;

/**
 * describe:
 *
 * @author 596928539@qq.com
 * @date 2019/04/09
 */
public interface OnDialogCheck12MnemonicCallbcak {
    /**
     * 回传给检查者,测试助记词是否正确
     * @param s 回传数据 ,测试助记词是否正确
     * @param editTextList 助记词编辑框列表
     * @param dialogCheckMnemonic 助记词检查对话框
     */
    void onDialogCheckMnemonics12(String[] s, List<AppCompatEditText> editTextList, DialogCheckMnemonic dialogCheckMnemonic);
}
