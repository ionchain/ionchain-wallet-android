package org.ionchain.wallet.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fast.lib.logger.Logger;
import com.fast.lib.utils.ToastUtil;
import com.fast.lib.widget.FragmentTabHost;

import org.ionchain.wallet.R;
import org.ionchain.wallet.comm.api.resphonse.ResponseModel;
import org.ionchain.wallet.ui.comm.BaseActivity;
import org.ionchain.wallet.ui.main.HomeFragment;
import org.ionchain.wallet.ui.main.InformationFragment;
import org.ionchain.wallet.ui.main.UserCenterFragment;

public class MainActivity extends BaseActivity {

    private long mExitTime = 0;



    FragmentTabHost tabhost;


    private int[] intImageViewArray = new int[]{R.drawable.tab_home_bg,R.drawable.tab_info_bg, R.drawable.tab_usercenter_bg};
    private Class[] fragmentArray = new Class[]{HomeFragment.class, InformationFragment.class, UserCenterFragment.class};
    private int[] nameArray = new int[]{R.string.tab_home,R.string.tab_info,R.string.tab_user_center};




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
    protected void onCreate(Bundle savedInstanceState) {
        setSystemBar(false);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        try{
            setContentView(R.layout.activity_main);

            tabhost = (FragmentTabHost) findViewById(android.R.id.tabhost);

        }catch (Throwable e){
            Logger.e(e,TAG);
        }
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        try{
            initFragmentTabHost();
            refreshData();

        }catch (Throwable e){
            Logger.e(e,TAG);
        }
    }

    private void refreshData(){
        try{



        }catch (Throwable e){
            Logger.e(e,TAG);
        }
    }

    private void initFragmentTabHost(){
        try{
            FragmentManager fragmentManager = getSupportFragmentManager();

            // 得到fragment的个数
            int count = intImageViewArray.length;
            // 实例化TabHost对象，得到TabHost
            tabhost.setup(this, fragmentManager, R.id.realtabcontent);
            for (int i = 0; i < count; i++) {
                // 将Tab按钮添加进Tab选项卡中
                View view = getTabItemView(i);
                tabhost.addTab(tabhost.newTabSpec(String.valueOf(i)).setIndicator(view), fragmentArray[i], null);
            }

            tabhost.getTabWidget().setDividerDrawable(getResources().getDrawable(R.color.white));
            tabhost.setCurrentTab(0);

            tabhost.getTabWidget().getChildTabViewAt(0).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tabhost.setCurrentTab(0);
                }
            });

            tabhost.getTabWidget().getChildTabViewAt(1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tabhost.setCurrentTab(1);
                }
            });

            tabhost.getTabWidget().getChildTabViewAt(2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tabhost.setCurrentTab(2);
                }
            });


        }catch (Throwable e){
            Logger.e(TAG,e);
        }
    }

    private View getTabItemView(int i) {
        try{
            View view = LayoutInflater.from(this).inflate(R.layout.layout_tab_item, null);
            ImageView mImageView = (ImageView) view.findViewById(R.id.icon);
            TextView mTextView = view.findViewById(R.id.name);

            mImageView.setImageResource(intImageViewArray[i]);
            mTextView.setText(nameArray[i]);
            return view;
        }catch (Throwable e){
            Logger.e(TAG,e);
        }
        return null;

    }


    @Override
    public int getActivityMenuRes() {
        return 0;
    }

    @Override
    public int getHomeAsUpIndicatorIcon() {
        return 0;
    }

    @Override
    public int getActivityTitleContent() {
        return 0;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if(keyCode == KeyEvent.KEYCODE_BACK){
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                ToastUtil.showShortToast(getString(R.string.exit_app));
                mExitTime = System.currentTimeMillis();
            } else {
                exitApp();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    void exitApp(){
        try{
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }catch (Throwable e){
            Logger.e(e,"");
        }
    }



}
