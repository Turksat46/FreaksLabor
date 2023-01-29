package com.turksat46.freakslabor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class CompassView extends View {
    private float winkel = 0;
    private Paint drawingcolor = new Paint();
    Path path = new Path();

    public void setWinkel(float winkel){
        this.winkel = winkel;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        drawingcolor.setAntiAlias(true);
        drawingcolor.setColor(Color.WHITE);
        drawingcolor.setStyle(Paint.Style.FILL);
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        int length = Math.min(width, height);
        path.moveTo(0, -length/2);
        path.lineTo(length/20, length/2);
        path.lineTo(-length/20, length/2);
        path.close();
        canvas.translate(width/2, height/2);
        canvas.rotate(winkel);
        canvas.drawPath(path,drawingcolor);
    }

    public CompassView(Context context) {
        super(context);
    }

    public CompassView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CompassView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
