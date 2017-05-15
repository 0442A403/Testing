package com.example.petro.newtesting;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by petro on 09.04.2017.
 */

public class AnswearsDataBase extends SQLiteOpenHelper {
    public AnswearsDataBase(Context context) {
        super(context, "data1.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table answears(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "test integer, student text, date text, mark integer, ans text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
