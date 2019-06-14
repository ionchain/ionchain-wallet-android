package org.ionchain.wallet.mvp.view.activity.transaction;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.ListView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.ionc.wallet.adapter.CommonAdapter;
import org.ionc.wallet.bean.TxRecordBean;
import org.ionc.wallet.sdk.IONCWalletSDK;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionchain.wallet.R;
import org.ionchain.wallet.adapter.txrecoder.TxRecordViewHelper;
import org.ionchain.wallet.bean.TxRecordBeanTemp;
import org.ionchain.wallet.mvp.callback.OnLoadingView;
import org.ionchain.wallet.mvp.callback.OnTxRecordCallback;
import org.ionchain.wallet.mvp.presenter.transcation.TxRecordPresenter;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;
import org.ionchain.wallet.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class TxRecordActivity extends AbsBaseActivity implements OnTxRecordCallback, OnLoadingView, OnRefreshListener, OnLoadMoreListener {
    private CommonAdapter adapterLv;
    private List<TxRecordBean> mTxRecordBeanList = new ArrayList<>();
    private String mAddress;
    private TxRecordPresenter mTxRecordPresenter;
    private SmartRefreshLayout mSmartRefreshLayout;
    private int totalCount = 0;
    private int pageNum = 2;
    List<TxRecordBean> mRecordBeanListLocalTemp = new ArrayList<>();

    @Override
    protected void initData() {
        IONCWalletSDK.getInstance().ethTransaction("http://api.ionchain.org","0x5fd16681a580e46b015d8719dd42e41e01c4b986f089d92bb7a20faefc309c4e",null);
        mRecordBeanListLocalTemp = IONCWalletSDK.getInstance().getAllTxRecordBeans();
        if (mRecordBeanListLocalTemp != null && mRecordBeanListLocalTemp.size() > 0) {
            int size = mRecordBeanListLocalTemp.size();
            for (int i = 0; i < size; i++) {
                LoggerUtils.i("local hs = " + mRecordBeanListLocalTemp.get(i).getTxHash());
                LoggerUtils.i("local bl = " + mRecordBeanListLocalTemp.get(i).getTxHash());
            }
            mTxRecordBeanList.addAll(mRecordBeanListLocalTemp);
            adapterLv.notifyDataSetChanged();
        }
        mTxRecordPresenter = new TxRecordPresenter();
        mTxRecordPresenter.getTxRecord(false, "3", mAddress, "1", "10", this);
    }

    @Override
    protected void initView() {
        mImmersionBar.titleView(R.id.toolbarlayout).statusBarDarkFont(true).execute();
        ListView tx_record_lv = findViewById(R.id.tx_recoder_lv);
        mSmartRefreshLayout = findViewById(R.id.refresh_tx_record);
        mSmartRefreshLayout.setOnRefreshListener(this);
        mSmartRefreshLayout.setOnLoadMoreListener(this);
        mSmartRefreshLayout.setEnableLoadMore(true);
        mSmartRefreshLayout.setEnableAutoLoadMore(false);
        adapterLv = new CommonAdapter(this, mTxRecordBeanList, R.layout.item_txrecoder, new TxRecordViewHelper());
        tx_record_lv.setAdapter(adapterLv);
        findViewById(R.id.back).setOnClickListener(v -> finish());
    }

    @Override
    protected void handleIntent(Intent intent) {
        mAddress = intent.getStringExtra("mAddress");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_tx_recoder;
    }


    /**
     * @param beans 处理刷新或加载更多的数据
     */
    @Override
    public void onTxRecordRefreshSuccess(TxRecordBeanTemp.DataBean beans) {
        if (beans.getTotalCount() == totalCount) {
            ToastUtil.showToastLonger(getAppString(R.string.smart_refresh_no_new_data));
            return;
        }

        //数据筛选
        List<TxRecordBean> itemBeansNew = filterData(beans);

        if (totalCount == 0) {
            //第一次加载
            int itemBeansNewCount = itemBeansNew.size();
            if (itemBeansNewCount == 0) {
                //没有数据
                return;
            }
            //有新数据
            int itemBeansCountTemp = mRecordBeanListLocalTemp.size(); //本地缓存的数据
            for (int i = 0; i < itemBeansNewCount; i++) {
                String hs = itemBeansNew.get(i).getTxHash(); //交易哈希值
                for (int j = 0; j < itemBeansCountTemp; j++) {

                    /*
                     * 比较本地的记录与网络的记录
                     * 如果本地的哈希值与网络的哈希值相同，则对比区块号是否相等，本地缓存的时候，交易区块的哈希设置为空""，
                     * 如果此次比较，本地的哈希值为空，则说明没有同步到网络区块，更新本地的区块号，保存到数据库中，
                     * 同时从现有的数据集中移除该条记录。
                     */

                    String hsTemp = mRecordBeanListLocalTemp.get(j).getTxHash();

                    String blockTemp = mRecordBeanListLocalTemp.get(j).getBlock();

                    if (hs.equals(hsTemp)) {
                        if (TextUtils.isEmpty(blockTemp)) {
                            //缓存记录被打包,更新本地的钱包
                            mRecordBeanListLocalTemp.get(j).setBlock(itemBeansNew.get(i).getBlock());//保存交易区块
                            IONCWalletSDK.getInstance().updateTxRecordBean(mRecordBeanListLocalTemp.get(j));
//                        mTxRecordBeanList.remove(mRecordBeanListLocalTemp.get(j));//从现有的数据集中移除新来的网络数据
                        }
                    } else {
                        //新的hs
                        mTxRecordBeanList.add(itemBeansNew.get(i));
                    }
                }
            }
            mTxRecordBeanList.addAll(mRecordBeanListLocalTemp.size(), itemBeansNew); //将新数据添加到旧数据的尾部
        } else {
            int newDataCount = beans.getTotalCount() - totalCount;
            for (int i = 0; i < newDataCount; i++) {
                mTxRecordBeanList.add(itemBeansNew.get(i));//把新数据加载列表的前面
            }
        }
        totalCount = beans.getTotalCount();
        adapterLv.notifyDataSetChanged();
    }

    /**
     * @param beans 网络数据
     * @return 所需数据
     */
    private List<TxRecordBean> filterData(TxRecordBeanTemp.DataBean beans) {
        int count = beans.getData().size();
        List<TxRecordBean> itemBeans = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            TxRecordBean txRecordBean = new TxRecordBean();
            txRecordBean.setBlock(beans.getData().get(i).getBlockHash());
            txRecordBean.setFrom(beans.getData().get(i).getTx_from());
            txRecordBean.setTo(beans.getData().get(i).getTx_to());
            txRecordBean.setTxFee(beans.getData().get(i).getTxFee());
            txRecordBean.setTxHash(beans.getData().get(i).getHash());
            txRecordBean.setValue(beans.getData().get(i).getValue());
            itemBeans.add(txRecordBean);
        }
        return itemBeans;
    }

    @Override
    public void onTxRecordLoadMoreSuccess(TxRecordBeanTemp.DataBean beans) {
        List<TxRecordBean> itemBeans = filterData(beans);
        if (itemBeans.size() <= 0) {
            ToastUtil.showToastLonger(getAppString(R.string.smart_refresh_footer_no_more));
        } else {
            pageNum++;

            int count = itemBeans.size();
            LoggerUtils.i("加载更多：size = " + count);
            for (int i = 0; i < count; i++) {
                mTxRecordBeanList.add(mTxRecordBeanList.size(), itemBeans.get(i));
            }
            adapterLv.notifyDataSetChanged();
        }
    }

    @Override
    public void onTxRecordFailure(String error) {
        ToastUtil.showShortToast(error);
    }

    @Override
    public void onLoadStart() {
        LoggerUtils.i("正在获取");
    }

    @Override
    public void onLoadFinish() {
        LoggerUtils.i("获取完成");
        mSmartRefreshLayout.finishRefresh();
        mSmartRefreshLayout.finishLoadMore();
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        LoggerUtils.i("刷新");
        mTxRecordPresenter.getTxRecord(false, "3", mAddress, "1", "10", this);
    }

    /**
     * @param refreshLayout 加载更多
     */
    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        mTxRecordPresenter.getTxRecord(true, "3", mAddress, String.valueOf(pageNum), "10", this);
    }
//
//    @Override
//    public void onBackPressed() {
//        OkGo.cancelTag(OkGo.getInstance().getOkHttpClient(), this);
//    }
}
