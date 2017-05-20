package com.example.petro.newtesting;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import static com.example.petro.newtesting.MainActivity.DONE;


public class SearchingActivity extends AppCompatActivity {
    private ArrayList<HashMap<String,Object>> allTests,relevanceTests;
    private SimpleAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_bar);

        allTests = new ArrayList<>();
        relevanceTests = new ArrayList<>();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://loploplop3.herokuapp.com/gettests.php";

        JsonArrayRequest serverRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    Gson gson = new Gson();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = (JSONObject) response.get(i);
                        SearchedTest curTest = gson.fromJson(object.toString(), SearchedTest.class);
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("id", curTest.id);
                        map.put("name", curTest.name);
                        if (curTest.anon == 0)
                            map.put("autor", curTest.autor);
                        else
                            map.put("autor", "");
                        map.put("date", new String(curTest.date.getBytes("UTF-8"), "UTF-8"));
                        allTests.add(0, map);
                        relevanceTests.add(0, map);
                    }
                    continueOnCreate();

                } catch (JSONException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(serverRequest);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


    private void continueOnCreate() {
        setContentView(R.layout.activity_searching);
        final EditText request = (EditText) findViewById(R.id.request);
        ListView listView = (ListView) findViewById(R.id.listView);
        request.setHint("Тест");

        int[] xmlId = {R.id.item1, R.id.item2, R.id.item3};
        String[] keys = {"name", "autor", "date"};
        adapter = new SimpleAdapter(this, relevanceTests, R.layout.search_test_item, keys, xmlId);
        listView.setAdapter(adapter);
        request.addTextChangedListener(new MyWatcher());
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==DONE) {
            setResult(DONE, data);
            finish();
        }
    }
    private class SearchedTest {
        String name, autor, date;
        int id, anon;
    }
}