package com.example.android.grocerysavingsfinder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;


public class DealListFragment extends Fragment {
    private RecyclerView mDealRecyclerView;
    private DealAdapter mAdapter;
    private SwipeRefreshLayout swipeContainer;

    private static final int REQUEST_CODE = 0;
    private static final String TAG = "DealListFragment";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //new FetchItemsTask().execute();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deal_list, container, false);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                DealCollection.get(getContext()).refreshItems(getContext());
                QueryPreferences.setStoredQuery(getActivity(), null);
                updateUI();
                swipeContainer.setRefreshing(false);
            }
        });
        mDealRecyclerView = (RecyclerView) view
                .findViewById(R.id.deal_recycler_view);
        mDealRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        mDealRecyclerView.addItemDecoration(itemDecoration);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                BarcodeNumFragment dialog = new BarcodeNumFragment()
                        .newInstance(0);
                dialog.setTargetFragment(DealListFragment.this, REQUEST_CODE);
                dialog.show(manager, "Enter Barcode");
            }
        });


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
                searchView.onActionViewCollapsed();
                updateUI(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                /**if(searchView.getQuery().length() == 0){
                    updateUI();
                }**/
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
        searchView.setQueryHint("Search through items, deals, and stores");

    }

    public void onBackPressed() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch ( (item.getItemId())) {
            case R.id.menu_item_clear:
                QueryPreferences.setStoredQuery(getActivity(), null);
                updateUI();
                return true;
            case R.id.refresh:
                DealCollection dealCollection = DealCollection.get(getActivity());
                dealCollection.refreshItems(getActivity());
                QueryPreferences.setStoredQuery(getActivity(), null);
                updateUI();
                return true;
            case R.id.barcode_search:
                FragmentManager manager = getFragmentManager();
                BarcodeNumFragment dialog = new BarcodeNumFragment()
                        .newInstance(0);
                dialog.setTargetFragment(DealListFragment.this, REQUEST_CODE);
                dialog.show(manager, "Enter Barcode");
                return true;
            case R.id.filter_all:
                QueryPreferences.setStoredQuery(getActivity(), null);
                updateUI();
                return true;
            case R.id.filter_ht:
                updateUI("Harris Teeter");
                return true;
            case R.id.filter_publix:
                updateUI("Publix");
                return true;
            case R.id.filter_walmart:
                updateUI("Walmart");
                QueryPreferences.setStoredQuery(getActivity(), "Walmart");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    private void updateUI() {
        DealCollection dealCollection = DealCollection.get(getActivity());
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

    private void updateUI(String[] searchString) {
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
        private ImageView mImageView;
        private TextView mExpireTextView;
        private TextView mStoreTextView;
        private TextView mNotesTextView;

        public DealHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_deal, parent, false));
            itemView.setOnClickListener(this);

            mNameTextView = (TextView) itemView.findViewById(R.id.item_name);
            mDealTextView = (TextView) itemView.findViewById(R.id.item_deal);
            mImageView = (ImageView) itemView.findViewById(R.id.deal_image);
            mExpireTextView = (TextView) itemView.findViewById(R.id.deal_expires);
            mStoreTextView = (TextView) itemView.findViewById(R.id.deal_store);
            mNotesTextView = (TextView) itemView.findViewById(R.id.deal_notes);
        }

        public void bind(Deal deal) {
            mDeal = deal;
            mNameTextView.setText(mDeal.getItem());
            mDealTextView.setText(mDeal.getDeal());
            mStoreTextView.setText(mDeal.getStore());

            Picasso.get().load(mDeal.getImage())
                    .placeholder(R.mipmap.ic_app_icon)
                    .into(mImageView);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK) {
            return;
        }

        if(requestCode == REQUEST_CODE) {
            String code = data.getSerializableExtra(BarcodeNumFragment.EXTRA_CODE).toString();
            new FetchItemsTask().execute(code);
            //updateUI("buffalo");
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class FetchItemsTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... code) {
            return new BarcodeCatcher().fetchItems(code[0]);
        }

        @Override
        protected void onPostExecute(String[] s) {
            if(s == null){
                try {
                    Toast.makeText(getActivity(), "Barcode for " + s[0] + " or " + s[1] + " or " + s[2] + " not found",
                            Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Not a Valid Barcode",
                            Toast.LENGTH_SHORT).show();
                }
            }else {
                Log.i(TAG, "This is the returned value: " + s[0] + " and " + s[1] + " and " + s[2] + " and " + s[3]);
                //`QueryPreferences.setStoredQuery(getActivity(), s[0]);
                updateUI(s);
            }
        }
    }
}
