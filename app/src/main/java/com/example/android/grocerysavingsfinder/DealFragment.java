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

    private static final String ARG_DEAL_ID = "deal_id";

    private Deal mDeal;
    private TextView mNameField;
    private TextView mDealField;
    private TextView mExpireTextView;
    private TextView mStoreTextView;
    private TextView mNotesTextView;

    public static DealFragment newInstance(UUID dealId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DEAL_ID, dealId);

        DealFragment fragment = new DealFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID dealId = (UUID) getArguments().getSerializable(ARG_DEAL_ID);
        mDeal = DealCollection.get(getActivity()).getDeal(dealId);
    }

    @Override
    public void onPause() {
        super.onPause();

        DealCollection.get(getActivity()).updateDeal(mDeal);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_deal, container, false);

        mNameField = (TextView) v.findViewById(R.id.name_item);
        mNameField.setText(mDeal.getItem());

        mDealField = (TextView) v.findViewById(R.id.deal_name);
        mDealField.setText(mDeal.getDeal());

        mExpireTextView = (TextView) v.findViewById(R.id.deal_expires);
        mStoreTextView = (TextView) v.findViewById(R.id.deal_store);
        mNotesTextView = (TextView) v.findViewById(R.id.deal_notes);

        mExpireTextView.setText(mDeal.getExpires());
        mStoreTextView.setText(mDeal.getStore());
        mNotesTextView.setText(mDeal.getNotes());
        return v;
    }
}
