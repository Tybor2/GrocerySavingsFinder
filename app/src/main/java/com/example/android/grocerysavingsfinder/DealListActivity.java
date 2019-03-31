package com.example.android.grocerysavingsfinder;

import android.support.v4.app.Fragment;

public class DealListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new DealListFragment();
    }
}
