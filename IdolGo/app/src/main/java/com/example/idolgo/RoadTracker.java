package com.example.idolgo;

import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 2018-02-09.
 */

public class RoadTracker {
    private static final String TAG = "RoadTracker";
    private GoogleMap mMap;
    private Tmap Tmap;

    //private GeoApiContext mContext;

    private ArrayList<LatLng> mCapturedLocations = new ArrayList<LatLng>();        //지나간 좌표 들을 저장하는 List
    private static final int PAGINATION_OVERLAP = 5;
    private static final int PAGE_SIZE_LIMIT = 100;
    private ArrayList<com.google.android.gms.maps.model.LatLng> mapPoints;

    int totalDistance;

    public RoadTracker(GoogleMap map) {
        mMap = map;
    }

    public ArrayList<com.google.android.gms.maps.model.LatLng> getJsonData(final LatLng startPoint, final LatLng endPoint) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                HttpClient httpClient = new DefaultHttpClient();

                Log.i("error", "!");
                String urlString = "https://api2.sktelecom.com/tmap/routes/pedestrian?version=1&format=json&appKey=ef0c9336-185f-4e6f-9691-fda9827f7276";
                try {
                    URI uri = new URI(urlString);

                    HttpClient client = new DefaultHttpClient();
                    //HttpClient httpclient = HttpClients.createDefault();
                    HttpPost httpPost = new HttpPost(urlString);
                    // httpPost.setURI(uri);

                    Log.i("error", "2");
                    List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("startX", Double.toString(startPoint.latitude)));
                    nameValuePairs.add(new BasicNameValuePair("startY", Double.toString(startPoint.longitude)));

                    nameValuePairs.add(new BasicNameValuePair("endX", Double.toString(endPoint.latitude)));
                    nameValuePairs.add(new BasicNameValuePair("endY", Double.toString(endPoint.longitude)));

                    nameValuePairs.add(new BasicNameValuePair("startName", "출발지"));
                    nameValuePairs.add(new BasicNameValuePair("endName", "도착지"));

                    nameValuePairs.add(new BasicNameValuePair("reqCoordType", "WGS84GEO"));
                    nameValuePairs.add(new BasicNameValuePair("resCoordType", "WGS84GEO"));

                    Log.i("error", "3");
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

                    HttpResponse response = httpClient.execute(httpPost);

                    Log.i("error", "4");


                    int code = response.getStatusLine().getStatusCode();
                    String message = response.getStatusLine().getReasonPhrase();
                    Log.i(TAG, "run: " + message);
                    String responseString;
                    if (response.getEntity() != null)
                        responseString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.toString());
                    else
                        return;
                    String strData = "";

                    Log.i("responseString", responseString);

                    JSONObject jAr = new JSONObject(responseString);

                    Log.d(TAG, "1\n");

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

                            Log.d(TAG, "-");
                            Log.d(TAG, lonJson + "," + latJson + "\n");
                            com.google.android.gms.maps.model.LatLng point = new com.google.android.gms.maps.model.LatLng(latJson, lonJson);
                            mapPoints.add(point);

                        }
                        if (geoType.equals("LineString")) {
                            for (int j = 0; j < coordinates.length(); j++) {
                                JSONArray JLinePoint = coordinates.getJSONArray(j);
                                double lonJson = JLinePoint.getDouble(0);
                                double latJson = JLinePoint.getDouble(1);

                                Log.d(TAG, "-");
                                Log.d(TAG, lonJson + "," + latJson + "\n");
                                com.google.android.gms.maps.model.LatLng point = new com.google.android.gms.maps.model.LatLng(latJson, lonJson);

                                mapPoints.add(point);

                            }
                        }
                    }

                } catch (URISyntaxException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                    e.printStackTrace();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        };
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mapPoints;
    }

    private String streamToString(InputStream is) {
        StringBuffer buffer = new StringBuffer();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String str = reader.readLine();
            while (str != null) {
                buffer.append(str);
                str = reader.readLine();
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return buffer.toString();
    }
}