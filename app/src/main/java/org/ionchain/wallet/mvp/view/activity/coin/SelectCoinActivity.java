package org.ionchain.wallet.mvp.view.activity.coin;

import android.view.View;
import android.widget.Button;

import org.ionchain.wallet.R;
import org.ionchain.wallet.mvp.view.base.AbsBaseCommonTitleTwoActivity;

import static org.ionchain.wallet.App.mCoinType;
import static org.ionchain.wallet.constant.ConstantParams.COIN_TYPE_CNY;
import static org.ionchain.wallet.constant.ConstantParams.COIN_TYPE_IDR;
import static org.ionchain.wallet.constant.ConstantParams.COIN_TYPE_KRW;
import static org.ionchain.wallet.constant.ConstantParams.COIN_TYPE_US;

public class SelectCoinActivity extends AbsBaseCommonTitleTwoActivity implements View.OnClickListener {
    private Button coinUs;
    private Button coinCny;
    private Button coinKrw;
    private Button coinIdr;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_select_coin;
    }

    @Override
    protected void initView() {
        findViews();
    }


    @Override
    protected String getTitleName() {
        return getAppString(R.string.coin_type);
    }


    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2019-06-19 13:30:44 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        coinUs = (Button) findViewById(R.id.coin_us);
        coinCny = (Button) findViewById(R.id.coin_cny);
        coinKrw = (Button) findViewById(R.id.coin_krw);
        coinIdr = (Button) findViewById(R.id.coin_idr);

        coinUs.setOnClickListener(this);
        coinCny.setOnClickListener(this);
        coinKrw.setOnClickListener(this);
        coinIdr.setOnClickListener(this);
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2019-06-19 13:30:44 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == coinUs) {
            mCoinType = COIN_TYPE_US;
        } else if (v == coinCny) {
            mCoinType = COIN_TYPE_CNY;
        } else if (v == coinKrw) {
            mCoinType = COIN_TYPE_KRW;
        } else if (v == coinIdr) {
            mCoinType = COIN_TYPE_IDR;
        }
    }

}
