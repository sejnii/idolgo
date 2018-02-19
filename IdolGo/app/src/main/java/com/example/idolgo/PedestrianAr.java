package com.example.idolgo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.ArrayList;

public class PedestrianAr extends AppCompatActivity implements SurfaceHolder.Callback
 {


     PinkArrowSetting pas;
     Camera camera;
     SurfaceView surfaceView;
     SurfaceHolder surfaceHolder;
     boolean previewing = false;

     Context context = this;


     String startX, startY, endX, endY;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedestrian_ar);
        Intent it = getIntent();
        endX = it.getStringExtra("it_endX");
        endY = it.getStringExtra("it_endY");

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFormat(PixelFormat.UNKNOWN);
        surfaceView = (SurfaceView)findViewById(R.id.camerapreview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


         pas = new PinkArrowSetting(this.getApplicationContext(), this);
         pas.endlat = Double.parseDouble(endY);
         pas.endlong = Double.parseDouble(endX);
        startX=Double.toString(pas.longitude);
        startY = Double.toString(pas.latitude);
        TmapPedestrian tmp = new TmapPedestrian(){
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                ArrayList<Location> pathPoints = getPathPoints();
                pas.setPathPoints(pathPoints);
            }
        };

        tmp.execute(startX, startY, endX, endY);

    }

     public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
     {
         // TODO Auto-generated method stub
         if(previewing)
          { camera.stopPreview();
          previewing = false; }
          if (camera != null)
          {
              try { camera.setPreviewDisplay(surfaceHolder);
                  camera.startPreview();
                  previewing = true;
              }
              catch (IOException e) {
                  // TODO Auto-generated catch block
                  //
                  e.printStackTrace();
              }
          }
     }
     @Override
     public void surfaceCreated(SurfaceHolder holder)
     { // TODO Auto-generated method stub
          camera = Camera.open();
          camera.setDisplayOrientation(90);
     }
     @Override
     public void surfaceDestroyed(SurfaceHolder holder)
     { // TODO Auto-generated method stub
          camera.stopPreview();
          camera.release();
          camera = null;
          previewing = false; }


}
