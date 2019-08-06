package org.ionchain.wallet.view.activity.create;

import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import org.ionc.wallet.bean.WalletBeanNew;
import org.ionc.wallet.callback.OnImportMnemonicCallback;
import org.ionc.wallet.callback.OnSimulateTimeConsume;
import org.ionc.wallet.sdk.IONCWallet;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionc.wallet.utils.StringUtils;
import org.ionchain.wallet.R;
import org.ionchain.wallet.utils.SoftKeyboardUtil;
import org.ionchain.wallet.utils.ToastUtil;
import org.ionchain.wallet.view.activity.MainActivity;
import org.ionchain.wallet.view.activity.imports.SelectImportModeActivity;
import org.ionchain.wallet.view.base.AbsBaseActivityTitleTwo;
import org.ionchain.wallet.view.widget.dialog.callback.OnDialogCheck12MnemonicCallbcak;
import org.ionchain.wallet.view.widget.dialog.mnemonic.DialogCheckMnemonic;
import org.ionchain.wallet.view.widget.dialog.export.DialogTextMessage;
import org.ionchain.wallet.view.widget.dialog.mnemonic.DialogMnemonicShow;

import java.util.List;

import static org.ionc.wallet.utils.RandomUntil.getNum;
import static org.ionc.wallet.utils.StringUtils.check;
import static org.ionchain.wallet.constant.ConstantActivitySkipTag.INTENT_FROM_WHERE_TAG;
import static org.ionchain.wallet.constant.ConstantParams.SERIALIZABLE_DATA_WALLET_BEAN;
import static org.ionchain.wallet.view.fragment.AssetFragment.NEW_WALLET_FOR_RESULT_CODE;

public class CreateWalletActivity extends AbsBaseActivityTitleTwo implements
        OnImportMnemonicCallback,
        OnSimulateTimeConsume,
        DialogMnemonicShow.OnSavedMnemonicCallback,
        DialogTextMessage.OnBtnClickedListener, OnDialogCheck12MnemonicCallbcak {

    private AppCompatEditText walletNameEt;
    private AppCompatEditText pwdEt;
    private AppCompatEditText resetPwdEt;
    private CheckBox checkbox;
    private TextView linkUrlTv;
    private Button createBtn;
    private TextView importBtn;

    private String walletnamestr;
    private String pass;
    private String resetpass;
    private DialogMnemonicShow dialogMnemonic;
    WalletBeanNew walletBean;


    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-09-17 19:48:43 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        walletNameEt = findViewById(R.id.walletNameEt);
        pwdEt = findViewById(R.id.pwdEt);
        resetPwdEt = findViewById(R.id.resetPwdEt);
        checkbox = findViewById(R.id.checkbox);
        linkUrlTv = findViewById(R.id.linkUrlTv);
        createBtn = findViewById(R.id.createBtn);
        importBtn = findViewById(R.id.importBtn);

    }

    @Override
    protected void handleIntent(@NonNull Intent intent) {
        super.handleIntent(intent);
        mActivityFrom = intent.getStringExtra(INTENT_FROM_WHERE_TAG);
    }

 

    @Override
    protected void initView() {
        findViews();

        createBtn.setOnClickListener(v -> {

            /*
             * 检查钱包名字是否符规则
             * */
            walletnamestr = walletNameEt.getText().toString().trim();
            pass = pwdEt.getText().toString().trim();
            resetpass = resetPwdEt.getText().toString().trim();
            if (TextUtils.isEmpty(walletnamestr)) {
                ToastUtil.showShort(getResources().getString(R.string.please_input_wallet_name));
                return;
            }
            if (!StringUtils.isNumENCN(walletnamestr)) {
                ToastUtil.showToastLonger(getResources().getString(R.string.illegal_name));
                return;
            }
            if (walletnamestr.length() > 8) {
                ToastUtil.showLong(getResources().getString(R.string.illegal_name_length));
                return;
            }

            /*
             * 从数据库比对，重复检查
             * */
            if (null != IONCWallet.getWalletByName(walletnamestr)) {
                Toast.makeText(mActivity.getApplicationContext(), getResources().getString(R.string.wallet_name_exists), Toast.LENGTH_SHORT).show();
                return;
            }


            if (!check(resetpass) || !check(pass)) {
                ToastUtil.showToastLonger(getResources().getString(R.string.illegal_password));
                return;
            }
            if (!resetpass.equals(pass)) {
                ToastUtil.showShortToast(getResources().getString(R.string.illegal_password_must_equal));
                return;
            }

            showProgress(getResources().getString(R.string.creating_wallet));
            IONCWallet.simulateTimeConsuming(CreateWalletActivity.this);

        });
        importBtn.setOnClickListener(v -> {
            Intent intent = new Intent(mActivity, SelectImportModeActivity.class);
            startActivityForResult(intent, NEW_WALLET_FOR_RESULT_CODE);
        });
        linkUrlTv.setOnClickListener(v -> skipWebProtocol());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_wallet_create;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected String getTitleName() {
        return getAppString(R.string.app_create_wallet);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == NEW_WALLET_FOR_RESULT_CODE) {
            Intent intent = new Intent();
            WalletBeanNew walletBeanNew = null;
            if (data != null) {
                walletBeanNew = data.getParcelableExtra(SERIALIZABLE_DATA_WALLET_BEAN);
                LoggerUtils.i("requestCode", "requestCode = " + requestCode + "resultCode = " + resultCode + "address = " + walletBeanNew.getAddress());
                intent.putExtra(SERIALIZABLE_DATA_WALLET_BEAN, walletBeanNew);
                setResult(NEW_WALLET_FOR_RESULT_CODE, intent);
                finish();
            }
        }
    }

    @Override
    public void onImportMnemonicSuccess(@NonNull WalletBeanNew walletBean) {
        Log.i(TAG, "onCreateSuccess: " + walletBean);
        hideProgress();
        walletBean.setMIconIndex(getNum(7));
        IONCWallet.saveWallet(walletBean);
        SoftKeyboardUtil.hideSoftKeyboard(this);
        this.walletBean = walletBean;
        //首先备份助记词
        String[] mnemonics = walletBean.getMnemonic().split(" ");
        dialogMnemonic = new DialogMnemonicShow(this, mnemonics, this);
        dialogMnemonic.show();
    }

    @Override
    public void onImportMnemonicFailure(@NonNull String error) {
        hideProgress();
        ToastUtil.showToastLonger(error);
        SoftKeyboardUtil.hideSoftKeyboard(this);
    }

    @Override
    public void onSimulateFinish() {
        IONCWallet.createIONCWallet(walletnamestr, pass, CreateWalletActivity.this);
    }

    /**
     * 如果点击下一步,则说明用户即将保存
     * 弹出提示助记词的重要性
     */
    @Override
    public void onSaveMnemonicSure() {
        String TO_SAVE = "to_save";
        new DialogTextMessage(this).setTitle(getResources().getString(R.string.attention))
                .setMessage(getResources().getString(R.string.key_store_to_save))
                .setBtnText(getResources().getString(R.string.i_know))
                .setHintMsg("")
                .setCancelByBackKey(true)
                .setTag(TO_SAVE)
                .setCopyBtnClickedListener(this).show();
    }

    /**
     * @param dialogMnemonic 取消备份助记词
     */
    @Override
    public void onSaveMnemonicCancel(DialogMnemonicShow dialogMnemonic) {
        dialogMnemonic.dismiss();
        IONCWallet.changeMainWalletAndSave(walletBean);
        skipToBack(walletBean); //创建钱包时，取消备份助记词
    }


    /**
     * @param dialogTextMessage
     */
    @Override
    public void onDialogTextMessageBtnClicked(DialogTextMessage dialogTextMessage) {
        dialogMnemonic.dismiss();
        dialogTextMessage.dismiss();
        //保存准状态
        //去测试一下助记词
        new DialogCheckMnemonic(this, this).show();
    }


    /**
     * 助记词检测结果,失败
     * <p>
     * 让用户输入密码,重新保存助记词,
     * 若密码输入错误则该钱包对用户来说,极大可能是不可用的,可以提示用户删除该钱包,
     * 此时删除钱包则无需密码,直接删除
     *
     * @param s                   助记词
     * @param editTextList
     * @param dialogCheckMnemonic
     */

    @Override
    public void onDialogCheckMnemonics12(String[] s, List<AppCompatEditText> editTextList, DialogCheckMnemonic dialogCheckMnemonic) {
        String[] mnemonics = walletBean.getMnemonic().split(" ");
        if (s.length != mnemonics.length) {
            ToastUtil.showToastLonger(getResources().getString(R.string.error_mnemonics));
            return;
        }
        int count = mnemonics.length;
        for (int i = 0; i < count; i++) {
            if (!mnemonics[i].equals(s[i])) {
                String index = String.valueOf((i + 1));
                ToastUtil.showToastLonger(getResources().getString(R.string.error_index_mnemonics, index));
                editTextList.get(i).setTextColor(Color.RED);
                return;
            }
        }
        //更新
        walletBean.setMnemonic("");
        IONCWallet.updateWallet(walletBean);
        ToastUtil.showToastLonger(getResources().getString(R.string.authentication_successful));
        //跳转到首页
        if (IONCWallet.getAllWalletNew().size() == 1) {
            LoggerUtils.i("导入私钥--钱包不存在---执行导入---导入私钥成功--只有一个钱包");
            skip(MainActivity.class);
        } else {
            skipToBack(walletBean);//创建钱包时，取消备份助记词
        }
    }
}
