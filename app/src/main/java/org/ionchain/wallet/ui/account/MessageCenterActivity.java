package org.ionchain.wallet.ui.account;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.fast.lib.logger.Logger;
import com.fast.lib.utils.ToastUtil;
import com.fast.lib.widget.recyclerview.LibraryRecyclerView;

import org.ionchain.wallet.R;
import org.ionchain.wallet.adpter.MessageCenterAdapter;
import org.ionchain.wallet.comm.api.resphonse.ResponseModel;
import org.ionchain.wallet.ui.comm.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MessageCenterActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    LibraryRecyclerView recyclerView;

    private MessageCenterAdapter messageCenterAdapter;

    @Override
    public void handleMessage(int what, Object obj) {
        super.handleMessage(what, obj);
        try {

            switch (what) {
                case R.id.navigationBack:
                    finish();
                    break;
                case 0:
                    dismissProgressDialog();
                    if (obj == null)
                        return;

                    ResponseModel<String> responseModel = (ResponseModel) obj;
                    if (!verifyStatus(responseModel)) {
                        ToastUtil.showShortToast(responseModel.getMsg());
                        return;
                    }


                    break;
            }

        } catch (Throwable e) {
            Logger.e(e, TAG);
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_message_center);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        messageCenterAdapter = new MessageCenterAdapter(recyclerView);

        messageCenterAdapter.addNewData(loadData());
        recyclerView.setAdapter(messageCenterAdapter);
    }

    private List<String> loadData() {
        List<String> stringList = new ArrayList<>();
        stringList.add("消息中心数据");
        stringList.add("消息中心数据");
        stringList.add("消息中心数据");
        stringList.add("消息中心数据");
        stringList.add("消息中心数据");
        stringList.add("消息中心数据");
        stringList.add("消息中心数据");
        stringList.add("消息中心数据");
        stringList.add("消息中心数据");
        return stringList;
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

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
        return R.string.activity_message_center;
    }

}
