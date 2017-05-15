package com.example.petro.newtesting;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.petro.newtesting.MainActivity.answDataBase;
import static com.example.petro.newtesting.MainActivity.myDataBase;

public class SeeResultActivity extends AppCompatActivity {
//    ArrayList<ArrayList<String>> options;
//    int taskCount;
    LayoutInflater inflater;
    ArrayList<ViewGroup> tasks;
    CurrentAnswer answer;
    CurrentTest test;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_result);

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "https://loploplop3.herokuapp.com/getanswer.php";
        Map<String, String> map = new HashMap<>();
        map.put("id", String.valueOf(getIntent().getIntExtra("id", -1)));
        JsonArrayPostRequest answerRequest = new JsonArrayPostRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    Log.d("response", "getanswer response");
                    JSONObject object = (JSONObject) response.get(0);
                    answer = new Gson().fromJson(object.toString(), CurrentAnswer.class);
                    answer.set();
                    continueOnCreate1();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getBaseContext(), "Some error", Toast.LENGTH_SHORT).show();
                finish();
            }
        }, map);
        queue.add(answerRequest);
    }

    private void continueOnCreate1() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "https://loploplop3.herokuapp.com/gettest.php";
        Map<String, String> map = new HashMap<>();
        map.put("id", String.valueOf(answer.test));
        JsonArrayPostRequest testRequest = new JsonArrayPostRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject object = (JSONObject) response.get(0);
                    test = new Gson().fromJson(object.toString(), CurrentTest.class);
                    test.set();
                    continueOnCreate2();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }, map);
        queue.add(testRequest);
    }
    private void continueOnCreate2() {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,42,getResources().getDisplayMetrics());

        LinearLayout horizontalView = (LinearLayout) findViewById(R.id.child_of_see_results_horizontal_scroll_view);
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.see_results_main_layout);
        final ScrollView scrollView = (ScrollView) findViewById(R.id.see_results_scroll_view);
        inflater = getLayoutInflater().from(getBaseContext());

        findViewById(R.id.see_results_horizontal_scroll_view).setHorizontalScrollBarEnabled(false );

        tasks=new ArrayList<>();
//        ArrayList<Boolean> isRadio=new ArrayList<>();
//        for (String str : testCursor.getString(testCursor.getColumnIndex("isRadio")).split("#"))
//            isRadio.add(Integer.parseInt(str)==1);
//        String[] questions=testCursor.getString(testCursor.getColumnIndex("questions")).split("#");
//        taskCount=questions.length;
//        options=new ArrayList<>();
//        String[] helpString1=testCursor.getString(testCursor.getColumnIndex("options")).split("#");
//        for (int i=0;i<taskCount;i++) {
//            options.add(new ArrayList<String>());
//            String[] helpString2=helpString1[i].split("`");
//            for (String str:helpString2)
//                options.get(i).add(str);
//        }
//        String[] studentAnswears=studentCursor.getString(studentCursor.getColumnIndex("ans")).split("#");
//        String[] testAnswears=testCursor.getString(testCursor.getColumnIndex("rightAnswears")).split("#");
//        String[] photos = testCursor.getString(14).split("#");
        for (int i = 0; i < test.size; i++) {
            Button b=new Button(getApplicationContext());
            b.getBackground().setColorFilter(getResources().getColor(R.color.blue), PorterDuff.Mode.SRC_ATOP);
            b.setText(String.valueOf(i+1));
            b.setLayoutParams(new LinearLayout.LayoutParams(px,px));

            horizontalView.addView(b);
            final LinearLayout currentTaskLayout = (LinearLayout) inflater.inflate(R.layout.see_results_task_pattern, null);
            ((TextView)currentTaskLayout.getChildAt(0)).setText((i+1) + ". " + test.questionsM[i]);
            Gson gson = new Gson();
            if (!test.photosM[i].equals("null")) {
                byte[] photoArray = gson.fromJson(test.photosM[i], byte[].class);
                ((ImageView) currentTaskLayout.findViewById(R.id.srtp_image_view)).setImageBitmap(
                        BitmapFactory.decodeByteArray(photoArray, 0, photoArray.length));
            }
            tasks.add(currentTaskLayout);

//            String[] studentCurrentTaskAnswears = studentAnswears[i].split("`");
//            String[] testCurrentTaskAnswears=testAnswears[i].split("`");
//            int len = testCurrentTaskAnswears.length;
            if (test.radioM[i]) {
                for (int j = 0; j < test.tasksSizes.get(i); j++) {
                    RadioButton rb;
                    rb = addRadioButton(answer.answerM.get(i).get(j) == (test.correctM.get(i).get(j)),
                            answer.answerM.get(i).get(j));
                    rb.setText(test.optionsM.get(i).get(j));
                    currentTaskLayout.addView(rb);
                }
            }
            else {
                for (int j = 0; j < test.tasksSizes.get(i); j++) {
                    CheckBox cb;
                    cb=addCheckBox(answer.answerM.get(i).get(j) == (test.correctM.get(i).get(j)),
                            answer.answerM.get(i).get(j));
                    cb.setText(test.optionsM.get(i).get(j));
                    currentTaskLayout.addView(cb);
                }
            }
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.smoothScrollTo(0, currentTaskLayout .getTop());
                        }
                    });
                }
            });
            mainLayout.addView(currentTaskLayout);
        }
    }
    RadioButton addRadioButton(boolean correct, boolean checked) {
        RadioButton rb ;
        if (correct)
            rb=(RadioButton) inflater.inflate(R.layout.right_radio_button, null);
        else
            rb=(RadioButton) inflater.inflate(R.layout.wrong_radio_button,null);
        rb.setChecked(checked);
        return rb;
    }
    CheckBox addCheckBox(boolean correct, boolean checked) {
        CheckBox cb;
        if (correct)
            cb= (CheckBox) inflater.inflate(R.layout.right_check_box,null);
        else
            cb= (CheckBox) inflater.inflate(R.layout.wrong_check_box,null);
        cb.setChecked(checked);
        return cb;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
