package org.ionchain.wallet.ui.wallet;

import android.os.Bundle;
import android.text.InputType;

import com.fast.lib.logger.Logger;
import com.fast.lib.utils.ToastUtil;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import org.ionchain.wallet.R;
import org.ionchain.wallet.comm.api.resphonse.ResponseModel;
import org.ionchain.wallet.ui.comm.BaseActivity;

public class ModifyWalletActivity extends BaseActivity {


    @Override
    public void handleMessage(int what, Object obj) {
        super.handleMessage(what, obj);
        try{

            switch (what){
                case R.id.navigationBack:
                    finish();
                    break;
                case R.id.saveBtn:
                    break;
                case R.id.delBtn:
                    finish();
                    break;
                case R.id.modifyPwdLayout:
                    transfer(ModifyWalletPwdActivity.class);
                    break;
                case R.id.importLayout:
                    showEditTextDialog();
                    break;

                case 0:
                    dismissProgressDialog();
                    if(obj == null)
                        return;

                    ResponseModel<String> responseModel = (ResponseModel)obj;
                    if(!verifyStatus(responseModel)){
                        ToastUtil.showShortToast(responseModel.getMsg());
                        return;
                    }


                    break;
            }

        }catch (Throwable e){
            Logger.e(e,TAG);
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_wallet_modify);

    }

    @Override
    protected void setListener() {
        setOnClickListener(R.id.delBtn);
        setOnClickListener(R.id.modifyPwdLayout);
        setOnClickListener(R.id.importLayout);

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }


    private void showEditTextDialog() {
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(this);
        builder.setTitle("输入密码")
                .setPlaceholder("在此输入密码")
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        CharSequence text = builder.getEditText().getText();
                        if (text != null && text.length() > 0) {

                            dialog.dismiss();
                        } else {

                        }
                    }
                })
                .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();
    }

    @Override
    public int getActivityMenuRes() {
        return R.menu.menu_wallet_modify;
    }

    @Override
    public int getHomeAsUpIndicatorIcon() {
        return R.mipmap.ic_arrow_back;
    }

    @Override
    public int getActivityTitleContent() {
        return R.string.activity_modify_wallet;
    }
}
