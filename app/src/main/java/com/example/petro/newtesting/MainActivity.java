package com.example.petro.newtesting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.net.InetAddress;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    public static final int SAVE = -145, DONE = 599;
    public static final String COCO = "coco";
    private static SharedPreferences sPref;
    private Five fives[];
    private Handler handler;
    private Timer timer;
    private TimerTask timerTask;
    private boolean shootingFives = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);;
        setContentView(R.layout.activity_main);
        sPref = getSharedPreferences("APP_DATA",MODE_PRIVATE);

        try {
            InetAddress ipAddr = InetAddress.getByName("https://loploplop3.herokuapp.com/");
        } catch (Exception ignored) {
        }

        findViewById(R.id.progressBar).setVisibility(View.GONE);
        if (sPref.getString("first name","").length()>0&&sPref.getString("second name","").length()>0)
            startMenu();
        else
            fillFields();
        fives = new Five[30];
        ViewGroup BALDESHLayout = ((ViewGroup)findViewById(R.id.BALDESH_layout));
        for (byte i = 0; i < 30; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(R.drawable.five);
            int m;
            if (i<12)
                m=22;
            else if (i<23)
                m=30;
            else
                m=37;
            int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, m, getResources().getDisplayMetrics());
            imageView.setLayoutParams(new LinearLayout.LayoutParams(px, ViewGroup.LayoutParams.WRAP_CONTENT));
            fives[i] = new Five(imageView);
            BALDESHLayout.addView(imageView);
        }
        handler = new Handler();
        timer = new Timer();
        timerTask = new MyTimerTask();
        timer.schedule(timerTask, 0, 1000 / 60);
    }

    private void fillFields() {
        findViewById(R.id.start_menu).setVisibility(View.GONE);
        findViewById(R.id.fill_names).setVisibility(View.VISIBLE);

        final EditText edit1=(EditText)findViewById(R.id.firstName),edit2=(EditText)findViewById(R.id.secondName);

        edit1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (edit1.getText().length()>0) {
                    edit2.requestFocus();

                }
                return true;
            }
        });

        edit2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                findViewById(R.id.filling_button).performClick();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edit2.getWindowToken(), 0);
                edit2.clearFocus();
                return true;
            }
        });

        findViewById(R.id.filling_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit1.length() > 0 && edit2.length() > 0) {
                    SharedPreferences.Editor editor = sPref.edit();
                    editor.putString("first name", edit1.getText().toString());
                    editor.putString("second name", edit2.getText().toString());
                    editor.apply();
                    startMenu();
                } else
                    Toast.makeText(getApplicationContext(), "Пожалуйста, заполните поля", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void startMenu() {
        findViewById(R.id.start_menu).setVisibility(View.VISIBLE);
        findViewById(R.id.fill_names).setVisibility(View.GONE);

        TextView searchB = (TextView) findViewById(R.id.searchingButton);
        TextView createB = (TextView) findViewById(R.id.creatingButton);
        TextView seeResults = (TextView) findViewById(R.id.acitivity_main_my_tests);

        ((TextView) findViewById(R.id.welcome)).setText("Здравствуй, " + sPref.getString("second name", "")
                + " " + sPref.getString("first name", ""));
        searchB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, SearchingActivity.class),1);
            }
        });
        createB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CreatingActivity.class));
            }
        });
        seeResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,MyTestsActivity.class));
            }
        });
        findViewById(R.id.edit_names).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sPref.edit();
                editor.putString("first name","");
                editor.putString("second name","");
                editor.apply();
                fillFields();
            }
        });
    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (shootingFives) {
                        for (Five five : fives)
                            five.update();
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==DONE) {
            Intent intent=new Intent(MainActivity.this, MarkActivity.class);
            intent.putExtra("id", data.getIntExtra("id", -1));
            intent.putExtra("mark", data.getIntExtra("mark", -1));
            intent.putExtra("Show wrong", data.getIntExtra("Show wrong", 0));
            startActivity(intent);
        }
        startMenu();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    @Override
    protected void onPause() {
        shootingFives = false;
        super.onPause();
    }

    @Override
    protected void onResume() {
        shootingFives = true;
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        timer.cancel();
        timerTask.cancel();
        super.onDestroy();
    }

    private class Five {
        ImageView five;
        float unitX,unitY;
        int  w ,h;
        float c;
        boolean disappearing;
        int alpha;
        Five(ImageView imageView) {
            five = imageView;
            w = Resources.getSystem().getDisplayMetrics().widthPixels;
            h = Resources.getSystem().getDisplayMetrics().heightPixels;
            unitX = w / 1000.f;
            unitY = h / 1000.f;
            teleport();
            five.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    disappearing = true;
                    return false;
                }
            });
        }
        void update() {
            five.setY(five.getY() + unitY * c);
            if (disappearing) {
                alpha -= 7;
                five.setImageAlpha(alpha);
            }
            if (five.getY() >= h || alpha <= 0)
                teleport();
        }
        void teleport() {
            five.setY(-unitY * new Random().nextInt(1001) - 400);
            five.setX(unitX * (new Random().nextInt(1001 + five.getWidth()) - five.getWidth()));
            c = new Random().nextInt(40) / 10.f + 2;
            alpha = 255;
            disappearing = false;
        }
    }
}