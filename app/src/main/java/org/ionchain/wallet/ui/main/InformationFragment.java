package org.ionchain.wallet.ui.main;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.fast.lib.immersionbar.ImmersionBar;
import com.fast.lib.logger.Logger;
import com.fast.lib.utils.ToastUtil;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import org.ionchain.wallet.R;
import org.ionchain.wallet.adpter.InformationAdapter;
import org.ionchain.wallet.comm.api.ApiArticle;
import org.ionchain.wallet.comm.api.model.Article;
import org.ionchain.wallet.comm.api.request.ViewParm;
import org.ionchain.wallet.comm.api.resphonse.ResponseModel;
import org.ionchain.wallet.comm.constants.Comm;
import org.ionchain.wallet.comm.constants.Global;
import org.ionchain.wallet.ui.comm.BaseFragment;
import org.ionchain.wallet.ui.login.LoginActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.bingoogolapple.baseadapter.BGAOnItemChildClickListener;
import cn.bingoogolapple.baseadapter.BGAOnRVItemClickListener;

public class InformationFragment extends BaseFragment implements OnRefreshLoadmoreListener, BGAOnItemChildClickListener, BGAOnRVItemClickListener {

//    @BindView(R.id.infoTv)
//    TextView infoTv;

    @BindView(R.id.srl)
    SmartRefreshLayout srl;

    @BindView(R.id.dataRv)
    RecyclerView dataRv;

    private int pageNo = 1;
    private int pageSize = 10;

    private InformationAdapter informationAdapter;
    private List<Article> articleList = new ArrayList<>();
    private int itemMark;

    @Override
    public void handleMessage(int what, Object obj) {
        super.handleMessage(what, obj);
        try {

            switch (what) {

                case Comm.article_refresh_type:
                    srl.autoRefresh();
                    break;

                case 100:
                    srl.finishLoadmore();
                    srl.finishRefresh();
                    if (obj == null)
                        return;
                    ResponseModel<List<Article>> responseModel2 = (ResponseModel) obj;
                    if (!verifyStatus(responseModel2)) {
                        ToastUtil.showShortToast(responseModel2.getMsg());
                        return;
                    }

                    if (pageNo == 1) articleList.clear();

                    articleList.addAll(responseModel2.data);
                    informationAdapter.setData(articleList);

                    if (responseModel2.data.size() < pageSize) srl.setEnableLoadmore(false);

                    break;

                case 101:
                    srl.finishLoadmore();
                    srl.finishRefresh();
                    if (obj == null)
                        return;
                    ResponseModel<List<Article>> responseModel3 = (ResponseModel) obj;
                    if (!verifyStatus(responseModel3)) {
                        ToastUtil.showShortToast(responseModel3.getMsg());
                        return;
                    }

                    articleList.get(itemMark).setIsPraise(1);
                    articleList.get(itemMark).setPraiseCount(articleList.get(itemMark).getPraiseCount() + 1);

                    informationAdapter.setData(articleList);

                    break;
                case 102:
                    srl.finishLoadmore();
                    srl.finishRefresh();
                    if (obj == null)
                        return;
                    ResponseModel<List<Article>> responseModel4 = (ResponseModel) obj;
                    if (!verifyStatus(responseModel4)) {
                        ToastUtil.showShortToast(responseModel4.getMsg());
                        return;
                    }

                    articleList.get(itemMark).setViewCount(articleList.get(itemMark).getViewCount() + 1);
                    informationAdapter.setData(articleList);

                    if (articleList.get(itemMark).getUrl() != null) {
                        //有url的话 跳转
                    }

                    break;
            }

        } catch (Throwable e) {
            Logger.e(e, TAG);
        }
    }

    @Override
    protected void immersionInit() {
        ImmersionBar.with(this)
                .statusBarDarkFont(false)
                .statusBarColor(R.color.qmui_config_color_blue)
                .navigationBarColor(R.color.black, 0.5f)
                .fitsSystemWindows(true)
                .init();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_information);
        dataRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
    }

    @Override
    protected void setListener() {
        srl.setOnRefreshLoadmoreListener(this);
        informationAdapter.setOnItemChildClickListener(this);
        informationAdapter.setOnRVItemClickListener(this);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

        informationAdapter = new InformationAdapter(dataRv);
        dataRv.setAdapter(informationAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Global.user == null) {
            transfer(LoginActivity.class);
            getActivity().finish();
            return;
        }
        srl.autoRefresh();
    }

    @Override
    protected void onUserVisible() {

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
        return R.string.tab_info;
    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {

        pageNo++;
        ViewParm viewParm = new ViewParm(null, InformationFragment.this, new TypeToken<ResponseModel<List<Article>>>() {
        }.getType(), 100);
        ApiArticle.getArticle(Global.user.getUserId() + "", pageNo + "", pageSize + "", viewParm);

    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {

        pageNo = 1;
        ViewParm viewParm = new ViewParm(null, InformationFragment.this, new TypeToken<ResponseModel<List<Article>>>() {
        }.getType(), 100);
        ApiArticle.getArticle(Global.user.getUserId() + "", pageNo + "", pageSize + "", viewParm);

    }

    @Override
    public void onItemChildClick(ViewGroup parent, View childView, int position) {

        switch (childView.getId()) {
            case R.id.praiseLy:
                if (1 != articleList.get(position).getIsPraise()) {
                    itemMark = position;

                    ViewParm viewParm = new ViewParm(null, InformationFragment.this, new TypeToken<ResponseModel>() {
                    }.getType(), 101);

                    ApiArticle.praiseArticle(Global.user.getUserId() + "", articleList.get(position).getId() + "", viewParm);
                }
                break;
        }

    }

    @Override
    public void onRVItemClick(ViewGroup parent, View itemView, int position) {
        itemMark = position;

        ViewParm viewParm = new ViewParm(null, InformationFragment.this, new TypeToken<ResponseModel>() {
        }.getType(), 102);

        ApiArticle.viewArticle(articleList.get(position).getId() + "", viewParm);

    }
}
