package com.maradroid.brmbrm;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by mara on 10/8/15.
 */
public class ControlPadBorderView extends View {

    Paint bigCircle;

    public ControlPadBorderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        bigCircle = new Paint();
        bigCircle.setColor(Color.BLUE);
        bigCircle.setAntiAlias(true);
        bigCircle.setStyle(Paint.Style.STROKE);
        bigCircle.setStrokeWidth(3);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        bigCircle = new Paint();
        bigCircle.setColor(Color.BLUE);
        bigCircle.setAntiAlias(true);
        bigCircle.setStyle(Paint.Style.STROKE);
        bigCircle.setStrokeWidth(3);

        canvas.drawCircle(getRight() / 2, getBottom() / 2, getRight() / 3, bigCircle);
    }
}
