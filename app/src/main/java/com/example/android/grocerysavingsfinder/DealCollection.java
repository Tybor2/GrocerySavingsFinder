package com.example.android.grocerysavingsfinder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.Tag;
import android.util.Log;

import com.example.android.grocerysavingsfinder.database.DealCollectionHelper;
import com.example.android.grocerysavingsfinder.database.DealCursorWrapper;
import com.example.android.grocerysavingsfinder.database.DealDbSchema.DealTable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
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

    public List<Deal> searchDeals(String[] queryStrings) {
        //List<Deal> deals = new ArrayList<>();
        HashSet<Deal> deals = new HashSet<Deal>();
        for(int i = 0; i < 4; i++) {
            String queryString = "%" + queryStrings[i] + "%";
            DealCursorWrapper cursor = queryDeals(
                    DealTable.Cols.ITEM  + " LIKE ? OR " +
                            DealTable.Cols.STORE + " Like ? OR " +
                            DealTable.Cols.DEAL + " LIKE ?",
                    new String[] {queryString, queryString, queryString}
            );
            //cursor = mDatabase.queryDeals("SELECT " + DealTable.Cols.ITEM + " FROM Deals ORDER BY " + DealTable.Cols.ITEM);

            try {
                cursor.moveToFirst();

                while(!cursor.isAfterLast()) {
                    boolean found = false;
                    for (Deal d: deals) {
                        Deal deal = cursor.getDeal();
                        //Log.e("DealCollection", "Checking deal " + d.getItem() + " with " + deal.getItem());

                        if(d.getItem().equals(deal.getItem()) && d.getDeal().equals(deal.getDeal()) && d.getStore().equals(deal.getStore())){
                            //cursor.moveToNext();
                            Log.e("DealCOllection", "FOund");
                            found = true;
                            break;
                        }

                    }
                    if(!found)
                        deals.add(cursor.getDeal());
                    cursor.moveToNext();
                }
            } finally {
                cursor.close();
            }
        }

        return new ArrayList<>(deals);


    }


    public void refreshItems(Context context){
        String json = null;
        int size = 0;
        String TABLE_NAME = "Deals";
        Context con = context.getApplicationContext();
        //context.deleteDatabase("Deals");
        mDatabase = new DealCollectionHelper(con)
                .getWritableDatabase();

        mDatabase.execSQL("delete from "+ TABLE_NAME);
        for (int j = 0; j < 3; j++) {
            try {
                InputStream is;
                if(j == 0)
                    is = context.getAssets().open("dataHT.json");
                else if(j == 1)
                    is = context.getAssets().open("dataPublix.json");
                else
                    is = context.getAssets().open("dataWalmart.json");
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
                    deal.setItem(jsonItem.getString("item").trim());
                    deal.setDeal(jsonItem.getString("deal").trim());
                    deal.setExpires(jsonItem.getString("expires").trim());
                    deal.setStore(jsonItem.getString("store").trim());
                    deal.setNotes(jsonItem.getString("notes").trim());
                    deal.setImage(jsonItem.getString("imageURL"));
                    addDeal(deal);
                }

            } catch (JSONException je) {
                je.printStackTrace();
            }


        }

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
                DealTable.Cols.ITEM
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
        values.put(DealTable.Cols.IMAGE, deal.getImage());

        return values;
    }
}
