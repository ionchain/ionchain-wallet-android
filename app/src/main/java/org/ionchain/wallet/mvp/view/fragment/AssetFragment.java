package org.ionchain.wallet.mvp.view.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;

import com.lzy.okgo.OkGo;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.ionc.wallet.adapter.CommonAdapter;
import org.ionc.wallet.bean.WalletBeanNew;
import org.ionc.wallet.callback.OnBalanceCallback;
import org.ionc.wallet.sdk.IONCWalletSDK;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionchain.wallet.App;
import org.ionchain.wallet.BuildConfig;
import org.ionchain.wallet.R;
import org.ionchain.wallet.adapter.device.DeviceViewHelper;
import org.ionchain.wallet.adapter.morewallet.MoreWalletViewHelper;
import org.ionchain.wallet.bean.DeviceBean;
import org.ionchain.wallet.bean.NodeBean;
import org.ionchain.wallet.mvp.callback.OnBindDeviceCallback;
import org.ionchain.wallet.mvp.callback.OnDeviceListCallback;
import org.ionchain.wallet.mvp.callback.OnIONCNodeCallback;
import org.ionchain.wallet.mvp.callback.OnUnbindDeviceButtonClickedListener;
import org.ionchain.wallet.mvp.callback.OnUnbindDeviceCallback;
import org.ionchain.wallet.mvp.model.ioncprice.callbcak.OnUSDExRateRMBCallback;
import org.ionchain.wallet.mvp.model.ioncprice.callbcak.OnUSDPriceCallback;
import org.ionchain.wallet.mvp.presenter.device.DevicePresenter;
import org.ionchain.wallet.mvp.presenter.ioncrmb.PricePresenter;
import org.ionchain.wallet.mvp.presenter.node.IONCNodePresenter;
import org.ionchain.wallet.mvp.view.activity.address.ShowAddressActivity;
import org.ionchain.wallet.mvp.view.activity.create.CreateWalletActivity;
import org.ionchain.wallet.mvp.view.activity.imports.SelectImportModeActivity;
import org.ionchain.wallet.mvp.view.activity.modify.ModifyAndExportWalletActivity;
import org.ionchain.wallet.mvp.view.activity.transaction.TxActivity;
import org.ionchain.wallet.mvp.view.activity.transaction.TxRecordActivity;
import org.ionchain.wallet.mvp.view.base.AbsBaseFragment;
import org.ionchain.wallet.qrcode.activity.CaptureActivity;
import org.ionchain.wallet.qrcode.activity.CodeUtils;
import org.ionchain.wallet.utils.SoftKeyboardUtil;
import org.ionchain.wallet.utils.ToastUtil;
import org.ionchain.wallet.widget.DialogBindDevice;
import org.ionchain.wallet.widget.PopupWindowBuilder;
import org.ionchain.wallet.widget.dialog.callback.OnDialogCheck12MnemonicCallbcak;
import org.ionchain.wallet.widget.dialog.check.DialogCheckMnemonic;
import org.ionchain.wallet.widget.dialog.export.DialogTextMessage;
import org.ionchain.wallet.widget.dialog.mnemonic.DialogMnemonic;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static java.math.BigDecimal.ROUND_HALF_UP;
import static org.ionchain.wallet.constant.ConstantActivitySkipTag.INTENT_FROM_MAIN_ACTIVITY;
import static org.ionchain.wallet.constant.ConstantActivitySkipTag.INTENT_FROM_WHERE_TAG;
import static org.ionchain.wallet.constant.ConstantIntentParam.INTENT_PARAM_CURRENT_WALLET;
import static org.ionchain.wallet.constant.ConstantNetCancelTag.NET_CANCEL_TAG_NODE;
import static org.ionchain.wallet.constant.ConstantNetCancelTag.NET_CANCEL_TAG_USD_PRICE;
import static org.ionchain.wallet.constant.ConstantNetCancelTag.NET_CANCEL_TAG_USD_RMB_RATE;
import static org.ionchain.wallet.constant.ConstantParams.CURRENT_ADDRESS;
import static org.ionchain.wallet.constant.ConstantParams.CURRENT_KSP;
import static org.ionchain.wallet.constant.ConstantParams.INTENT_PARAME_WALLET_ADDRESS;
import static org.ionchain.wallet.constant.ConstantParams.PARCELABLE_WALLET_BEAN;
import static org.ionchain.wallet.constant.ConstantParams.QRCODE_BIND_DEVICE;


public class AssetFragment extends AbsBaseFragment implements
        PopupWindowBuilder.OnItemBuilder,
        OnRefreshListener,
        OnBindDeviceCallback,
        OnDeviceListCallback,
        OnUnbindDeviceButtonClickedListener,
        OnUnbindDeviceCallback,
        OnBalanceCallback,
        DialogMnemonic.OnSavedMnemonicCallback,
        DialogTextMessage.OnBtnClickedListener,
        OnDialogCheck12MnemonicCallbcak,
        OnUSDPriceCallback,
        OnUSDExRateRMBCallback, OnIONCNodeCallback {


    /**
     * 刷新容器
     */
    private SmartRefreshLayout mRefresh;
    /**
     * 当前展示的钱包
     */
    private WalletBeanNew mCurrentWallet;
    /**
     * 钱包名字
     */
    private TextView walletNameTx;
    /**
     * 当前主链IP
     */
    private TextView node;
    /**
     * 更多钱包入口
     */
    private ImageView moreWallet;

    /**
     * 当前钱包的余额
     */
    private TextView ioncBalanceTx;

    /**
     * 离子比余额
     */
    private BigDecimal mIONCBalance;
    /**
     * 离子比美元总价格
     */
    private double mUSDPrice;
    /**
     * 离子比美元总价格
     */
    private BigDecimal mTotalUSDPrice;
    /**
     * 人民币余额
     */
    private TextView rmb_balance_tx;
    /**
     * 当前钱包的余额--以太坊
     */
    private TextView walletBalanceTxETH;
    /**
     * 交易记录,点击跳转到交易记录
     */
    private TextView tx_recoder;
    /**
     * 提示用户备份钱包,在用户备份钱包的情况下
     */
    private TextView please_backup_wallet;
    /**
     * 更多钱包
     */
    private PopupWindowBuilder mBuilder;
    /**
     * 添加设设备
     */
    private ImageView addDeviceScan;
    /**
     * 钱包的图标
     */
    private ImageView wallet_logo;

    /**
     * 更多钱包适配器
     */
    private CommonAdapter mAdapterMore;
    /**
     * 更多钱包
     */
    private List<WalletBeanNew> mMoreWallets = new ArrayList<>();
    /**
     * 更过钱包缓存
     */
    private List<WalletBeanNew> mMoreWalletsTemp = new ArrayList<>();
    /**
     * 更多钱包列表
     */
    private ListView mMoreWalletListView;

    /**
     * 设备适配器
     */
    private CommonAdapter mAdapterDeviceLv;

    /**
     * 设备数据
     */
    private List<DeviceBean.DataBean> mDataBeans = new ArrayList<>();

    /**
     * 设备代理
     */
    private DevicePresenter mDevicePresenter;


    /**
     * 转出
     */
    private LinearLayout tx_out_ll;
    /**
     * 转入
     */
    private LinearLayout tx_in_ll;

    /**
     * 绑定设备的弹窗
     */
    private DialogBindDevice mDialogBindCardWithWallet;
    /**
     * 解绑位置
     */
    private int mUnbindPos = 0;
    /**
     * 引导用户备份,助记词
     */
    private DialogMnemonic dialogMnemonic;

    /**
     * 币价信息
     */
    private PricePresenter mPricePresenter;
    private String mNodeIONC;


    /**
     * @param rootView 实例化资产页头部
     */
    private void initListViewHeaderViews(View rootView) {
        walletNameTx = rootView.findViewById(R.id.wallet_name_tv);
        node = rootView.findViewById(R.id.node);
        moreWallet = rootView.findViewById(R.id.wallet_list);
        ioncBalanceTx = rootView.findViewById(R.id.ionc_balance_tx);
        rmb_balance_tx = rootView.findViewById(R.id.rmb_balance_tx);
        walletBalanceTxETH = rootView.findViewById(R.id.wallet_balance_tx_eth);
        addDeviceScan = rootView.findViewById(R.id.add_device);
        wallet_logo = rootView.findViewById(R.id.wallet_logo);
        tx_in_ll = rootView.findViewById(R.id.tx_in_ll);
        tx_out_ll = rootView.findViewById(R.id.tx_out_ll);
        tx_recoder = rootView.findViewById(R.id.tx_recoder_tv);
        please_backup_wallet = rootView.findViewById(R.id.please_backup_wallet);
        if (!BuildConfig.DEBUG) {
            node.setVisibility(View.GONE);
        }
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            mImmersionBar
                    .statusBarColor(R.color.blue_top)
                    .statusBarDarkFont(false).execute();

        }
    }

    @Override
    protected void handleShow() {
        LoggerUtils.i("handleShow");
        SoftKeyboardUtil.hideSoftKeyboard(Objects.requireNonNull(getActivity()));
        mCurrentWallet = IONCWalletSDK.getInstance().getMainWallet();
        if (mCurrentWallet == null) {
            ToastUtil.showLong(getResources().getString(R.string.wallet_null_toast));
//            跳转到钱包创建或者导入界面
            return;
        }
        setBackupTag();
        walletNameTx.setText(mCurrentWallet.getName());
        Integer id = mCurrentWallet.getMIconIndex();
        wallet_logo.setImageResource(App.sRandomHeader[id]);
        getNetData(); //回到前台 handleShow
    }

    @Override
    protected void handleHidden() {
        OkGo.cancelTag(OkGo.getInstance().getOkHttpClient(), NET_CANCEL_TAG_USD_PRICE);
        OkGo.cancelTag(OkGo.getInstance().getOkHttpClient(), NET_CANCEL_TAG_NODE);
        OkGo.cancelTag(OkGo.getInstance().getOkHttpClient(), NET_CANCEL_TAG_USD_RMB_RATE);
    }

    @Override
    public void onResume() {
        super.onResume();
        LoggerUtils.i("onResume");
        SoftKeyboardUtil.hideSoftKeyboard(Objects.requireNonNull(getActivity()));
        mCurrentWallet = IONCWalletSDK.getInstance().getMainWallet();
        if (mCurrentWallet == null) {
            ToastUtil.showLong(getResources().getString(R.string.wallet_null_toast));
//            跳转到钱包创建或者导入界面
            return;
        }
        setBackupTag();
        walletNameTx.setText(mCurrentWallet.getName());
        Integer id = mCurrentWallet.getMIconIndex();
        wallet_logo.setImageResource(App.sRandomHeader[id]);
        getNetData(); //回到前台  onResume
    }

    /**
     * 获取网络数据
     */
    private void getNetData() {
//        getDeviceList();
        new IONCNodePresenter().getNodes(this);
    }

    /**
     * 如果用户备份了助记词,则助记词会从本机数据库消失,
     * 则不显示提示用户备份的信息,否则显示
     */
    private void setBackupTag() {
        if (!TextUtils.isEmpty(mCurrentWallet.getMnemonic())) {
            please_backup_wallet.setVisibility(View.VISIBLE);
        } else {
            please_backup_wallet.setVisibility(View.GONE);
        }
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_asset;
    }

    /**
     * @param view 实例化的ivew
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void initView(View view) {

        //设备列表
        ListView mDevicesLv = view.findViewById(R.id.devices_items);
        //lv头部
        View lvHeader = LayoutInflater.from(mActivity).inflate(R.layout.header_lv_home_page, null);
        initListViewHeaderViews(lvHeader);

        mDevicesLv.addHeaderView(lvHeader);
        mRefresh = view.findViewById(R.id.refresh_asset);
        mRefresh.setOnRefreshListener(this);
        mBuilder = new PopupWindowBuilder(mActivity, R.layout.item_popup_list_layout, this);

        mAdapterDeviceLv = new CommonAdapter(mActivity, mDataBeans, R.layout.item_devices_layout, new DeviceViewHelper(this));
        mDevicesLv.setAdapter(mAdapterDeviceLv);

    }

    @Override
    protected void setListener() {
        super.setListener();
        /*
         * 更多钱包
         * */
        moreWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayMetrics metrics = new DisplayMetrics();
                Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
                int screenHeight = metrics.heightPixels;
                int screenWidth = metrics.widthPixels;
                mBuilder.setAnimationStyle(R.style.push_left_in_out)
                        .setWidth((int) (screenWidth * 0.6))
                        .setHeight(screenHeight)
                        .setLocation(mRefresh, Gravity.END | Gravity.TOP, 0, 0)
                        .build()
                        .show();
                mMoreWalletListView.smoothScrollToPosition(0);

                mMoreWalletsTemp = IONCWalletSDK.getInstance().getAllWalletNew();
                if (mMoreWalletsTemp != null && mMoreWalletsTemp.size() > 0) {
                    mMoreWallets.clear();
                    mMoreWallets.addAll(mMoreWalletsTemp);
                    mAdapterMore.notifyDataSetChanged();
                }
            }

        });
        /*
         * 修改钱包
         * */
        wallet_logo.setOnClickListener(v -> {
            if (pleaseBackupWallet()) return;
            /*
             *  将币价信息携带过去
             */
            skip(ModifyAndExportWalletActivity.class, PARCELABLE_WALLET_BEAN, mCurrentWallet);
        });
        /*
         * 绑定设备
         * */
        addDeviceScan.setOnClickListener(v -> {
            if (pleaseBackupWallet()) return;
            Intent intent = new Intent(getActivity(), CaptureActivity.class);
            startActivityForResult(intent, QRCODE_BIND_DEVICE);

        });

        /*
         * 转账
         * */
        tx_out_ll.setOnClickListener(v -> {
            if (pleaseBackupWallet()) return;
            Intent intent = new Intent(getActivity(), TxActivity.class);
            intent.putExtra(INTENT_PARAM_CURRENT_WALLET, mCurrentWallet);
            intent.putExtra(CURRENT_ADDRESS, mCurrentWallet.getAddress());
            intent.putExtra(CURRENT_KSP, mCurrentWallet.getKeystore());
            skip(intent);
        });
        /*
         * 转入
         * */
        tx_in_ll.setOnClickListener(v -> {
            if (pleaseBackupWallet()) return;
            Intent intent = new Intent(getActivity(), ShowAddressActivity.class);
            intent.putExtra(INTENT_PARAME_WALLET_ADDRESS, mCurrentWallet.getAddress());
            skip(intent);
        });
        /*
         * 交易记录
         * */
        tx_recoder.setOnClickListener(v -> {
            if (pleaseBackupWallet()) return;
            Intent intent = new Intent(getActivity(), TxRecordActivity.class);
            intent.putExtra("address", mCurrentWallet.getAddress());
            skip(intent);
        });
        /*
         * 备份钱包
         * */
        please_backup_wallet.setOnClickListener(v -> {
            String[] mnemonics = mCurrentWallet.getMnemonic().split(" ");
            dialogMnemonic = new DialogMnemonic(mActivity, mnemonics, AssetFragment.this);
            dialogMnemonic.show();
        });
    }

    /**
     * 钱包没有备份,则提示用户去备份钱包
     *
     * @return ..
     */
    private boolean pleaseBackupWallet() {
        if (BuildConfig.DEBUG) {
            return false;
        }
        if (please_backup_wallet.getVisibility() == View.VISIBLE) {
            ToastUtil.showToastLonger(getResources().getString(R.string.toast_please_backup_wallet));
            return true;
        }
        return false;
    }


    @Override
    protected void initData() {
        mDevicePresenter = new DevicePresenter();
    }

    private void getDeviceList() {
//        mDevicePresenter.getCurrentWalletDevicesList(mCurrentWallet, this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == QRCODE_BIND_DEVICE) {
            final String result = data.getStringExtra("result");
            LoggerUtils.i("result", result);

        }
        /**
         * 处理二维码扫描结果
         */
        if (requestCode == QRCODE_BIND_DEVICE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    final String result = bundle.getString(CodeUtils.RESULT_STRING);
                    mDialogBindCardWithWallet = new DialogBindDevice(mActivity);
                    mDialogBindCardWithWallet.setMessageText(result);
                    mDialogBindCardWithWallet.setLeftBtnClickedListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialogBindCardWithWallet.dismiss();
                        }
                    });
                    mDialogBindCardWithWallet.setRightBtnClickedListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialogBindCardWithWallet.dismiss();
                            String address = mCurrentWallet.getAddress();
                            mDevicePresenter.bindDeviceToWallet(address, result, AssetFragment.this);
                        }
                    });
                    mDialogBindCardWithWallet.show();
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    ToastUtil.showLong(getResources().getString(R.string.error_parase_toast_qr_code));
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initItems(final PopupWindow instance, View contentView) {
        mMoreWalletsTemp = IONCWalletSDK.getInstance().getAllWalletNew();
        if (mMoreWalletsTemp != null && mMoreWalletsTemp.size() > 0) {
            mMoreWallets.clear();
//            Collections.reverse(mMoreWalletsTemp);
            mMoreWallets.addAll(mMoreWalletsTemp);
        }
        mMoreWalletListView = contentView.findViewById(R.id.data_list);
        LinearLayout scan_popu = contentView.findViewById(R.id.scan_popu);
        LinearLayout new_ll = contentView.findViewById(R.id.new_ll);
        mAdapterMore = new CommonAdapter(getActivity(), mMoreWallets, R.layout.item_popup_list, new MoreWalletViewHelper());
        mMoreWalletListView.setAdapter(mAdapterMore);
        scan_popu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instance.dismiss();
                skip(SelectImportModeActivity.class);//

            }
        });
        new_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instance.dismiss();
                skip(CreateWalletActivity.class, INTENT_FROM_WHERE_TAG, INTENT_FROM_MAIN_ACTIVITY);
            }
        });
        mMoreWalletListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LoggerUtils.i(TAG, "onItemClick: ");

                for (int i = 0; i < mMoreWallets.size(); i++) {
                    if (i != position) {
                        mMoreWallets.get(i).setIsMainWallet(false);
                    } else {
                        mMoreWallets.get(i).setIsMainWallet(true);
                    }
                    IONCWalletSDK.getInstance().saveWallet(mMoreWallets.get(i));
                }
                mCurrentWallet = mMoreWallets.get(position);
                walletNameTx.setText(mCurrentWallet.getName());
                setBackupTag();
                Integer ids = mCurrentWallet.getMIconIndex();
                wallet_logo.setImageResource(App.sRandomHeader[ids]);
                mDataBeans.clear();
                mAdapterDeviceLv.notifyDataSetChanged();
                ioncBalanceTx.setText(mCurrentWallet.getBalance());
                rmb_balance_tx.setText(mCurrentWallet.getRmb()); //切换时读取余额
                instance.dismiss();
                getNetData(); //切换钱包
            }
        });

    }


    @Override
    public void onLoadStart() {
        LoggerUtils.i(TAG, "onLoadStart: ");
    }

    @Override
    public void onLoadFinish() {
        LoggerUtils.i(TAG, "onLoadFinish: ");
        mRefresh.finishRefresh();
    }


    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        getNetData();  //刷新
//        getDeviceList();
    }


    /**
     * @param dataBean 绑定设备成功
     */
    @Override
    public void onBindSuccess(DeviceBean.DataBean dataBean) {
        DeviceBean.DataBean bean = new DeviceBean.DataBean();
        bean.setCksn(dataBean.getCksn());
        bean.setCreated_at(dataBean.getCreated_at());
        bean.setId(dataBean.getId());
        bean.setImage_url(dataBean.getImage_url());
        bean.setName(dataBean.getName());
        bean.setSystem(dataBean.getSystem());
        mDataBeans.add(bean);
        mAdapterDeviceLv.notifyDataSetChanged();
    }

    /**
     * @param result 绑定失败
     */
    @Override
    public void onBindFailure(String result) {
        ToastUtil.showLong(result);
    }

    /**
     * @param list 设备列表
     */
    @Override
    public void onDeviceListSuccess(List<DeviceBean.DataBean> list) {
        LoggerUtils.i(TAG, "onDeviceListSuccess: " + list.toString());
        mDataBeans.clear();
        mDataBeans.addAll(list);
        mAdapterDeviceLv.notifyDataSetChanged();
        mRefresh.finishRefresh();
    }

    /**
     * @param errorMessage 失败信息 获取设备列表失败
     */
    @Override
    public void onDeviceListFailure(String errorMessage) {
        LoggerUtils.i(TAG, "onDeviceListFailure: " + errorMessage);
        mRefresh.finishRefresh();
    }

    /**
     * 解绑设备
     *
     * @param cksn
     * @param position
     */
    @Override
    public void onUnbindButtonClick(final String cksn, int position) {
        LoggerUtils.i(TAG, "onUnbindButtonClick: " + cksn);
        mUnbindPos = position;
        mDialogBindCardWithWallet = new DialogBindDevice(mActivity);
        mDialogBindCardWithWallet.setMessageText(cksn);
        mDialogBindCardWithWallet.setTitleText(getResources().getString(R.string.sure_to_bind_device));
        mDialogBindCardWithWallet.setLeftBtnClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogBindCardWithWallet.dismiss();
            }
        });
        mDialogBindCardWithWallet.setRightBtnClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogBindCardWithWallet.dismiss();
                mDevicePresenter.unbindDeviceToWallet(mCurrentWallet.getAddress(), cksn, AssetFragment.this);
            }
        });
        mDialogBindCardWithWallet.show();
    }

    /**
     * 解绑定成功
     */
    @Override
    public void onUnbindSuccess(DeviceBean.DataBean dataBean) {
        mDataBeans.remove(mUnbindPos);
        mAdapterDeviceLv.notifyDataSetChanged();
    }

    /**
     * @param result 解绑定失败
     */
    @Override
    public void onUnbindFailure(String result) {
        LoggerUtils.i(TAG, "onUnbindFailure: " + result);
    }

    /**
     * @param balanceBigDecimal 原始形式
     * @param nodeUrlTag
     */
    @Override
    public void onBalanceSuccess(BigDecimal balanceBigDecimal, String nodeUrlTag) {

        mIONCBalance = balanceBigDecimal;
        ioncBalanceTx.setText(balanceBigDecimal.toPlainString());
        LoggerUtils.i("离子币余额获取成功:" + balanceBigDecimal.toPlainString());
        mCurrentWallet.setBalance(balanceBigDecimal.toPlainString());  //缓存余额
        mRefresh.finishRefresh(); //
        //获取美元价格
        mPricePresenter = new PricePresenter();
        mPricePresenter.getUSDPrice(this);
    }

    /**
     * @param error 失败信息 获取余额失败
     */
    @Override
    public void onBalanceFailure(String error) {
        LoggerUtils.e("离子币余额获取失败:", error);
        ToastUtil.showToastLonger(getAppString(R.string.error_net_get_balance));
        mRefresh.finishRefresh();
        String balance = mCurrentWallet.getBalance();
        String rmb = mCurrentWallet.getRmb();
        if (TextUtils.isEmpty(balance)) {
            balance = "****";
        }
        if (TextUtils.isEmpty(rmb)) {
            rmb = "****";
        }
        ioncBalanceTx.setText(balance);
        rmb_balance_tx.setText(rmb);   //缓存
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBuilder.release();
    }

    @Override
    public void onSaveMnemonicSure() {
        new DialogTextMessage(Objects.requireNonNull(getActivity())).setTitle(getResources().getString(R.string.attention))
                .setMessage(getResources().getString(R.string.key_store_to_save))
                .setBtnText(getResources().getString(R.string.i_know))
                .setHintMsg("")
                .setCancelByBackKey(true)
                .setTag("")
                .setCopyBtnClickedListener(AssetFragment.this).show();
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
        new DialogCheckMnemonic(requireActivity(), this).show();
    }

    /**
     * @param s                   回传数据 ,测试助记词是否正确
     * @param editTextList        助记词编辑框列表
     * @param dialogCheckMnemonic 助记词检查对话框
     */
    @Override
    public void onDialogCheckMnemonics12(String[] s, List<AppCompatEditText> editTextList, DialogCheckMnemonic dialogCheckMnemonic) {
        String[] mnemonics = mCurrentWallet.getMnemonic().split(" ");
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
        mCurrentWallet.setMnemonic("");
        IONCWalletSDK.getInstance().updateWallet(mCurrentWallet);
        ToastUtil.showToastLonger(getResources().getString(R.string.authentication_successful));
        dialogCheckMnemonic.dismiss();
//        skip(MainActivity.class);
        //隐藏备份提示布局
        please_backup_wallet.setVisibility(View.GONE);
    }

    @Override
    public void onUSDPriceStart() {
        LoggerUtils.i("正在美元价格......");
    }

    @Override
    public void onUSDPriceSuccess(double usdPrice) {
        //获取人民币汇率
        mUSDPrice = usdPrice;
        LoggerUtils.i("美元价格：", String.valueOf(mUnbindPos));
        mTotalUSDPrice = mIONCBalance.multiply(BigDecimal.valueOf(usdPrice));
        IONCWalletSDK.getInstance().updateWallet(mCurrentWallet);
        mPricePresenter.getUSDExchangeRateRMB(this);
    }

    @Override
    public void onUSDPriceFailure(String error) {
        LoggerUtils.e(error);
        ToastUtil.showToastLonger(getAppString(R.string.error_net_getting_usd));
        LoggerUtils.e("美元价格获取失败:");
        ioncBalanceTx.setText(mCurrentWallet.getBalance());
        rmb_balance_tx.setText(mCurrentWallet.getRmb()); //切换时读取余额
    }

    @Override
    public void onUSDPriceFinish() {
        LoggerUtils.i("美元价格请求结束");
        mRefresh.finishRefresh();
    }

    @Override
    public void onUSDExRateRMBStart() {
        LoggerUtils.i("正在获取人民币队美元汇率......");
    }

    @Override
    public void onUSDExRateRMBSuccess(double usdPrice) {
        //转换为人民币
        BigDecimal rmb = mTotalUSDPrice.multiply(BigDecimal.valueOf(usdPrice));
        LoggerUtils.i("balance = ", rmb.setScale(4, ROUND_HALF_UP).toPlainString());
        mCurrentWallet.setRmb(rmb.setScale(4, ROUND_HALF_UP).toPlainString());
        rmb_balance_tx.setText(mCurrentWallet.getRmb()); //网络数据
        IONCWalletSDK.getInstance().updateWallet(mCurrentWallet);
    }

    @Override
    public void onUSDExRateRMBFailure(String error) {
        ioncBalanceTx.setText(mCurrentWallet.getBalance());
        rmb_balance_tx.setText(mCurrentWallet.getRmb()); //切换时读取余额
        LoggerUtils.e(error);
        ToastUtil.showToastLonger(getAppString(R.string.error_net_getting_rate_rmb));
    }

    @Override
    public void onUSDExRateRMBFinish() {
        LoggerUtils.i("汇率请求结束");
        mRefresh.finishRefresh();
    }

    @Override
    public void onIONCNodeSuccess(List<NodeBean.DataBean> dataBean) {
        //取出主链节点
        mNodeIONC = dataBean.get(0).getIonc_node();
        LoggerUtils.i("node", mNodeIONC);
        node.setText("当前节点：" + mNodeIONC);
        //获取主链成功后,获取余额
        IONCWalletSDK.getInstance().getIONCWalletBalance(mNodeIONC, mCurrentWallet.getAddress(), this);

    }

    @Override
    public void onIONCNodeError(String error) {
        LoggerUtils.e("获取离子链节点失败......" + ("".equals(error) ? "数据解析失败" : error));
        ioncBalanceTx.setText(mCurrentWallet.getBalance());
        rmb_balance_tx.setText(mCurrentWallet.getRmb()); //切换时读取余额
        ToastUtil.showToastLonger(getAppString(R.string.error_net_node));
    }

    @Override
    public void onIONCNodeStart() {
        LoggerUtils.i("正在离子链节点......");
    }

    @Override
    public void onIONCNodeFinish() {
        LoggerUtils.i("节点请求结束");
        mRefresh.finishRefresh();
    }

    @Override
    public void onPause() {
        super.onPause();
        OkGo.cancelTag(OkGo.getInstance().getOkHttpClient(), NET_CANCEL_TAG_USD_PRICE);
        OkGo.cancelTag(OkGo.getInstance().getOkHttpClient(), NET_CANCEL_TAG_NODE);
        OkGo.cancelTag(OkGo.getInstance().getOkHttpClient(), NET_CANCEL_TAG_USD_RMB_RATE);
    }

}
