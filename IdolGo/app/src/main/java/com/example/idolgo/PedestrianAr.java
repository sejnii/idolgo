package com.example.idolgo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;

public class PedestrianAr extends AppCompatActivity implements SurfaceHolder.Callback, OnMapReadyCallback, LocationListener
{

    Location location;
    LocationManager manager;
    Double latitude, longitude;
    GoogleMap mMap;
    PinkArrowSetting pas;
    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    boolean previewing = false;
    Boolean isfinal;
    ArrayList<LatLng> mapPoints;
    Context context = this;
    private Marker currentMarker = null;


    String startX, startY, endX, endY;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedestrian_ar);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.maponar);
        mapFragment.getMapAsync(this);

        startLocationService();
        float height = getApplicationContext().getResources().getDisplayMetrics().heightPixels;
        float width = getApplicationContext().getResources().getDisplayMetrics().widthPixels;



        ViewGroup.LayoutParams params = mapFragment.getView().getLayoutParams();
        params.height=500;

        params.width = Math.round(width/2);

        Intent it = getIntent();
        isfinal =  it.getBooleanExtra("it_isfinal", false);
        Log.i("isfinal",isfinal.toString());
        endX = it.getStringExtra("it_endX");
        endY = it.getStringExtra("it_endY");

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFormat(PixelFormat.UNKNOWN);
        surfaceView = (SurfaceView)findViewById(R.id.camerapreview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


        pas = new PinkArrowSetting(context, this);
        pas.endlat = Double.parseDouble(endY);
        pas.endlong = Double.parseDouble(endX);
        pas.isfinal = isfinal;
        startX=Double.toString(pas.longitude);
        startY = Double.toString(pas.latitude);
        TmapPedestrian tmp = new TmapPedestrian(){
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                ArrayList<Location> pathPoints = getPathPoints();
                mapPoints = new ArrayList<LatLng>();
                for(int i=0;i<pathPoints.size();i++){
                    mapPoints.add(new LatLng(pathPoints.get(i).getLatitude(), pathPoints.get(i).getLongitude()));
                }
                pas.setPathPoints(pathPoints);


                PolylineOptions rectOptions = new PolylineOptions();
                rectOptions.addAll(mapPoints).color(Color.parseColor("#8da2f0")); // Closes the polyline.
                mMap.addPolyline(rectOptions);
            }
        };

        tmp.execute(startX, startY, endX, endY);

    }


    private Location startLocationService() {

        Log.i("startlocaionservice", "start");
        manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        long minTime = 1000;
        float minDistance = 1;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "need permission", Toast.LENGTH_LONG);

            return null;
        } else {
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                showSettingsAlert();
        }

        Boolean isGPSEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // 현재 네트워크 상태 값 알아오기
        Boolean isNetworkEnabled = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


        Log.i("gps",""+isGPSEnabled);
        Log.i("net", ""+isNetworkEnabled);
        Log.i("startlocaionservice2", "start22222222222222");


        if (isNetworkEnabled) {
            manager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    1000,
                    1, this);

            if (manager != null) {
                location = manager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    // 위도 경도 저장
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }
        }

        if (isGPSEnabled) {


            if (location == null) {
                manager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        1000,
                        1, this);
                if (manager != null) {
                    location = manager
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
            }
        }

        Log.i("lat", "" + latitude);
        Log.i("long", "" + longitude);

        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, this);
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, this);

        return location;

    }
    public void showSettingsAlert() {
        {
            // GPS OFF 일때 Dialog 표시
            new MaterialDialog.Builder(context).title("Location Service Settings").content("Setting GPS Service").positiveText("Confirm").onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    // GPS설정 화면으로 이동
                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    context.startActivity(intent);
                }
            }).backgroundColor(Color.parseColor("#bbbcbf")).cancelable(false).canceledOnTouchOutside(false).show();

        }
    }


    public void onMapReady(GoogleMap map) {

        mMap = map;
        LatLng start = new LatLng(pas.latitude,pas.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(start));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(17));
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

    }









    public void onStatusChanged(String s, int i, Bundle bundle){

    }




    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
    public  void setCurrentLocation(Double lat, Double lon){

        if ( currentMarker != null ) currentMarker.remove();

        LatLng currentLocation = new LatLng(lat,lon);

        MarkerOptions markerOptions = new MarkerOptions();

        markerOptions.position(currentLocation);

        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
        currentMarker = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));

        return;




    }
    public void onLocationChanged(Location location){
        setCurrentLocation(location.getLatitude(), location.getLongitude());
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