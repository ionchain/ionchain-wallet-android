package org.ionchain.wallet.ui.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.ionchain.wallet.ui.base.AbsBaseFragment;
import org.ionchain.wallet.R;
import org.ionchain.wallet.comm.constants.Global;
import org.ionchain.wallet.ui.account.MessageCenterActivity;
import org.ionchain.wallet.ui.account.WalletManageActivity;
import org.ionchain.wallet.ui.login.LoginActivity;
import org.ionchain.wallet.ui.main.SettingActivity;

public class UserCenterFragment extends AbsBaseFragment {

    private View topView;
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
        topView = (View) rootView.findViewById(R.id.top_view);
        loginRegTv = (TextView) rootView.findViewById(R.id.loginRegTv);
        walletManageRLayout = (RelativeLayout) rootView.findViewById(R.id.walletManageRLayout);
        messageCenterRLayout = (RelativeLayout) rootView.findViewById(R.id.messageCenterRLayout);
        arrowIv = (ImageView) rootView.findViewById(R.id.arrowIv);
        hintMessageNum = (TextView) rootView.findViewById(R.id.hint_message_num);
        arrowIv1 = (ImageView) rootView.findViewById(R.id.arrowIv1);
    }


    @Override
    protected void setListener() {
        loginRegTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Global.user == null)
                    skip(LoginActivity.class);
                else {
                    skip(SettingActivity.class);
                }
            }
        });
        walletManageRLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skip(WalletManageActivity.class);
            }
        });
        messageCenterRLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skip(MessageCenterActivity.class);
            }
        });
        hintMessageNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            mImmersionBar
                    .statusBarColor("#ffffff")
                    .statusBarDarkFont(true)
                    .execute();
        }
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_user_center;
    }

    @Override
    protected void initView(View view) {
        findViews(view);
    }

    @Override
    protected void getData() {
        if (Global.user != null) {
            loginRegTv.setText(Global.user.getUserName());
        }
    }

    @Override
    protected void setImmersionBar() {
        super.setImmersionBar();
        mImmersionBar
                .statusBarDarkFont(true)
                .statusBarColor("#ffffff")
                .execute();
    }
}
