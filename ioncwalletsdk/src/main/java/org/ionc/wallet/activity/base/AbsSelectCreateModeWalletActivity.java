package org.ionc.wallet.activity.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.ionc.wallet.sdk.R;

public abstract class AbsSelectCreateModeWalletActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn1;
    private Button btn2;
    private Button btn3;
    private Button btn4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abs_create_wallet);
        findViews();
    }

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2019-01-09 14:33:51 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        btn4 = (Button) findViewById(R.id.btn4);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2019-01-09 14:33:51 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == btn1) {
            // Handle clicks for 新建钱包
            startActivity(getCreateActivity());
        } else if (v == btn2) {
            // Handle clicks for 私钥
            startActivity(getPrivateKeyActivity());
        } else if (v == btn3) {
            // Handle clicks for 助记词
            startActivity(getMnemonicActivity());
        } else if (v == btn4) {
            // Handle clicks for keystore
            startActivity(getKeyStoreActivity());
        }
    }

    /**
     * @return keystore activity
     */
    protected abstract Intent getKeyStoreActivity();

    /**
     * @return 助记词 activity
     */
    protected abstract Intent getMnemonicActivity();

    /**
     * @return 私钥 activity
     */
    protected abstract Intent getPrivateKeyActivity();

    /**
     * @return 新建 activity
     */
    protected abstract Intent getCreateActivity();


}
