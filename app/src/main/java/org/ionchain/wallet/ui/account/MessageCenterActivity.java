package org.ionchain.wallet.ui.account;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import org.ionchain.wallet.R;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;

import java.util.ArrayList;
import java.util.List;

public class MessageCenterActivity extends AbsBaseActivity {


    private ImageView back;
    private ListView listview;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-09-26 17:05:28 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        back = (ImageView)findViewById( R.id.back );
        listview = (ListView)findViewById( R.id.listview );
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
    protected void initData() {

    }


    @Override
    protected void initView() {
        findViews();
        mImmersionBar.titleBar(R.id.header).statusBarDarkFont(true).execute();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_message_center;
    }

}
