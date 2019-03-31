package com.example.android.grocerysavingsfinder;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.UUID;

public class DealActivity extends SingleFragmentActivity {

    public static final String EXTRA_DEAL_ID =
            "com.examplle.android.grocerysavingsfinder.deal_id";

    public static Intent newIntent(Context packageContext, UUID dealId){
        Intent intent = new Intent(packageContext, DealActivity.class);
        intent.putExtra(EXTRA_DEAL_ID, dealId);
        return intent;
    }
    @Override
    protected Fragment createFragment() {
        return new DealFragment();
    }
}
