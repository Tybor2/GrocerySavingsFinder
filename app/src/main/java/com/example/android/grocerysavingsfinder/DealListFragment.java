package com.example.android.grocerysavingsfinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.SearchView;
import android.widget.TextView;

import com.example.android.grocerysavingsfinder.database.DealCursorWrapper;

import java.util.ArrayList;
import java.util.List;

public class DealListFragment extends Fragment {
    private RecyclerView mDealRecyclerView;
    private DealAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_deal_list, menu);

        final MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                QueryPreferences.setStoredQuery(getActivity(), query);
                updateUI(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = QueryPreferences.getStoredQuery(getActivity());
                searchView.setQuery(query, false);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /**
        switch (item.getItemId()) {
            case R.id.refresh:
                Deal deal = new Deal();
                DealCollection.get(getActivity()).addDeal(deal);
                Intent intent = DealPagerActivity
                        .newIntent(getActivity(), deal.getId());
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }**/


        //DealCollection.get(getActivity()).refreshItems(getContext());

            //dealCollection.refreshItems(getContext());
        switch ( (item.getItemId())) {
            /**case R.id.menu_item_search:
                updateUI(QueryPreferences.getStoredQuery(getActivity()));
                return true;**/
            case R.id.menu_item_clear:
                QueryPreferences.setStoredQuery(getActivity(), null);
                updateUI();
                return true;
            case R.id.refresh:
                updateUI();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    private void updateUI() {

        DealCollection dealCollection = DealCollection.get(getActivity());
        //List<Deal> deals = dealCollection.getDeals();
        String query = QueryPreferences.getStoredQuery(getActivity());
        List<Deal> deals;
        if(query == null)
            deals = dealCollection.getDeals();
        else
            deals = dealCollection.searchDeals(query);
        if(deals.size() == 0) {
            dealCollection.refreshItems(getContext());
        }
        if(mAdapter == null) {
            mAdapter = new DealAdapter(deals);
            mDealRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setDeals(deals);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void updateUI(String searchString) {
        DealCollection dealCollection = DealCollection.get(getActivity());
        List<Deal> deals = dealCollection.searchDeals(searchString);
        if(mAdapter == null) {
            mAdapter = new DealAdapter(deals);
            mDealRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setDeals(deals);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class DealHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private Deal mDeal;
        private TextView mNameTextView;
        private TextView mDealTextView;
        private TextView mExpireTextView;
        private TextView mStoreTextView;
        private TextView mNotesTextView;

        public DealHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_deal, parent, false));
            itemView.setOnClickListener(this);

            mNameTextView = (TextView) itemView.findViewById(R.id.item_name);
            mDealTextView = (TextView) itemView.findViewById(R.id.item_deal);
            mExpireTextView = (TextView) itemView.findViewById(R.id.deal_expires);
            mStoreTextView = (TextView) itemView.findViewById(R.id.deal_store);
            mNotesTextView = (TextView) itemView.findViewById(R.id.deal_notes);
        }

        public void bind(Deal deal) {
            mDeal = deal;
            mNameTextView.setText(mDeal.getItem());
            mDealTextView.setText(mDeal.getDeal());
            /**mExpireTextView.setText(mDeal.getExpires());
            mStoreTextView.setText(mDeal.getStore());
            mNotesTextView.setText(mDeal.getNotes());**/
        }

        @Override
        public void onClick(View v) {
            Intent intent = DealPagerActivity.newIntent(getActivity(), mDeal.getId());
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

        public void setDeals(List<Deal> deals) {
            mDeals = deals;
        }
    }
}
