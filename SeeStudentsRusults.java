package com.example.petro.newtesting;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.petro.newtesting.MainActivity.COCO;
import static com.example.petro.newtesting.MainActivity.answDataBase;
import static com.example.petro.newtesting.MainActivity.myDataBase;

public class SeeStudentsRusults extends AppCompatActivity {
    private ArrayList<HashMap<String,Object>> allAnswears,relevanceAnswears;
    private SimpleAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching);
        getSupportActionBar().hide();

        EditText request = (EditText) findViewById(R.id.request);
        request.setHint("Ученик");
        ListView listView = (ListView) findViewById(R.id.listView);

        Intent generalIntent = getIntent();
        int testPosition = generalIntent.getIntExtra("Test position",-1);

        Log.d(COCO,"test position = "+testPosition);
        Cursor testCursor = myDataBase.getReadableDatabase().rawQuery("select * from tests where id = " + testPosition, null);
        Cursor answearCursor = answDataBase.getReadableDatabase().rawQuery("select * from answears where test = " + testPosition, null);
        testCursor.moveToFirst();

        allAnswears = new ArrayList<>();
        relevanceAnswears = new ArrayList<>();

        if (answearCursor.moveToFirst()) {
            do {
                HashMap<String,Object> onceAnswear = new HashMap<>();
                onceAnswear.put("id",answearCursor.getInt(0));
                onceAnswear.put("student",answearCursor.getString(2));
                onceAnswear.put("date",answearCursor.getString(3));
                onceAnswear.put("mark",answearCursor.getInt(4));
                //onceAnswear.put("scores",answearCursor.getInt(...));
                allAnswears.add(0,onceAnswear);
            } while (answearCursor.moveToNext());
        }
        int[] xmlId = {R.id.item1, R.id.item2, R.id.item3};
        String[] keys = {"student", "date", /*"scores",*/ "mark"};
        adapter = new SimpleAdapter(this, relevanceAnswears,
                R.layout.my_tests_item, keys, xmlId);
        listView.setAdapter(adapter);
        request.addTextChangedListener(new MyWatcher());
        request.setText("");
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(SeeStudentsRusults.this,SeeResultActivity.class);
                intent.putExtra("id", (int) relevanceAnswears.get(position).get("id"));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        finish();
        return super.onContextItemSelected(item);
    }

    class MyWatcher implements TextWatcher {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            relevanceAnswears.clear();
            for (HashMap<String,Object> test:allAnswears) {
                if (test.get("student").toString().toLowerCase().contains(s.toString().toLowerCase())) {
                    relevanceAnswears.add(test);
                    Log.d(COCO,"2");
                }
            }
            adapter.notifyDataSetChanged();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void afterTextChanged(Editable s) {}
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
