package org.ionchain.wallet.mvp.view.fragment;

import android.Manifest;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.smart.holder.CommonAdapter;

import org.ionchain.wallet.R;
import org.ionchain.wallet.adapterhelper.device.DeviceViewHelper;
import org.ionchain.wallet.adapterhelper.moewwallet.MoreWalletViewHelper;
import org.ionchain.wallet.bean.DeviceBean;
import org.ionchain.wallet.bean.DeviceListBean;
import org.ionchain.wallet.bean.WalletBean;
import org.ionchain.wallet.callback.OnBindDeviceCallback;
import org.ionchain.wallet.callback.OnCreateWalletCallback;
import org.ionchain.wallet.callback.OnDeviceListCallback;
import org.ionchain.wallet.callback.OnUnbindDeviceCallback;
import org.ionchain.wallet.callback.UnbindDeviceButtonClickedListener;
import org.ionchain.wallet.comm.api.ApiWalletManager;
import org.ionchain.wallet.comm.constants.Comm;
import org.ionchain.wallet.dao.WalletDaoTools;
import org.ionchain.wallet.mvp.presenter.Presenter;
import org.ionchain.wallet.mvp.view.activity.TxOutActivity;
import org.ionchain.wallet.ui.base.AbsBaseFragment;
import org.ionchain.wallet.ui.comm.ScanActivity;
import org.ionchain.wallet.ui.main.ShowAddressActivity;
import org.ionchain.wallet.ui.wallet.CreateWalletActivity;
import org.ionchain.wallet.ui.wallet.ImprotWalletActivity;
import org.ionchain.wallet.ui.wallet.ModifyWalletActivity;
import org.ionchain.wallet.utils.SoftKeyboardUtil;
import org.ionchain.wallet.utils.StringUtils;
import org.ionchain.wallet.widget.DialogBindDevice;
import org.ionchain.wallet.widget.PopupWindowBuilder;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;

/**
 *
 *
 */
public class HomeFragment extends AbsBaseFragment implements
        PopupWindowBuilder.OnItemBuilder,
        OnRefreshListener,
        OnBindDeviceCallback,
        OnDeviceListCallback,
        OnCreateWalletCallback, UnbindDeviceButtonClickedListener, OnUnbindDeviceCallback {


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
    private static final int REQUEST_CODE_QRCODE_PERMISSIONS = 1;
    private static final int REQUEST_CODE_IMPORT_PERMISSIONS = 2;
    private SmartRefreshLayout mRefresh;
    private WalletBean mCurrentWallet;
    private TextView walletNameTx;
    private ImageView moreWallet;
    private LinearLayout modifyWallet;
    private TextView walletBalanceTx;
    private RelativeLayout walletAddressLayout;
    private TextView walletAddressTx;//钱包地址
    private PopupWindowBuilder mBuilder;
    private ImageView addDevice;
    private ImageView wallet_logo;

    private CommonAdapter mAdapterMore;
    private List<WalletBean> mWallets = new ArrayList<>();
    private ListView mDevicesLv;//设备列表
    private ListView mMoreWalletListView;//更过钱包

    private CommonAdapter mAdapterDeviceLv;

    private List<DeviceListBean.DataBean> mDataBeans = new ArrayList<>();
    private View lvHeader;//lv头部

    private Presenter mPresenter;


    private LinearLayout tx_out_ll;
    private LinearLayout tx_in_ll;

    private DialogBindDevice mDialogBindCardWithWallet;//绑定设备的弹窗
    private int mUnbindPos = 0;

    private void initListViewHeaderViews(View rootView) {
        walletNameTx = (TextView) rootView.findViewById(R.id.wallet_name_tv);
        moreWallet = (ImageView) rootView.findViewById(R.id.wallet_list);
        modifyWallet = (LinearLayout) rootView.findViewById(R.id.modify_wallet);
        walletBalanceTx = (TextView) rootView.findViewById(R.id.walletBalanceTx);
        walletAddressLayout = (RelativeLayout) rootView.findViewById(R.id.wallet_address_layout);
        addDevice = (ImageView) rootView.findViewById(R.id.add_device);
        wallet_logo = (ImageView) rootView.findViewById(R.id.wallet_logo);
        walletAddressTx = (TextView) rootView.findViewById(R.id.wallet_address_tv);
        walletAddressTx = (TextView) rootView.findViewById(R.id.wallet_address_tv);
        tx_in_ll = rootView.findViewById(R.id.tx_in_ll);
        tx_out_ll = rootView.findViewById(R.id.tx_out_ll);

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
        SoftKeyboardUtil.hideSoftKeyboard(getActivity());
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
        return R.layout.fragment_home;
    }

    /**
     * @param view 实例化的ivew
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void initView(View view) {

        mDevicesLv = (ListView) view.findViewById(R.id.devices_items);
        lvHeader = LayoutInflater.from(getActivity()).inflate(R.layout.header_lv_home_page, null);
        initListViewHeaderViews(lvHeader);

        mDevicesLv.addHeaderView(lvHeader);

        mRefresh = view.findViewById(R.id.refresh_asset);
        mRefresh.setOnRefreshListener(this);
        mBuilder = new PopupWindowBuilder(getActivity(), R.layout.layout_popup_list, this);

        mAdapterDeviceLv = new CommonAdapter(getActivity(), mDataBeans, R.layout.item_devices_layout, new DeviceViewHelper(this));
        mDevicesLv.setAdapter(mAdapterDeviceLv);



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
        walletAddressLayout.setOnClickListener(new View.OnClickListener() {
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

        /*
         * 转账
         * */
        tx_out_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skip(TxOutActivity.class);
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
    protected void initImmersionBar() {
        mImmersionBar
                .statusBarColor(R.color.blue_top)
                .execute();
    }


    /*
     * 请求余额结果
     * */
    @Override
    public void onCreateSuccess(WalletBean walletBean) {
        walletBalanceTx.setText(mCurrentWallet.getBalance());
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
            mDialogBindCardWithWallet = new DialogBindDevice(getActivity());
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
                    mPresenter.bindDeviceToWallet(address, result, HomeFragment.this);
                }
            });
            mDialogBindCardWithWallet.show();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initItems(final PopupWindow instance, View contentView) {
        mWallets.addAll(WalletDaoTools.getAllWallet());

        mMoreWalletListView = contentView.findViewById(R.id.data_list);
        LinearLayout scan_popu = contentView.findViewById(R.id.scan_popu);
        LinearLayout new_ll = contentView.findViewById(R.id.new_ll);
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
                mDataBeans.clear();
                mAdapterDeviceLv.notifyDataSetChanged();
                getDeviceList();
                instance.dismiss();
            }
        });

    }

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
    public void onLoadStart() {
        Log.i(TAG, "onLoadStart: ");
    }

    @Override
    public void onLoadFinish() {
        Log.i(TAG, "onLoadFinish: ");
        mRefresh.finishRefresh();
    }


    @Override
    public void onCreateFailure(String result) {
        Log.i(TAG, "onCreateFailure: " + result);
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        ApiWalletManager.getInstance().getBalance(mCurrentWallet, this);
        getDeviceList();
    }


    @Override
    public void onBindSuccess(DeviceBean.DataBean dataBean) {
        DeviceListBean.DataBean bean = new DeviceListBean.DataBean();
        bean.setCksn(dataBean.getCksn());
        bean.setCreated_at(dataBean.getCreated_at());
        bean.setId(dataBean.getId());
        bean.setImage_url(dataBean.getImage_url());
        bean.setName(dataBean.getName());
        bean.setSystem(dataBean.getSystem());
        mDataBeans.add(bean);
        mAdapterDeviceLv.notifyDataSetChanged();
    }

    @Override
    public void onBindFailure(String result) {
        Log.i(TAG, "onBindFailure: " + result);
    }

    @Override
    public void onDeviceListSuccess(List<DeviceListBean.DataBean> list) {
        Log.i(TAG, "onDeviceListSuccess: " + list.toString());
        mDataBeans.clear();
        mDataBeans.addAll(list);
        mAdapterDeviceLv.notifyDataSetChanged();
    }

    @Override
    public void onDeviceListFailure(String errorMessage) {
        Log.i(TAG, "onDeviceListFailure: " + errorMessage);
    }

    @Override
    public void onUnbindButtonClick(final String cksn, int position) {
        Log.i(TAG, "onUnbindButtonClick: " + cksn);
        mUnbindPos = position;
        mDialogBindCardWithWallet = new DialogBindDevice(getActivity());
        mDialogBindCardWithWallet.setMessageText(cksn);
        mDialogBindCardWithWallet.setTitleText("确定解绑设备?");
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
                mPresenter.unbindDeviceToWallet(mCurrentWallet.getAddress(), cksn, HomeFragment.this);
            }
        });
        mDialogBindCardWithWallet.show();


    }

    @Override
    public void onUnbindSuccess(DeviceBean.DataBean dataBean) {
        mDataBeans.remove(mUnbindPos);
        mAdapterDeviceLv.notifyDataSetChanged();
    }

    @Override
    public void onUnbindFailure(String result) {
        Log.i(TAG, "onUnbindFailure: " + result);
    }

}
