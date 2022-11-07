package com.phc.prs.Adepter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.phc.prs.Fragments.CheckStatusAllFragment;
import com.phc.prs.Fragments.CheckStatusReceiveFragment;
import com.phc.prs.Fragments.CheckStatusRepairFragment;
import com.phc.prs.Fragments.CheckStatusRequestFragment;
import com.phc.prs.R;

public class SectionsPagerCheckStatusAdapter extends FragmentPagerAdapter {
    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3, R.string.tab_text_4};
    private static final int PAGES = TAB_TITLES.length;
    private final Context mContext;
    private CheckStatusAllFragment AllStatusFragment;
    private CheckStatusRequestFragment RequestStatusFragment;
    private CheckStatusReceiveFragment ReceiveStatusFragment;
    private CheckStatusRepairFragment RepairStatusFragment;

    public SectionsPagerCheckStatusAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            if (AllStatusFragment == null) {
                AllStatusFragment = new CheckStatusAllFragment();
            }
            return AllStatusFragment;
        }
        if(position == 1){
            if(RequestStatusFragment == null){
                RequestStatusFragment = new CheckStatusRequestFragment();
            }
            return  RequestStatusFragment;
        }
        if(position == 2){
            if(ReceiveStatusFragment == null){
                ReceiveStatusFragment = new CheckStatusReceiveFragment();
            }
            return  ReceiveStatusFragment;
        }
        if(position == 3){
            if(RepairStatusFragment == null){
                RepairStatusFragment = new CheckStatusRepairFragment();
            }
            return  RepairStatusFragment;
        }
        if (RequestStatusFragment == null) {
            RequestStatusFragment = new CheckStatusRequestFragment();
        }
        return RequestStatusFragment;
    }

    public View getTabView(int position) {
        View _view = LayoutInflater.from(mContext).inflate(R.layout.custom_tab_check_status, null);
        TextView tab_name = (TextView) _view.findViewById(R.id.tab_name);
        tab_name.setText(TAB_TITLES[position]);

        return _view;
    }

    @Override
    public int getCount() {
        return PAGES;
    }
}
