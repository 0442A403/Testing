package com.example.petro.newtesting;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by petro on 08.04.2017.
 */


public class TestsDataBase extends SQLiteOpenHelper {
    public TestsDataBase(Context context) {
        super(context, "data.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table tests(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name text, autor text, showWrong integer, five integer, four integer," +
                "three integer, questions text, options text, rightAnswears text," +
                "isRadio text, scores text, date text, anon int, photos text, timer int," +
                "time integer, enabled int);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
