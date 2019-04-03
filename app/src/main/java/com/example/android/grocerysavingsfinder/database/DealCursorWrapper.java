package com.example.android.grocerysavingsfinder.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.android.grocerysavingsfinder.Deal;
import com.example.android.grocerysavingsfinder.database.DealDbSchema.DealTable;

import java.util.Date;
import java.util.UUID;

public class DealCursorWrapper extends CursorWrapper {
    public DealCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Deal getDeal() {
        String uuidString = getString(getColumnIndex(DealTable.Cols.UUID));
        String item = getString(getColumnIndex(DealTable.Cols.ITEM));
        String deal = getString(getColumnIndex(DealTable.Cols.DEAL));
        String expires = getString(getColumnIndex(DealTable.Cols.EXPIRE));
        String store = getString(getColumnIndex(DealTable.Cols.STORE));
        String notes = getString(getColumnIndex(DealTable.Cols.NOTES));

        Deal d = new Deal(UUID.fromString(uuidString));
        d.setItem(item);
        d.setDeal(deal);
        d.setExpires(expires);
        d.setStore(store);
        d.setNotes(notes);

        return d;
    }
}
