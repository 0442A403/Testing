package com.example.petro.newtesting;

import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import java.util.ArrayList;

import static com.example.petro.newtesting.MainActivity.answDataBase;
import static com.example.petro.newtesting.MainActivity.myDataBase;

public class SeeResultActivity extends AppCompatActivity {
    ArrayList<ArrayList<String>> options;
    int taskCount;
    LayoutInflater inflater;
    ArrayList<ViewGroup> tasks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_result);
        getSupportActionBar().hide();

        Cursor studentCursor = answDataBase.getReadableDatabase().rawQuery("select * from answears where id = " + getIntent().getIntExtra("id",-1), null);
        studentCursor.moveToFirst();
        Cursor testCursor = myDataBase.getReadableDatabase().rawQuery("select * from tests where id = " + studentCursor.getInt(studentCursor.getColumnIndex("test")),null);
        testCursor.moveToFirst();

        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,42,getResources().getDisplayMetrics());

        LinearLayout horizontalView = (LinearLayout) findViewById(R.id.child_of_see_results_horizontal_scroll_view);
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.see_results_main_layout);
        final ScrollView scrollView = (ScrollView) findViewById(R.id.see_results_scroll_view);
        inflater=getLayoutInflater().from(getBaseContext());

        findViewById(R.id.see_results_horizontal_scroll_view).setHorizontalScrollBarEnabled(false );

        tasks=new ArrayList<>();
        ArrayList<Boolean> isRadio=new ArrayList<>();
        for (String str:testCursor.getString(testCursor.getColumnIndex("isRadio")).split("#"))
            isRadio.add(Integer.parseInt(str)==1);
        String[] questions=testCursor.getString(testCursor.getColumnIndex("questions")).split("#");
        taskCount=questions.length;
        options=new ArrayList<>();
        String[] helpString1=testCursor.getString(testCursor.getColumnIndex("options")).split("#");
        for (int i=0;i<taskCount;i++) {
            options.add(new ArrayList<String>());
            String[] helpString2=helpString1[i].split("`");
            for (String str:helpString2)
                options.get(i).add(str);
        }
        String[] studentAnswears=studentCursor.getString(studentCursor.getColumnIndex("ans")).split("#");
        String[] testAnswears=testCursor.getString(testCursor.getColumnIndex("rightAnswears")).split("#");
        String[] photos = testCursor.getString(14).split("#");
        for (int i = 0; i < taskCount; i++) {
            Button b=new Button(getApplicationContext());
            b.getBackground().setColorFilter(getResources().getColor(R.color.blue), PorterDuff.Mode.SRC_ATOP);
            b.setText(String.valueOf(i+1));
            b.setLayoutParams(new LinearLayout.LayoutParams(px,px));

            horizontalView.addView(b);
            final LinearLayout currentTaskLayout= (LinearLayout) inflater.inflate(R.layout.see_results_task_pattern,null);
            ((TextView)currentTaskLayout.getChildAt(0)).setText((i+1)+". "+questions[i]);
            if (!photos[i].equals(" "))
                ((ImageView)currentTaskLayout.findViewById(R.id.srtp_image_view)).setImageBitmap(BitmapFactory.decodeFile(photos[i]));
            tasks.add(currentTaskLayout);

            String[] studentCurrentTaskAnswears=studentAnswears[i].split("`");
            String[] testCurrentTaskAnswears=testAnswears[i].split("`");
            int len=testCurrentTaskAnswears.length;
            if (isRadio.get(i)) {
                for (int j = 0; j < len; j++) {
                    RadioButton rb;
                    rb=addRadioButton(studentCurrentTaskAnswears[j].equals(testCurrentTaskAnswears[j]),
                            Integer.valueOf(studentCurrentTaskAnswears[j])==1);
                    rb.setText(options.get(i).get(j));
                    currentTaskLayout.addView(rb);
                }
            }
            else {
                for (int j = 0; j < len; j++) {
                    CheckBox cb;
                    cb=addCheckBox(studentCurrentTaskAnswears[j].equals(testCurrentTaskAnswears[j]),
                            Integer.valueOf(studentCurrentTaskAnswears[j])==1);
                    cb.setText(options.get(i).get(j));
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
