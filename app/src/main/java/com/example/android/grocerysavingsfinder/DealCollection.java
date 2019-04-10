package com.example.android.grocerysavingsfinder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.android.grocerysavingsfinder.database.DealCollectionHelper;
import com.example.android.grocerysavingsfinder.database.DealCursorWrapper;
import com.example.android.grocerysavingsfinder.database.DealDbSchema.DealTable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DealCollection {
    private static DealCollection sDealCollection;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static DealCollection get(Context context) {
        if(sDealCollection == null)
            sDealCollection = new DealCollection(context);

        return sDealCollection;
    }

    private DealCollection(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new DealCollectionHelper(mContext)
                .getWritableDatabase();

    }

    public void addDeal(Deal d) {
        ContentValues values = getContentValues(d);

        mDatabase.insert(DealTable.NAME, null, values);
    }

    public List<Deal> getDeals() {
        List<Deal> deals = new ArrayList<>();

        DealCursorWrapper cursor = queryDeals(null, null);

        try {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                deals.add(cursor.getDeal());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return deals;
    }

    public Deal getDeal(UUID id) {
        DealCursorWrapper cursor = queryDeals(
                DealTable.Cols.UUID + " = ?",
                new String[] {id.toString()}
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getDeal();
        } finally {
            cursor.close();
        }
    }

    public List<Deal> searchDeals(String queryString) {
        queryString = "%" + queryString + "%";
        List<Deal> deals = new ArrayList<>();
        String[] columns = new String[]{ "ITEM", "STORE", "DEAL"};
        //Cursor cursor = mDatabase.query("Deals", columns, "some_col like '%" + queryString + "%'", null, null, null, null);
        DealCursorWrapper cursor = queryDeals(
                DealTable.Cols.ITEM  + " LIKE ? OR " +
                        DealTable.Cols.STORE + " Like ? OR " +
                        DealTable.Cols.DEAL + " LIKE ?",
                new String[] {queryString, queryString, queryString}
        );

        try {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                deals.add(cursor.getDeal());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return deals;


    }

    public void refreshItems(Context context){
        String json = null;
        int size = 0;
        for (int j = 0; j < 2; j++) {
            try {
                InputStream is;
                if(j == 0)
                    is = context.getAssets().open("dataHT.json");
                else
                    is = context.getAssets().open("dataPublix.json");
                size = is.available();

                byte[] buffer = new byte[size];

                is.read(buffer);

                is.close();

                json = new String(buffer, "UTF-8");


            } catch (IOException ex) {
                ex.printStackTrace();

            }

            try {
                JSONObject jsonBody = new JSONObject(json);

                for(int i = 0; i < size; i++) {
                    Deal deal = new Deal();
                    JSONObject jsonItem = (jsonBody.getJSONObject(String.valueOf(i)));
                    deal.setItem(jsonItem.getString("item"));
                    deal.setDeal(jsonItem.getString("deal"));
                    deal.setExpires(jsonItem.getString("expires"));
                    deal.setStore(jsonItem.getString("store"));
                    deal.setNotes(jsonItem.getString("notes"));
                    addDeal(deal);
                }

            } catch (JSONException je) {
                je.printStackTrace();
            }
        }
        //return null;
    }



    public void updateDeal(Deal deal) {
        String uuidString = deal.getId().toString();
        ContentValues values = getContentValues(deal);

        mDatabase.update(DealTable.NAME, values,
                DealTable.Cols.UUID + " = ?",
                new String[] {uuidString});
    }

    private DealCursorWrapper queryDeals(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                DealTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new DealCursorWrapper(cursor);
    }

    private DealCursorWrapper queryDeals(String[] columns, String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                DealTable.NAME,
                columns,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new DealCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(Deal deal) {
        ContentValues values = new ContentValues();
        values.put(DealTable.Cols.UUID, deal.getId().toString());
        values.put(DealTable.Cols.ITEM, deal.getItem());
        values.put(DealTable.Cols.DEAL, deal.getDeal());
        values.put(DealTable.Cols.EXPIRE, deal.getExpires());
        values.put(DealTable.Cols.STORE, deal.getStore());
        values.put(DealTable.Cols.NOTES, deal.getNotes());

        return values;
    }
}
