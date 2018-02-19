package com.example.idolgo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Entity;
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



public class Tmap extends FragmentActivity implements OnMapReadyCallback  {

    private GoogleMap mMap;
    private ArrayList<com.google.android.gms.maps.model.LatLng> mapPoints;
    private MapView mapView = null;
    String strUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmap);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        try{
        strUrl = "https://api2.sktelecom.com/tmap/routes/pedestrian?version-1&format=json" +
                "&startName=" + URLEncoder.encode("출발지","UTF-8") +
                "&endName=" + URLEncoder.encode("도착지","UTF-8") +
                "&startX=127.066847&startY=37.510350" +
                "&endX=127.0254323&endY=37.497942" +
                "&appKey=5a00bd31-9b20-44ce-a868-fc4696dfa3a1";
        }
                catch(Exception E){

                }
        new DownloadWebpageTask().execute(strUrl);


        LatLng startPoint = new LatLng(127.066847, 37.510350);
        LatLng endPoint = new LatLng(127.0254323,37.497942);
      //  rt.getJsonData(startPoint,endPoint);

        // 20.2.2

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){


        View layout = inflater.inflate(R.layout.activity_tmap,container,false);
        mapView = (MapView)layout.findViewById(R.id.map);
        return layout;
    }


    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {


        protected String doInBackground(String... urls) {

            try {
                Log.i("tag", "doinbackground");
                return (String) downloadUrl((String) urls[0]);
            } catch (IOException e) {
                return "다운로드 실패";
            }
        }

        protected void onPostExecute(String result) {

            int totalDistance=0;
            try {
                JSONObject jAr = new JSONObject(result);


                JSONArray features = jAr.getJSONArray("features");

                mapPoints = new ArrayList<>();
                for (int i = 0; i < features.length(); i++) {
                    JSONObject test2 = features.getJSONObject(i);
                    if (i == 0) {
                        JSONObject properties = test2.getJSONObject("properties");
                        totalDistance += properties.getInt("totalDistance");
                    }
                    JSONObject geometry = test2.getJSONObject("geometry");
                    JSONArray coordinates = geometry.getJSONArray("coordinates");


                    String geoType = geometry.getString("type");
                    if (geoType.equals("Point")) {
                        double lonJson = coordinates.getDouble(0);
                        double latJson = coordinates.getDouble(1);


                        Log.d("long lat", lonJson + "," + latJson + "\n");
                        com.google.android.gms.maps.model.LatLng point = new com.google.android.gms.maps.model.LatLng(latJson, lonJson);
                        mapPoints.add(point);

                    }
                    if (geoType.equals("LineString")) {
                        for (int j = 0; j < coordinates.length(); j++) {
                            JSONArray JLinePoint = coordinates.getJSONArray(j);
                            double lonJson = JLinePoint.getDouble(0);
                            double latJson = JLinePoint.getDouble(1);


                            Log.d("long, lat", lonJson + "," + latJson + "\n");
                            com.google.android.gms.maps.model.LatLng point = new com.google.android.gms.maps.model.LatLng(latJson, lonJson);

                            mapPoints.add(point);

                        }
                    }
                }
            }
            catch(Exception e){

            }
            PolylineOptions rectOptions = new PolylineOptions()
            .addAll(mapPoints); // Closes the polyline.

// Get back the mutable Polyline
            Polyline polyline = mMap.addPolyline(rectOptions);

        }
        private String downloadUrl(String myurl) throws IOException {


            Log.i("tag", "downloadURL");
            String uri = myurl;
            BufferedReader bufferedReader = null;
            HttpURLConnection con=null;
            try {
                URL url = new URL(uri);
                con = (HttpURLConnection) url.openConnection();
                StringBuilder sb = new StringBuilder();
                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String json;
                while ((json = bufferedReader.readLine()) != null) {
                    sb.append(json + "\n");
                    Log.i("str", json);
                }

                return sb.toString().trim();

            } catch (Exception e) {
                Log.i("err", "연결");
                return null;
            }

            finally {
                con.disconnect();

            }


        }
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

}
