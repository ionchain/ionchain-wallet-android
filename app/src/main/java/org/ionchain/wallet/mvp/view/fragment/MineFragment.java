package org.ionchain.wallet.mvp.view.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.ionchain.wallet.R;
import org.ionchain.wallet.mvp.view.activity.ManageWalletActivity;
import org.ionchain.wallet.mvp.view.base.AbsBaseFragment;
import org.ionchain.wallet.utils.ToastUtil;

public class MineFragment extends AbsBaseFragment {

    private TextView loginRegTv;
    private RelativeLayout walletManageRLayout;
    private RelativeLayout messageCenterRLayout;
    private ImageView arrowIv;
    private TextView hintMessageNum;
    private ImageView arrowIv1;
    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-09-12 16:34:06 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews(View rootView) {
        loginRegTv = rootView.findViewById(R.id.loginRegTv);
        walletManageRLayout = rootView.findViewById(R.id.walletManageRLayout);
        messageCenterRLayout = rootView.findViewById(R.id.messageCenterRLayout);
        arrowIv = rootView.findViewById(R.id.arrowIv);
        hintMessageNum = rootView.findViewById(R.id.hint_message_num);
        arrowIv1 = rootView.findViewById(R.id.arrowIv1);
    }

    @Override
    protected void setListener() {
        loginRegTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showToastLonger("暂不需要登录，不影响钱包的使用");
            }
        });
        walletManageRLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skip(ManageWalletActivity.class);
            }
        });
        messageCenterRLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                skip(MessageCenterActivity.class);
            }
        });

        hintMessageNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }


    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initView(View view) {
        findViews(view);
    }

    @Override
    protected void initData() {

    }
}
