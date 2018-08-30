package org.ionchain.wallet.adpter;

import android.support.v7.widget.RecyclerView;

import org.ionchain.wallet.R;

import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper;

public class MessageCenterAdapter extends BGARecyclerViewAdapter<String>{

    public MessageCenterAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.item_message_center);
    }

    @Override
    protected void fillData(BGAViewHolderHelper helper, int position, String s) {
        helper.setText(R.id.titleTv,s);
    }
}
