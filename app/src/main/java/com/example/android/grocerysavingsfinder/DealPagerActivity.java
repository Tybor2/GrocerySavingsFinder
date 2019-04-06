package com.example.android.grocerysavingsfinder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

public class DealPagerActivity extends AppCompatActivity {

    private static final String EXTRA_DEAL_ID =
            "com.example.android.grocerysavingsfinder.deal_id";

    private ViewPager mViewPager;
    private List<Deal> mDeals;



    public static Intent newIntent(Context packageContext, UUID dealId) {
        Intent intent = new Intent(packageContext, DealPagerActivity.class);
        intent.putExtra(EXTRA_DEAL_ID, dealId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal_pager);

        UUID dealId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_DEAL_ID);

        mViewPager = (ViewPager) findViewById(R.id.deal_view_pager);

        mDeals = DealCollection.get(this).getDeals();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Deal deal = mDeals.get(position);
                //Deal deal = mDeals.get(0);
                return DealFragment.newInstance(deal.getId());
            }

            @Override
            public int getCount() {
                return mDeals.size();
            }
        });

        //Pulls up correct deal on click
        for(int i = 0; i < mDeals.size(); i++) {
            if(mDeals.get(i).getId().equals(dealId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}
