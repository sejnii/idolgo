package com.example.idolgo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.DocumentsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Vector;

public class PlaceInfo extends AppCompatActivity {

    String imgurl, strname, strcategory, strurl;
    ImageView photo, title_icon;
    TextView info, address, tel, url, title;
    Context context = this;
    String addr;
    String endX, endY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_info);


        Intent it = getIntent();
        strname = it.getStringExtra("it_name");
        strcategory = it.getStringExtra("it_category");
        strurl = it.getStringExtra("it_url");

        photo = (ImageView) findViewById(R.id.photo);
        title_icon = (ImageView) findViewById(R.id.title_icon);
        info = (TextView) findViewById(R.id.info);
        address = (TextView) findViewById(R.id.address);
        tel = (TextView) findViewById(R.id.tel);
        url = (TextView) findViewById(R.id.url);
        title = (TextView) findViewById(R.id.title);

        title.setText(strname);

        info.setMovementMethod(ScrollingMovementMethod.getInstance());

        DownloadWebpageTask dwt = new DownloadWebpageTask();
        dwt.execute();
    }


    private class DownloadWebpageTask extends AsyncTask<Void, Void, Void> {

        Elements element;

        String strinfo;

        Elements dl, dt, dd;
        Vector<String> vecdt, vecdd;

        protected Void doInBackground(Void... urls) {
            try {
                Document doc = Jsoup.connect(strurl).get();
                element = doc.select("body").select("#container").select(".holder").select("#content").select(".box-content-slider").select("#main-img").select("img");
                //;
                imgurl = element.attr("src");

                element = doc.select("body").select("#container").select(".holder").select("#content").select(".box-content.defaultopen").select(".content").select(".fc-black");
                strinfo = element.toString();

                dl = doc.select("body").select("#container").select(".holder").select("#content").select(".box-content.defaultopen.detail").select(".content").select(".cnt-detail.demilight.fc-black2");
                Log.i("dl", dl.toString());
                dt = dl.select("dt");
                dd = dl.select("dd");
                Log.i("dt", dt.toString());
                Log.i("dd", dd.toString());
                Log.i("dt size", "" + dt.size());
                vecdt = new Vector<String>();

                for (int i = 0; i < dt.size(); i++) {

                    vecdt.add(dt.get(i).text());

                    Log.i("dt : " + i, vecdt.get(i));

                }
                vecdd = new Vector<String>();
                for (int i = 0; i < dd.size(); i++) {
                    vecdd.add(dd.get(i).text());
                    Log.i("dd : " + i, vecdd.get(i));
                }
                Log.i("element", element.toString());


                Log.i("imgurl", "http://english.visitseoul.net" + imgurl);

                /*DownloadWebpageTask2 dwt2 = new DownloadWebpageTask2();
                dwt2.execute();*/

                /*InputStream in = new java.net.URL("http://english.visitseoul.net"+imgurl).openStream();
                photoicon = BitmapFactory.decodeStream(in);*/

            } catch (IOException e) {

            }
            return null;
        }

        protected void onPostExecute(Void result) {
            info.setText(Html.fromHtml(strinfo));

            for (int i = 0; i < vecdt.size(); i++) {
                if (vecdt.get(i).equals("• Address")) {
                    addr = vecdd.get(i);
                    address.setText(addr);
                    try {
                        String query = URLEncoder.encode(addr, "utf-8");
                        new DownloadWebpageTask2().execute("https://maps.googleapis.com/maps/api/geocode/json?address=" + query + "&key=AIzaSyDIv3KMuSLlyu4yV0M2g105H_7QLhtLywY");

                    } catch (Exception e) {

                    }

                } else if (vecdt.get(i).equals("• Phone"))
                    tel.setText(vecdd.get(i));
                else if (vecdt.get(i).equals("• Website"))
                    url.setText(dd.get(i).select("a").attr("href"));
            }
            Glide.with(context).load(Uri.parse("http://english.visitseoul.net" + imgurl + ".jpg")).into(photo);
        }


    }

    public void next(View v) {
        Intent it = new Intent(this, PublicTransportCase.class);
        it.putExtra("it_endX", endX);
        it.putExtra("it_endY", endY);
        it.putExtra("it_placename", strname);
        startActivity(it);
    }


    private class DownloadWebpageTask2 extends AsyncTask<String, Void, String> {


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
                Log.i("json", json.toString());
                JSONObject jobj = json.getJSONArray("results").getJSONObject(0);
                Log.i("jobj", jobj.toString());
                JSONObject geo = jobj.getJSONObject("geometry");
                Log.i("geo", geo.toString());
                JSONObject location = geo.getJSONObject("location");
                Log.i("location", location.toString());
                endY = location.getString("lat");
                endX = location.getString("lng");

                Log.i("endX", endX);

                Log.i("endy", endY);


            } catch (Exception e) {

            }

        }


        private String downloadUrl(String myurl) throws IOException {

            Log.i("tag", "downloadURL");
            String uri = myurl;
            Log.i("myurl", myurl);

            BufferedReader bufferedReader = null;
            HttpURLConnection con = null;
            try {
                URL url = new URL(uri);
                con = (HttpURLConnection) url.openConnection();
                StringBuilder sb = new StringBuilder();
                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String json;
                while ((json = bufferedReader.readLine()) != null) {
                    sb.append(json + "\n");
                }

                return sb.toString().trim();

            } catch (Exception e) {
                e.printStackTrace();
                Log.i("err", e.toString());
                return null;
            } finally {
                con.disconnect();

            }

        }

    }

}