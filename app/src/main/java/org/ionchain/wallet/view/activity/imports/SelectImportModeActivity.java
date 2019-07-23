package org.ionchain.wallet.view.activity.imports;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import org.ionc.wallet.bean.WalletBeanNew;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionchain.wallet.R;
import org.ionchain.wallet.view.base.AbsBaseActivityTitleTwo;

import static org.ionchain.wallet.constant.ConstantActivitySkipTag.INTENT_FROM_WHERE_TAG;
import static org.ionchain.wallet.constant.ConstantParams.SERIALIZABLE_DATA_WALLET_BEAN;
import static org.ionchain.wallet.view.fragment.AssetFragment.NEW_WALLET_FOR_RESULT_CODE;
import static org.ionchain.wallet.utils.AnimationUtils.setViewAlphaAnimation;

public class SelectImportModeActivity extends AbsBaseActivityTitleTwo implements View.OnClickListener {

    private ImageView back;
    private Button byMnemonic;
    private Button byPrivateKey;
    private Button byKeystore;


    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-10-01 00:25:42 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        RelativeLayout header = (RelativeLayout) findViewById(R.id.header);
        byMnemonic = (Button) findViewById(R.id.by_mnemonic);
        byPrivateKey = (Button) findViewById(R.id.by_private_key);
        byKeystore = (Button) findViewById(R.id.by_keystore);

        byMnemonic.setOnClickListener(this);
        byPrivateKey.setOnClickListener(this);
        byKeystore.setOnClickListener(this);

    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2018-10-01 00:25:42 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        setViewAlphaAnimation(v);
        if (v == byMnemonic) {
            Intent intent = new Intent(this, ImportByMnemonicActivity.class);
            intent.putExtra(INTENT_FROM_WHERE_TAG, mActivityFrom);
            startActivityForResult(intent, NEW_WALLET_FOR_RESULT_CODE);
        } else if (v == byPrivateKey) {
            Intent intent = new Intent(this, ImportByPriKeyActivity.class);
            intent.putExtra(INTENT_FROM_WHERE_TAG, mActivityFrom);
            startActivityForResult(intent, NEW_WALLET_FOR_RESULT_CODE);
        } else if (v == byKeystore) {
            Intent intent = new Intent(this, ImportByKeystoreActivity.class);
            intent.putExtra(INTENT_FROM_WHERE_TAG, mActivityFrom);
            startActivityForResult(intent, NEW_WALLET_FOR_RESULT_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*
         *交易成功，返回成功的hash值给fragment
         */

        if (resultCode == NEW_WALLET_FOR_RESULT_CODE) {
            Intent intent = new Intent();
            WalletBeanNew walletBeanNew;
            if (data != null) {
                walletBeanNew = data.getParcelableExtra(SERIALIZABLE_DATA_WALLET_BEAN);
                LoggerUtils.i("requestCode", "requestCode = " + requestCode + "resultCode = " + resultCode + "address = " + walletBeanNew.getAddress());
                intent.putExtra(SERIALIZABLE_DATA_WALLET_BEAN, walletBeanNew);
                setResult(NEW_WALLET_FOR_RESULT_CODE, intent);
            finish();
            }
        } else {
            finish();
        }
    }

    @Override
    protected String getTitleName() {
        return getAppString(R.string.select_import_title);
    }

    @Override
    protected void initView() {
        findViews();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_select_import_mode;
    }
}
