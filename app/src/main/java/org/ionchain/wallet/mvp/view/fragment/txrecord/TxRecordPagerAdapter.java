package org.ionchain.wallet.mvp.view.fragment.txrecord;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.ionchain.wallet.mvp.view.base.AbsBaseViewPagerFragment;

import java.util.List;

public class TxRecordPagerAdapter extends FragmentPagerAdapter {
    private List<AbsBaseViewPagerFragment> mFragmentListTxRecord;
    List<String> titles;

    public TxRecordPagerAdapter(@NonNull FragmentManager fm, List<AbsBaseViewPagerFragment> list, List<String> titles) {
        super(fm);
        mFragmentListTxRecord = list;
        this.titles = titles;


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
