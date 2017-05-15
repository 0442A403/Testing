package com.example.petro.newtesting;

import android.content.res.Resources;
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
    public Five(ImageView imageView) {
        five = imageView;
        w = Resources.getSystem().getDisplayMetrics().widthPixels;
        h = Resources.getSystem().getDisplayMetrics().heightPixels;
        unitX = ((float) w) / 1000;
        unitY = ((float) h) / 1000;
        teleport();
    }
    public void update() {
        five.setY(five.getY() + unitY * c);
        if (five.getY() >= h)
            teleport();
    }
    private void teleport() {
        five.setY(-unitY * new Random().nextInt(1001) - 400);
        five.setX(unitX * (new Random().nextInt(1001 + five.getWidth()) - five.getWidth()));
        c = new Random().nextInt(40) / 10.f + 2;
    }
}
