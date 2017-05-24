package com.example.petro.newtesting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyTestsActivity extends AppCompatActivity {
    ArrayList<HashMap<String,Object>> myTests, relevance, helpArray;
    ArrayList<Integer> ids, marksCount;
    ArrayList<Float> middleMarks;
    ListView relevanceAnswer;
    SimpleAdapter adapter;
    EditText searchField;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_bar);

        helpArray = new ArrayList<>();
        ids = new ArrayList<>();
        middleMarks = new ArrayList<>();
        marksCount = new ArrayList<>();
        myTests = new ArrayList<>();
        relevance = new ArrayList<>();

        SharedPreferences preferences = getSharedPreferences("APP_DATA",MODE_PRIVATE);
        String autor = preferences.getString("second name","Фамилия")+" "+preferences.getString("first name","Имя");

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "https://loploplop3.herokuapp.com/getmytests.php";
        Map<String, String> map = new HashMap<>();
        map.put("autor", autor);
        Log.d("author", autor);
        JsonArrayPostRequest testRequest = new JsonArrayPostRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    Log.d("Response", "getmytests response");
                    Gson gson = new Gson();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = (JSONObject) response.get(i);
                        SearchedTest test = gson.fromJson(object.toString(), SearchedTest.class);
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("date", test.date);
                        map.put("name", test.name);
                        map.put("id", test.id);
                        ids.add(test.id);
                        middleMarks.add(0.f);
                        marksCount.add(0);
                        helpArray.add(map);
                    }
                    Log.d("Response", "getmytests ended");
                    getAnswers();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                finishOnCreate();
            }
        }, map);
        queue.add(testRequest);

    }

    private void getAnswers() {
        Log.d("response", "contonue 1");
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "https://loploplop3.herokuapp.com/getanswers.php";
        Map<String, String> map = new HashMap<>();
        JsonArrayPostRequest testRequest = new JsonArrayPostRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    Log.d("Response", "getallanswers response");
                    Gson gson = new Gson();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = (JSONObject) response.get(i);
                        helpClass answer = gson.fromJson(object.toString(), helpClass.class);
                        int index;
                        if ((index = ids.indexOf(answer.test)) >= 0) {
                            middleMarks.set(index, middleMarks.get(index) + answer.mark);
                            marksCount.set(index, marksCount.get(index) + 1);
                        }
                    }
                    for (int i = 0; i < helpArray.size(); i++) {
                        HashMap<String, Object> map = helpArray.get(i);

                        String str = String.valueOf(middleMarks.get(i) / marksCount.get(i));
                        if (str.equals("NaN"))
                            map.put("middle mark", "0.0");
                        else if (str.length()>3)
                            map.put("middle mark", str.substring(0,3));
                        else
                            map.put("middle mark", str);

                        myTests.add(0, map);
                        relevance.add(0, map);
                    }
                    finishOnCreate();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }, map);
        queue.add(testRequest);
    }

    private void finishOnCreate() {
        setContentView(R.layout.activity_searching);
        searchField=(EditText)findViewById(R.id.request);
        searchField.setHint("Название теста");
        searchField.addTextChangedListener(new MyWatcher());
        relevanceAnswer = (ListView) findViewById(R.id.listView);
        int[] xmlId={R.id.item1,R.id.item2,R.id.item3};
        String[] keys={"name","date","middle mark"};
        adapter=new SimpleAdapter(this,relevance,R.layout.my_tests_item,keys,xmlId);
        relevanceAnswer.setAdapter(adapter);

        relevanceAnswer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(MyTestsActivity.this,SeeStudentsRusults.class);
                intent.putExtra("Test position", (Integer) relevance.get(position).get("id"));
                startActivityForResult(intent, 1);
            }
        });
        registerForContextMenu(relevanceAnswer);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.listView)
            getMenuInflater().inflate(R.menu.menu_list,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final int itemIndex = ((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).position;
        final int deletedTest = (int) relevance.get(itemIndex).get("id");
        Log.i("Deletion", "Id of test is " + deletedTest);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://loploplop3.herokuapp.com/deletetest.php";

        StringRequest request = new StringRequest (Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getBaseContext(), "Тест удален", Toast.LENGTH_SHORT).show();
                myTests.remove(itemIndex);
                searchField.setText(searchField.getText());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(deletedTest));
                return params;
            }
        };
        queue.add(request);
        return super.onContextItemSelected(item);
    }

    private class MyWatcher implements TextWatcher {
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
    private class helpClass {
        int test, mark, id;
    }
    private class SearchedTest {
        String name, date;
        int id;
    }
}
