package com.example.petro.newtesting;

import android.content.res.Resources;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.util.Random;

/**
 * Created by petro on 22.04.2017.
 */

public class Five {
    public ImageView five;
    private float unitX,unitY;
    private int  w ,h;
    private float c;
    private boolean disappearing;
    private int alpha;
    public Five(ImageView imageView) {
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
    public void update() {
        five.setY(five.getY() + unitY * c);
        if (disappearing) {
            alpha -= 6;
            five.setImageAlpha(alpha);
        }
        if (five.getY() >= h || alpha <= 0)
            teleport();
    }
    private void teleport() {
        five.setY(-unitY * new Random().nextInt(1001) - 400);
        five.setX(unitX * (new Random().nextInt(1001 + five.getWidth()) - five.getWidth()));
        c = new Random().nextInt(40) / 10.f + 2;
        alpha = 255;
        disappearing = false;
    }
}
