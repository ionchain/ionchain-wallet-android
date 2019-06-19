package org.ionchain.wallet.mvp.view.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.lzy.okgo.OkGo;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.ionc.wallet.adapter.CommonAdapter;
import org.ionc.wallet.bean.TxRecordBean;
import org.ionc.wallet.bean.WalletBeanNew;
import org.ionc.wallet.callback.OnBalanceCallback;
import org.ionc.wallet.callback.OnTxRecordFromNodeCallback;
import org.ionc.wallet.sdk.IONCWalletSDK;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionchain.wallet.App;
import org.ionchain.wallet.BuildConfig;
import org.ionchain.wallet.R;
import org.ionchain.wallet.adapter.morewallet.MoreWalletViewHelper;
import org.ionchain.wallet.bean.NodeBean;
import org.ionchain.wallet.bean.USDExRmb;
import org.ionchain.wallet.constant.ConstantParams;
import org.ionchain.wallet.mvp.callback.OnIONCNodeCallback;
import org.ionchain.wallet.mvp.model.ioncprice.callbcak.OnUSDExRateRMBCallback;
import org.ionchain.wallet.mvp.model.ioncprice.callbcak.OnUSDPriceCallback;
import org.ionchain.wallet.mvp.presenter.ioncrmb.PricePresenter;
import org.ionchain.wallet.mvp.view.activity.address.ShowAddressActivity;
import org.ionchain.wallet.mvp.view.activity.create.CreateWalletActivity;
import org.ionchain.wallet.mvp.view.activity.imports.SelectImportModeActivity;
import org.ionchain.wallet.mvp.view.activity.modify.ModifyAndExportWalletActivity;
import org.ionchain.wallet.mvp.view.activity.transaction.TxActivity;
import org.ionchain.wallet.mvp.view.activity.transaction.TxRecordActivity;
import org.ionchain.wallet.mvp.view.base.AbsBaseFragment;
import org.ionchain.wallet.mvp.view.base.AbsBaseViewPagerFragment;
import org.ionchain.wallet.mvp.view.fragment.txrecord.TxRecordAllFragment;
import org.ionchain.wallet.mvp.view.fragment.txrecord.TxRecordInFragment;
import org.ionchain.wallet.mvp.view.fragment.txrecord.TxRecordOutFragment;
import org.ionchain.wallet.mvp.view.fragment.txrecord.TxRecordPagerAdapter;
import org.ionchain.wallet.utils.SoftKeyboardUtil;
import org.ionchain.wallet.utils.ToastUtil;
import org.ionchain.wallet.widget.PopupWindowBuilder;
import org.ionchain.wallet.widget.dialog.callback.OnDialogCheck12MnemonicCallbcak;
import org.ionchain.wallet.widget.dialog.check.DialogCheckMnemonic;
import org.ionchain.wallet.widget.dialog.export.DialogTextMessage;
import org.ionchain.wallet.widget.dialog.mnemonic.DialogMnemonic;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

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
import static org.ionchain.wallet.constant.ConstantParams.TX_ACTIVITY_FOR_RESULT_CODE;
import static org.ionchain.wallet.constant.ConstantParams.TX_ACTIVITY_RESULT;
import static org.ionchain.wallet.utils.UrlUtils.getHostNode;


public class AssetFragment extends AbsBaseFragment implements
        PopupWindowBuilder.OnItemBuilder,
        OnRefreshListener,
        OnBalanceCallback,
        DialogMnemonic.OnSavedMnemonicCallback,
        DialogTextMessage.OnBtnClickedListener,
        OnDialogCheck12MnemonicCallbcak,
        OnUSDPriceCallback,
        OnUSDExRateRMBCallback,
        OnIONCNodeCallback, OnTxRecordFromNodeCallback {


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
    private TextView mWalletNameTx;

    /**
     * 更多钱包入口
     */
    private ImageView mMoreWallet;

    /**
     * 当前钱包的余额
     */
    private TextView mIoncBalanceTx;

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
    private TextView mRmbTx;
    private TextView mRmbIcon;

    /**
     * 交易记录,点击跳转到交易记录
     */
    private TextView mTxRecoder;
    /**
     * 提示用户备份钱包,在用户备份钱包的情况下
     */
    private TextView mPleaseBackupWallet;
    /**
     * 更多钱包
     */
    private PopupWindowBuilder mBuilder;
    /**
     * 钱包的图标
     */
    private ImageView mWalletLogo;

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
     * 转出
     */
    private LinearLayout mTxOutLl;
    /**
     * 转入
     */
    private LinearLayout mTxInLl;

    /**
     * 引导用户备份,助记词
     */
    private DialogMnemonic dialogMnemonic;

    /**
     * 币价信息
     */
    private PricePresenter mPricePresenter;
    private String mNodeIONC = getHostNode();

    private ClassicsHeader mRefreshHeader;

    /**
     * 交易记录的分类页面
     */
    private TabLayout tabLayout;
    /**
     * 交易记录的容器
     */
    private ViewPager viewPager;

    private List<AbsBaseViewPagerFragment> mFragmentListTxRecord = new ArrayList<>();

    private TxRecordInFragment mTxRecordInFragment;
    private TxRecordAllFragment mTxRecordAllFragment;
    private TxRecordOutFragment mTxRecordOutFragment;

    private String mOldAddress = "";
    private double mCNY = 0;
    private double mUS = 0;
    private double mKRW = 0;
    private double mIDR = 0;
    private boolean mCanSet = false;

    /**
     * 实例化资产页头部
     *
     * @param header
     */
    private void initListViewHeaderViews(View header) {
        mWalletNameTx = header.findViewById(R.id.wallet_name_tv);
        mMoreWallet = header.findViewById(R.id.wallet_list);
        mIoncBalanceTx = header.findViewById(R.id.ionc_balance_tx);
        mRmbTx = header.findViewById(R.id.rmb_balance_tx);
        mRmbIcon = header.findViewById(R.id.rmb_icon);
        mWalletLogo = header.findViewById(R.id.wallet_logo);
        mTxInLl = header.findViewById(R.id.tx_in_ll);
        mTxOutLl = header.findViewById(R.id.tx_out_ll);
        mTxRecoder = header.findViewById(R.id.tx_recoder_tv);
        mPleaseBackupWallet = header.findViewById(R.id.please_backup_wallet);
        mIoncBalanceTx.setText("0000");
        mRmbTx.setText("0000");
    }

    @Override
    protected void handleShow() {
        mImmersionBar.statusBarColor(R.color.blue_top).statusBarDarkFont(false).execute();
        LoggerUtils.i("handleShow");
        SoftKeyboardUtil.hideSoftKeyboard(Objects.requireNonNull(getActivity()));
        mCurrentWallet = IONCWalletSDK.getInstance().getMainWallet();
        if (mCurrentWallet == null) {
            ToastUtil.showLong(getResources().getString(R.string.wallet_null_toast));
//            跳转到钱包创建或者导入界面
            return;
        }
        setBackupTag();
        mWalletNameTx.setText(mCurrentWallet.getName());
        Integer id = mCurrentWallet.getMIconIndex();
        mWalletLogo.setImageResource(App.sRandomHeader[id]);
        balance(); //回到前台 handleShow
        mTxRecordAllFragment.onPullToDown(mCurrentWallet);
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

        mOldAddress = mCurrentWallet.getAddress();//初始化和赋值
        mTxRecordAllFragment.initRecordWalletBean(mCurrentWallet);
        setBackupTag();
        mWalletNameTx.setText(mCurrentWallet.getName());
        Integer id = mCurrentWallet.getMIconIndex();
        mWalletLogo.setImageResource(App.sRandomHeader[id]);
        balance(); //回到前台  onResume
        if (!mOldAddress.equals(mCurrentWallet.getAddress())) {
            mTxRecordAllFragment.onAddressChanged(mCurrentWallet);
        } else {
            mTxRecordAllFragment.onPullToDown(mCurrentWallet);
        }
        setBalance();
    }

    /**
     * 如果用户备份了助记词,则助记词会从本机数据库消失,
     * 则不显示提示用户备份的信息,否则显示
     */
    private void setBackupTag() {
        if (!TextUtils.isEmpty(mCurrentWallet.getMnemonic())) {
            mPleaseBackupWallet.setVisibility(View.VISIBLE);
        } else {
            mPleaseBackupWallet.setVisibility(View.GONE);
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
        //lv头部
        View header = view.findViewById(R.id.header_lv_home_page);

        initListViewHeaderViews(header);
        mRefresh = view.findViewById(R.id.refresh_asset);
        mRefresh.setHeaderHeight(40);
        mRefresh.setFooterHeight(40);
        mRefresh.setHeaderMaxDragRate(2);//最大显示下拉高度/Header标准高度
        mRefresh.setFooterMaxDragRate(2);//最大显示下拉高度/Footer标准高度
        mRefresh.setHeaderTriggerRate(1);//触发刷新距离 与 HeaderHeight 的比率1.0.4
        mRefresh.setFooterTriggerRate(1);//触发加载距离 与 FooterHeight 的比率1.0.4
        mRefresh.setEnableAutoLoadMore(false);
        mRefreshHeader = view.findViewById(R.id.refresh_header);
        mRefresh.setOnRefreshListener(this);
        mBuilder = new PopupWindowBuilder(mActivity, R.layout.item_popup_list_layout, this);
        tabLayout = view.findViewById(R.id.tx_record_tabs);
        viewPager = view.findViewById(R.id.tx_record_content);

    }

    @Override
    protected void setListener() {
        super.setListener();
        /*
         * 更多钱包
         * */
        mMoreWallet.setOnClickListener(v -> {
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
        });
        /*
         * 修改钱包
         * */
        mWalletLogo.setOnClickListener(v -> {
            if (pleaseBackupWallet()) return;
            /*
             *  将币价信息携带过去
             */
            cancelGetNode();// 修改钱包
            skip(ModifyAndExportWalletActivity.class, PARCELABLE_WALLET_BEAN, mCurrentWallet);
        });


        /*
         * 转账
         * */
        mTxOutLl.setOnClickListener(v -> {
            if (pleaseBackupWallet()) return;
            Intent intent = new Intent(getActivity(), TxActivity.class);
            intent.putExtra(INTENT_PARAM_CURRENT_WALLET, mCurrentWallet);
            intent.putExtra(CURRENT_ADDRESS, mCurrentWallet.getAddress());
            intent.putExtra(CURRENT_KSP, mCurrentWallet.getKeystore());
            cancelGetNode();//转账
            startActivityForResult(intent, TX_ACTIVITY_FOR_RESULT_CODE);
        });
        /*
         * 转入
         * */
        mTxInLl.setOnClickListener(v -> {
            if (pleaseBackupWallet()) return;
            Intent intent = new Intent(getActivity(), ShowAddressActivity.class);
            intent.putExtra(INTENT_PARAME_WALLET_ADDRESS, mCurrentWallet.getAddress());
            cancelGetNode();  //转入
            skip(intent);
        });
        /*
         * 交易记录
         * */
        mTxRecoder.setOnClickListener(v -> {
            if (pleaseBackupWallet()) return;
            LoggerUtils.i("address" + mCurrentWallet.getAddress());

            Intent intent = new Intent(getActivity(), TxRecordActivity.class);
            intent.putExtra(INTENT_PARAME_WALLET_ADDRESS, mCurrentWallet.getAddress());
            intent.putExtra(PARCELABLE_WALLET_BEAN, mCurrentWallet);
            skip(intent);
            cancelGetNode();// 交易记录
        });
        /*
         * 备份钱包
         * */
        mPleaseBackupWallet.setOnClickListener(v -> {
            String[] mnemonics = mCurrentWallet.getMnemonic().split(" ");
            dialogMnemonic = new DialogMnemonic(mActivity, mnemonics, AssetFragment.this);
            dialogMnemonic.show();
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        tabLayout.selectTab(tabLayout.getTabAt(0));
        //
        TxRecordBean t = null;
        if (data != null) {
            t = data.getParcelableExtra(TX_ACTIVITY_RESULT);
            if (TextUtils.isEmpty(t.getHash())) {
                mTxRecordAllFragment.onNewRecord(t);
                IONCWalletSDK.getInstance().saveTxRecordBean(t);
                return;
            }
            IONCWalletSDK.getInstance().ethTransaction(mNodeIONC
                    , t.getHash()
                    , t
                    , this);
        }
        LoggerUtils.i("onActivityResult   ----     " + t.toString());

    }


    /**
     * 钱包没有备份,则提示用户去备份钱包
     *
     * @return ..
     */
    private boolean pleaseBackupWallet() {
        if (BuildConfig.APP_DEBUG) {
            return false;
        }
        if (mPleaseBackupWallet.getVisibility() == View.VISIBLE) {
            ToastUtil.showToastLonger(getResources().getString(R.string.toast_please_backup_wallet));
            return true;
        }
        return false;
    }

    @Override
    protected void initData() {
        if (mCurrentWallet == null) {
            mCurrentWallet = IONCWalletSDK.getInstance().getMainWallet();
        }
        mTxRecordInFragment = new TxRecordInFragment();
        mTxRecordOutFragment = new TxRecordOutFragment();
        mTxRecordAllFragment = new TxRecordAllFragment();
        mFragmentListTxRecord.add(mTxRecordAllFragment);
        mFragmentListTxRecord.add(mTxRecordOutFragment);
        mFragmentListTxRecord.add(mTxRecordInFragment);


        viewPager.setAdapter(new TxRecordPagerAdapter(getChildFragmentManager(), mFragmentListTxRecord));
        tabLayout.setupWithViewPager(viewPager);
    }


    @Override
    public void initMoreItems(final PopupWindow instance, View contentView) {
        mMoreWalletsTemp = IONCWalletSDK.getInstance().getAllWalletNew();
        if (mMoreWalletsTemp != null && mMoreWalletsTemp.size() > 0) {
            mMoreWallets.clear();
//            Collections.reverse(mMoreWalletsTemp);
            mMoreWallets.addAll(mMoreWalletsTemp);
        }
        mMoreWalletListView = contentView.findViewById(R.id.data_list);
        LinearLayout importWallet = contentView.findViewById(R.id.scan_popu);
        LinearLayout createWallet = contentView.findViewById(R.id.new_ll);
        mAdapterMore = new CommonAdapter(getActivity(), mMoreWallets, R.layout.item_popup_list, new MoreWalletViewHelper());
        mMoreWalletListView.setAdapter(mAdapterMore);
        importWallet.setOnClickListener(v -> {
            instance.dismiss();
            skip(SelectImportModeActivity.class);//

        });
        createWallet.setOnClickListener(v -> {
            instance.dismiss();
            skip(CreateWalletActivity.class, INTENT_FROM_WHERE_TAG, INTENT_FROM_MAIN_ACTIVITY);
        });
        mMoreWalletListView.setOnItemClickListener((parent, view, position, id) -> {
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
            mWalletNameTx.setText(mCurrentWallet.getName());
            setBackupTag();
            Integer ids = mCurrentWallet.getMIconIndex();
            mWalletLogo.setImageResource(App.sRandomHeader[ids]);
            mIoncBalanceTx.setText(mCurrentWallet.getBalance());
            mRmbTx.setText(mCurrentWallet.getRmb()); //切换时读取余额
            instance.dismiss();
            LoggerUtils.i("钱包切换 mOldAddress = " + mOldAddress);
            LoggerUtils.i("钱包切换 mNewAddress = " + mCurrentWallet.getAddress());
            if (!mOldAddress.equals(mCurrentWallet.getAddress())) {
                balance();//切换
                changeWallet();
            }
            mOldAddress = mCurrentWallet.getAddress();
            cancelGetNode();//切换
        });

    }

    /**
     * 钱包切换时，将记录面板切换到地换全部记录面板
     * 同时并通知它
     */
    private void changeWallet() {
        tabLayout.selectTab(tabLayout.getTabAt(0));
        mTxRecordAllFragment.onAddressChanged(mCurrentWallet);
        mTxRecordOutFragment.onAddressChanged(mCurrentWallet);
        mTxRecordInFragment.onAddressChanged(mCurrentWallet);
    }


    /**
     * 余额获取失败，刷新获取主链节点
     * 1、获取余额
     * 2、获取交易记录
     *
     * @param refreshLayout
     */
    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        balance();
        mTxRecordAllFragment.onPullToDown(mCurrentWallet);
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
        mPleaseBackupWallet.setVisibility(View.GONE);
    }

    @Override
    public void onIONCNodeStart() {
        LoggerUtils.i("正在离子链节点......");
    }

    @Override
    public void onIONCNodeSuccess(List<NodeBean.DataBean> dataBean) {
        //取出主链节点
        mNodeIONC = dataBean.get(0).getIonc_node();
        LoggerUtils.i("node", mNodeIONC);
        //获取主链成功后,获取离子币余额
        balance();//onIONCNodeSuccess
    }

    /**
     * 从中心服务器 获取节点失败，使用内置的备用节点
     *
     * @param error 失败
     */
    @Override
    public void onIONCNodeError(String error) {
        LoggerUtils.e("node error", " 获取离子链节点失败......" + ("".equals(error) ? "数据解析失败" : error));
        //获取主链成功后,获取余额
        ToastUtil.showToastLonger(getAppString(R.string.error_net_node));
    }

    /**
     * 获取余额
     */
    private void balance() {
        IONCWalletSDK.getInstance().getIONCWalletBalance(mNodeIONC, mCurrentWallet.getAddress(), this);
    }


    @Override
    public void onIONCNodeFinish() {
        LoggerUtils.i("节点请求结束");
    }

    /**
     * @param balanceBigDecimal 原始形式
     * @param nodeUrlTag        节点
     */
    @Override
    public void onBalanceSuccess(BigDecimal balanceBigDecimal, String nodeUrlTag) {
        LoggerUtils.i("离子币余额获取成功:" + balanceBigDecimal.toPlainString());
        mIoncBalanceTx.setTextColor(Color.WHITE);
        mIONCBalance = balanceBigDecimal;
        //正常显示离子币余额
        mIoncBalanceTx.setText(balanceBigDecimal.toPlainString());

        mCurrentWallet.setBalance(balanceBigDecimal.toPlainString());  //缓存余额
        IONCWalletSDK.getInstance().updateWallet(mCurrentWallet);//更新余额到数据库

        //获取美元价格
        mPricePresenter = new PricePresenter();
        mPricePresenter.getUSDPrice(this);
    }

    /**
     * 获取余额失败，不再去获取离子币对应的美元价格
     * 显示余额为灰色
     *
     * @param error 失败信息 获取余额失败
     */
    @Override
    public void onBalanceFailure(String error) {
        mRefresh.finishRefresh();

        LoggerUtils.e("离子币余额获取失败:", error);

        String balance = mCurrentWallet.getBalance();
        String rmb = mCurrentWallet.getRmb();
        if (TextUtils.isEmpty(balance)) {
            balance = "0000";
        }
        if (TextUtils.isEmpty(rmb)) {
            rmb = "0000";
        }
        mRmbTx.setTextColor(Color.GRAY);
        mIoncBalanceTx.setTextColor(Color.GRAY);

        mIoncBalanceTx.setText(balance);
        setBalance();
    }

    @Override
    public void onUSDPriceStart() {
        LoggerUtils.i("正在美元价格......");
    }

    /**
     * 美元价格获取成功
     *
     * @param usdPrice IONC 的美元价格
     */
    @Override
    public void onUSDPriceSuccess(double usdPrice) {
        //获取人民币汇率
        mUSDPrice = usdPrice;
        //计算美元价格
        mTotalUSDPrice = mIONCBalance.multiply(BigDecimal.valueOf(usdPrice));
        IONCWalletSDK.getInstance().updateWallet(mCurrentWallet);

        /*
         * 请求汇率
         * 如果用户需要显示总的美元价格  就不要去请求汇率了
         * 设置美元价格
         *  mRmbTx.setText(mCurrentWallet.getBalance());
         *  mRmbTx.setTextColor(Color.WHITE);
         */
        mPricePresenter.getUSDExchangeRateRMB(this);
    }

    /**
     * 获取美元汇率失败 ，影响价格转换，但不影响IONC的数量显示
     *
     * @param error 请求usd价格失败
     */
    @Override
    public void onUSDPriceFailure(String error) {
        ToastUtil.showToastLonger(getAppString(R.string.error_net_getting_usd));
        LoggerUtils.e("美元价格获取失败:" + error);
        mRmbTx.setTextColor(Color.GRAY);
        if (TextUtils.isEmpty(mCurrentWallet.getRmb())) {
            mRmbTx.setText("0000"); //切换时读取余额
        } else {
            setBalance();
        }
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
    public void onUSDExRateRMBSuccess(USDExRmb.DataBean dataBean) {
        //转换为人民币
        mRmbTx.setTextColor(Color.WHITE);
        mCNY = dataBean.getCNY();
        mUS = dataBean.getUSD();
        mIDR = dataBean.getIDR();
        mKRW = dataBean.getKRW();
        LoggerUtils.i("balance = ");
        mCanSet = true;
        setBalance();
        IONCWalletSDK.getInstance().updateWallet(mCurrentWallet); //更新到数据库
        mRefreshHeader.setLastUpdateText(new SimpleDateFormat("上次更新:yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
    }

    @Override
    public void onUSDExRateRMBFailure(String error) {
        LoggerUtils.e(error);
        mRmbTx.setTextColor(Color.GRAY);
        if (TextUtils.isEmpty(mCurrentWallet.getRmb())) {
            mRmbTx.setText("0000"); //切换时读取余额
        } else {
            setBalance();
        }
        ToastUtil.showToastLonger(getAppString(R.string.error_net_getting_rate_rmb));
    }

    @Override
    public void onUSDExRateRMBFinish() {
        LoggerUtils.i("汇率请求结束");
        mCanSet = true;
        mRefresh.finishRefresh();
    }

    @Override
    public void onPause() {
        super.onPause();
        mOldAddress = mCurrentWallet.getAddress();
        cancelGetNode();//pause
    }

    @Override
    public void onDataParseError() {
        ToastUtil.showToastLonger(getAppString(R.string.error_data_parase));
    }

    private void cancelGetNode() {
        OkGo.cancelTag(OkGo.getInstance().getOkHttpClient(), NET_CANCEL_TAG_USD_RMB_RATE);
        OkGo.cancelTag(OkGo.getInstance().getOkHttpClient(), NET_CANCEL_TAG_USD_PRICE);
    }

    @Override
    public void OnTxRecordNodeSuccess(TxRecordBean txRecordBean) {
        mTxRecordAllFragment.onNewRecord(txRecordBean);
        IONCWalletSDK.getInstance().saveTxRecordBean(txRecordBean);
    }

    @Override
    public void onTxRecordNodeFailure(String error, TxRecordBean recordBean) {
        mTxRecordAllFragment.onNewRecord(recordBean);
        IONCWalletSDK.getInstance().saveTxRecordBean(recordBean);
    }

    public interface OnPullToRefreshCallback {
        /**
         * 下拉刷新
         */
        void onPullToDown(WalletBeanNew walletBeanNew);

        /**
         * 上拉加载更多
         */
        void onPullToUp(WalletBeanNew walletBeanNew);

        /**
         * @param currentWallet 地址发生改变的时候
         */
        void onAddressChanged(WalletBeanNew currentWallet);

        /**
         * @param txRecordBean 有新的交易记录的时候
         */
        void onNewRecord(TxRecordBean txRecordBean);

    }

    @SuppressLint("SetTextI18n")
    private void setBalance() {
        BigDecimal bigDecimal;
        String balance = "0000";
        switch (App.mCoinType) {
            case ConstantParams.COIN_TYPE_CNY:
                if (mTotalUSDPrice == null) {
                    balance = "0000";
                } else {
                    bigDecimal = mTotalUSDPrice.multiply(BigDecimal.valueOf(mCNY));
                    balance = bigDecimal.setScale(4, ROUND_HALF_UP).toPlainString();
                }
                mCurrentWallet.setIdr(balance);
                mRmbIcon.setText("  元");
                break;
            case ConstantParams.COIN_TYPE_IDR:
                if (mTotalUSDPrice == null) {
                    balance = "0000";
                } else {
                    bigDecimal = mTotalUSDPrice.multiply(BigDecimal.valueOf(mIDR));
                    balance = bigDecimal.setScale(4, ROUND_HALF_UP).toPlainString();

                }
                mCurrentWallet.setIdr(balance);
                mRmbIcon.setText("  Rp");
                break;
            case ConstantParams.COIN_TYPE_KRW:
                if (mTotalUSDPrice == null) {
                    balance = "0000";
                } else {
                    bigDecimal = mTotalUSDPrice.multiply(BigDecimal.valueOf(mKRW));
                    balance = bigDecimal.setScale(4, ROUND_HALF_UP).toPlainString();
                }
                mCurrentWallet.setKrw(balance);
                mRmbIcon.setText("  원");
                break;
            case ConstantParams.COIN_TYPE_US:
                if (mTotalUSDPrice == null) {
                    balance = "0000";
                } else {
                    bigDecimal = mTotalUSDPrice.multiply(BigDecimal.valueOf(mUS));
                    balance = bigDecimal.setScale(4, ROUND_HALF_UP).toPlainString();
                }
                mCurrentWallet.setUs(balance);
                mRmbIcon.setText("  $");
                break;
        }
        LoggerUtils.i("balance = COIN_TYPE_IDR ", balance);
        mRmbTx.setText(balance);
        IONCWalletSDK.getInstance().updateWallet(mCurrentWallet);
    }
}
