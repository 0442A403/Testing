package com.example.petro.newtesting;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;

import org.json.JSONArray;

import static com.example.petro.newtesting.MainActivity.answDataBase;
import static com.example.petro.newtesting.MainActivity.myDataBase;

public class MarkActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Intent intent = getIntent();
        final int id = intent.getIntExtra("id", -1);
        ((TextView) findViewById(R.id.state)).setText(String.valueOf(intent.getIntExtra("mark", -1)));
        findViewById(R.id.backToMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (intent.getIntExtra("Show wrong", 0) == 1)
            findViewById(R.id.seeResults).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MarkActivity.this, SeeResultActivity.class);
                    intent.putExtra("id", id);
                    Log.d("answer id now now", String.valueOf(intent.getIntExtra("id", -1)));
                    startActivity(intent);
                }
            });
        else
            findViewById(R.id.seeResults).setVisibility(View.INVISIBLE);
    }
}
