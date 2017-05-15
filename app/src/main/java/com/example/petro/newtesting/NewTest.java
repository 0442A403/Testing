package com.example.petro.newtesting;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.example.petro.newtesting.MainActivity.COCO;
import static com.example.petro.newtesting.MainActivity.DONE;
import static com.example.petro.newtesting.MainActivity.answDataBase;
import static com.example.petro.newtesting.MainActivity.myDataBase;

/**
 * Created by petro on 07.04.2017.
 */

public class NewTest extends AppCompatActivity {
    CurrentTest curTask;
    ArrayList<ArrayList<Integer>> randomicity;
    Gson gson;
    int id;
    LayoutInflater inflater;
    ViewGroup horizontalScrollView;
    LinearLayout taskLayout;
    RelativeLayout summaryLayout;
    Button summaryLayoutButton;
    boolean dones[];
    ArrayList<Pair<Button,ViewGroup>> pairs;
    TextView viewTime;
    int px,time;
    MyTimer timer;
    RequestQueue queue;
    float studentScores = 0;
    int allScores = 0, res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        id = getIntent().getIntExtra("code of test", -1);
        queue = Volley.newRequestQueue(this);
        String url = "https://loploplop3.herokuapp.com/gettest.php";
        gson = new Gson();

        Map<String, String> map = new HashMap<>();
        map.put("id", String.valueOf(id));
        JsonArrayPostRequest serverRequest = new JsonArrayPostRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject object = (JSONObject) response.get(0);
                    curTask = gson.fromJson(object.toString(), CurrentTest.class);
                    curTask.set();
                    continueOnCreate();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d("Error", "response error");
            }
        }, map);

        queue.add(serverRequest);

    }

    private void continueOnCreate() {

        setContentView(R.layout.activity_test);

        inflater=getLayoutInflater().from(getBaseContext());
        horizontalScrollView=(ViewGroup)findViewById(R.id.horizontalScrollViewItself);
        taskLayout=(LinearLayout)findViewById(R.id.taskLayoutItself);
        summaryLayout= (RelativeLayout) findViewById(R.id.summary);
        summaryLayoutButton=(Button)findViewById(R.id.finish);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,41,getResources().getDisplayMetrics());
        findViewById(R.id.tests_task_panel).setHorizontalScrollBarEnabled(false);
        viewTime = (TextView) findViewById(R.id.left);

        if (curTask.timer == 1) {
            findViewById(R.id.static_left).setVisibility(View.VISIBLE);
            viewTime.setVisibility(View.VISIBLE);
            time = curTask.time;
            timer = new MyTimer();
            timer.start();
            Log.d(COCO,"Timer's been activated");
            Log.d(COCO,"Time of timer is " + time);
        }

        pairs = new ArrayList<>();
        dones = new boolean[curTask.size];
        randomicity = new ArrayList<>();

        Arrays.fill(dones,false);
        for (int i=0; i < dones.length; i++) {
            final Button b=new Button(getApplicationContext());
            b.setLayoutParams(new LinearLayout.LayoutParams(px,px));
            b.setText((i+1)+"");
            final ViewGroup vgd = (ViewGroup) inflater.inflate(R.layout.task,null);
            final ViewGroup vg = (ViewGroup) vgd.findViewById(R.id.task_layout);
            pairs.add(new Pair<>(b,vgd));

            final int index = i;

            if (!curTask.photosM[i].equals("null")) {
                byte[] decodedImage = gson.fromJson(curTask.photosM[i], byte[].class);
                ((ImageView) vg.findViewById(R.id.task_image)).setImageBitmap(BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length));
            }

            randomicity.add(new ArrayList<Integer>());
            ((TextView)vg.findViewById(R.id.question)).setText(curTask.questionsM[i]);
            final RadioGroup rg = (RadioGroup) vg.findViewById(R.id.radioGroup);
            int len = curTask.optionsM.get(i).size();
            for (int j = 0; j < len ; j++) {
                int random = new Random().nextInt(len);
                while (randomicity.get(i).contains(random))
                    random = new Random().nextInt(len);

                randomicity.get(i).add(random);

                if (curTask.radioM[i]) {
                    RadioButton rb =(RadioButton) getLayoutInflater().inflate(R.layout.radio_button, null);
                    rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            setButtonsColor(rg,b);
                        }
                    });
                    rb.setText(curTask.optionsM.get(i).get(random));
                    rb.setTextColor(Color.BLACK);
                    rg.addView(rb);
                }
                else {
                    CheckBox cb= (CheckBox)getLayoutInflater().inflate(R.layout.check_box,null);
                    cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            setButtonsColor(rg,b);
                        }
                    });
                    cb.setText(curTask.optionsM.get(i).get(random));
                    cb.setTextColor(Color.BLACK);
                    rg.addView(cb);
                }
            }
            vg.findViewById(R.id.nextTask).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (index<dones.length-1)
                        pairs.get(index+1).first.performClick();
                    else
                        summaryLayoutButton.performClick();
                }
            });

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setTaskLayout(vgd);
                    setButtonsColor((RadioGroup) vg.findViewById(R.id.radioGroup),b);
                }
            });
            horizontalScrollView.addView(b);
        }

        horizontalScrollView.getChildAt(0).performClick();


        summaryLayoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskLayout.setVisibility(View.GONE);
                summaryLayout.setVisibility(View.VISIBLE);
                setDarkButtonsColor();
                int done=0;
                for (Pair<Button,ViewGroup> pair:pairs) {
                    if (hasDone((RadioGroup)pair.second.findViewById(R.id.task_layout).findViewById(R.id.radioGroup)))
                        done++;
                }
                ((TextView)summaryLayout.findViewById(R.id.state)).setText(done+" / "+dones.length);
            }
        });
        findViewById(R.id.finishTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NewTest.this);
                alertDialogBuilder.setMessage("Закончить тест?");
                alertDialogBuilder.setPositiveButton("Закончить",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                finishTest();
                            }
                        });
                alertDialogBuilder.setNegativeButton("Отмена",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

    }

    private void finishTest() {
        ArrayList<ArrayList<Boolean>> studentAnswears = new ArrayList<>();
        final ArrayList<String> studentAnswerM = new ArrayList<>();

        for (int i=0; i<dones.length; i++) {
            int len = randomicity.get(i).size();
            boolean[] helpArray = new boolean[len];
            studentAnswears.add(new ArrayList<Boolean>());

            ViewGroup myViewGroup=(ViewGroup)pairs.get(i).second.findViewById(R.id.task_layout).findViewById(R.id.radioGroup);
            for (int j=0; j<len; j++)
                helpArray[randomicity.get(i).get(j)] = ((CompoundButton)myViewGroup.getChildAt(j)).isChecked();
            for (int j=0; j < len; j++)
                studentAnswears.get(i).add(helpArray[j]);
            studentAnswerM.add(gson.toJson(helpArray));
        }
        for (int i=0;i<dones.length;i++) {
            int len = curTask.tasksSizes.get(i);
            allScores += curTask.scoresM[i];
            if (curTask.radioM[i]) {
                for (int j = 0; j < len; j++) {
                    if (curTask.correctM.get(i).get(j)) {
                        if (studentAnswears.get(i).get(j))
                            studentScores += curTask.scoresM[i];
                        break;
                    }
                }
            }
            else {
                float collectedScores = curTask.scoresM[i];
                float singleScore = collectedScores/2;
                for (int j = 0; j < len; j++)
                    if (curTask.correctM.get(i).get(j) != studentAnswears.get(i).get(j))
                        collectedScores -= singleScore;
                if (collectedScores > 0)
                    studentScores += collectedScores;
            }
        }
        res = Math.round((studentScores*100)/allScores);
        if (res >= curTask.five)
            res = 5;
        else if (res >= curTask.four)
            res=4;
        else if (res >= curTask.three)
            res=3;
        else
            res=2;

        RequestQueue queue = Volley.newRequestQueue(getBaseContext());
        String url = "https://loploplop3.herokuapp.com/addanswer.php";
        StringRequest serverRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Intent myIntent = new Intent();
                myIntent.putExtra("id", Integer.valueOf(response.replace(" ", "")));
                Log.d("answer id", String.valueOf(myIntent.getIntExtra("id", -1)));
                myIntent.putExtra("mark", res);
                myIntent.putExtra("Show wrong", curTask.showwrong);
                Log.d("Show wrong", String.valueOf(curTask.showwrong));
                Log.d("server response", response);
                setResult(DONE, myIntent);
                if (timer != null)
                    timer.cancel();
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.wtf("error", "what a....");
                Toast.makeText(getBaseContext(), "Some error's been happen", Toast.LENGTH_SHORT).show();
                finish();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();

                SharedPreferences sp = getSharedPreferences("APP_DATA", MODE_PRIVATE);

                Time now = new Time(Time.getCurrentTimezone());
                now.setToNow();
                String[] months = { "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август",
                        "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
                String date = now.hour + ":" + now.minute + ":" + now.second + " " + now.monthDay + " " +
                        months[now.month];

                map.put("student", sp.getString("second name","Фамилия")+" "+sp.getString("first name","Имя"));
                map.put("date", date);
                map.put("answer", gson.toJson(studentAnswerM.toArray()));
                map.put("scores", Math.round(studentScores) + "/" + allScores);
                map.put("mark", String.valueOf(res));
                map.put("test", String.valueOf(id));
                map.put("time", String.valueOf(time));

                Log.d("Params", "Params for answers's been created");
                return map;
            }
        };
        queue.add(serverRequest);
    }

    private void setTaskLayout(ViewGroup view) {
        taskLayout.setVisibility(View.VISIBLE);
        summaryLayout.setVisibility(View.GONE);
        taskLayout.removeAllViews();
        taskLayout.addView(view);
    }

    private void setButtonsColor(RadioGroup rg,Button b) {
        setDarkButtonsColor();
        if (hasDone(rg))
            b.getBackground().setColorFilter(getResources().getColor(R.color.liteGreen), PorterDuff.Mode.SRC_ATOP);
        else
            b.getBackground().setColorFilter(getResources().getColor(R.color.liteRed), PorterDuff.Mode.SRC_ATOP);
    }

    private void setDarkButtonsColor() {
        for (Pair<Button,ViewGroup> pair:pairs) {
            if (hasDone((RadioGroup)pair.second.findViewById(R.id.task_layout).findViewById(R.id.radioGroup)))
                pair.first.getBackground().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_ATOP);
            else
                pair.first.getBackground().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
        }
    }

    private boolean hasDone(RadioGroup rg) {
        int len=rg.getChildCount();
        for (int i=0;i<len;i++)
            if (((CompoundButton)rg.getChildAt(i)).isChecked())
                return true;
        return false;
    }

    class MyTimer extends CountDownTimer {
        public MyTimer() {
            super(Integer.MAX_VALUE, 1000);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            Log.d(COCO,"now time is "+time);
            time--;
            if (time==0) {
                Log.d("Timer", "Timer is done");
                finishTest();
            }
            viewTime.setText(time/60+":"+time%60);
        }

        @Override
        public void onFinish() {
        }
    }

    @Override
    public void onBackPressed() {
        backDialogBox();
    }
    public void backDialogBox() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Вы уверены? Все данные будут потеряны");
        alertDialogBuilder.setPositiveButton("Выйти",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        queue.stop();
                        finish();
                    }
                });
        alertDialogBuilder.setNegativeButton("Отмена",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer!=null)
            timer.cancel();
    }
}
