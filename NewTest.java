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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static com.example.petro.newtesting.MainActivity.COCO;
import static com.example.petro.newtesting.MainActivity.DONE;
import static com.example.petro.newtesting.MainActivity.answDataBase;
import static com.example.petro.newtesting.MainActivity.myDataBase;

/**
 * Created by petro on 07.04.2017.
 */

public class NewTest extends AppCompatActivity {
    String name,autor;
    ArrayList<String> questions,photos;
    ArrayList<ArrayList<String>> options;
    ArrayList<Boolean> isRadio;
    ArrayList<ArrayList<Boolean>> rightAnswears,studentAnswears;
    ArrayList<ArrayList<Integer>> randomicity;
    ArrayList<Integer> scores;
    int id,showWrong,five,four,three;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);
        getSupportActionBar().hide();
        inflater=getLayoutInflater().from(getBaseContext());
        horizontalScrollView=(ViewGroup)findViewById(R.id.horizontalScrollViewItself);
        taskLayout=(LinearLayout)findViewById(R.id.taskLayoutItself);
        summaryLayout= (RelativeLayout) findViewById(R.id.summary);
        summaryLayoutButton=(Button)findViewById(R.id.finish);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,41,getResources().getDisplayMetrics());
        studentAnswears=new ArrayList<>();
        findViewById(R.id.tests_task_panel).setHorizontalScrollBarEnabled(false);
        viewTime = (TextView) findViewById(R.id.left);

        Intent intent=getIntent();
        id = intent.getIntExtra("code of test", -1);

        final Cursor cursor = myDataBase.getReadableDatabase().rawQuery("select * from tests where id = " + id, null);
        cursor.moveToFirst();

        name = cursor.getString(cursor.getColumnIndex("name"));
        autor=cursor.getString(cursor.getColumnIndex("autor"));
        showWrong=cursor.getInt(cursor.getColumnIndex("showWrong"));
        five=cursor.getInt(cursor.getColumnIndex("five"));
        four=cursor.getInt(cursor.getColumnIndex("four"));
        three=cursor.getInt(cursor.getColumnIndex("three"));
        if (cursor.getInt(15)==1) {
            findViewById(R.id.static_left).setVisibility(View.VISIBLE);
            viewTime.setVisibility(View.VISIBLE);
            time = cursor.getInt(16);
            timer = new MyTimer(time);
            timer.start();
            Log.d(COCO,"Timer's been activated");
            Log.d(COCO,"Time of timer is "+time);
        }

        questions=new ArrayList<>();
        options=new ArrayList<>();
        isRadio=new ArrayList<>();
        rightAnswears=new ArrayList<>();
        scores=new ArrayList<>();
        randomicity = new ArrayList<>();
        photos = new ArrayList<>();

        String helpString[]=cursor.getString(cursor.getColumnIndex("questions")).split("#");
        for (String str:helpString)
            questions.add(str);

        helpString=cursor.getString(cursor.getColumnIndex("options")).split("#");
        for (String taskOptions:helpString) {
            options.add(new ArrayList<String>());
            for (String option:taskOptions.split("`"))
                options.get(options.size()-1).add(option);
        }

        helpString=cursor.getString(cursor.getColumnIndex("rightAnswears")).split("#");
        for (String taskAnswears:helpString) {
            rightAnswears.add(new ArrayList<Boolean>());
            for (String helpString1:taskAnswears.split("`"))
                rightAnswears.get(rightAnswears.size()-1).add(helpString1.equals("1"));
        }

        helpString=cursor.getString(cursor.getColumnIndex("isRadio")).split("#");
        for (String str:helpString)
            isRadio.add(Integer.parseInt(str) == 1);

        helpString=cursor.getString(cursor.getColumnIndex("scores")).split("#");
        for (String str:helpString)
            scores.add(Integer.parseInt(str));

        helpString = cursor.getString(14).split("#");
        for (String str : helpString)
            photos.add(str);

        pairs=new ArrayList<>();
        dones=new boolean[questions.size()];
        Arrays.fill(dones,false);
        for (int i=0;i<dones.length;i++) {
            final Button b=new Button(getApplicationContext());
            b.setLayoutParams(new LinearLayout.LayoutParams(px,px));
            b.setText((i+1)+"");
            final ViewGroup vgd = (ViewGroup) inflater.inflate(R.layout.task,null);
            final ViewGroup vg = (ViewGroup) vgd.findViewById(R.id.task_layout);
            pairs.add(new Pair<Button, ViewGroup>(b,vgd));

            final int index=i;

            if (!photos.get(i).equals(" "))
                ((ImageView)vg.findViewById(R.id.task_image)).setImageBitmap(BitmapFactory.decodeFile(photos.get(i)));

            randomicity.add(new ArrayList<Integer>());
            ((TextView)vg.findViewById(R.id.question)).setText(questions.get(i));
            final RadioGroup rg = (RadioGroup) vg.findViewById(R.id.radioGroup);
            int len = options.get(i).size();
            for (int j = 0; j < options.get(i).size() ; j++) {
                int random = new Random().nextInt(len);
                while (randomicity.get(i).contains(random))
                    random = new Random().nextInt(len);

                randomicity.get(i).add(random);

                if (isRadio.get(i)) {
                    RadioButton rb =(RadioButton) getLayoutInflater().inflate(R.layout.radio_button, null);
                    rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            setButtonsColor(rg,b);
                        }
                    });
                    rb.setText(options.get(i).get(random));
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
                    cb.setText(options.get(i).get(random));
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
        int allScrores=0;
        float studentScores=0;
        StringBuilder str=new StringBuilder();
        for (int i=0; i<dones.length; i++) {
            int len = randomicity.get(i).size();
            boolean[] helpArray = new boolean[len];
            studentAnswears.add(new ArrayList<Boolean>());

            ViewGroup myViewGroup=(ViewGroup)pairs.get(i).second.findViewById(R.id.task_layout).findViewById(R.id.radioGroup);
            for (int j=0; j<len; j++) {
                Boolean myBool=((CompoundButton)myViewGroup.getChildAt(j)).isChecked();
                helpArray[randomicity.get(i).get(j)] = myBool;
            }
            for (int j=0; j < len; j++) {
                studentAnswears.get(i).add(helpArray[j]);
                str.append(helpArray[j]?"1`":"0`");
            }
            str.append("#");
        }
        for (int i=0;i<dones.length;i++) {
            int len = rightAnswears.get(i).size();
            allScrores += scores.get(i);
            if (isRadio.get(i)) {
                for (int j = 0; j < len; j++) {
                    if (rightAnswears.get(i).get(j)) {
                        if (studentAnswears.get(i).get(j))
                            studentScores += scores.get(i);
                        break;
                    }
                }
            }
            else {
                float collectedScores = scores.get(i);
                float singleScore = collectedScores/2;
                for (int j = 0; j < len; j++)
                    if (rightAnswears.get(i).get(j) != studentAnswears.get(i).get(j))
                        collectedScores -= singleScore;
                if (collectedScores > 0)
                    studentScores += collectedScores;
            }
        }
        int res= Math.round((studentScores*100)/allScrores);
        if (res>=five)
            res = 5;
        else if (res>=four)
            res=4;
        else if (res>=three)
            res=3;
        else
            res=2;
        ContentValues cv=new ContentValues();
        cv.put("test", id);
        SharedPreferences sp=getSharedPreferences("APP_DATA", MODE_PRIVATE);
        cv.put("student",sp.getString("second name","Фамилия")+" "+sp.getString("first name","Имя"));
        Time now = new Time(Time.getCurrentTimezone());
        now.setToNow();
        String[] months = { "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август",
                "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
        String date = now.hour + ":" + now.minute + ":" + now.second + " " + now.monthDay + " " +
                months[now.month];
        cv.put("date",date);
        cv.put("mark",res);
        cv.put("ans",str.toString());
        answDataBase.getWritableDatabase().insert("answears",null,cv);
        Cursor cur = answDataBase.getWritableDatabase().query("answears",null,null,null,null,null,null);
        cur.moveToLast();
        Intent myIntent=new Intent();
        myIntent.putExtra("id", cur.getInt(0));
        setResult(DONE,myIntent);
        if (timer!=null)
            timer.cancel();
        finish();
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
            if (hasDone((RadioGroup)((ViewGroup)pair.second.findViewById(R.id.task_layout)).findViewById(R.id.radioGroup)))
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        backDialogBox();
        return super.onOptionsItemSelected(item);
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

    class MyTimer extends CountDownTimer {
        public MyTimer(int time) {
            super(Integer.MAX_VALUE,1000);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            Log.d(COCO,"now time is "+time);
            time--;
            if (time==0)
                finishTest();
            viewTime.setText(time/60+":"+time%60);
        }

        @Override
        public void onFinish() {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer!=null)
            timer.cancel();
    }
}
