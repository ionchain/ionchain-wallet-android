package org.ionchain.wallet.mvp.view.fragment.txrecord;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.ionchain.wallet.mvp.view.base.AbsBaseViewPagerFragment;

import java.util.ArrayList;
import java.util.List;

public class TxRecordPagerAdapter extends FragmentPagerAdapter {
    private List<AbsBaseViewPagerFragment> mFragmentListTxRecord = new ArrayList<>();
    List<String> titles = new ArrayList<>();
    public TxRecordPagerAdapter(@NonNull FragmentManager fm, List<AbsBaseViewPagerFragment> list) {
        super(fm);
        mFragmentListTxRecord = list;
        titles.add("全部");
        titles.add("转出");
        titles.add("转入");

    }



    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragmentListTxRecord.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentListTxRecord.size();
    }
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        return titles.get(position);
    }
}
