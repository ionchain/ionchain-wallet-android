package org.ionchain.wallet.widget.dialog.modify;

/**
 * describe: 修改对话框回调接口
 *
 * @author xubinbin
 * @date 2019/03/12
 */
public interface OnModifyPasswordDialogCallback {
    /** 确定修改
     * @param currentPassword 当前密码
     * @param newPassword 新密码
     * @param newPasswordAgain 重复新密码
     */
    void onModifyDialogParam(String currentPassword, String newPassword, String newPasswordAgain);
}
