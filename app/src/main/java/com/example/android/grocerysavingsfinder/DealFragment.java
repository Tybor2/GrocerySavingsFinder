package com.example.android.grocerysavingsfinder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.UUID;

public class DealFragment extends Fragment {
    private Deal mDeal;
    private TextView mNameField;
    private TextView mDealField;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID dealId = (UUID) getActivity().getIntent()
                .getSerializableExtra(DealActivity.EXTRA_DEAL_ID);
        mDeal = DealCollection.get(getActivity()).getDeal(dealId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_deal, container, false);

        mNameField = (TextView) v.findViewById(R.id.name_item);
        mNameField.setText(mDeal.getItem());

        mDealField = (TextView) v.findViewById(R.id.deal_name);
        mDealField.setText(mDeal.getDeal());
        return v;
    }
}
