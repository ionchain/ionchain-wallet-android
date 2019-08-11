package org.ionchain.wallet.view.activity.setting.coin;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.sdk.wallet.utils.LoggerUtils;
import org.ionchain.wallet.R;
import org.ionchain.wallet.utils.ViewUtils;
import org.ionchain.wallet.view.base.AbsBaseActivityTitleTwo;
import org.ionchain.wallet.utils.SPUtils;

import static org.ionchain.wallet.App.mCoinType;
import static org.ionchain.wallet.constant.ConstantCoinType.COIN_TYPE_CNY;
import static org.ionchain.wallet.constant.ConstantCoinType.COIN_TYPE_IDR;
import static org.ionchain.wallet.constant.ConstantCoinType.COIN_TYPE_KRW;
import static org.ionchain.wallet.constant.ConstantCoinType.COIN_TYPE_USD;

public class SelectCoinActivity extends AbsBaseActivityTitleTwo implements View.OnClickListener {
    private Button coinUs;
    private Button coinCny;
    private Button coinKrw;
    private Button coinIdr;

    private TextView mUserSelect;
    String split = " ：";

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


    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        super.initData();
        setCoinType();
    }

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2019-06-19 13:30:44 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        mUserSelect = findViewById(R.id.current_coin_type);
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
    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        if (v == coinUs) {
            mCoinType = COIN_TYPE_USD;
        } else if (v == coinCny) {
            mCoinType = COIN_TYPE_CNY;
        } else if (v == coinKrw) {
            mCoinType = COIN_TYPE_KRW;
        } else if (v == coinIdr) {
            mCoinType = COIN_TYPE_IDR;
        }
        SPUtils.getInstance().saveCoinType(mCoinType);
        setCoinType();
    }

    @SuppressLint("SetTextI18n")
    private void setCoinType() {
        String ct = SPUtils.getInstance().getCoinType();
        switch (ct) {
            case COIN_TYPE_USD:
                ct = getAppString(R.string.coin_type_us);
                ViewUtils.setBtnSelectedColor(this,coinUs);
                ViewUtils.setBtnUnSelectedColor(this,coinCny);
                ViewUtils.setBtnUnSelectedColor(this,coinKrw);
                ViewUtils.setBtnUnSelectedColor(this,coinIdr);
                break;
            case COIN_TYPE_CNY:
                ct = getAppString(R.string.coin_type_rmb);
                ViewUtils.setBtnSelectedColor(this,coinCny);
                ViewUtils.setBtnUnSelectedColor(this,coinUs);
                ViewUtils.setBtnUnSelectedColor(this,coinKrw);
                ViewUtils.setBtnUnSelectedColor(this,coinIdr);
                break;
            case COIN_TYPE_KRW:
                ct = getAppString(R.string.coin_type_han_yuan);
                ViewUtils.setBtnSelectedColor(this,coinKrw);
                ViewUtils.setBtnUnSelectedColor(this,coinUs);
                ViewUtils.setBtnUnSelectedColor(this,coinCny);
                ViewUtils.setBtnUnSelectedColor(this,coinIdr);
                break;
            case COIN_TYPE_IDR:
                ct = getAppString(R.string.coin_type_yin_ni_dun);
                ViewUtils.setBtnSelectedColor(this,coinIdr);
                ViewUtils.setBtnUnSelectedColor(this,coinUs);
                ViewUtils.setBtnUnSelectedColor(this,coinKrw);
                ViewUtils.setBtnUnSelectedColor(this,coinCny);
                break;
        }
        LoggerUtils.i("cointype = "+ct);
        mUserSelect.setText(getAppString(R.string.setting_select_coin_type) + split + ct);
    }

}
