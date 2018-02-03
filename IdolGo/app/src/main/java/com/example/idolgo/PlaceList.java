package com.example.idolgo;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import java.util.Vector;

public class PlaceList extends AppCompatActivity {

    Vector placeTitleVector = new Vector<String>();
    Vector placeDescriptionVector = new Vector<String>();
    String place_title="";
    String place_description="";
    LinearLayout listLayout;
    final int nll = 10000;
    final int nnum = 20000;
    final int npic = 30000;
    final int nplace = 40000;
    final int ndistance = 50000;
    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_list);

        listLayout = (LinearLayout)findViewById(R.id.dynamicList) ;
        //if(it_dc.equals(it facilities))
        String strUrl = "http://contents.visitseoul.net/file_save/rss/0004003002004en.xml";

        new DownloadWebpageTask().execute(strUrl);


    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String>{


        protected String doInBackground(String...urls){
            try {
                return (String) downloadUrl((String) urls[0]);

            } catch (IOException e) {
                return "다운로드 실패";
            }
        }
        protected void onPostExecute(String result) {
            try{
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





            for(int i=0;i<placeTitleVector.size();i++) {
                Log.i("vector num",Integer.toString(i));
                Log.i("title vector", (String) placeTitleVector.elementAt(i));
                Log.i("description vector",(String)placeDescriptionVector.elementAt(i) );

                LinearLayout ll = new LinearLayout(context);
                ll.setId(nll + i);
                ll.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams layparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200);
                layparam.setMargins(10,10,10,10);
                ll.setBackground(getResources().getDrawable(R.drawable.information_border));
                ll.setLayoutParams(layparam);

                TextView num = new TextView(context);
                num.setId(nnum + i);
               layparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.MATCH_PARENT);
                layparam.setMargins(5, 5, 5, 5);
                layparam.weight=1;
                num.setLayoutParams(layparam);
                num.setGravity(Gravity.CENTER);
                num.setTextColor(Color.parseColor("#c9b3b5"));
                num.setTextSize(25);
                String numstring = ""+(i+1);
                num.setText(numstring);


                ImageView pic  = new ImageView(context);
                pic.setId(npic + i);
                layparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.MATCH_PARENT);
                layparam.setMargins(5, 5, 5, 5);
                layparam.weight=2;
                num.setLayoutParams(layparam);
                num.setGravity(Gravity.CENTER);


                TextView place = new TextView(context);
                place.setId(nplace + i);
                layparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.MATCH_PARENT);
                layparam.setMargins(5, 5, 5, 5);
                layparam.weight=5;
                place.setLayoutParams(layparam);
                place.setGravity(Gravity.CENTER_VERTICAL);
                place.setTextSize(20);
                place.setText((String)placeTitleVector.elementAt(i));



                TextView distance = new TextView(context);
                distance.setId(ndistance + i);
                layparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.MATCH_PARENT);
                layparam.setMargins(5, 5, 5, 5);
                layparam.weight=2;
                distance.setLayoutParams(layparam);
                distance.setGravity(Gravity.CENTER);
                distance.setTextColor(Color.parseColor("#ba7c8d"));
                distance.setTextSize(20);
                distance.setText("3.5km");

                ll.addView(num);
                ll.addView(pic);
                ll.addView(place);
                ll.addView(distance);

                listLayout.addView(ll);

                Document doc = Jsoup.parse((String)placeDescriptionVector.elementAt(i));


            }




        }



        }

        private String downloadUrl(String myurl) throws IOException{
            HttpURLConnection conn = null;
            try {
                URL url = new URL(myurl);
                conn = (HttpURLConnection) url.openConnection();
                BufferedInputStream buf = new BufferedInputStream(conn.getInputStream());
                BufferedReader bufreader = new BufferedReader(new InputStreamReader(buf, "utf-8"));
                String line = null;
                String page = "";
                while ((line = bufreader.readLine()) != null) {
                    page += line;
                }

                return page;
            }
            finally{
                conn.disconnect();

            }


            }

}

