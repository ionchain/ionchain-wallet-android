package org.ionchain.wallet.mvp.view.activity.createwallet;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.ionc.dialog.callback.OnStringCallbcak;
import org.ionc.dialog.check.DialogCheckMnemonic;
import org.ionc.dialog.export.DialogTextMessage;
import org.ionc.dialog.mnemonic.DialogMnemonic;
import org.ionc.wallet.bean.WalletBean;
import org.ionc.wallet.callback.OnImportMnemonicCallback;
import org.ionc.wallet.callback.OnSimulateTimeConsume;
import org.ionc.wallet.sdk.IONCWalletSDK;
import org.ionc.wallet.utils.StringUtils;
import org.ionchain.wallet.App;
import org.ionchain.wallet.R;
import org.ionchain.wallet.mvp.view.activity.MainActivity;
import org.ionchain.wallet.mvp.view.activity.importmode.SelectImportModeActivity;
import org.ionchain.wallet.mvp.view.activity.sdk.SDKSelectCreateModeWalletActivity;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;
import org.ionchain.wallet.utils.SoftKeyboardUtil;
import org.ionchain.wallet.utils.ToastUtil;

import java.util.List;
import java.util.Objects;

import static org.ionc.wallet.utils.RandomUntil.getNum;
import static org.ionc.wallet.utils.StringUtils.check;
import static org.ionchain.wallet.constant.ConstantParams.SERVER_PROTOCOL_VALUE;

public class CreateWalletActivity extends AbsBaseActivity implements
        TextWatcher,
        OnImportMnemonicCallback,
        OnSimulateTimeConsume,
        DialogMnemonic.OnSavedMnemonicCallback,
        DialogTextMessage.OnBtnClickedListener, OnStringCallbcak {

    private ImageView back;
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
    private DialogMnemonic dialogMnemonic;
    WalletBean walletBean;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-09-17 19:48:43 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        back = findViewById(R.id.back);
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
    }

    @Override
    protected void initView() {
        findViews();
       mImmersionBar.titleView(R.id.toolbarlayout).statusBarDarkFont(true).execute();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (walletNameEt.getText() != null && pwdEt.getText() != null && resetPwdEt.getText() != null) {
                    String content = walletNameEt.getText().toString().trim();
                    String pwdstr = pwdEt.getText().toString().trim();
                    String pass2 = resetPwdEt.getText().toString().trim();

                    if (!TextUtils.isEmpty(content) && !TextUtils.isEmpty(pwdstr) && !TextUtils.isEmpty(pass2) && checkbox.isChecked()) {
                        createBtn.setEnabled(true);
                        createBtn.setBackgroundColor(getResources().getColor(R.color.blue_top));
                    } else {
                        createBtn.setEnabled(false);
                        createBtn.setBackgroundColor(getResources().getColor(R.color.grey));
                    }
                }
            }
        });
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                /*
                 * 检查钱包名字是否符规则
                 * */

                if (TextUtils.isEmpty(walletnamestr)) {
                    ToastUtil.showShort("请输入钱包名字");
                    return;
                }
                if (!StringUtils.isNumENCN(walletnamestr)) {
                    ToastUtil.showToastLonger("名字不符合规则!");
                    return;
                }
                if (walletnamestr.length() > 8) {
                    ToastUtil.showLong("名字字数不能超过8个");
                    return;
                }
                /*
                 * 从数据库比对，重复检查
                 * */
                if (null != IONCWalletSDK.getInstance().getWalletByName(walletnamestr)) {
                    Toast.makeText(mActivity.getApplicationContext(), "该名称的钱包已经存在，请换一个钱包名称", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (!check(resetpass) || !check(pass)) {
                    ToastUtil.showToastLonger("密码不符合要求！");
                    return;
                }
                if (!resetpass.equals(pass)) {
                    ToastUtil.showShortToast("密码和重复密码必须相同");
                    return;
                }

                showProgress("正在创建钱包……");
                IONCWalletSDK.getInstance().simulateTimeConsuming(CreateWalletActivity.this);

            }
        });
        importBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (App.SDK_Debug) {
                    skip(SDKSelectCreateModeWalletActivity.class);
                } else {
                    skip(SelectImportModeActivity.class);//
                }
            }
        });
        linkUrlTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipWeb(SERVER_PROTOCOL_VALUE);
            }
        });
        walletNameEt.addTextChangedListener(this);
        pwdEt.addTextChangedListener(this);
        resetPwdEt.addTextChangedListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_wallet_create;
    }

    @Override
    protected void initData() {

    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (walletNameEt.getText() == null) {
            ToastUtil.showToastLonger("请输入钱包名字");
            return;
        }
        if (pwdEt.getText() == null) {
            ToastUtil.showToastLonger("请输入钱包密码");
            return;
        }
        if (resetPwdEt.getText() == null) {
            ToastUtil.showToastLonger("请输入钱包重复密码");
            return;
        }
        walletnamestr = walletNameEt.getText().toString().trim();
        pass = pwdEt.getText().toString().trim();
        resetpass = resetPwdEt.getText().toString().trim();

        if (!TextUtils.isEmpty(walletnamestr) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(resetpass) && checkbox.isChecked()) {
            createBtn.setEnabled(true);
            createBtn.setBackgroundColor(getResources().getColor(R.color.blue_top));
        } else {
            createBtn.setEnabled(false);
            createBtn.setBackgroundColor(getResources().getColor(R.color.grey));
        }
    }


    @Override
    public void onImportMnemonicSuccess(@NonNull WalletBean walletBean) {
        Log.i(TAG, "onCreateSuccess: " + walletBean);
        hideProgress();
        IONCWalletSDK.getInstance().saveWallet(walletBean);
        SoftKeyboardUtil.hideSoftKeyboard(this);
        walletBean.setMIconIdex(getNum(7));
        this.walletBean = walletBean;
        //首先备份助记词
        String[] mnemonics = walletBean.getMnemonic().split(" ");
        dialogMnemonic = new DialogMnemonic(this, mnemonics, this);
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
        IONCWalletSDK.getInstance().createBip39Wallet(walletnamestr, pass, CreateWalletActivity.this);
    }

    /**
     * 如果点击下一步,则说明用户即将保存
     * 弹出提示助记词的重要性
     */
    @Override
    public void onToSaved() {
        String TO_SAVE = "to_save";
        new DialogTextMessage(this).setTitle("注意")
                .setMessage("请务必妥善保管您的助记词,一旦丢失,你的财产可能面临重大损失!")
                .setBtnText("我已知晓并保存")
                .setHintMsg("")
                .setCancelByBackKey(true)
                .setTag(TO_SAVE)
                .setCopyBtnClickedListener(this).show();
    }

    @Override
    public void onCancel(DialogMnemonic dialogMnemonic) {
        dialogMnemonic.dismiss();
        skip(MainActivity.class);
    }


    @Override
    public void onBtnClick(DialogTextMessage dialogTextMessage) {
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
     *  @param s 助记词
     * @param editTextList
     * @param dialogCheckMnemonic
     */

    @Override
    public void onString(String[] s, List<AppCompatEditText> editTextList, DialogCheckMnemonic dialogCheckMnemonic) {
        String[] mnemonics = walletBean.getMnemonic().split(" ");
        if (s.length != mnemonics.length) {
            ToastUtil.showToastLonger("您输入的助记词有误");
            return;
        }
        int count = mnemonics.length;
        for (int i = 0; i < count; i++) {
            if (!mnemonics[i].equals(s[i])) {
                ToastUtil.showToastLonger("您输入的第" + (i + 1) + "个助记词有误");
                editTextList.get(i).setTextColor(Color.RED);
                return;
            }
        }
        //更新
        walletBean.setMnemonic("");
        IONCWalletSDK.getInstance().updateWallet(walletBean);
        ToastUtil.showToastLonger("验证成功!");
        skip(MainActivity.class);
    }
}
