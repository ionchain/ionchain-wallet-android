package org.ionchain.wallet.ui.wallet;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.gyf.barlibrary.ImmersionBar;

import org.ionchain.wallet.R;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;

/**
 * 创建钱包。导入钱包，第一次安装时，由启动页跳转过来
 */
public class CreateWalletSelectActivity extends AbsBaseActivity {



    private Button createBtn;
    private Button importBtn;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-09-19 19:22:58 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        createBtn = (Button)findViewById( R.id.createBtn );
        importBtn = (Button)findViewById( R.id.importBtn );
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skip(CreateWalletActivity.class);
            }
        });
        importBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skip(ImportWalletActivity.class);
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImmersionBar.with(this)
                .statusBarDarkFont(false)
                .transparentStatusBar()
                .navigationBarColor(R.color.black,0.5f)
                .fitsSystemWindows(false)
                .init();
    }

    @Override
    protected void initData() {

    }



    @Override
    protected void initView() {
        findViews();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_create_wallet_select;
    }

    @Override
    protected void handleIntent() {
        super.handleIntent();
        requestCodeQRCodePermissions();
    }

}
