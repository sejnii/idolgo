package com.example.idolgo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class PublicTransportDetail extends AppCompatActivity {

    String it_placename, it_path;
    String subpathArr[][];
    int caseInfoArr[] = new int[4];
    int walktotaltime = 0;
    Context context = this;
    int subpathcnt;
    LinearLayout public_transport_deatail_linear;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_transport_detail);

        public_transport_deatail_linear = (LinearLayout)findViewById(R.id.detail_linear);
        Intent it = getIntent();
        it_path = it.getStringExtra("it_path");
        Log.i("it_path",it_path);
        it_placename = it.getStringExtra("it_placename");
        Log.i("it_placename", it_placename);
        JSONObject jsonObject;
        try {
            JSONObject jsonPathObject = new JSONObject(it_path);
            JSONArray jArrSubpath = jsonPathObject.getJSONArray("subPath");
            Log.i("subpatharr", jArrSubpath.toString());
            subpathcnt = jArrSubpath.length();
            subpathArr = new String[subpathcnt][8];
            for (int j = 0; j < subpathcnt; j++) {
                jsonObject = jArrSubpath.getJSONObject(j);
                Log.i("subpath"+j , jsonObject.toString());
                int trafficType = Integer.parseInt(jsonObject.getString("trafficType"));
                subpathArr[j][0] = ""+trafficType;
                Log.i("traffictype", ""+trafficType);

                if (trafficType == 3) {//도보
                    int walktime = Integer.parseInt(jsonObject.getString("sectionTime"));
                    Log.i("walkstime", ""+walktime);
                    walktotaltime += walktime;
                    int distance = Integer.parseInt(jsonObject.getString("distance"));
                    subpathArr[j][2]=""+distance;
                   subpathArr[j][1] = ""+walktime;
                }

                else if (trafficType == 1) {//지하철
                    String stationCount = jsonObject.getString("stationCount");
                    subpathArr[j][5] = stationCount;
                    Log.i("stationCount", stationCount);
                    String startName = jsonObject.getString("startName");
                    subpathArr[j][3] = startName;
                    Log.i("startName", startName);
                    String endName = jsonObject.getString("endName");
                    subpathArr[j][4] = endName;
                    Log.i("endName", endName);
                    JSONArray jArrLane = jsonObject.getJSONArray("lane");
                    JSONObject lane = jArrLane.getJSONObject(0);
                    Log.i("lane", lane.toString());
                    int subwaycode = Integer.parseInt(lane.getString("subwayCode"));
                    Log.i("subwaycode", "" + subwaycode);
                    subpathArr[j][6] = ""+subwaycode;
                    subpathArr[j][7] = ""+subwaycode;
                    int walktime = Integer.parseInt(jsonObject.getString("sectionTime"));
                    subpathArr[j][1] = ""+walktime;

                } else if (trafficType == 2) {//버스
                    String stationCount = jsonObject.getString("stationCount");
                    subpathArr[j][5] = stationCount;
                    Log.i("stationCount", stationCount);
                    String startName = jsonObject.getString("startName");
                    subpathArr[j][3] = startName;
                    Log.i("startName", startName);
                    String endName = jsonObject.getString("endName");
                    subpathArr[j][4] = endName;
                    Log.i("endName", endName);
                    JSONArray jArrLane = jsonObject.getJSONArray("lane");
                    JSONObject lane = jArrLane.getJSONObject(0);
                    Log.i("buslane", lane.toString());
                    String busNo = lane.getString("busNo");
                    Log.i("busNo", ""+busNo);
                    int type = Integer.parseInt(lane.getString("type"));
                    Log.i("type", ""+type);
                    subpathArr[j][6] = busNo;
                    subpathArr[j][7] = ""+type;
                    int walktime = Integer.parseInt(jsonObject.getString("sectionTime"));
                    subpathArr[j][1] = ""+walktime;

                }


            }

            JSONObject jObjInfo = jsonPathObject.getJSONObject("info");
            Log.i("info", jObjInfo.toString());
            caseInfoArr[0] = Integer.parseInt(jObjInfo.getString("totalTime"));
            Log.i("totalTime", jObjInfo.getString("totalTime"));
            caseInfoArr[1] = walktotaltime;
            Log.i("walktotal", ""+caseInfoArr[1]);
            caseInfoArr[2] = Integer.parseInt(jObjInfo.getString("payment"));
            Log.i("payment", ""+jObjInfo.getString("payment"));
            caseInfoArr[3] = Integer.parseInt(jObjInfo.getString("busTransitCount")) + Integer.parseInt(jObjInfo.getString("subwayTransitCount"));

            for(int i=0;i<subpathcnt;i++){
                for(int j=0;j<8;j++){
                    Log.i("subpatharr"+i+j, subpathArr[i][j]);
                }
            }

            for(int i=0;i<4;i++){
                Log.i("info"+i, ""+caseInfoArr[i]);
            }

        }
        catch(Exception e) {

        }
//////////////////////////////subpath배열 넣기 끝 layout 만들기 시작////////////////////////////////

        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 300);
        ll.setBackground(getResources().getDrawable(R.drawable.information_border));
        layparam.setMargins(10,10,10,10);
        ll.setLayoutParams(layparam);

//////////////////////////////////////////////////////////
        LinearLayout ll2 = new LinearLayout(context);
        ll2.setOrientation(LinearLayout.HORIZONTAL);
        layparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layparam.setMargins(20, 20, 20, 20);
        ll2.setLayoutParams(layparam);

        TextView time = new TextView(context);
        layparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layparam.setMargins(10, 10, 40, 10);
        layparam.gravity = Gravity.LEFT;
        if(caseInfoArr[0] >= 60) {
            int hour = caseInfoArr[0] / 60;
            int min =caseInfoArr[0] % 60;
            time.setText(hour+" hr " + min + " min");

        }
        else
            time.setText(caseInfoArr[0] + " min");
        time.setTextSize(25);
        time.setTextColor(Color.parseColor("#000000"));
        time.setTypeface(null, Typeface.BOLD);
        time.setLayoutParams(layparam);

        TextView walk_money = new TextView(context);
        layparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layparam.setMargins(10, 10, 10, 10);
        layparam.gravity = Gravity.RIGHT;
        layparam.gravity = Gravity.CENTER_VERTICAL;
        walk_money.setTextSize(15);
        walk_money.setLayoutParams(layparam);
        walk_money.setText("On Foot " + caseInfoArr[1] + " min   |   " + caseInfoArr[2] + " won");

        ll2.addView(time);
        ll2.addView(walk_money);


        ///////////////////////////////////////


        LinearLayout ll3 = new LinearLayout(context);
        ll3.setOrientation(LinearLayout.HORIZONTAL);
        layparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layparam.setMargins(20, 20, 20, 20);
        ll3.setLayoutParams(layparam);
        ll3.setGravity(Gravity.LEFT);

        int subwaybuscnt=0;
        for (int j = 0; j < subpathcnt; j++) {
            ImageView lane_pic = new ImageView(context);
            layparam = new LinearLayout.LayoutParams(90, 90);
            layparam.setMargins(10, 10, 10, 10);
            lane_pic.setLayoutParams(layparam);
            if (Integer.parseInt(subpathArr[j][0]) == 1) {//지하철
                lane_pic.setImageDrawable(getResources().getDrawable(R.drawable.subway));
                subwaybuscnt++;
                TextView lane = new TextView(context);
                layparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layparam.setMargins(10, 10, 10, 10);
                layparam.gravity = Gravity.CENTER_VERTICAL;
                lane.setLayoutParams(layparam);
                lane.setTextSize(15);
                lane.setText("line " + subpathArr[j][6]);
                lane.setTextColor(Color.parseColor("#ffffff"));
                switch (Integer.parseInt(subpathArr[j][7])) {
                    case 1:
                        lane.setBackgroundColor(Color.parseColor(getResources().getString(R.string.lane1)));
                        break;
                    case 2:
                        lane.setBackgroundColor(Color.parseColor(getResources().getString(R.string.lane2)));
                        break;
                    case 3:
                        lane.setBackgroundColor(Color.parseColor(getResources().getString(R.string.lane3)));
                        break;
                    case 4:
                        lane.setBackgroundColor(Color.parseColor(getResources().getString(R.string.lane4)));
                        break;
                    case 5:
                        lane.setBackgroundColor(Color.parseColor(getResources().getString(R.string.lane5)));
                        break;
                    case 6:
                        lane.setBackgroundColor(Color.parseColor(getResources().getString(R.string.lane6)));
                        break;
                    case 7:
                        lane.setBackgroundColor(Color.parseColor(getResources().getString(R.string.lane7)));
                        break;
                    case 8:
                        lane.setBackgroundColor(Color.parseColor(getResources().getString(R.string.lane8)));
                        break;
                    case 9:
                        lane.setBackgroundColor(Color.parseColor(getResources().getString(R.string.lane9)));
                        break;
                    case 100:
                        lane.setBackgroundColor(Color.parseColor("#ffcc01"));
                        break;

                }

                ll3.addView(lane_pic);
                ll3.addView(lane);

                if(subwaybuscnt != caseInfoArr[3]) {
                    TextView tvnext = new TextView(context);
                    layparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layparam.setMargins(10, 10, 10, 10);
                    layparam.gravity = Gravity.CENTER_VERTICAL;
                    tvnext.setLayoutParams(layparam);
                    tvnext.setTextSize(15);
                    tvnext.setText(">");
                    ll3.addView(tvnext);
                }



            }

            if (Integer.parseInt(subpathArr[j][0]) == 2) {//버스
                lane_pic.setImageDrawable(getResources().getDrawable(R.drawable.bus));
                subwaybuscnt++;
                TextView lane = new TextView(context);
                layparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layparam.setMargins(10, 10, 10, 10);
                layparam.gravity = Gravity.CENTER_VERTICAL;
                lane.setLayoutParams(layparam);
                lane.setTextSize(15);
                lane.setText(""+subpathArr[j][6]);
                lane.setTextColor(Color.parseColor("#ffffff"));
                switch (Integer.parseInt(subpathArr[j][7])) {
                    case 1://일반버스
                        lane.setBackgroundColor(Color.parseColor("#038762"));
                        break;
                    case 11://간선버스
                        lane.setBackgroundColor(Color.parseColor("#4049ee"));
                        break;
                    case 3://마을버스
                        lane.setBackgroundColor(Color.parseColor("#66a37a"));
                        break;
                    case 13://순환버스
                        lane.setBackgroundColor(Color.parseColor("#f9d412"));
                        break;
                    case 14://광역버스
                        lane.setBackgroundColor(Color.parseColor("#f00000"));
                        break;
                    case 12://지선버스
                        lane.setBackgroundColor(Color.parseColor("#76b08a"));
                        break;

                }

                ll3.addView(lane_pic);
                ll3.addView(lane);

                Log.i("subwaybustransittotal"+j,""+caseInfoArr[3] );
                Log.i("cnt", ""+subwaybuscnt);
                if(subwaybuscnt != caseInfoArr[3]) {
                    TextView tvnext = new TextView(context);
                    layparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layparam.setMargins(10, 10, 10, 10);
                    layparam.gravity = Gravity.CENTER_VERTICAL;
                    tvnext.setLayoutParams(layparam);
                    tvnext.setTextSize(15);
                    tvnext.setText(">");

                    ll3.addView(tvnext);

                }



            }
        }


        ll.addView(ll2);
        ll.addView(ll3);


        public_transport_deatail_linear.addView(ll);
//////////////윗쪽 path정보////////////////////////

        /////////////젤 위 출발 위치 표시//////////////////////////
        ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        layparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200);
        ll.setBackground(getResources().getDrawable(R.drawable.information_border));
        layparam.setMargins(10,10,10,10);
        ll.setLayoutParams(layparam);


        ImageView dep_pic = new ImageView(context);
        layparam = new LinearLayout.LayoutParams(120, 120);
        layparam.setMargins(30, 10, 30, 10);
        layparam.gravity = Gravity.CENTER_VERTICAL;
        dep_pic.setLayoutParams(layparam);
        dep_pic.setImageDrawable(getResources().getDrawable(R.drawable.dep));


        TextView tvdep = new TextView(context);
        layparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layparam.setMargins(10, 10, 10, 10);
        layparam.gravity = Gravity.CENTER_VERTICAL;
        tvdep.setText("Current Location");
        tvdep.setLayoutParams(layparam);
        tvdep.setTextSize(20);
        tvdep.setTextColor(Color.parseColor("#000000"));
        tvdep.setTypeface(null, Typeface.BOLD);

        ll.addView(dep_pic);
        ll.addView(tvdep);

        public_transport_deatail_linear.addView(ll);


        ///////////////////subpath detail한 부분 시작//////////////////////////

        for(int i=0;i<subpathArr.length;i++){




            if(Integer.parseInt(subpathArr[i][0])==3){//도보일 때

                ll = new LinearLayout(context);
                ll.setOrientation(LinearLayout.HORIZONTAL);
                layparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200);
                ll.setBackground(getResources().getDrawable(R.drawable.information_border));
                layparam.setMargins(10,10,10,10);
                ll.setLayoutParams(layparam);

                ImageView walk_pic = new ImageView(context);
                layparam = new LinearLayout.LayoutParams(120, 120);
                layparam.setMargins(30, 10, 30, 10);
                layparam.gravity = Gravity.CENTER_VERTICAL;
                walk_pic.setLayoutParams(layparam);
                walk_pic.setImageDrawable(getResources().getDrawable(R.drawable.walk));

                TextView tv_walk = new TextView(context);
                layparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layparam.setMargins(10, 10, 10, 10);
                layparam.gravity = Gravity.CENTER_VERTICAL;
                tv_walk.setLayoutParams(layparam);
                tv_walk.setText("Walk   "+subpathArr[i][1]+"min,  "+subpathArr[i][2]+"m");
                tv_walk.setTextSize(15);

                ll.addView(walk_pic);
                ll.addView(tv_walk);

                public_transport_deatail_linear.addView(ll);

            }

            else if(Integer.parseInt(subpathArr[i][0])==1) {//지하철일 때

                ll = new LinearLayout(context);
                ll.setOrientation(LinearLayout.HORIZONTAL);
                layparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200);
                ll.setBackground(getResources().getDrawable(R.drawable.information_border));
                layparam.setMargins(10,10,10,10);
                ll.setLayoutParams(layparam);

                TextView tv_start_station = new TextView(context);
                layparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                layparam.setMargins(10, 10, 10, 10);
                layparam.gravity = Gravity.CENTER;
                tv_start_station.setGravity(Gravity.CENTER);
                tv_start_station.setLayoutParams(layparam);
                tv_start_station.setText(subpathArr[i][3]+" Station");
                tv_start_station.setTextSize(15);

                ll.addView(tv_start_station);
                public_transport_deatail_linear.addView(ll);



                ll = new LinearLayout(context);
                ll.setOrientation(LinearLayout.HORIZONTAL);
                layparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200);
                ll.setBackground(getResources().getDrawable(R.drawable.information_border));
                layparam.setMargins(10,10,10,10);
                ll.setLayoutParams(layparam);

                ImageView subway_pic = new ImageView(context);
                layparam = new LinearLayout.LayoutParams(120, 120);
                layparam.setMargins(30, 10, 30, 10);
                layparam.gravity = Gravity.CENTER_VERTICAL;
                subway_pic.setLayoutParams(layparam);
                subway_pic.setImageDrawable(getResources().getDrawable(R.drawable.subway));

                TextView tv_subway1 = new TextView(context);
                layparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layparam.setMargins(10, 10, 10, 10);
                layparam.gravity = Gravity.CENTER_VERTICAL;
                tv_subway1.setLayoutParams(layparam);
                tv_subway1.setText("line"+subpathArr[i][6]);
                tv_subway1.setTextColor(Color.parseColor("#ffffff"));
                tv_subway1.setTextSize(15);

                switch (Integer.parseInt(subpathArr[i][7])) {
                    case 1:
                        tv_subway1.setBackgroundColor(Color.parseColor(getResources().getString(R.string.lane1)));
                        break;
                    case 2:
                        tv_subway1.setBackgroundColor(Color.parseColor(getResources().getString(R.string.lane2)));
                        break;
                    case 3:
                        tv_subway1.setBackgroundColor(Color.parseColor(getResources().getString(R.string.lane3)));
                        break;
                    case 4:
                        tv_subway1.setBackgroundColor(Color.parseColor(getResources().getString(R.string.lane4)));
                        break;
                    case 5:
                        tv_subway1.setBackgroundColor(Color.parseColor(getResources().getString(R.string.lane5)));
                        break;
                    case 6:
                        tv_subway1.setBackgroundColor(Color.parseColor(getResources().getString(R.string.lane6)));
                        break;
                    case 7:
                        tv_subway1.setBackgroundColor(Color.parseColor(getResources().getString(R.string.lane7)));
                        break;
                    case 8:
                        tv_subway1.setBackgroundColor(Color.parseColor(getResources().getString(R.string.lane8)));
                        break;
                    case 9:
                        tv_subway1.setBackgroundColor(Color.parseColor(getResources().getString(R.string.lane9)));
                        break;
                    case 100:
                        tv_subway1.setBackgroundColor(Color.parseColor("#ffcc01"));
                        break;

                }

                TextView tv_subway2 = new TextView(context);
                layparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layparam.setMargins(10, 10, 10, 10);
                layparam.gravity = Gravity.CENTER_VERTICAL;
                tv_subway2.setLayoutParams(layparam);
                tv_subway2.setText(subpathArr[i][1]+"min,  "+subpathArr[i][5]+"stops");
                tv_subway2.setTextSize(15);

                ll.addView(subway_pic);
                ll.addView(tv_subway1);
                ll.addView(tv_subway2);

                public_transport_deatail_linear.addView(ll);


                ll = new LinearLayout(context);
                ll.setOrientation(LinearLayout.HORIZONTAL);
                layparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200);
                ll.setBackground(getResources().getDrawable(R.drawable.information_border));
                layparam.setMargins(10,10,10,10);
                ll.setLayoutParams(layparam);

                TextView tv_end_station = new TextView(context);
                layparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                layparam.setMargins(10, 10, 10, 10);
                layparam.gravity = Gravity.CENTER;
                tv_end_station.setGravity(Gravity.CENTER);
                tv_end_station.setLayoutParams(layparam);
                tv_end_station.setText(subpathArr[i][4]+" Station");
                tv_end_station.setTextSize(15);

                ll.addView(tv_end_station);
                public_transport_deatail_linear.addView(ll);


            }

            else if(Integer.parseInt(subpathArr[i][0])==2) {//버스일 때

                ll = new LinearLayout(context);
                ll.setOrientation(LinearLayout.HORIZONTAL);
                layparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200);
                ll.setBackground(getResources().getDrawable(R.drawable.information_border));
                layparam.setMargins(10,10,10,10);
                ll.setLayoutParams(layparam);

                TextView tv_start_station = new TextView(context);
                layparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                layparam.setMargins(10, 10, 10, 10);
                layparam.gravity = Gravity.CENTER;
                tv_start_station.setGravity(Gravity.CENTER);
                tv_start_station.setLayoutParams(layparam);
                tv_start_station.setText(subpathArr[i][3]);
                tv_start_station.setTextSize(15);

                ll.addView(tv_start_station);
                public_transport_deatail_linear.addView(ll);



                ll = new LinearLayout(context);
                ll.setOrientation(LinearLayout.HORIZONTAL);
                layparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200);
                ll.setBackground(getResources().getDrawable(R.drawable.information_border));
                layparam.setMargins(10,10,10,10);
                ll.setLayoutParams(layparam);

                ImageView bus_pic = new ImageView(context);
                layparam = new LinearLayout.LayoutParams(120, 120);
                layparam.setMargins(30, 10, 30, 10);
                layparam.gravity = Gravity.CENTER_VERTICAL;
                bus_pic.setLayoutParams(layparam);
                bus_pic.setImageDrawable(getResources().getDrawable(R.drawable.bus));

                TextView tv_bus1 = new TextView(context);
                layparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layparam.setMargins(10, 10, 10, 10);
                layparam.gravity = Gravity.CENTER_VERTICAL;
                tv_bus1.setLayoutParams(layparam);
                tv_bus1.setText(subpathArr[i][6]);
                tv_bus1.setTextColor(Color.parseColor("#ffffff"));
                tv_bus1.setTextSize(15);

                switch (Integer.parseInt(subpathArr[i][7])) {
                    case 1://일반버스
                        tv_bus1.setBackgroundColor(Color.parseColor("#038762"));
                        break;
                    case 11://간선버스
                        tv_bus1.setBackgroundColor(Color.parseColor("#4049ee"));
                        break;
                    case 3://마을버스
                        tv_bus1.setBackgroundColor(Color.parseColor("#66a37a"));
                        break;
                    case 13://순환버스
                        tv_bus1.setBackgroundColor(Color.parseColor("#f9d412"));
                        break;
                    case 14://광역버스
                        tv_bus1.setBackgroundColor(Color.parseColor("#f00000"));
                        break;
                    case 12://지선버스
                        tv_bus1.setBackgroundColor(Color.parseColor("#76b08a"));
                        break;


                }

                TextView tv_bus2 = new TextView(context);
                layparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layparam.setMargins(10, 10, 10, 10);
                layparam.gravity = Gravity.CENTER_VERTICAL;
                tv_bus2.setLayoutParams(layparam);
                tv_bus2.setText(subpathArr[i][1]+"min,  "+subpathArr[i][5]+"stops");
                tv_bus2.setTextSize(15);

                ll.addView(bus_pic);
                ll.addView(tv_bus1);
                ll.addView(tv_bus2);

                public_transport_deatail_linear.addView(ll);


                ll = new LinearLayout(context);
                ll.setOrientation(LinearLayout.HORIZONTAL);
                layparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200);
                ll.setBackground(getResources().getDrawable(R.drawable.information_border));
                layparam.setMargins(10,10,10,10);
                ll.setLayoutParams(layparam);

                TextView tv_end_station = new TextView(context);
                layparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                layparam.setMargins(10, 10, 10, 10);
                layparam.gravity = Gravity.CENTER;
                tv_end_station.setGravity(Gravity.CENTER);
                tv_end_station.setLayoutParams(layparam);
                tv_end_station.setText(subpathArr[i][4]);
                tv_end_station.setTextSize(15);

                ll.addView(tv_end_station);
                public_transport_deatail_linear.addView(ll);


            }










        }




        /////////////젤 밑 도착 위치 표시//////////////////////////
        ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        layparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200);
        ll.setBackground(getResources().getDrawable(R.drawable.information_border));
        layparam.setMargins(10,10,10,10);
        ll.setLayoutParams(layparam);


        ImageView arr_pic = new ImageView(context);
        layparam = new LinearLayout.LayoutParams(120, 120);
        layparam.setMargins(30, 10, 30, 10);
        layparam.gravity = Gravity.CENTER_VERTICAL;
        arr_pic.setLayoutParams(layparam);
        arr_pic.setImageDrawable(getResources().getDrawable(R.drawable.arr));


        TextView tvarr = new TextView(context);
        layparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layparam.setMargins(10, 10, 10, 10);
        layparam.gravity = Gravity.CENTER_VERTICAL;
        tvarr.setText(it_placename);
        tvarr.setLayoutParams(layparam);
        tvarr.setTextSize(20);
        tvarr.setTextColor(Color.parseColor("#000000"));
        tvarr.setTypeface(null, Typeface.BOLD);

        ll.addView(arr_pic);
        ll.addView(tvarr);

        public_transport_deatail_linear.addView(ll);

    }
}
