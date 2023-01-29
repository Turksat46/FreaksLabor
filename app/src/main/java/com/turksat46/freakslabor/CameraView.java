package com.turksat46.freakslabor;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.io.IOException;

public class CameraView extends SurfaceView  implements SurfaceHolder.Callback, Camera.PreviewCallback {

    SurfaceHolder surfaceHolder;
    Camera camera;

    public CameraView(Context context) {
        super(context);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        camera = Camera.open();
        camera.setDisplayOrientation(90);
        //camera.setOneShotPreviewCallback();
        camera.startPreview();
        try {
            camera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            camera.release();
            camera = null;
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        try {
            Camera.Parameters parameters = camera.getParameters();
            //parameters.setPreviewSize(i1, i2);
            parameters.setPreviewSize(1080, 1920);
            camera.setParameters(parameters);
            camera.startPreview();
        }catch (Exception e){
            Log.w("CameraView", "Exception:", e);
        }
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    public void setOneShotPreviewCallback(Camera.PreviewCallback callback){
        if(camera != null){
            camera.setOneShotPreviewCallback(callback);
        }
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {

    }
}
