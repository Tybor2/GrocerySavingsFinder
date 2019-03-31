package com.example.android.grocerysavingsfinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class DealListFragment extends Fragment {
    private RecyclerView mDealRecyclerView;
    private DealAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deal_list, container, false);

        mDealRecyclerView = (RecyclerView) view
                .findViewById(R.id.deal_recycler_view);
        mDealRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        DealCollection dealCollection = DealCollection.get(getActivity());
        List<Deal> deals = dealCollection.getDeal();
        if(mAdapter == null) {
            mAdapter = new DealAdapter(deals);
            mDealRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private class DealHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private Deal mDeal;
        private TextView mNameTextView;
        private TextView mDealTextView;

        public DealHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_deal, parent, false));
            itemView.setOnClickListener(this);

            mNameTextView = (TextView) itemView.findViewById(R.id.item_name);
            mDealTextView = (TextView) itemView.findViewById(R.id.item_deal);
        }

        public void bind(Deal deal) {
            mDeal = deal;
            mNameTextView.setText(mDeal.getItem());
            mDealTextView.setText(mDeal.getDeal());
        }

        @Override
        public void onClick(View v) {
            Intent intent = DealActivity.newIntent(getActivity(), mDeal.getId());
            startActivity(intent);
        }
    }

    private class DealAdapter extends RecyclerView.Adapter<DealHolder> {
        private List<Deal> mDeals;

        public DealAdapter(List<Deal> deals) {
            mDeals = deals;
        }

        @NonNull
        @Override
        public DealHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new DealHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull DealHolder holder, int position) {
            Deal deal = mDeals.get(position);
            holder.bind(deal);
        }

        @Override
        public int getItemCount() {
            return mDeals.size();
        }
    }
}
