package org.ionchain.wallet.mvp.view.activity;

import android.graphics.Color;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.OnRefreshLoadmoreListener;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import org.ionc.wallet.adapter.CommonAdapter;
import org.ionc.wallet.bean.WalletBean;
import org.ionc.wallet.sdk.IONCWalletSDK;
import org.ionchain.wallet.App;
import org.ionchain.wallet.R;
import org.ionchain.wallet.adapter.walletmanager.ManagerWalletHelper;
import org.ionchain.wallet.mvp.view.activity.create.CreateWalletActivity;
import org.ionchain.wallet.mvp.view.activity.imports.SelectImportModeActivity;
import org.ionchain.wallet.mvp.view.activity.modify.ModifyAndExportWalletActivity;
import org.ionchain.wallet.mvp.view.activity.sdk.SDKCreateActivity;
import org.ionchain.wallet.mvp.view.activity.sdk.SDKSelectCreateModeWalletActivity;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;
import org.ionchain.wallet.utils.ToastUtil;
import org.ionchain.wallet.widget.dialog.callback.OnStringCallbcak;
import org.ionchain.wallet.widget.dialog.check.DialogCheckMnemonic;
import org.ionchain.wallet.widget.dialog.export.DialogTextMessage;
import org.ionchain.wallet.widget.dialog.mnemonic.DialogMnemonic;

import java.util.ArrayList;
import java.util.List;

import static org.ionchain.wallet.constant.ConstantParams.INTENT_PARAME_TAG;
import static org.ionchain.wallet.constant.ConstantParams.SERIALIZABLE_DATA;
import static org.ionchain.wallet.utils.AnimationUtils.setViewAlphaAnimation;

public class ManageWalletActivity extends AbsBaseActivity implements OnRefreshLoadmoreListener, ManagerWalletHelper.OnWalletManagerItemClickedListener, DialogMnemonic.OnSavedMnemonicCallback, DialogTextMessage.OnBtnClickedListener, OnStringCallbcak {


    private SmartRefreshLayout srl;
    private ListView listview;
    private Button importBtn;
    private Button createBtn;
    private CommonAdapter mAdapter;
    private List<WalletBean> mWalletBeans = new ArrayList<>();
    private DialogMnemonic dialogMnemonic;
    private WalletBean mCurrentWallet;

    private void findViews() {
        srl = (SmartRefreshLayout) findViewById(R.id.srl);
        listview = (ListView) findViewById(R.id.listview);
        importBtn = (Button) findViewById(R.id.importBtn);
        createBtn = (Button) findViewById(R.id.createBtn);
    }


    @SuppressWarnings("unchecked")
    @Override
    protected void initData() {
        mWalletBeans = IONCWalletSDK.getInstance().getAllWallet();
        mAdapter = new CommonAdapter(this, mWalletBeans, R.layout.item_wallet_manager_layout, new ManagerWalletHelper(this));
        listview.setAdapter(mAdapter);
    }

    @Override
    protected void setListener() {
        srl.setOnRefreshLoadmoreListener(this);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewAlphaAnimation(createBtn);
                if (App.SDK_Debug) {
                    skip(SDKCreateActivity.class, INTENT_PARAME_TAG, "1");//
                } else {
                    skip(CreateWalletActivity.class, INTENT_PARAME_TAG, "1");
                }
            }
        });

        importBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewAlphaAnimation(importBtn);
                if (App.SDK_Debug) {
                    skip(SDKSelectCreateModeWalletActivity.class,INTENT_PARAME_TAG,"");
                }else {
                    skip(SelectImportModeActivity.class,INTENT_PARAME_TAG,"");//
                }
            }
        });

    }

    @Override
    protected void initView() {
        findViews();
        mImmersionBar.titleView(R.id.toolbarlayout)
                .statusBarDarkFont(true)
                .execute();
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_wallet_manage;
    }


    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        srl.finishLoadmore();

    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        srl.finishRefresh();
        mWalletBeans.clear();
        mWalletBeans.addAll(IONCWalletSDK.getInstance().getAllWallet());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWalletBeans.clear();
        mWalletBeans.addAll(IONCWalletSDK.getInstance().getAllWallet());
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onItemClicked(int position) {
        mCurrentWallet = (WalletBean) mAdapter.getItem(position);
        if (!mCurrentWallet.getMnemonic().equals("")) {
            ToastUtil.showToastLonger(getResources().getString(R.string.toast_please_backup_wallet));
            String[] mnemonics = mCurrentWallet.getMnemonic().split(" ");
            dialogMnemonic = new DialogMnemonic(this, mnemonics, this);
            dialogMnemonic.show();
            return;
        }
        skip(ModifyAndExportWalletActivity.class, SERIALIZABLE_DATA, mCurrentWallet);
    }

    @Override
    public void onToKeepMnemonic() {
        new DialogTextMessage(this).setTitle(getResources().getString(R.string.attention))
                .setMessage(getResources().getString(R.string.key_store_to_save))
                .setBtnText(getResources().getString(R.string.i_know))
                .setHintMsg("")
                .setCancelByBackKey(true)
                .setTag("")
                .setCopyBtnClickedListener(this).show();
    }

    @Override
    public void onCancelKeepMnemonic(DialogMnemonic dialogMnemonic) {
        dialogMnemonic.dismiss();
    }

    @Override
    public void onDialogTextMessageBtnClicked(DialogTextMessage dialogTextMessage) {
        dialogMnemonic.dismiss();
        dialogTextMessage.dismiss();
        //保存准状态
        //去测试一下助记词
        new DialogCheckMnemonic(this, this).show();
    }

    @Override
    public void onString(String[] s, List<AppCompatEditText> editTextList, DialogCheckMnemonic dialogCheckMnemonic) {
        String[] mnemonics = mCurrentWallet.getMnemonic().split(" ");
        if (s.length != mnemonics.length) {
            ToastUtil.showToastLonger(getResources().getString(R.string.mnemonics_error));
            return;
        }
        int count = mnemonics.length;
        for (int i = 0; i < count; i++) {
            if (!mnemonics[i].equals(s[i])) {
                String index = String.valueOf((i + 1));
                ToastUtil.showToastLonger(getResources().getString(R.string.mnemonics_error_index, index));
                editTextList.get(i).setTextColor(Color.RED);
                return;
            }
        }
        //更新
        mCurrentWallet.setMnemonic("");
        IONCWalletSDK.getInstance().updateWallet(mCurrentWallet);
        ToastUtil.showToastLonger(getResources().getString(R.string.authentication_successful));
        dialogCheckMnemonic.dismiss();
//        skip(MainActivity.class);
    }
}
