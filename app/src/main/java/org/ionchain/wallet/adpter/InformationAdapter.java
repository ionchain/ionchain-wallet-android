package org.ionchain.wallet.adpter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.fast.lib.glideimageloader.ImageLoadConfig;

import org.ionchain.wallet.R;
import org.ionchain.wallet.comm.api.model.Article;
import org.ionchain.wallet.comm.api.model.Wallet;
import org.ionchain.wallet.config.ImgLoader;

import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper;

public class InformationAdapter extends BGARecyclerViewAdapter<Article>{

    public InformationAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.layout_article_item);
    }

    @Override
    protected void fillData(BGAViewHolderHelper helper, int position, Article model) {

        helper.setText(R.id.articleTitleTv,model.getTitle());
        helper.setText(R.id.praiseCountTv,model.getPraiseCount()+"");
        helper.setText(R.id.viewCountTv,model.getViewCount()+"");
        helper.setText(R.id.createTimeTv,model.getCreateTime());
        helper.setItemChildClickListener(R.id.praiseLy);

        if (1==model.getIsPraise()){
            helper.setImageResource(R.id.praiseImg,R.mipmap.ic_is_praise);
        }else {
            helper.setImageResource(R.id.praiseImg,R.mipmap.ic_praise);
        }
    }
}
