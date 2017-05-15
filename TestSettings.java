package com.example.petro.newtesting;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import static com.example.petro.newtesting.MainActivity.RETURN;
import static com.example.petro.newtesting.MainActivity.SAVE;

public class TestSettings extends AppCompatActivity {
    TextView fifthTV,fourthTV,thirdTV;
    SeekBar fifthSB,fourthSB,thirdSB;
    CheckBox setTimer;
    Button comlete;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        setResult(RETURN);
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_settings);
        setTitle("Оценивание");
        getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        fifthTV=(TextView)findViewById(R.id.fifthDecorator);
        fourthTV=(TextView)findViewById(R.id.fourthDecorator);
        thirdTV=(TextView)findViewById(R.id.thirdDecorator);
        fifthSB=(SeekBar)findViewById(R.id.fifthSeekBar);
        fourthSB=(SeekBar)findViewById(R.id.fourthSeekBar);
        thirdSB=(SeekBar)findViewById(R.id.thirdSeekBar);

        fifthSB.setOnSeekBarChangeListener(new ShortCutSeekBarListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress>1) {
                    fifthTV.setText(progress+"%");
                    if (progress<=fourthSB.getProgress())
                        fourthSB.setProgress(progress-1);
                }
                else
                    seekBar.setProgress(2);
            }
        });

        fourthSB.setOnSeekBarChangeListener(new ShortCutSeekBarListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress>0&&progress<100) {
                    fourthTV.setText(progress+"%");
                    if (progress<=thirdSB.getProgress())
                        thirdSB.setProgress(progress-1);
                    else if (progress>=fifthSB.getProgress())
                        fifthSB.setProgress(progress+1);
                }
                else
                    seekBar.setProgress(progress==0?1:99);
            }
        });

        thirdSB.setOnSeekBarChangeListener(new ShortCutSeekBarListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress<99) {
                    thirdTV.setText(progress+"%");
                    if (progress >= fourthSB.getProgress())
                        fourthSB.setProgress(progress+1);
                }
                else
                    thirdSB.setProgress(98);
            }
        });

        setTimer = (CheckBox) findViewById(R.id.set_timer);
        final RelativeLayout timer = (RelativeLayout) findViewById(R.id.layout_set_timer);
        final EditText minutes = (EditText) findViewById(R.id.timer_minutes);
        final EditText seconds = (EditText) findViewById(R.id.timer_seconds);
        seconds.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (Integer.valueOf(s.toString()) > 59)
                        seconds.setText(seconds.getText().toString().substring(0, seconds.getText().toString().length() - 1));
                } catch (Exception e) {};
            }
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        });
        setTimer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    timer.setVisibility(View.VISIBLE);
                else
                    timer.setVisibility(View.GONE);
            }
        });

        minutes.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                seconds.requestFocus();
                return false;
            }
        });

        findViewById(R.id.complete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int time = 0;
                if (minutes.length()>0)
                    time += Integer.valueOf(minutes.getText().toString())*60;
                if (seconds.length()>0)
                    time += Integer.valueOf(seconds.getText().toString());
                if (time<300 && setTimer.isChecked()) {
                    dialogBox(time);
                    return;
                }
                Intent intent = getIntent();
                intent.putExtra("five",fifthSB.getProgress());
                intent.putExtra("four",fourthSB.getProgress());
                intent.putExtra("three",thirdSB.getProgress());
                intent.putExtra("showWrong",((CheckBox)findViewById(R.id.showWrong)).isChecked());
                intent.putExtra("anonymity",!((CompoundButton)findViewById(R.id.anonymity)).isChecked());
                intent.putExtra("time",time);
                intent.putExtra("timer",setTimer.isChecked());
                setResult(SAVE, intent);
                finish();
            }
        });
    }

    public void dialogBox(final int time) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Вы выбрали время меньше 5 минут. Сохранить?");
        alertDialogBuilder.setPositiveButton("ОК",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent=getIntent();
                        intent.putExtra("five",fifthSB.getProgress());
                        intent.putExtra("four",fourthSB.getProgress());
                        intent.putExtra("three",thirdSB.getProgress());
                        intent.putExtra("showWrong",((CheckBox)findViewById(R.id.showWrong)).isChecked());
                        intent.putExtra("anonymity",!((CompoundButton)findViewById(R.id.anonymity)).isChecked());
                        intent.putExtra("timer",setTimer.isChecked());
                        intent.putExtra("time",time);
                        setResult(SAVE,intent);
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

    private class ShortCutSeekBarListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    }
}
