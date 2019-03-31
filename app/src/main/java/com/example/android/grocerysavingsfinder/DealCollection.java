package com.example.android.grocerysavingsfinder;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DealCollection {
    private static DealCollection sDealCollection;

    private List<Deal> mDeals;

    public static DealCollection get(Context context) {
        if(sDealCollection == null)
            sDealCollection = new DealCollection(context);

        return sDealCollection;
    }

    private DealCollection(Context context){
        mDeals = new ArrayList<>();
        for(int i = 0; i < 100; i++){
            Deal deal = new Deal();
            deal.setItem("Deal #" + i);
            deal.setDeal("BOGO");
            deal.setStore("Random Store");
            deal.setNotes("Gotta use your card");
            mDeals.add(deal);
        }
    }

    public List<Deal> getDeals() {
        return mDeals;
    }

    public Deal getDeal(UUID id) {
        for(Deal deal: mDeals) {
            if(deal.getId().equals(id))
                return deal;

        }

        return null;
    }
}
