package com.turksat46.freakslabor.FreaksAR;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class OverlayDebuggingTool extends View {


    SensorManager sensorManager;
    Sensor sensor;
    private Paint drawingcolor = new Paint();
    Path path = new Path();

    float azimut, polar;

    public void setParameters(float azimutKamera, float polarKamera){
        //TODO: change position on frame
        invalidate();
    }

    public OverlayDebuggingTool(Context context) {
        super(context);
    }

    public OverlayDebuggingTool(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public OverlayDebuggingTool(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawingcolor.setAntiAlias(true);
        drawingcolor.setColor(Color.WHITE);
        drawingcolor.setStyle(Paint.Style.FILL);
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        int length = Math.min(width, height);
        path.moveTo(0, -length/4);
        path.lineTo(length/40, length/4);
        path.lineTo(-length/40, length/4);
        path.close();
        //canvas.translate(, );

        canvas.drawPath(path,drawingcolor);
    }

}
