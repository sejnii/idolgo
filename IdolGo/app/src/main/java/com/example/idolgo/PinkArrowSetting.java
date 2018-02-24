package com.example.idolgo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

/**
 * Created by 세진 on 2018-02-11.
 */
//////////////////사용자 위치 받아오기, 각 point와 사용자 위치 계산해서 arrow 회전 등등 ////////////
public class PinkArrowSetting implements SensorEventListener, LocationListener {

    SensorManager sensorManager;
    Sensor sensor;
    Context context;
    LocationManager manager;
    Activity activity;
    ArrayList<Location> pathPoints;
    int cntpoint = 0;
    float mybearing;
    Double endlat, endlong;
    Double destDist;
    ImageView pinkarrow;
    Double latitude, longitude;
    Location location;
    TextView tv1, tv2;
    String tv1str;
    boolean isfinal;

    public PinkArrowSetting(Context context, PedestrianAr activity) {

        this.context = context;
        this.activity = activity;
        pinkarrow = activity.findViewById(R.id.pinkarrow);
        tv1 = activity.findViewById(R.id.tv_pointdist);
        tv2 = activity.findViewById(R.id.tv_totaldist);
        float height = activity.getApplicationContext().getResources().getDisplayMetrics().heightPixels;
        float width = activity.getApplicationContext().getResources().getDisplayMetrics().widthPixels;
        float imgHeight = pinkarrow.getLayoutParams().height;
        pinkarrow.setX(500);
        pinkarrow.setY((height - imgHeight) / 2 + (-(-90 + 90) / (float) 90) * (height));
        startLocationService();
        sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sensorManager.registerListener(this, sensor, sensorManager.SENSOR_DELAY_UI);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            if (pathPoints != null) {
                Double pointDistance = distance(latitude, longitude, pathPoints.get(cntpoint).getLatitude(), pathPoints.get(cntpoint).getLongitude());
                Log.i("pointdistnace", "" + pointDistance);
                destDist = distance(latitude, longitude, endlat, endlong);//전체길이
                tv2.setText("About " + destDist + "m");
                Log.i("destdist", "" + destDist);
                Log.i("pathpoint 몇개?", "" + cntpoint);

                if (pointDistance <= 10 && cntpoint < pathPoints.size() - 1) {
                    Log.i("cntpoint", "" + cntpoint);
                    cntpoint++; //다음 pathpoint로 이동

                } else {
                    tv1str = "About " + pointDistance + "m";//point까지 거리
                    tv1.setText(tv1str);
                }

                mybearing = event.values[0];
                Double bearing = bearingP1toP2(latitude, longitude, pathPoints.get(cntpoint).getLatitude(), pathPoints.get(cntpoint).getLongitude());
                Double pointbearing = mybearing - bearing;//내 각도 ~ point각도

                if (pointbearing > 0)
                    while (pointbearing > 180)
                        pointbearing -= 360;
                else
                    while (pointbearing < -180)
                        pointbearing += 360;

                Log.i("point bearing", "" + pointbearing);
                if (pointbearing > -45 && pointbearing <= 45)
                    pinkarrow.setRotation(0);//그대로
                else if (pointbearing > 45 && pointbearing <= 135)
                    pinkarrow.setRotation(-90);//-90도 회전
                else if (pointbearing > -135 && pointbearing <= -45)
                    pinkarrow.setRotation(90);//90도회전
                else
                    pinkarrow.setRotation(180);//180도회전

            }
        }
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

        Log.i("gps", "" + isGPSEnabled);
        Log.i("net", "" + isNetworkEnabled);
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

    public double bearingP1toP2(double P1_latitude, double P1_longitude, double P2_latitude, double
            P2_longitude) {
        // 현재 위치 : 위도나 경도는 지구 중심을 기반으로 하는 각도이기 때문에
        //라디안 각도로 변환한다.
        double Cur_Lat_radian = P1_latitude * (3.141592 / 180);
        double Cur_Lon_radian = P1_longitude * (3.141592 / 180);
        // 목표 위치 : 위도나 경도는 지구 중심을 기반으로 하는 각도이기 때문에
        // 라디안 각도로 변환한다.
        double Dest_Lat_radian = P2_latitude * (3.141592 / 180);
        double Dest_Lon_radian = P2_longitude * (3.141592 / 180);
        double radian_distance = 0;
        radian_distance = Math.acos(Math.sin(Cur_Lat_radian) * Math.sin(Dest_Lat_radian) + Math.cos
                (Cur_Lat_radian) * Math.cos(Dest_Lat_radian) * Math.cos(Cur_Lon_radian - Dest_Lon_radian));
        // 목적지 이동 방향을 구한다.(현재 좌표에서 다음 좌표로 이동하기 위해서는
        //방향을 설정해야 한다. 라디안값이다.
        double radian_bearing = Math.acos((Math.sin(Dest_Lat_radian) - Math.sin(Cur_Lat_radian) * Math.cos
                (radian_distance)) / (Math.cos(Cur_Lat_radian) * Math.sin(radian_distance)));
        // acos의 인수로 주어지는 x는 360분법의 각도가 아닌 radian(호도)값이다.
        double true_bearing = 0;
        if (Math.sin(Dest_Lon_radian - Cur_Lon_radian) < 0) {
            true_bearing = radian_bearing * (180 / 3.141592);
            true_bearing = 360 - true_bearing;
        } else {
            true_bearing = radian_bearing * (180 / 3.141592);
        }
        return true_bearing;
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344; //mile->km
        dist = dist * 1000; //km -> m
        dist = Math.round((dist * 100) / 100.0);
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        if (pathPoints != null) {
            //user위치로 부터 ~ 각 path point까지 거리
            Double pointDistance = distance(latitude, longitude, pathPoints.get(cntpoint).getLatitude(), pathPoints.get(cntpoint).getLongitude());
            Log.i("pointdistnace", "" + pointDistance);
            destDist = distance(latitude, longitude, endlat, endlong);
            Log.i("destdist", "" + destDist);
            Log.i("pathpoint 몇개?", "" + pathPoints.size());
            if (pointDistance <= 3 && cntpoint < pathPoints.size() - 1) {
                Log.i("cntpoint", "" + cntpoint);
                cntpoint++; //다음 pathpoint로 이동
                //북쪽각도 ~ point와 현재위치 직선 각도
                Double bearing = bearingP1toP2(latitude, longitude, pathPoints.get(cntpoint).getLatitude(), pathPoints.get(cntpoint).getLongitude());
                Double pointbearing = mybearing - bearing;//내 각도 ~ point각도

                if (pointbearing > 0)
                    while (pointbearing > 180)
                        pointbearing -= 360;
                else
                    while (pointbearing < -180)
                        pointbearing += 360;

                if (pointbearing > -45 && pointbearing <= 45)
                    pinkarrow.setRotation(0);//그대로
                else if (pointbearing > 45 && pointbearing <= 135)
                    pinkarrow.setRotation(90);//90도 회전
                else if (pointbearing > -135 && pointbearing <= -45)
                    pinkarrow.setRotation(-90);//-90도회전
                else
                    pinkarrow.setRotation(180);//180도회전

            }
            if (destDist <= 5) {//최종목적지까지 거리
                Toast.makeText(context, "목적지도착", Toast.LENGTH_LONG).show();
                if (isfinal == false) {
                    new MaterialDialog.Builder(context).title("Arrived At Your Destination").content("End the AR guidance service")
                            .positiveText("Confirm").onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            activity.finish();
                        }
                    }).backgroundColor(Color.parseColor("#bbbcbf")).cancelable(false).canceledOnTouchOutside(false).show();
                }
            }
        }
    }

    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public void setPathPoints(ArrayList<Location> pathPoints) {
        this.pathPoints = pathPoints;
    }


}