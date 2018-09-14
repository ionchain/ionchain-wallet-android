package org.ionchain.wallet.mvp.view.activity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import org.ionchain.wallet.R;
import org.ionchain.wallet.ui.base.AbsBaseActivity;

public class TxOutActivity extends AbsBaseActivity {

    private RelativeLayout header;
    private EditText txAddress;
    private EditText txAccount;
    private EditText txCost;
    private SeekBar txSeekBarIndex;
    private Button txNext;

    private ImageView back;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-09-14 13:23:14 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        header = (RelativeLayout)findViewById( R.id.header );
        txAddress = (EditText)findViewById( R.id.tx_address );
        txAccount = (EditText)findViewById( R.id.tx_account );
        txCost = (EditText)findViewById( R.id.tx_cost );
        txSeekBarIndex = (SeekBar)findViewById( R.id.tx_seek_bar_index );
        txNext = (Button)findViewById( R.id.tx_next );
        back = (ImageView) findViewById( R.id.back );

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                finish();
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void handleIntent() {

    }

    @Override
    protected void initView() {
        findViews();
        mImmersionBar.titleBar(header).statusBarDarkFont(true).execute();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_tx_out;
    }
}
