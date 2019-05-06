package org.ionchain.wallet.mvp.presenter;

import org.ionc.wallet.bean.WalletBean;
import org.ionc.wallet.callback.OnTxRecoderCallback;
import org.ionchain.wallet.mvp.callback.OnBindDeviceCallback;
import org.ionchain.wallet.mvp.callback.OnDeviceDetailCallback;
import org.ionchain.wallet.mvp.callback.OnDeviceListCallback;
import org.ionchain.wallet.mvp.callback.OnUnbindDeviceCallback;
import org.ionchain.wallet.mvp.model.update.OnCheckUpdateInfoCallback;

import java.util.List;

/**
 * USER: binny
 * DATE: 2018/9/13
 * 描述: 中间层
 */
public interface IPresenter {

    /**
     * 获取用户当前钱包的设备列表
     *
     * @param walletBean 钱包地址
     * @param callback   钱包数据集
     */
    void getCurrentWalletDevicesList(WalletBean walletBean, OnDeviceListCallback callback);

    /**
     * 获取用户所有钱包的相关的设备
     *
     * @param walletBeans 用户的所有钱包
     * @param callback    钱包数据集
     */
    void getAllWalletDevicesList(List<WalletBean> walletBeans, OnDeviceListCallback callback);

    /**
     * 将设备与钱包绑定
     *
     * @param address  钱包地址
     * @param cksn     设备的唯一识别码
     * @param callback 绑定成功，返回的结果
     */
    void bindDeviceToWallet(String address, String cksn, OnBindDeviceCallback callback);

    /**
     * 将设备与钱包解绑定
     *
     * @param address 钱包地址
     * @param cksn    设备的唯一识别码
     */
    void unbindDeviceToWallet(String address, String cksn, OnUnbindDeviceCallback callback);

    /**
     * @param cksn     设备识别码
     * @param callback 设备信息
     */
    void getDeviceDetail(String cksn, OnDeviceDetailCallback callback);

    /**
     * @param type
     * @param key        钱包地址
     * @param pageNumber
     * @param pageSize
     */
    void getTxRecord(String type, String key, String pageNumber, String pageSize, OnTxRecoderCallback callback);

    /**
     * @param callback 检查APP更新 检查结果回调
     */
    void checkForUpdate(OnCheckUpdateInfoCallback callback);
}
