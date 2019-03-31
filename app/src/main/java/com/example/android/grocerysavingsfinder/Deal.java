package com.example.android.grocerysavingsfinder;

import java.util.Date;
import java.util.UUID;

public class Deal {
    private UUID mId;
    private String mItem;
    private String mDeal;
    private Date mExpires;
    private String mStore;
    private String mNotes;

    public Deal() {
        mId = UUID.randomUUID();
        mExpires = new Date();
    }
    public UUID getId() {
        return mId;
    }

    public String getItem() {
        return mItem;
    }

    public void setItem(String item) {
        mItem = item;
    }

    public String getDeal() {
        return mDeal;
    }

    public void setDeal(String deal) {
        mDeal = deal;
    }

    public Date getExpires() {
        return mExpires;
    }

    public void setExpires(Date expires) {
        mExpires = expires;
    }

    public String getStore() {
        return mStore;
    }

    public void setStore(String store) {
        mStore = store;
    }

    public String getNotes() {
        return mNotes;
    }

    public void setNotes(String notes) {
        mNotes = notes;
    }
}
