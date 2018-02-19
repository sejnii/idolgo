package com.example.idolgo;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by 세진 on 2018-02-11.
 */
//////////////////tmap 연결해서 도보 point 받아오기///////////////////////
public class TmapPedestrian extends AsyncTask<String, Void, Void> {


    ArrayList<Location> pathPoints;

    String result;




        protected Void doInBackground(String... params) {

            try {
                Log.i("tag", "doinbackground");
                Log.i("param",params[0]+" "+params[1]+" "+params[2]+" "+params[3]);

                downloadUrl( params[0], params[1], params[2], params[3]);

            } catch (Exception e) {

            }
            return null;
        }

        protected ArrayList<Location> getPathPoints() {

            int totalDistance = 0;
            pathPoints = new ArrayList<Location>();
            try {
                JSONObject jAr = new JSONObject(result);
                Log.i("jar", jAr.toString());


                JSONArray features = jAr.getJSONArray("features");


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


                        Log.d("lat long ", latJson + "," + lonJson + "\n");
                        Location location = new Location("point");
                        location.setLongitude(lonJson);
                        location.setLatitude(latJson);
                        pathPoints.add(location);
                        com.google.android.gms.maps.model.LatLng point = new com.google.android.gms.maps.model.LatLng(latJson, lonJson);
                        //mapPoints.add(point);

                    }
                    if (geoType.equals("LineString")) {
                        for (int j = 0; j < coordinates.length(); j++) {
                            JSONArray JLinePoint = coordinates.getJSONArray(j);
                            double lonJson = JLinePoint.getDouble(0);
                            double latJson = JLinePoint.getDouble(1);
                            Location location = new Location("line");
                            location.setLongitude(lonJson);
                            location.setLatitude(latJson);

                           //if(j==0  || j==coordinates.length()-1)
                               pathPoints.add(location);


                            com.google.android.gms.maps.model.LatLng point = new com.google.android.gms.maps.model.LatLng(latJson, lonJson);

                            //   mapPoints.add(point);

                        }

                    }
                }
            } catch (Exception e) {

            }
            return pathPoints;
        }


        private String downloadUrl(String startX, String startY, String endX,String endY) throws IOException {


            Log.i("tag", "downloadURL");
            String myurl = "https://api2.sktelecom.com/tmap/routes/pedestrian?version-1&format=json" +
                    "&startName=" + URLEncoder.encode("출발지", "UTF-8") +
                    "&endName=" + URLEncoder.encode("도착지", "UTF-8") +
                    "&startX=" + startX +
                    "&startY=" + startY +
                    "&endX=" + endX+
                    "&endY=" + endY +
                    "&appKey=5a00bd31-9b20-44ce-a868-fc4696dfa3a1";
            String uri = myurl;
            BufferedReader bufferedReader = null;
            HttpURLConnection con = null;
            try {
                URL url = new URL(uri);
                con = (HttpURLConnection) url.openConnection();
                con.connect();
                StringBuilder sb = new StringBuilder();
                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String json;
                while ((json = bufferedReader.readLine()) != null) {
                    sb.append(json + "\n");
                    Log.i("str", json);
                }

                result = sb.toString().trim();
                return sb.toString().trim();

            } catch (Exception e) {
                Log.i("err", "연결");
                return null;
            } finally {
                con.disconnect();

            }


        }


}
