package com.example.idolgo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Entity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapTapi;
import com.skt.Tmap.TMapView;

import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import static java.net.Proxy.Type.HTTP;



public class Tmap extends FragmentActivity implements OnMapReadyCallback {
    PolylineOptions rectOptions;

    private MapView mapView = null;
    Context context = this;
    private GoogleMap mMap;
    private ArrayList<ArrayList<com.google.android.gms.maps.model.LatLng>> mapPoints;

    String it_startX, it_startY, it_endX, it_endY;
    int polycnt=0;
    String it_placename,it_path;
    String strUrl;
    String it_endX2, it_endY2;

    public void onMapReady(GoogleMap googleMap) {

        Log.i("ready", "map");
        mMap = googleMap;
        Log.i("polycnt", ""+polycnt);
        for(int i=0;i<mapPoints.size();i++){
            rectOptions = new PolylineOptions();
            rectOptions.addAll(mapPoints.get(i)).color(Color.parseColor("#8da2f0")); // Closes the polyline.
            mMap.addPolyline(rectOptions);
            Log.i("mappoints "+ i , "Gg");
        }

        LatLng start = new LatLng(Double.parseDouble(it_startY), Double.parseDouble(it_startX));
        LatLng end = new LatLng(Double.parseDouble(it_endY), Double.parseDouble(it_endX));



        mMap.addMarker(new MarkerOptions()
                .position(start)
                .title("Current Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.dep2)));
        mMap.addMarker(new MarkerOptions()
                .position(end)
                .title(it_placename)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.arr2)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(start));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));




    }
    public void goAr(View v){
        Intent it = new Intent(this, PedestrianAr.class);
        it.putExtra("it_endX", it_endX2);
        it.putExtra("it_endY", it_endY2);
        startActivity(it);



    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){


        View layout = inflater.inflate(R.layout.activity_tmap,container,false);
        mapView = (MapView)layout.findViewById(R.id.map);
        return layout;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmap);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Intent it = getIntent();
        it_placename = it.getStringExtra("it_placename");
        String it_time = it.getStringExtra("it_time");
        String it_distance = it.getStringExtra("it_distance");
        it_path = it.getStringExtra("it_path");
        it_startX = it.getStringExtra("it_startX");
        it_startY = it.getStringExtra("it_startY");
        it_endX = it.getStringExtra("it_endX");
        it_endY = it.getStringExtra("it_endY");


        try {
            JSONObject jsonPathObject = new JSONObject(it_path);
            JSONArray jArrSubpath = jsonPathObject.getJSONArray("subPath");
            Log.i("subpatharr", jArrSubpath.toString());
            int subpathcnt = jArrSubpath.length();
            Log.i("subpathcnt", "" + subpathcnt);

            String latlng[][] = new String[subpathcnt][6];//startx, starty, endx, endy, traffictype, jsonobj : passstop


            Log.i("1", "1");
            int cnt = 0;
            latlng[cnt][2] = it_startX;
            latlng[cnt][3] = it_startY;
            latlng[cnt][4] = "" + 3;
            cnt++;
            for (int j = 0; j < subpathcnt; j++) {
                Log.i("2", "2");
                JSONObject jsonObject = jArrSubpath.getJSONObject(j);
                Log.i("subpath" + j, jsonObject.toString());
                int trafficType = Integer.parseInt(jsonObject.getString("trafficType"));
                Log.i("traffictype", "" + trafficType);
                if (trafficType == 1 || trafficType == 2) {

                    latlng[cnt][0] = jsonObject.getString("startX");
                    latlng[cnt][1] = jsonObject.getString("startY");
                    latlng[cnt][2] = jsonObject.getString("endX");
                    latlng[cnt][3] = jsonObject.getString("endY");
                    /*
                  if(jsonObject.getString("endExitX")!=null){//지하철
                        latlng[cnt][2] = jsonObject.getString("endExitX");
                        latlng[cnt][3] = jsonObject.getString("endExitY");
                 }
                else{
                        latlng[cnt][2] = jsonObject.getString("endX");
                        latlng[cnt][3] = jsonObject.getString("endY");
                    }
                    */


                    latlng[cnt][4] = "" + 1;
                    JSONObject objpassstop = jsonObject.getJSONObject("passStopList");
                    latlng[cnt][5] = objpassstop.toString();
                    cnt++;
                }
            }
            latlng[cnt][0] = it_endX;
            latlng[cnt][1] = it_endY;
            it_endX2 = latlng[1][0];
            it_endY2 = latlng[1][1];


            Log.i("cnt 개수", ""+cnt);

            mapPoints = new ArrayList<ArrayList<LatLng>>();
            rectOptions = new PolylineOptions();
            for (int i = 0; i < cnt ; i++) {


                Log.i("tmap에서의 i", "" + i);

                Log.i("tmap onpost에서의 i", "" + i);
                final TmapPedestrian tmapPedestrian = new TmapPedestrian() {
                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        ArrayList<Location> pathpoints = getPathPoints();
                        ArrayList<LatLng> mapPoints = new ArrayList<com.google.android.gms.maps.model.LatLng>();
                        for (int k = 0; k < pathpoints.size(); k++)
                            mapPoints.add(new LatLng(pathpoints.get(k).getLatitude(), pathpoints.get(k).getLongitude()));


                        for (int i = 0; i < mapPoints.size(); i++)
                            Log.i("mappointsddd", mapPoints.get(i).toString());
                        PolylineOptions rectOptions = new PolylineOptions();
                        rectOptions.addAll(mapPoints).color(R.color.mainPink1);
                        mMap.addPolyline(rectOptions);
                    }
                };
                Log.i("startlat", latlng[i][2]);
                Log.i("startlong", latlng[i][3]);
                Log.i("endlat", latlng[i+1][0]);
                Log.i("endlong", latlng[i+1][1]);
                tmapPedestrian.execute(latlng[i][2], latlng[i][3], latlng[i + 1][0], latlng[i + 1][1]);
            }

////////////////////////////////////////////////////////////////////////////////////////////////////////

            for (int i = 1; i < cnt ; i++){

                ArrayList<LatLng> submapPoints = new ArrayList<LatLng>();

                LatLng point = new com.google.android.gms.maps.model.LatLng(Double.parseDouble(latlng[i][1]), Double.parseDouble(latlng[i][0]));

                submapPoints.add(point);

                JSONObject objpassstop = new JSONObject(latlng[i][5]);
                JSONArray stations = objpassstop.getJSONArray("stations");
                for (int k = 0; k < stations.length(); k++) {
                    point = new com.google.android.gms.maps.model.LatLng(Double.parseDouble(stations.getJSONObject(k).getString("y")), Double.parseDouble(stations.getJSONObject(k).getString("x")));
                    Log.i("passstopy", stations.getJSONObject(k).getString("y"));
                    Log.i("passstopx", stations.getJSONObject(k).getString("x"));
                    submapPoints.add(point);
                }

                point = new com.google.android.gms.maps.model.LatLng(Double.parseDouble(latlng[i][3]), Double.parseDouble(latlng[i][2]));

                submapPoints.add(point);

                Log.i("mappoints", "" + submapPoints.size());
                for (int k = 0; k < submapPoints.size(); k++)
                    Log.i("submappointsddd", submapPoints.get(k).toString());

                mapPoints.add(submapPoints);
                //  rectOptions[polycnt].addAll(mapPoints).color(Color.parseColor("#8da2f0")); // Closes the polyline.
                polycnt++;
                Log.i("polycnt",""+polycnt);


            }


        } catch (Exception e) {
        }


        // Get back the mutable Polyline


        TextView total = (TextView) findViewById(R.id.total);
        int total_time = Integer.parseInt(it_time);
        Double total_distance = Double.parseDouble(it_distance) / 1000;

        int total_hr, total_min;
        if (total_time >= 60) {
            total_hr = total_time / 60;
            total_min = total_time % 60;
            total.setText("Total    " + total_hr + "hours " + total_min + "min  " + total_distance + "km");
        } else {
            total.setText("Total    " + total_time + "min  " + total_distance + "km");
        }

    }

/*

        //     new DownloadWebpageTask().execute(strUrl);
/*

        LatLng startPoint = new LatLng(127.066847, 37.510350);
        LatLng endPoint = new LatLng(127.0254323, 37.497942);
        //  rt.getJsonData(startPoint,endPoint);

        // 20.2.2
*/












    public void detailed_route(View v) {




        Intent it = new Intent(context, PublicTransportDetail.class);

        it.putExtra("it_placename", it_placename);
        it.putExtra("it_path", it_path);
        startActivity(it);

    }
}



