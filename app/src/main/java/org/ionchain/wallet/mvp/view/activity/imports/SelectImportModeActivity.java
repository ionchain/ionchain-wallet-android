package org.ionchain.wallet.mvp.view.activity.imports;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.ionchain.wallet.R;
import org.ionchain.wallet.mvp.view.base.AbsBaseCommonTitleTwoActivity;

import static org.ionchain.wallet.constant.ConstantActivitySkipTag.INTENT_FROM_WHERE_TAG;
import static org.ionchain.wallet.utils.AnimationUtils.setViewAlphaAnimation;

public class SelectImportModeActivity extends AbsBaseCommonTitleTwoActivity implements View.OnClickListener {

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
            skip(ImportByMnemonicActivity.class,INTENT_FROM_WHERE_TAG, mActivityFrom);
        } else if (v == byPrivateKey) {
            skip(ImportByPriKeyActivity.class,INTENT_FROM_WHERE_TAG, mActivityFrom);
        } else if (v == byKeystore) {
            skip(ImportByKeystoreActivity.class,INTENT_FROM_WHERE_TAG, mActivityFrom);
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
