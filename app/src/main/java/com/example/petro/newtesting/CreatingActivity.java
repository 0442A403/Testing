package com.example.petro.newtesting;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.example.petro.newtesting.MainActivity.COCO;
import static com.example.petro.newtesting.MainActivity.SAVE;

public class CreatingActivity extends AppCompatActivity {
    LayoutInflater inflater;
    ViewGroup horizontalView,taskLayout;
    EditText testName;
    int length;
    ArrayList<Task> tasks;
    HorizontalScrollView trueHorizontalScrollView;
    ScrollView trueScrollView;
    int px;
    private final int PICK_PHOTO=7888;
    private int indexOfPhotoTask=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creating);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,35,getResources().getDisplayMetrics());
        length = 0;
        tasks=new ArrayList<>();
        testName=(EditText)findViewById(R.id.testName);
        Button additionButton=(Button)findViewById(R.id.addTask);
        inflater = LayoutInflater.from(getBaseContext());
        horizontalView=(ViewGroup) findViewById(R.id.horizontalScrollView);
        trueHorizontalScrollView=(HorizontalScrollView)findViewById(R.id.trueHorizontalScrollView);
        trueHorizontalScrollView.setHorizontalScrollBarEnabled(false);
        trueScrollView=(ScrollView)findViewById(R.id.trueScrollView);
        taskLayout=(ViewGroup)findViewById(R.id.taskLayout);

        additionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                addTask();
            }
        });

        addTask();

        testName.requestFocus();
        Log.d(COCO,"Name1 focus");
        testName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (testName.getText().length()>0) {
                    taskLayout.getChildAt(0).requestFocus();
                    Log.d(COCO,"Question2 focus");
                }
                return true;
            }
        });
//        правильный фокус

//        final Handler handler = new Handler();
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                try {
//                    sleep(120);
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            testName.requestFocus();
//                            Log.d(COCO,"Name1 focus");
//                        }
//                    });
//                } catch (InterruptedException e) {}
//            }
//        }.start();
        findViewById(R.id.save_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (testName.getText().length()==0) {
                    Toast.makeText(getApplicationContext(), "Введите имя теста", Toast.LENGTH_LONG).show();
                    return;
                }
                for (Task tsk:tasks) {
                    if (tsk.hasUnfill()) {
                        horizontalView.getChildAt(tasks.indexOf(tsk)).performClick();
                        return;
                    }
                }
                startActivityForResult(new Intent(CreatingActivity.this, TestSettings.class), 1);
            }
        });
    }

    private void addTask() {
        if (tasks.size()<30) {
            length++;
            final Button b = new Button(getApplicationContext());
            b.setLayoutParams(new LinearLayout.LayoutParams(px,px));
            b.setText(length + "");
            final ViewGroup view = (ViewGroup) inflater.inflate(R.layout.task_pattern, null);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int len = horizontalView.getChildCount();
                    for (int i = 0; i < len; i++)
                        horizontalView.getChildAt(i).getBackground().setColorFilter(ContextCompat.getColor(CreatingActivity.this, R.color.standartButtonsColor), PorterDuff.Mode.SRC_ATOP);
                    v.getBackground().setColorFilter(ContextCompat.getColor(CreatingActivity.this, R.color.pressedButton), PorterDuff.Mode.SRC_ATOP);
                    setTaskLayout(view);
                }
            });
            b.performClick();
            horizontalView.addView(b);
            trueHorizontalScrollView.postDelayed(new Runnable() {
                public void run() {
                    trueHorizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                }
            }, 100L);
            tasks.add(new Task(view, b));
            ((EditText) view.getChildAt(0)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (((EditText) view.getChildAt(0)).getText().length() > 0)
                        tasks.get(horizontalView.indexOfChild(b)).options.get(0).requestFocus();
                    return true;
                }
            });
            view.getChildAt(0).requestFocus();
            Log.d(COCO,"Question3 focus");
        }
        else
            Toast.makeText(getApplicationContext(),"Ограничение",Toast.LENGTH_SHORT).show();
    }

    private void setTaskLayout(ViewGroup viewGroup) {
        taskLayout.removeAllViews();
        taskLayout.addView(viewGroup);
        viewGroup.getChildAt(0).requestFocus();
    }

    private class Task {
        EditText question;
        EditText scores;
        ArrayList<EditText> options;
        boolean isRadio;
        int rightAnswear;
        ArrayList<RadioButton> radioButtons;
        ArrayList<CheckBox> checkBoxes;
        ViewGroup viewGroup;
        ImageView image;
        String imagePath;

        Task(final ViewGroup viewGroup, final Button button) {
            question = (EditText) viewGroup.getChildAt(0);
            image = (ImageView) viewGroup.getChildAt(1);
            scores = (EditText) viewGroup.getChildAt(4);
            isRadio=true;
            checkBoxes=new ArrayList<>();
            radioButtons=new ArrayList<>();
            options=new ArrayList<>();
            rightAnswear=0;
            this.viewGroup = viewGroup;
            imagePath = " ";

            scores.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (v.length() > 0) {
                        if (tasks.indexOf(Task.this) < (tasks.size() - 1)) {
                            horizontalView.getChildAt(tasks.indexOf(Task.this) + 1).performClick();
                            tasks.get(tasks.indexOf(Task.this) + 1).question.requestFocus();
                            Log.d(COCO, "quest11 focus");
                        }
                        else
                            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                }
                    return false;
                }
            });


            ((ViewGroup)(((ViewGroup)viewGroup.getChildAt(3)).getChildAt(0))).getChildAt(0).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (options.size()<10) {
                        addOption(viewGroup, Task.this);
                        radioFunc();
                        isRadio = !isRadio;
                        changeInput();
                        options.get(options.size() - 1).requestFocus();
                        Log.d(COCO, "Quest10 focus");
                    }
                    else
                        Toast.makeText(getApplicationContext(),"Ограничение",Toast.LENGTH_SHORT).show();
                }
            });
            for (int i=0;i<2;i++)
                ((ViewGroup)(((ViewGroup)viewGroup.getChildAt(3)).getChildAt(0))).getChildAt(0).performClick();

            ((ViewGroup)(((ViewGroup)viewGroup.getChildAt(3)).getChildAt(1))).getChildAt(0).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeInput();
                }
            });
            ((ViewGroup)(((ViewGroup)viewGroup.getChildAt(3)).getChildAt(2))).getChildAt(0).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (imagePath.equals(" ")) {
                        indexOfPhotoTask = tasks.indexOf(Task.this);
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_PHOTO);
                    }
                    else {
                        image.setImageBitmap(null);
                        imagePath=" ";
                    }
                }
            });

            ((ViewGroup)(((ViewGroup)viewGroup.getChildAt(3)).getChildAt(3))).getChildAt(0).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tasks.size()>1) {
                        if (tasks.indexOf(Task.this) > 0)
                            horizontalView.getChildAt(tasks.indexOf(Task.this)-1).performClick();
                        else
                            horizontalView.getChildAt(1).performClick();
                    }
                    else
                        addTask();
                    tasks.remove(Task.this);
                    horizontalView.removeView(button);
                    length--;
                    int childCount=horizontalView.getChildCount();
                    for (int i=0;i<childCount;i++)
                        ((Button)horizontalView.getChildAt(i)).setText(String.valueOf(i+1));
                }
            });
        }

        private void addOption(final ViewGroup viewGroup, final Task task) {
            final ViewGroup view=(ViewGroup)inflater.inflate(R.layout.option_pattern, null);
            ((ViewGroup)viewGroup.getChildAt(2)).addView(view);
//            view.getChildAt(2).requestFocus();

            task.options.add((EditText)view.getChildAt(3));
            task.checkBoxes.add((CheckBox) view.getChildAt(0));
            task.radioButtons.add((RadioButton)view.getChildAt(1));
            view.getChildAt(2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    task.options.remove(view.getChildAt(3));
                    task.checkBoxes.remove(view.getChildAt(0));
                    task.radioButtons.remove(view.getChildAt(1));
                    ((ViewGroup)viewGroup.getChildAt(2)).removeView(view);
                    while (task.options.size()<2)
                        ((ViewGroup)(((ViewGroup)viewGroup.getChildAt(3)).getChildAt(0))).getChildAt(0).performClick();
                }
            });
            ((EditText) view.getChildAt(3)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (((EditText) view.getChildAt(3)).getText().length()>0) {
                        if (task.options.indexOf(view.getChildAt(3)) < task.options.size()-1&& ((EditText) view.getChildAt(3)).getText().length() > 0) {
                            task.options.get(task.options.indexOf(view.getChildAt(3)) + 1).requestFocus();
                            Log.d(COCO,"Option5 focus");
                        }
                        else {
                            task.scores.requestFocus();
                            Log.d(COCO,"Score6 focus");
                        }
                    }
                    return true;
                }
            });
            trueScrollView.postDelayed(new Runnable() {
                public void run() {
                    trueScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            }, 100L);
        }

        void radioFunc() {
            radioButtons.get(radioButtons.size()-1).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                        for (RadioButton rb:radioButtons)
                            if (rb!=buttonView)
                                rb.setChecked(false);
                }
            });
        }

        boolean hasUnfill() {
            if (question.getText().length()==0) {
                Toast.makeText(getApplicationContext(),"Введите вопрос",Toast.LENGTH_LONG).show();
                question.requestFocus();
                Log.d(COCO,"Question7 focus");
                return true;
            }
            if (scores.getText().length()==0) {
                Toast.makeText(getApplicationContext(), "Введите кол-во баллов", Toast.LENGTH_LONG).show();
                scores.requestFocus();
                Log.d(COCO,"Score8 focus");
                return true;
            }
            for (EditText et:options)
                if (et.getText().length()==0) {
                    Toast.makeText(getApplicationContext(),"Заполните все варианты ответов",Toast.LENGTH_LONG).show();
                    et.requestFocus();
                    Log.d(COCO,"option9 focus");
                    return true;
                }
            if (isRadio) {
                for (RadioButton rb : radioButtons)
                    if (rb.isChecked())
                        return false;
            }
            else
                for (CheckBox cb:checkBoxes)
                    if (cb.isChecked())
                        return false;
            if (isRadio)
                Toast.makeText(getApplicationContext(),"Выберите правильный вариант",Toast.LENGTH_LONG).show();
            else
                Toast.makeText(getApplicationContext(),"Выберите правильные варианты",Toast.LENGTH_LONG).show();
            return true;
        }

        void changeInput() {
            if (isRadio) {
                for (RadioButton rb:radioButtons)
                    rb.setVisibility(View.INVISIBLE);
                for (CheckBox cb:checkBoxes)
                    cb.setVisibility(View.VISIBLE);
                isRadio=false;
            }
            else {
                for (RadioButton rb:radioButtons)
                    rb.setVisibility(View.VISIBLE);
                for (CheckBox cb:checkBoxes)
                    cb.setVisibility(View.INVISIBLE);
                isRadio=true;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==SAVE) {
            setContentView(R.layout.progress_bar);

            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://loploplop3.herokuapp.com/addtest.php";

            StringRequest request = new StringRequest (Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("Server response", response);
                    Toast.makeText(getBaseContext(), "Тест создан", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Log.e("Error", error.getMessage());
                    Toast.makeText(getBaseContext(), "Извините, произошла ошибка", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Log.d("debugging", "params");
                    Map<String, String> params = new HashMap<>();

                    params.put("name",testName.getText().toString());
                    params.put("autor",getSharedPreferences("APP_DATA",MODE_PRIVATE).getString("second name","Фамилия")+
                            " "+getSharedPreferences("APP_DATA",MODE_PRIVATE).getString("first name","Имя"));
                    params.put("showWrong", String.valueOf(data.getBooleanExtra("showWrong",false)? 1 : 0));
                    params.put("anon", String.valueOf(data.getBooleanExtra("anonymity",false)? 1 : 0));
                    params.put("five", String.valueOf(data.getIntExtra("five",75)));
                    params.put("four", String.valueOf(data.getIntExtra("four",50)));
                    params.put("three", String.valueOf(data.getIntExtra("three",25)));
                    params.put("time", String.valueOf(data.getIntExtra("time",0)));
                    params.put("timer", String.valueOf(data.getBooleanExtra("timer", false)? 1 : 0));


                    ArrayList<String> questons = new ArrayList<>();
                    ArrayList<ArrayList<String>> options = new ArrayList<>();
                    ArrayList<ArrayList<Boolean>> rightAnswears = new ArrayList<>();
                    ArrayList<Boolean> isRadio = new ArrayList<>();
                    ArrayList<String> scores = new ArrayList<>();
                    ArrayList<String> photos = new ArrayList<>();

                    Gson gson = new Gson();

                    for (Task task:tasks) {
                        questons.add(task.question.getText().toString());

                        options.add(new ArrayList<String>());
                        for (EditText option:task.options)
                            options.get(options.size() - 1).add(option.getText().toString());

                        isRadio.add(task.isRadio);

                        rightAnswears.add(new ArrayList<Boolean>());
                        if (task.isRadio)
                            for (CompoundButton cb:task.radioButtons)
                                rightAnswears.get(rightAnswears.size() - 1).add(cb.isChecked());
                        else
                            for (CompoundButton cb:task.checkBoxes)
                                rightAnswears.get(rightAnswears.size() - 1).add(cb.isChecked());

                        scores.add(task.scores.getText().toString());

                        if (!task.imagePath.equals(" ")) {
                            Bitmap bitmap = BitmapFactory.decodeFile(task.imagePath);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] imageBytes = baos.toByteArray();
                            photos.add(gson.toJson(imageBytes));
                        }
                        else {
                            photos.add("null");
                        }
                    }

                    ArrayList<String> helpOptionArray = new ArrayList<>();
                    for (ArrayList<String> help : options)
                        helpOptionArray.add(gson.toJson(help.toArray()));
                    ArrayList<String> helpCorrectArray = new ArrayList<>();
                    for (ArrayList<Boolean> help : rightAnswears)
                        helpCorrectArray.add(gson.toJson(help.toArray()));

                    Calendar c = Calendar.getInstance();
                    String[] months = { "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
                    String date = c.get(Calendar.HOUR) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND) + " " + c.get(Calendar.DAY_OF_MONTH) + " " + months[c.get(Calendar.MONTH)];

                    params.put("date", date);
                    params.put("questions", gson.toJson(questons.toArray()));
                    params.put("options", gson.toJson(helpOptionArray.toArray()));
                    params.put("correct", gson.toJson(helpCorrectArray.toArray()));
                    params.put("radio", gson.toJson(isRadio.toArray()));
                    params.put("scores", gson.toJson(scores.toArray()));
                    params.put("photos", gson.toJson(photos.toArray()));

                    Log.d("converting", "params's been created");

                    return params;
                }
            };
            queue.add(request);

            Log.d("converting", "prefinish log");
        }
        else {
            try {
                if (requestCode == PICK_PHOTO && resultCode == RESULT_OK
                        && null != data) {

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;

                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    assert cursor != null;
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String imgDecodableString = cursor.getString(columnIndex);
                    cursor.close();

                    tasks.get(indexOfPhotoTask).image.setImageBitmap(BitmapFactory
                            .decodeFile(imgDecodableString/*, options*/));
                    tasks.get(indexOfPhotoTask).imagePath = imgDecodableString;
                    Log.d(COCO, "Photo's been added to " + indexOfPhotoTask + " task");
                }
            } catch (Exception ignored) {}
        }
    }

    @Override
    public void onBackPressed() {
        dialogBox();
    }

    public void dialogBox() {
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
}