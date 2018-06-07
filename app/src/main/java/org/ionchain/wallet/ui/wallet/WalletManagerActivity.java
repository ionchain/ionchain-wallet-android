package org.ionchain.wallet.ui.wallet;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.fast.lib.logger.Logger;
import com.fast.lib.utils.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.ionchain.wallet.R;
import org.ionchain.wallet.comm.api.resphonse.ResponseModel;
import org.ionchain.wallet.ui.comm.BaseActivity;

import butterknife.BindView;

public class WalletManagerActivity extends BaseActivity {

    @BindView(R.id.srl)
    SmartRefreshLayout srl;

    @BindView(R.id.dataRv)
    RecyclerView dataRv;


    @Override
    public void handleMessage(int what, Object obj) {
        super.handleMessage(what, obj);
        try{

            switch (what){
                case R.id.navigationBack:
                    finish();
                    break;
                case 0:
                    dismissProgressDialog();
                    if(obj == null)
                        return;

                    ResponseModel<String> responseModel = (ResponseModel)obj;
                    if(!verifyStatus(responseModel)){
                        ToastUtil.showShortToast(responseModel.getMsg());
                        return;
                    }


                    break;
            }

        }catch (Throwable e){
            Logger.e(e,TAG);
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_wallet_manager);

    }

    @Override
    protected void setListener() {
        setOnClickListener(R.id.importBtn);
        setOnClickListener(R.id.createBtn);



    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }

    @Override
    public int getActivityMenuRes() {
        return 0;
    }

    @Override
    public int getHomeAsUpIndicatorIcon() {
        return R.mipmap.ic_arrow_back;
    }

    @Override
    public int getActivityTitleContent() {
        return R.string.activity_wallet_manager_title;
    }
}
