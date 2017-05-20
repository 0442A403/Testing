package com.example.petro.newtesting;

import android.content.Intent;
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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SeeStudentsRusults extends AppCompatActivity {
    private ArrayList<HashMap<String,Object>> allAnswears,relevanceAnswears;
    private SimpleAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_bar);

        allAnswears = new ArrayList<>();
        relevanceAnswears = new ArrayList<>();

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        Map<String, String> map = new HashMap<>();
        map.put("test", String.valueOf(getIntent().getIntExtra("Test position", -1)));
        Log.i("test id", String.valueOf(getIntent().getIntExtra("Test position", -1)));
        String url = "https://loploplop3.herokuapp.com/gettestsanswers.php";
        JsonArrayPostRequest serverRequest = new JsonArrayPostRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    Log.i("Server response", "Got a response");
                    Gson gson = new Gson();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = (JSONObject) response.get(i);
                        helpClass answer = gson.fromJson(object.toString(), helpClass.class);
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("student", answer.student);
                        map.put("date", answer.date);
                        map.put("mark", answer.mark);
                        map.put("id", answer.id);
                        allAnswears.add(0, map);
                        relevanceAnswears.add(0, map);
                    }
                    Log.i("Server response", "Got a student's results");
                    continueOnCreate();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        },map);
        queue.add(serverRequest);
    }

    private void continueOnCreate() {
        setContentView(R.layout.activity_searching);

        EditText request = (EditText) findViewById(R.id.request);
        request.setHint("Ученик");
        ListView listView = (ListView) findViewById(R.id.listView);

        int[] xmlId = {R.id.item1, R.id.item2, R.id.item3};
        String[] keys = {"student", "date", /*"scores",*/ "mark"};
        adapter = new SimpleAdapter(this, relevanceAnswears,
                R.layout.my_tests_item, keys, xmlId);
        listView.setAdapter(adapter);
        request.addTextChangedListener(new MyWatcher());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SeeStudentsRusults.this,SeeResultActivity.class);
                intent.putExtra("id", (int) relevanceAnswears.get(position).get("id"));
                startActivity(intent);
            }
        });
    }

    private class MyWatcher implements TextWatcher {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            relevanceAnswears.clear();
            for (HashMap<String,Object> test:allAnswears) {
                if (test.get("student").toString().toLowerCase().contains(s.toString().toLowerCase()))
                    relevanceAnswears.add(test);
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
    private class helpClass {
        String student, date;
        int mark, id;
    }
}
