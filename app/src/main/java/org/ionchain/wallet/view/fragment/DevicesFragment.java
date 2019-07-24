package org.ionchain.wallet.view.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.ionc.wallet.adapter.CommonAdapter;
import org.ionc.wallet.bean.WalletBeanNew;
import org.ionc.wallet.sdk.IONCWalletSDK;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionc.wallet.utils.ToastUtil;
import org.ionchain.wallet.App;
import org.ionchain.wallet.R;
import org.ionchain.wallet.adapter.device.DeviceViewHelper;
import org.ionchain.wallet.bean.DeviceBean;
import org.ionchain.wallet.callback.OnBindDeviceCallback;
import org.ionchain.wallet.callback.OnDeviceListCallback;
import org.ionchain.wallet.callback.OnUnbindDeviceButtonClickedListener;
import org.ionchain.wallet.presenter.device.DevicePresenter;
import org.ionchain.wallet.view.base.AbsBaseFragment;
import org.ionchain.wallet.qrcode.activity.CaptureActivity;
import org.ionchain.wallet.qrcode.activity.CodeUtils;
import org.ionchain.wallet.view.widget.DialogBindDevice;
import org.ionchain.wallet.view.widget.dialog.callback.OnDialogCheck12MnemonicCallbcak;
import org.ionchain.wallet.view.widget.dialog.mnemonic.DialogCheckMnemonic;
import org.ionchain.wallet.view.widget.dialog.export.DialogTextMessage;
import org.ionchain.wallet.view.widget.dialog.mnemonic.DialogMnemonic;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的设备
 */
public class DevicesFragment extends AbsBaseFragment implements OnUnbindDeviceButtonClickedListener, OnRefreshListener, OnDeviceListCallback, DialogMnemonic.OnSavedMnemonicCallback, DialogTextMessage.OnBtnClickedListener, OnDialogCheck12MnemonicCallbcak, OnBindDeviceCallback {
    private static final int QRCODE_BIND_DEVICE = 10;
    private ListView mListView;
    private CommonAdapter mAdapter;

    private List<DeviceBean.DataBean> mDataBeanList = new ArrayList<>();
    private SmartRefreshLayout mSwipeRefreshLayout;
    private DevicePresenter mDevicePresenter;
    private ImageView   addDevice;
    private ImageView   wallet_img_device;
    private ImageView   no_device_img;
    private TextView wallet_name_devices;

    private WalletBeanNew mCurrentWallet;
    private DialogMnemonic dialogMnemonic;

    private DialogBindDevice mDialogBindCardWithWallet;//绑定设备的弹窗
    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_devices;
    }

    @Override
    protected void initView(View view) {

        mSwipeRefreshLayout = view.findViewById(R.id.refresh_asset);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mSwipeRefreshLayout.setEnableRefresh(false);
        mListView = view.findViewById(R.id.devices_lv);
        addDevice = view.findViewById(R.id.add_devices);
        wallet_img_device = view.findViewById(R.id.wallet_img_device);
        no_device_img = view.findViewById(R.id.no_device_img);
        wallet_name_devices = view.findViewById(R.id.wallet_name_devices);

        mAdapter = new CommonAdapter(mActivity, mDataBeanList, R.layout.item_devices_layout, new DeviceViewHelper(this));
        mListView.setAdapter(mAdapter);


    }

    @Override
    protected void setListener() {
        super.setListener();
        /*
         * 绑定设备
         * */
        addDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mCurrentWallet.getMnemonic())) {
                    org.ionchain.wallet.utils.ToastUtil.showToastLonger(getResources().getString(R.string.toast_please_backup_wallet));
                    String[] mnemonics = mCurrentWallet.getMnemonic().split(" ");
                    dialogMnemonic = new DialogMnemonic(mActivity, mnemonics, DevicesFragment.this);
                    dialogMnemonic.show();
                    return;
                }
                Intent intent = new Intent(mActivity, CaptureActivity.class);
                startActivityForResult(intent, 10);

            }
        });

    }

    @Override
    protected void initData() {
        mDevicePresenter = new DevicePresenter();
        mCurrentWallet = IONCWalletSDK.getInstance().getMainWallet();
        wallet_img_device.setImageResource(App.sRandomHeader[mCurrentWallet.getMIconIndex()]);
        wallet_name_devices.setText(mCurrentWallet.getName());
//        getDeviceList();
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            mImmersionBar
                    .statusBarColor(R.color.main_color)
                    .execute();
        }
    }

    @Override
    protected void handleShow() {
        mCurrentWallet = IONCWalletSDK.getInstance().getMainWallet();
        wallet_img_device.setImageResource(App.sRandomHeader[mCurrentWallet.getMIconIndex()]);
        wallet_name_devices.setText(mCurrentWallet.getName());
    }

    @Override
    protected void handleHidden() {

    }

    private void getDeviceList() {
//        List<WalletBeanNew> list = IONCWalletSDK.getInstance().getAllWalletNew();
//        if (list == null || list.size() == 0) {
//            return;
//        }
//        int size = list.size();
//        StringBuilder builder = new StringBuilder();
//        for (int i = 0; i < size; i++) {
//            builder.append(list.get(i).getAddress() + ",");
//        }
//        String address = builder.toString();
//        if (StringUtils.isEmpty(address)) {
//            return;
//        }
//        mDevicePresenter.getAllWalletDevicesList(address, this);

    }


    @Override
    public void onUnbindButtonClick(String cksn, int position) {
        LoggerUtils.i(TAG, "onUnbindButtonClick: " + cksn);

    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        isRefreshing = true;
        getDeviceList();
    }

    @Override
    public void onDeviceListSuccess(@NotNull List<DeviceBean.DataBean> list) {
//        mSwipeRefreshLayout.finishRefresh();
        isRefreshing = false;
        mDataBeanList.clear();
        mDataBeanList.addAll(list);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDeviceListFailure(@NotNull String errorMessage) {
        ToastUtil.showToastLonger(errorMessage);
    }

    @Override
    public void onLoadStart() {
//        showProgress(getAppString(R.string.being_loaded));
    }

    @Override
    public void onLoadFinish() {
        mSwipeRefreshLayout.finishRefresh();
    }

    @Override
    public void onSaveMnemonicSure() {
        new DialogTextMessage(mActivity).setTitle(getResources().getString(R.string.attention))
                .setMessage(getResources().getString(R.string.key_store_to_save))
                .setBtnText(getResources().getString(R.string.i_know))
                .setHintMsg("")
                .setCancelByBackKey(true)
                .setTag("")
                .setCopyBtnClickedListener(this).show();
    }

    @Override
    public void onSaveMnemonicCancel(DialogMnemonic dialogMnemonic) {
        dialogMnemonic.dismiss();
    }

    @Override
    public void onDialogTextMessageBtnClicked(DialogTextMessage dialogTextMessage) {
        dialogMnemonic.dismiss();
        dialogTextMessage.dismiss();
        //保存准状态
        //去测试一下助记词
        new DialogCheckMnemonic(mActivity, this).show();
    }

    @Override
    public void onDialogCheckMnemonics12(String[] s, List<AppCompatEditText> editTextList, DialogCheckMnemonic dialogCheckMnemonic) {
        String[] mnemonics = mCurrentWallet.getMnemonic().split(" ");
        if (s.length != mnemonics.length) {
            org.ionchain.wallet.utils.ToastUtil.showToastLonger(getResources().getString(R.string.error_mnemonics));
            return;
        }
        int count = mnemonics.length;
        for (int i = 0; i < count; i++) {
            if (!mnemonics[i].equals(s[i])) {
                String index = String.valueOf((i + 1));
                org.ionchain.wallet.utils.ToastUtil.showToastLonger(getResources().getString(R.string.error_index_mnemonics, index));
                editTextList.get(i).setTextColor(Color.RED);
                return;
            }
        }
        //更新
        mCurrentWallet.setMnemonic("");
        IONCWalletSDK.getInstance().updateWallet(mCurrentWallet);
        org.ionchain.wallet.utils.ToastUtil.showToastLonger(getResources().getString(R.string.authentication_successful));
        dialogCheckMnemonic.dismiss();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        /**
         * 处理二维码扫描结果
         */
        if (requestCode == QRCODE_BIND_DEVICE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    ToastUtil.showLong(getResources().getString(R.string.error_parase_toast_qr_code));
                    return;
                }
                final String result = bundle.getString(CodeUtils.RESULT_STRING);
                if (TextUtils.isEmpty(result)) {
                    ToastUtil.showLong(getResources().getString(R.string.error_parase_toast_qr_code));
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {

                    mDialogBindCardWithWallet = new DialogBindDevice(mActivity);
                    mDialogBindCardWithWallet.setMessageText(result);
                    mDialogBindCardWithWallet.setLeftBtnClickedListener(v -> mDialogBindCardWithWallet.dismiss());
                    mDialogBindCardWithWallet.setRightBtnClickedListener(v -> {
                        mDialogBindCardWithWallet.dismiss();
                        String address = mCurrentWallet.getAddress();
                        mDevicePresenter.bindDeviceToWallet(address, result, DevicesFragment.this);
                    });
                    mDialogBindCardWithWallet.show();
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    ToastUtil.showLong(getResources().getString(R.string.error_parase_toast_qr_code));
                }
            }
        }
    }

    @Override
    public void onBindSuccess(DeviceBean.DataBean dataBean) {
        DeviceBean.DataBean bean = new DeviceBean.DataBean();
        bean.setCksn(dataBean.getCksn());
        bean.setCreated_at(dataBean.getCreated_at());
        bean.setId(dataBean.getId());
        bean.setImage_url(dataBean.getImage_url());
        bean.setName(dataBean.getName());
        bean.setSystem(dataBean.getSystem());
        mDataBeanList.add(bean);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBindFailure(String result) {
        LoggerUtils.e(result);
        ToastUtil.showShortToast("该业务暂未开放");
    }
}
