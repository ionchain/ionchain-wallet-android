package org.ionchain.wallet.ui.fragment;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fast.lib.utils.ToastUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.smart.holder.CommonAdapter;

import org.ionchain.wallet.R;
import org.ionchain.wallet.adapterhelper.device.DeviceViewHelper;
import org.ionchain.wallet.adapterhelper.moewwallet.MoreWalletViewHelper;
import org.ionchain.wallet.bean.DeviceBean;
import org.ionchain.wallet.bean.DeviceListBean;
import org.ionchain.wallet.bean.WalletBean;
import org.ionchain.wallet.callback.DataCallback;
import org.ionchain.wallet.callback.OnBalanceRefreshCallback;
import org.ionchain.wallet.comm.api.ApiWalletManager;
import org.ionchain.wallet.comm.constants.Comm;
import org.ionchain.wallet.dao.WalletDaoTools;
import org.ionchain.wallet.mvp.presenter.Presenter;
import org.ionchain.wallet.ui.base.AbsBaseFragment;
import org.ionchain.wallet.ui.comm.ScanActivity;
import org.ionchain.wallet.ui.main.ShowAddressActivity;
import org.ionchain.wallet.ui.wallet.CreateWalletActivity;
import org.ionchain.wallet.ui.wallet.ImprotWalletActivity;
import org.ionchain.wallet.ui.wallet.ModifyWalletActivity;
import org.ionchain.wallet.utils.NetUtils;
import org.ionchain.wallet.utils.StringUtils;
import org.ionchain.wallet.widget.DialogBindCardWithWallet;
import org.ionchain.wallet.widget.PopupWindowBuilder;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;
import static org.ionchain.wallet.constant.ConstantUrl.DEVICES_BIND_POST;

/**
 *
 *
 */
public class HomeFragment extends AbsBaseFragment implements
        OnBalanceRefreshCallback,
        PopupWindowBuilder.OnItemBuilder,
        DataCallback<List<DeviceListBean.DataBean>>,
        OnRefreshListener {

    private static final int REQUEST_CODE_QRCODE_PERMISSIONS = 1;
    private static final int REQUEST_CODE_IMPORT_PERMISSIONS = 2;
    private com.scwang.smartrefresh.layout.SmartRefreshLayout mRefresh;
    private Bitmap mBitmap;//二维码
    private WalletBean mCurrentWallet;
    private TextView statusView;
    private TextView walletNameTx;
    private ImageView moreWallet;
    private LinearLayout modifyWallet;
    private TextView walletBalanceTx;
    private RelativeLayout walletLayout;
    private TextView walletAddressTx;
    private PopupWindowBuilder mBuilder;
    private ImageView addDevice;
    private ImageView wallet_logo;

    private CommonAdapter mAdapterMore;
    private List<WalletBean> mWallets = new ArrayList<>();
    private ListView mDevicesLv;//设备列表
    private ListView mMoreWalletListView;//更过钱包

    private CommonAdapter mAdapterDeviceLv;

    private List<DeviceListBean.DataBean> mDevicelistbeans = new ArrayList<>();
    private View lvHeader;

    private Presenter mPresenter;

    private void findViews(View rootView) {
        walletNameTx = (TextView) rootView.findViewById(R.id.wallet_name_tv);
        moreWallet = (ImageView) rootView.findViewById(R.id.wallet_list);
        modifyWallet = (LinearLayout) rootView.findViewById(R.id.modify_wallet);
        walletBalanceTx = (TextView) rootView.findViewById(R.id.walletBalanceTx);
        walletLayout = (RelativeLayout) rootView.findViewById(R.id.wallet_layout);
        addDevice = (ImageView) rootView.findViewById(R.id.add_device);
        wallet_logo = (ImageView) rootView.findViewById(R.id.wallet_logo);
        walletAddressTx = (TextView) rootView.findViewById(R.id.wallet_address_tv);

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
    public void onResume() {
        super.onResume();
        mImmersionBar
                .statusBarColor(R.color.blue_top)
                .statusBarDarkFont(false).execute();
        if (walletNameTx != null && WalletDaoTools.getWalletTop() != null) {
            String name = WalletDaoTools.getWalletTop().getName();
            walletNameTx.setText(name);
        }
        if (WalletDaoTools.getAllWallet() != null && WalletDaoTools.getAllWallet().size() > 0) {
            mWallets.clear();
            mWallets.addAll(WalletDaoTools.getAllWallet());
            mAdapterMore.notifyDataSetChanged();
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
    protected void initView(View view) {

        mDevicesLv = (ListView) view.findViewById(R.id.devices_items);
        lvHeader = LayoutInflater.from(getActivity()).inflate(R.layout.header_lv_home_page, null) ;
        findViews(lvHeader);
        mDevicesLv.addHeaderView(lvHeader);
        mRefresh = view.findViewById(R.id.refresh_asset);

        mRefresh.setOnRefreshListener(this);
        mBuilder = new PopupWindowBuilder(getActivity(), R.layout.layout_popup_list, this);

        mAdapterDeviceLv = new CommonAdapter(getActivity(), mDevicelistbeans, R.layout.item_devices_layout, new DeviceViewHelper());
        mDevicesLv.setAdapter(mAdapterDeviceLv);
        mDevicesLv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //判断listview第一个可见的条目是否是第一个条目
                if (mDevicesLv.getFirstVisiblePosition() == 0) {
                    View firstVisibleItemView = mDevicesLv.getChildAt(0);
                    //判断第一个条目距离listview是否是0,如果是，则srLayout可用，否则不可用
                    if (firstVisibleItemView != null && firstVisibleItemView.getTop() == 0) {
                        mRefresh.setEnabled(true);
                    } else {
                        mRefresh.setEnabled(false);
                    }
                } else {
                    mRefresh.setEnabled(false);
                }
                //根据当前是否是在刷新数据，来决定是否拦截listview的触摸事件
                return isRefreshing;
            }
        });
        /*
         * 长按复制
         * */
        walletAddressTx.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                StringUtils.copy(getActivity(), walletAddressTx.getText().toString());
                Toast.makeText(getActivity(), "已复制地址", Toast.LENGTH_LONG).show();
                return false;
            }
        });
        /*
         * 显示地址信息
         * */
        walletLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShowAddressActivity.class);
                intent.putExtra("msg", mCurrentWallet.getAddress());
                skip(intent);
            }
        });

        /*
         * 更多钱包
         * */
        moreWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayMetrics metrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
                int screenHeight = metrics.heightPixels;
                int screenWidth = metrics.widthPixels;
                mBuilder.setAnimationStyle(R.style.push_left_in_out)
                        .setWidth((int) (screenWidth * 0.6))
                        .setHeight(screenHeight)
                        .setLocation(mRefresh, Gravity.END | Gravity.TOP, 0, 0)
                        .build()
                        .show();
                mMoreWalletListView.smoothScrollToPosition(0);
//                mImmersionBar.statusBarDarkFont(true).execute();

            }

        });
        /*
         * 修改钱包
         * */
        modifyWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skip(ModifyWalletActivity.class, Comm.SERIALIZABLE_DATA, mCurrentWallet);
            }
        });
        /*
         * 绑定设备
         * */
        addDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCodeQRCodePermissions();

            }
        });
    }


    @Override
    protected void initData() {
        mPresenter = new Presenter();
        mPresenter.initHomePageModel();
        mCurrentWallet = WalletDaoTools.getWalletTop();
        if (mCurrentWallet == null) {
            return;
        }
        walletNameTx.setText(mCurrentWallet.getName());
        walletAddressTx.setText(mCurrentWallet.getAddress());
        if (!StringUtils.isEmpty(mCurrentWallet.getBalance())) {
            walletBalanceTx.setText(mCurrentWallet.getBalance());// 钱包金额
        } else {
            walletBalanceTx.setText("0.0000");// 钱包金额
        }
        int id = mCurrentWallet.getIconIdex();
        wallet_logo.setImageResource(sRandomHeader[id]);
        ApiWalletManager.getInstance().getBalance(mCurrentWallet, this);
        getDeviceList();

//        mBitmap = generateQRCode(ApiWalletManager.getInstance().getMainWallet().getAddress());
//        codeIv.setImageBitmap(mBitmap);

    }

    private void getDeviceList() {
        mPresenter.getCurrentWalletDevicesList(mCurrentWallet, this);
    }


    @Override
    protected void setImmersionBar() {
        mImmersionBar
                .statusBarColor(R.color.blue_top)
                .execute();
    }


    /*
     * 请求余额结果
     * */
    @Override
    public void onBalanceRefreshSuccess(WalletBean balance) {
        walletBalanceTx.setText(mCurrentWallet.getBalance());

    }

    @Override
    public void onBalanceRefreshFailure(String s) {

    }

    /*
     * 请求余额结果
     * */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 10) {
            final String result = data.getStringExtra("result");
            Log.i("result", result);
            final DialogBindCardWithWallet dialog = new DialogBindCardWithWallet(getActivity());
            dialog.setMessageText(result);
            dialog.setLeftBtnClickedListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.setRightBtnClickedListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    //绑定设备
                    String address = mCurrentWallet.getAddress();
                    OkGo.<String>post(DEVICES_BIND_POST)                            // 请求方式和请求url
                            .tag(this)// 请求的 tag, 主要用于取消对应的请求
                            .params("eth_address", address)
                            .params("cksn", result)
                            .cacheKey("cacheKey")            // 设置当前请求的缓存key,建议每个不同功能的请求设置一个
                            .cacheMode(CacheMode.DEFAULT)    // 缓存模式，详细请看缓存介绍
                            .execute(new StringCallback() {
                                @Override
                                public void onSuccess(Response<String> response) {

                                    if (response == null) {
                                        ToastUtil.showToastLonger("绑定失败");
                                        return;
                                    }

                                    String json = response.body();
                                    DeviceBean bindBean = NetUtils.gsonToBean(json, DeviceBean.class);
                                    if (bindBean == null || bindBean.getData() == null) {
                                        ToastUtil.showToastLonger("绑定失败");
                                        return;
                                    }
                                    if (bindBean != null && bindBean.getSuccess() != 0) {
                                        ToastUtil.showToastLonger(bindBean.getMessage());
                                        return;
                                    } else {
                                        DeviceListBean.DataBean bean = new DeviceListBean.DataBean();
                                        bean.setCksn(bindBean.getData().getCksn());
                                        bean.setCreated_at(bindBean.getData().getCreated_at());
                                        bean.setId(bindBean.getData().getId());
                                        bean.setImage_url(bindBean.getData().getImage_url());
                                        bean.setName(bindBean.getData().getName());
                                        bean.setSystem(bindBean.getData().getSystem());
                                        mDevicelistbeans.add(bean);
                                        mAdapterDeviceLv.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onError(Response<String> response) {
                                    super.onError(response);
                                    ToastUtil.showToastLonger("绑定失败");
                                    dialog.dismiss();
                                }
                            });
                }
            });
            dialog.show();
        }
    }


    private LinearLayout scan_popu;
    private LinearLayout new_ll;

    @Override
    public void initItems(final PopupWindow instance, View contentView) {
        mWallets.addAll(WalletDaoTools.getAllWallet());

        mMoreWalletListView = contentView.findViewById(R.id.data_list);
        scan_popu = contentView.findViewById(R.id.scan_popu);
        new_ll = contentView.findViewById(R.id.new_ll);
        mAdapterMore = new CommonAdapter(getActivity(), mWallets, R.layout.item_popup_list, new MoreWalletViewHelper());
        mMoreWalletListView.setAdapter(mAdapterMore);
        scan_popu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo
                skip(ImprotWalletActivity.class);
                instance.dismiss();
            }
        });
        new_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skip(CreateWalletActivity.class);
                instance.dismiss();
            }
        });
        mMoreWalletListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "onItemClick: ");
                mCurrentWallet = mWallets.get(position);
                walletNameTx.setText(mCurrentWallet.getName());
                walletAddressTx.setText(mCurrentWallet.getAddress());
                if (!StringUtils.isEmpty(mCurrentWallet.getBalance())) {
                    walletBalanceTx.setText(mCurrentWallet.getBalance());// 钱包金额
                } else {
                    walletBalanceTx.setText("0.0000");// 钱包金额
                }
                int ids = mCurrentWallet.getIconIdex();
                wallet_logo.setImageResource(sRandomHeader[ids]);
                ApiWalletManager.getInstance().getBalance(mCurrentWallet, HomeFragment.this);
                mDevicelistbeans.clear();
                mAdapterDeviceLv.notifyDataSetChanged();
                getDeviceList();
                instance.dismiss();
            }
        });

    }


    public static int[] sRandomHeader = {
            R.mipmap.random_header_more_1,
            R.mipmap.random_header_more_2,
            R.mipmap.random_header_more_3,
            R.mipmap.random_header_more_4,
            R.mipmap.random_header_more_5,
            R.mipmap.random_header_more_6,
            R.mipmap.random_header_more_7,
            R.mipmap.random_header_more_8,
    };
    public static int[] sRandomHeaderMore = {
            R.mipmap.random_header_1,
            R.mipmap.random_header_2,
            R.mipmap.random_header_3,
            R.mipmap.random_header_4,
            R.mipmap.random_header_5,
            R.mipmap.random_header_6,
            R.mipmap.random_header_7,
            R.mipmap.random_header_8,
    };

    @AfterPermissionGranted(REQUEST_CODE_QRCODE_PERMISSIONS)
    private void requestCodeQRCodePermissions() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.VIBRATE};
        if (!EasyPermissions.hasPermissions(getActivity(), perms)) {
            EasyPermissions.requestPermissions(this, "扫描二维码需要打开相机和散光灯的权限", REQUEST_CODE_QRCODE_PERMISSIONS, perms);
        } else {
            Intent intent = new Intent(getActivity(), ScanActivity.class);
            startActivityForResult(intent, 10);
        }
    }

    @Override
    public void onLoading() {
        Log.i(TAG, "onLoading: ");
    }

    @Override
    public void onLoadDone() {
        Log.i(TAG, "onLoadDone: ");
        mRefresh.finishRefresh();
    }

    @Override
    public void onSuccess(List<DeviceListBean.DataBean> dataBeans) {
        mDevicelistbeans.clear();
        mDevicelistbeans.addAll(dataBeans);
        mAdapterDeviceLv.notifyDataSetChanged();
    }

    @Override
    public void onFailure(String result) {

    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        ApiWalletManager.getInstance().getBalance(mCurrentWallet, this);
        getDeviceList();
    }
}
