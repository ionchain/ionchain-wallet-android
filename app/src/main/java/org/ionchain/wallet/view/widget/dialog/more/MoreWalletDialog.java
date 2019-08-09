package org.ionchain.wallet.view.widget.dialog.more;

import android.content.Context;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.ionc.wallet.adapter.CommonAdapter;
import org.ionc.wallet.bean.WalletBeanNew;
import org.ionc.wallet.sdk.IONCWallet;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionchain.wallet.R;
import org.ionchain.wallet.adapter.morewallet.MoreWalletViewHelper;
import org.ionchain.wallet.view.widget.dialog.base.AbsBaseDialog;

import java.util.ArrayList;
import java.util.List;

public class MoreWalletDialog extends AbsBaseDialog {
    private ImageView closeMore;
    private ListView mMoreWalletListView;
    private TextView importWallet;
    private TextView createWallet;
    /**
     * 更多钱包
     */
    private List<WalletBeanNew> mMoreWallets = new ArrayList<>();
    private Context mContext;
    /**
     * 更多钱包适配器
     */
    private CommonAdapter mAdapterMore;
    private OnMoreWalletDialogItemClickedListener mItemClickedListener;

    public MoreWalletDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2019-06-27 14:39:42 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        closeMore = (ImageView) findViewById(R.id.close_more_btn);
        mMoreWalletListView = (ListView) findViewById(R.id.data_list);
        importWallet = (TextView) findViewById(R.id.scan_import);
        createWallet = (TextView) findViewById(R.id.create_wallet_tv);

    }

    @Override
    protected int getLayout() {
        return R.layout.dialog_more_wallet;
    }

    @Override
    protected void initView() {
        findViews();
    }

    @Override
    protected void initData() {
        mMoreWallets = IONCWallet.getAllWalletNew();
        LoggerUtils.e("wallet" + mMoreWallets.size());
        mAdapterMore = new CommonAdapter(mContext, mMoreWallets, R.layout.item_more_wallet_list, new MoreWalletViewHelper());
        mMoreWalletListView.setAdapter(mAdapterMore);
    }

    @Override
    protected void setListener() {
        super.setListener();
        mMoreWalletListView.setOnItemClickListener((parent, view, position, id) -> mItemClickedListener.onMoreWalletDialogItemClick(mMoreWallets.get(position),position));
        importWallet.setOnClickListener(v -> mItemClickedListener.onMoreWalletDialogImportBtnClick(this));
        createWallet.setOnClickListener(v -> mItemClickedListener.onMoreWalletDialogCreateBtnClick(this));
        closeMore.setOnClickListener(v -> dismiss());
    }

    /**
     * @param data     钱包列表
     * @param listener 监听
     * @return 本实例
     */
    public MoreWalletDialog setLisenter(OnMoreWalletDialogItemClickedListener listener) {
        mItemClickedListener = listener;
        return this;
    }

    public interface OnMoreWalletDialogItemClickedListener {
        /**
         * @param wallet
         * @param position
         */
        void onMoreWalletDialogItemClick(WalletBeanNew wallet, int position);

        /**
         * 导入钱包
         *
         * @param moreWalletDialog
         */
        void onMoreWalletDialogImportBtnClick(MoreWalletDialog moreWalletDialog);

        /**
         * 创建钱包
         *
         * @param moreWalletDialog
         */
        void onMoreWalletDialogCreateBtnClick(MoreWalletDialog moreWalletDialog);
    }
}
