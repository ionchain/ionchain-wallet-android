package org.ionchain.wallet.mvp.view.activity.createwallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.ionchain.wallet.R;
import org.ionchain.wallet.mvp.view.activity.importmode.SelectImportModeActivity;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;

import static org.ionchain.wallet.constant.ConstantParams.FROM_WELCOME;

/**
 * 创建钱包。导入钱包，第一次安装时，由启动页跳转过来
 */
public class CreateWalletSelectActivity extends AbsBaseActivity {



    private Button createBtn;
    private Button importBtn;
    private boolean isWelcome;

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
                Intent intent = new Intent(getMActivity(),CreateWalletActivity.class);
                intent.putExtra(FROM_WELCOME,isWelcome);
                startActivity(intent);
            }
        });
        importBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getMActivity(),SelectImportModeActivity.class);
                intent.putExtra(FROM_WELCOME,isWelcome);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getMImmersionBar()
                .statusBarDarkFont(false)
                .transparentStatusBar()
                .navigationBarColor(R.color.black,0.5f)
                .fitsSystemWindows(false)
                .execute();
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
    protected void handleIntent(Intent intent) {
        super.handleIntent(intent);
        isWelcome = intent.getBooleanExtra(FROM_WELCOME,false);
        requestCodeQRCodePermissions();
    }

}
