package com.example.petro.newtesting;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import static com.example.petro.newtesting.MainActivity.answDataBase;
import static com.example.petro.newtesting.MainActivity.myDataBase;

/**
 * Created by petro on 09.04.2017.
 */

public class MarkActivity extends AppCompatActivity {
    int answearId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().hide();
        answearId = getIntent().getIntExtra("id",-1);
        Cursor cursor = answDataBase.getReadableDatabase().rawQuery("select * from answears where id = " + answearId, null);
        cursor.moveToFirst();
        ((TextView) findViewById(R.id.state)).setText(String.valueOf(cursor.getInt(cursor.getColumnIndex("mark"))));
        findViewById(R.id.backToMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Cursor showWrongCursor = myDataBase.getReadableDatabase().rawQuery("select * from tests where id = " + cursor.getInt(cursor.getColumnIndex("test")), null);
        showWrongCursor.moveToFirst();
        if (showWrongCursor.getInt(showWrongCursor.getColumnIndex("showWrong"))==1)
            findViewById(R.id.seeResults).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MarkActivity.this, SeeResultActivity.class);
                    intent.putExtra("id", answearId);
                    startActivity(intent);
                }
            });
        else
            findViewById(R.id.seeResults).setVisibility(View.INVISIBLE);
        cursor.close();
        showWrongCursor.close();
    }
}
