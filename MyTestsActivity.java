package com.example.petro.newtesting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.petro.newtesting.MainActivity.answDataBase;
import static com.example.petro.newtesting.MainActivity.myDataBase;

public class MyTestsActivity extends AppCompatActivity {
    ArrayList<HashMap<String,Object>> myTests, relevance;
    ListView relevanceAnswear;
    SimpleAdapter adapter;
    EditText searchField;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching);
        getSupportActionBar().hide();
        setTitle("Мои тесты");

        searchField=(EditText)findViewById(R.id.request);
        searchField.setHint("Название теста");
        searchField.addTextChangedListener(new MyWatcher());
        relevanceAnswear=(ListView) findViewById(R.id.listView);

        SharedPreferences preferences=getSharedPreferences("APP_DATA",MODE_PRIVATE);
        String autor = preferences.getString("second name","Фамилия")+" "+preferences.getString("first name","Имя");
        Cursor cursor=myDataBase.getReadableDatabase().query("tests",null,null,null,null,null,null);
        myTests=new ArrayList<>();
        relevance=new ArrayList<>();
        Cursor myCursor = answDataBase.getReadableDatabase().query("answears",null,null,null,null,null,null);
        if (cursor.moveToFirst()) {
            do {
                if (autor.toLowerCase().equals(cursor.getString(cursor.getColumnIndex("autor")).toLowerCase())) {
                    HashMap<String,Object> curItem=new HashMap<>();
                    curItem.put("test",cursor.getInt(0));
                    curItem.put("name",cursor.getString(cursor.getColumnIndex("name")));
                    curItem.put("date",cursor.getString(cursor.getColumnIndex("date")));
                    int middleMark=0;
                    int count=0;
                    int id = cursor.getInt(cursor.getColumnIndex("id"));
                    if (myCursor.moveToFirst()) {
                        do {
                            if (myCursor.getInt(1) == id) {
                                middleMark+=myCursor.getInt(4);
                                count++;
                            }
                        } while (myCursor.moveToNext());
                    }
                    String mark=((float)middleMark)/count+"";
                    if (mark.length()>3)
                        mark=mark.substring(0,3);
                    if (mark.equals("NaN"))
                        mark="0.0";

                    curItem.put("middle mark",mark);
                    myTests.add(0,curItem);
                    relevance.add(0,curItem);
                }
            } while (cursor.moveToNext());
        }
        int[] xmlId={R.id.item1,R.id.item2,R.id.item3};
        String[] keys={"name","date","middle mark"};
        adapter=new SimpleAdapter(this,relevance,R.layout.my_tests_item,keys,xmlId);
        relevanceAnswear.setAdapter(adapter);

        relevanceAnswear.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(MyTestsActivity.this,SeeStudentsRusults.class);
                intent.putExtra("Test position", (Integer) relevance.get(position).get("test"));
                startActivityForResult(intent,1);
            }
        });
        registerForContextMenu(relevanceAnswear);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.listView)
            getMenuInflater().inflate(R.menu.menu_list,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int itemIndex = ((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).position;
        int deletedTest = (int) relevance.get(itemIndex).get("test");
        myDataBase.getWritableDatabase().delete("tests", "id = " + deletedTest, null);
        answDataBase.getWritableDatabase().delete("answears", "test = " + deletedTest, null);
        myTests.remove(itemIndex);
        searchField.setText(searchField.getText());
        return super.onContextItemSelected(item);
    }

    class MyWatcher implements TextWatcher {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            relevance.clear();
            for (HashMap<String,Object> test:myTests) {
                if (test.get("name").toString().toLowerCase().contains(s.toString().toLowerCase()))
                    relevance.add(test);
            }
            adapter.notifyDataSetChanged();

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void afterTextChanged(Editable s) {}
    }
}
