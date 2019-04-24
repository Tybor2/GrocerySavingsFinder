package com.example.android.grocerysavingsfinder.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.grocerysavingsfinder.database.DealDbSchema.DealTable;

public class DealCollectionHelper extends SQLiteOpenHelper {
    private static final int VERSION = 2;
    private static final String DATABASE_NAME = "dealcollection.db";

    public DealCollectionHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + DealTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                DealTable.Cols.UUID + ", " +
                DealTable.Cols.ITEM + ", " +
                DealTable.Cols.DEAL + ", " +
                DealTable.Cols.EXPIRE + ", " +
                DealTable.Cols.STORE + ", " +
                DealTable.Cols.NOTES + "," +
                DealTable.Cols.IMAGE + ")"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("DealCollectionHelper", "Updating table from " + oldVersion + " to " + newVersion);
        if(oldVersion < 2) {
            db.execSQL("DROP TABLE Deals");

        }
        onCreate(db);
    }
}
