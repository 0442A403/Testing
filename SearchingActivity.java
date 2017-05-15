package com.example.petro.newtesting;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.petro.newtesting.MainActivity.DONE;
import static com.example.petro.newtesting.MainActivity.myDataBase;


public class SearchingActivity extends AppCompatActivity {
    private ArrayList<HashMap<String,Object>> allTests,relevanceTests;
    private SimpleAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RelativeLayout layout = new RelativeLayout(this);
        layout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        getLayoutInflater().inflate(R.layout.activity_searching,layout);
//        layout.addView(BALDESHLayout,0);
        setContentView(R.layout.activity_searching);
        getSupportActionBar().hide();
        EditText request = (EditText) findViewById(R.id.request);
        ListView listView = (ListView) findViewById(R.id.listView);
        Cursor cursor = myDataBase.getReadableDatabase().query("tests",null,null,null,null,null,null);
        request.setHint("Тест");
        allTests = new ArrayList<>();
        relevanceTests = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getInt(cursor.getColumnIndex("enabled"))==1) {
                    HashMap<String, Object> curTest = new HashMap<>();
                    curTest.put("id", cursor.getInt(0));
                    curTest.put("name", cursor.getString(1));
                    if (cursor.getInt(13) == 1)
                        curTest.put("autor", cursor.getString(2));
                    else
                        curTest.put("autor", "");
                    curTest.put("date", cursor.getString(12));
                    allTests.add(0, curTest);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        int[] xmlId = {R.id.item1, R.id.item2, R.id.item3};
        String[] keys = {"name", "autor", "date"};
        adapter = new SimpleAdapter(this, relevanceTests, R.layout.search_test_item, keys, xmlId);
        listView.setAdapter(adapter);
        request.addTextChangedListener(new MyWatcher());
        request.setText("");
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(SearchingActivity.this,NewTest.class);
                intent.putExtra("code of test", ((Integer)relevanceTests.get(position).get("id")));
                startActivityForResult(intent,0);
            }
        });
    }

    private class MyWatcher implements TextWatcher {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            relevanceTests.clear();
            for (HashMap<String,Object> test:allTests)
                if (test.get("name").toString().toLowerCase().contains(s.toString().toLowerCase())
                        ||(test.get("autor").toString().toLowerCase().contains(s.toString().toLowerCase())))
                    relevanceTests.add(test);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void afterTextChanged(Editable s) {}
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, v.getId(), 0, "Delete");
    }

    public boolean onContextItemSelected(MenuItem item) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==DONE) {
            setResult(DONE,data);
            finish();
        }
    }
}
