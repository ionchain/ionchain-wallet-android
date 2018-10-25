package org.ionchain.wallet.mvp.view.activity.importmode;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.ionchain.wallet.R;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;

import static org.ionchain.wallet.constant.ConstantParams.FROM_WELCOME;

public class SelectImportModeActivity extends AbsBaseActivity implements View.OnClickListener {

    private RelativeLayout header;
    private ImageView back;
    private Button byMnemonic;
    private Button byPrivateKey;
    private Button byKeystore;

    private boolean isWelcome;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-10-01 00:25:42 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        header = (RelativeLayout) findViewById(R.id.header);
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
        if (v == byMnemonic) {
            Intent intent = new Intent(getMActivity(), ImportByMnemonicActivity.class);
            intent.putExtra(FROM_WELCOME, isWelcome);
            startActivity(intent);
        } else if (v == byPrivateKey) {
            Intent intent = new Intent(getMActivity(), ImportByPriKeyActivity.class);
            intent.putExtra(FROM_WELCOME, isWelcome);
            startActivity(intent);
        } else if (v == byKeystore) {
            Intent intent = new Intent(getMActivity(), ImportByKeystoreActivity.class);
            intent.putExtra(FROM_WELCOME, isWelcome);
            startActivity(intent);
        } else if (v == back) {
            finish();
        }
    }

    @Override
    protected void handleIntent(Intent intent) {
        super.handleIntent(intent);
        isWelcome = intent.getBooleanExtra(FROM_WELCOME, false);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        findViews();
        getMImmersionBar().titleBar(R.id.header_top).statusBarDarkFont(true).execute();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_select_import_mode;
    }
}
