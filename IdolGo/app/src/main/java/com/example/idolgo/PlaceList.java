package com.example.idolgo;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class PlaceList extends AppCompatActivity {

    String myJSON;

    private static final String TAG_RESULTS = "result";
    private static final String TAG_ID = "id";
    private static final String TAG_CAT = "category";
    private static final String TAG_NAME = "name";
    private static final String TAG_URL = "url";


    JSONArray spot = null;

    ArrayList<HashMap<String, String>> spotList;


    Vector placeCategoryVector = new Vector<String>();
    Vector placeNameVector = new Vector<String>();
    Vector placeUrlVector = new Vector<String>();
    String place_title = "";
    String place_description = "";
    LinearLayout listLayout;
    final int nll = 10000;
    final int nnum = 20000;
    final int npic = 30000;
    final int nplace = 40000;
    final int ndistance = 50000;
    Context context = this;

    String it_cat="Top10";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_list);

/*
        Intent it = getIntent();
        it_cat = it.getStringExtra("it_cat");
        if(it_cat.equals("Traditional \nHeritage"))
            it_cat = "Traditional Heritage";

*/

        listLayout = (LinearLayout) findViewById(R.id.dynamicList);
        //if(it_dc.equals(it facilities))
        spotList = new ArrayList<HashMap<String, String>>();


        // String strUrl = "http://contents.visitseoul.net/file_save/rss/0004003002004en.xml";

        new DownloadWebpageTask().execute("http://192.168.20.96/PHP_connection.php");


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
            try {

                JSONObject json = new JSONObject(result);
                JSONArray jArr = json.getJSONArray("result");

                Log.i("json 크기", "" + jArr.length());
                for (int i = 0; i < jArr.length(); i++) {

                    json = jArr.getJSONObject(i);

                    String category = json.getString("category");
                    Log.i("cat" + i, category);
                    String name = json.getString("name");
                    Log.i("name" + i, name);
                    String url = json.getString("url");
                    Log.i("url" + i, url);


                    placeCategoryVector.add(category);
                    placeNameVector.add(name);
                    placeUrlVector.add(url);
                }
            } catch (Exception e) {
                Log.i("err", "파싱");
                // TODO: handle exception
            }



/*
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput(new StringReader(result));


            int eventType = xpp.getEventType();


            boolean bSet_item = false;
            String tag_name="";
            while (eventType != XmlPullParser.END_DOCUMENT) {

               if(eventType == XmlPullParser.START_DOCUMENT){
                   ;
               }
               else if(eventType == XmlPullParser.START_TAG){
                   tag_name = xpp.getName();
                   if(tag_name.equals("item"))
                       bSet_item = true;

               }

               else if(eventType == XmlPullParser.TEXT){
                   if(bSet_item && tag_name.equals("title")) {
                       place_title += xpp.getText();
                       Log.i("title", place_title);
                   }

                   if(bSet_item && tag_name.equals("description")){
                       place_description += xpp.getText();
                       Log.i("description", place_description);
                   }
               }

               else if(eventType == XmlPullParser.END_TAG){
                   tag_name = xpp.getName();
                   if(tag_name.equals("item")){
                       placeTitleVector.add(place_title);
                       placeDescriptionVector.add(place_description);
                       place_description="";
                       place_title="";
                       bSet_item = false;

                   }
               }

                eventType = xpp.next();
            }

        }

        catch(Exception e){

        }



*/
int cnt=1;
            for (int i = 0; i < placeNameVector.size(); i++) {
                if(placeCategoryVector.get(i).equals(it_cat)) {
                    Log.i("vector num", Integer.toString(i));
                    Log.i("cat vector", (String) placeCategoryVector.elementAt(i));
                    Log.i("name vector", (String) placeNameVector.elementAt(i));
                    Log.i("url vector", (String) placeUrlVector.elementAt(i));

                    LinearLayout ll = new LinearLayout(context);
                    ll.setId(nll + i);
                    ll.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout.LayoutParams layparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 250);
                    layparam.setMargins(10, 10, 10, 10);
                    ll.setBackground(getResources().getDrawable(R.drawable.information_border));
                    ll.setLayoutParams(layparam);

                    TextView num = new TextView(context);
                    num.setId(nnum + i);
                    layparam = new LinearLayout.LayoutParams(30, LinearLayout.LayoutParams.MATCH_PARENT);
                    layparam.setMargins(5, 5, 5, 5);
                    num.setLayoutParams(layparam);
                    num.setGravity(Gravity.CENTER);
                    num.setTextColor(Color.parseColor("#c9b3b5"));
                    num.setTextSize(25);
                    String numstring = "" + (cnt);
                    num.setText(numstring);


                    ImageView pic = new ImageView(context);
                    pic.setId(npic + i);
                    layparam = new LinearLayout.LayoutParams(60, LinearLayout.LayoutParams.MATCH_PARENT);
                    layparam.setMargins(5, 5, 5, 5);
                    layparam.weight = 2;
                    num.setLayoutParams(layparam);
                    num.setGravity(Gravity.CENTER);


                    TextView place = new TextView(context);
                    place.setId(nplace + i);
                    layparam = new LinearLayout.LayoutParams(170, LinearLayout.LayoutParams.MATCH_PARENT);
                    layparam.setMargins(5, 5, 5, 5);
                    layparam.weight = 5;
                    place.setLayoutParams(layparam);
                    place.setGravity(Gravity.CENTER_VERTICAL);
                    place.setTextSize(20);
                    place.setText((String) placeNameVector.elementAt(i));


                    TextView distance = new TextView(context);
                    distance.setId(ndistance + i);
                    layparam = new LinearLayout.LayoutParams(60, LinearLayout.LayoutParams.MATCH_PARENT);
                    layparam.setMargins(5, 5, 5, 5);
                    layparam.weight = 2;
                    distance.setLayoutParams(layparam);
                    distance.setGravity(Gravity.CENTER);
                    distance.setTextColor(Color.parseColor("#ba7c8d"));
                    distance.setTextSize(20);
                    distance.setText("3.5km");

                    ll.addView(num);
                    ll.addView(pic);
                    ll.addView(place);
                    ll.addView(distance);

                    ll.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int id = view.getId();
                            Intent it = new Intent(context,PlaceInfo.class);
                            Log.i("id", ""+(id-nll));
                            it.putExtra("it_url", (String)placeUrlVector.get(id-nll));
                            it.putExtra("it_category", (String)placeCategoryVector.get(id-nll));
                            it.putExtra("it_name", (String) placeNameVector.get(id-nll));
                            startActivity(it);

                    }
                    });

                    listLayout.addView(ll);

                    cnt++;
                }

            }

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
}



