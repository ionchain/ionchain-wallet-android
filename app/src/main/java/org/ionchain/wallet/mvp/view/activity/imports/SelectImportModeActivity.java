package org.ionchain.wallet.mvp.view.activity.imports;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.ionchain.wallet.R;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;

import static org.ionchain.wallet.utils.AnimationUtils.setViewAlphaAnimation;

public class SelectImportModeActivity extends AbsBaseActivity implements View.OnClickListener {

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
        back = (ImageView) findViewById(R.id.back);
        byMnemonic = (Button) findViewById(R.id.by_mnemonic);
        byPrivateKey = (Button) findViewById(R.id.by_private_key);
        byKeystore = (Button) findViewById(R.id.by_keystore);

        byMnemonic.setOnClickListener(this);
        byPrivateKey.setOnClickListener(this);
        byKeystore.setOnClickListener(this);
        back.setOnClickListener(this);

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
        Intent intent = null;
        if (v == byMnemonic) {
            intent = new Intent(mActivity, ImportByMnemonicActivity.class);
            startActivity(intent);
        } else if (v == byPrivateKey) {
            intent = new Intent(mActivity, ImportByPriKeyActivity.class);
            startActivity(intent);
        } else if (v == byKeystore) {
            intent = new Intent(mActivity, ImportByKeystoreActivity.class);
            startActivity(intent);
        } else if (v == back) {
            finish();
        }

    }

    @Override
    protected void handleIntent(@NonNull Intent intent) {
        super.handleIntent(intent);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        findViews();
        mImmersionBar.titleView(R.id.header_top).statusBarDarkFont(true).execute();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_select_import_mode;
    }
}
