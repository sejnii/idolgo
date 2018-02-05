package com.example.idolgo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Vector;


public class PublicTransportCase extends AppCompatActivity {


    LinearLayout linearLayout;
    private ODsayService odsayService;
    private JSONObject jsonObject;
    final int nll = 10000;
    Context context = this;


   // Vector <Vector <Integer>> caseVector = new Vector<Vector <Integer>>();
    int caseSubPathArr[][][];//path, subpath, (trafficType, num, color, time)
    int caseInfoArr[][];//path, (totaltime, walktotaltime, payment, busTransitCount+subwayTransitCount)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_transport_case);


        linearLayout = (LinearLayout) findViewById(R.id.public_transport_case_linear);


        odsayService = ODsayService.init(context, getString(R.string.odsay_key));
        odsayService.setReadTimeout(5000);
        odsayService.setConnectionTimeout(5000);


        odsayService.requestSearchPubTransPath("126.926493082645", "37.6134436427887", "127.126936754911", "37.5004198786564", "0", "0", "0", onResultCallbackListener);


    }

    private OnResultCallbackListener onResultCallbackListener = new OnResultCallbackListener() {
        @Override
        public void onSuccess(ODsayData oDsayData, API api) {
            try {

                int walktotaltime = 0;
                int pathcnt = 0, subpathcnt = 0;//path개수 subpath 개수
                JSONArray jArrSubpath;
                JSONObject jObjInfo;
                jsonObject = oDsayData.getJson();
                Log.i("jsonobj", jsonObject.toString());

                jsonObject = jsonObject.getJSONObject("result");
                Log.i("result", jsonObject.toString());
                JSONArray jArrPath = jsonObject.getJSONArray("path");
                pathcnt = jArrPath.length();
                caseSubPathArr = new int[pathcnt][][];
                caseInfoArr = new int[pathcnt][4];
                for (int i = 0; i < pathcnt; i++) {
                    JSONObject jsonPathObject = jArrPath.getJSONObject(i);
                    Log.i("path"+i, jsonPathObject.toString());
                    jArrSubpath = jsonPathObject.getJSONArray("subPath");
                    subpathcnt = jArrSubpath.length();
                    caseSubPathArr[i] = new int[subpathcnt][5];
                    for (int j = 0; j < subpathcnt; j++) {
                        jsonObject = jArrSubpath.getJSONObject(j);
                        Log.i("subpath"+j , jsonObject.toString());
                        int trafficType = Integer.parseInt(jsonObject.getString("trafficType"));
                        Log.i("traffictype", ""+trafficType);
                        caseSubPathArr[i][j][0] = trafficType;
                        if (trafficType == 3) {//도보
                            caseSubPathArr[i][j][1] = -1;
                            caseSubPathArr[i][j][2] = -1;
                            int walktime = Integer.parseInt(jsonObject.getString("sectionTime"));
                            Log.i("walktime", ""+walktime);
                            walktotaltime += walktime;
                            caseSubPathArr[i][j][3] = walktime;
                        } else if (trafficType == 1) {//지하철
                            JSONArray jArrLane = jsonObject.getJSONArray("lane");
                            JSONObject lane = jArrLane.getJSONObject(0);
                            Log.i("lane", lane.toString());
                            int subwaycode = Integer.parseInt(lane.getString("subwayCode"));
                            Log.i("subwaycode", "" + subwaycode);
                            caseSubPathArr[i][j][1] = subwaycode;
                            caseSubPathArr[i][j][2] = subwaycode;
                            int walktime = Integer.parseInt(jsonObject.getString("sectionTime"));
                            caseSubPathArr[i][j][3] = walktime;

                        } else if (trafficType == 2) {//버스
                            JSONArray jArrLane = jsonObject.getJSONArray("lane");
                            JSONObject lane = jArrLane.getJSONObject(0);
                            Log.i("buslane", lane.toString());
                            int busNo = Integer.parseInt(lane.getString("busNo"));
                            Log.i("busNo", ""+busNo);
                            int type = Integer.parseInt(lane.getString("type"));
                            Log.i("type", ""+type);
                            caseSubPathArr[i][j][1] = busNo;
                            caseSubPathArr[i][j][2] = type;
                            int walktime = Integer.parseInt(jsonObject.getString("sectionTime"));
                            caseSubPathArr[i][j][3] = walktime;

                        }

                    }

                    jObjInfo = jsonPathObject.getJSONObject("info");
                    Log.i("info", jObjInfo.toString());
                    caseInfoArr[i][0] = Integer.parseInt(jObjInfo.getString("totalTime"));
                    Log.i("totalTime", jObjInfo.getString("totalTime"));
                    caseInfoArr[i][1] = walktotaltime;
                    Log.i("walktotal", ""+caseInfoArr[i][1]);
                    caseInfoArr[i][2] = Integer.parseInt(jObjInfo.getString("payment"));
                    Log.i("payment", ""+jObjInfo.getString("payment"));
                    caseInfoArr[i][3] = Integer.parseInt(jObjInfo.getString("busTransitCount")) + Integer.parseInt(jObjInfo.getString("subwayTransitCount"));

                }

                Log.i("pathcnt", ""+pathcnt);
                Log.i("subpathcnt", ""+subpathcnt);
                for(int i=0;i<pathcnt;i++){
                    for(int j=0;j<caseSubPathArr[i].length;j++){
                        for(int k=0;k<4;k++){
                            Log.i("arr"+i+j+k, ""+caseSubPathArr[i][j][k]);
                        }
                    }
                }
                for(int i=0;i<pathcnt;i++){
                    for(int j=0;j<3;j++){
                        Log.i("caseInfoArr"+i+j, ""+caseInfoArr[i][j]);
                    }
                }


                for (int i = 0; i < pathcnt; i++) {

                    LinearLayout ll = new LinearLayout(context);
                    ll.setId(nll + i);
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
                    if(caseInfoArr[i][0] >= 60) {
                        int hour = caseInfoArr[i][0] / 60;
                        int min =caseInfoArr[i][0] % 60;
                        time.setText(hour+" hr " + min + " min");

                    }
                    else
                     time.setText(caseInfoArr[i][0] + " min");
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
                    walk_money.setText("On Foot " + caseInfoArr[i][1] + " min   |   " + caseInfoArr[i][2] + " won");

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
                    for (int j = 0; j < caseSubPathArr[i].length; j++) {
                        ImageView lane_pic = new ImageView(context);
                        layparam = new LinearLayout.LayoutParams(90, 90);
                        layparam.setMargins(10, 10, 10, 10);
                        lane_pic.setLayoutParams(layparam);
                        if (caseSubPathArr[i][j][0] == 1) {//지하철
                            lane_pic.setImageDrawable(getResources().getDrawable(R.drawable.subway));
                            subwaybuscnt++;
                            TextView lane = new TextView(context);
                            layparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            layparam.setMargins(10, 10, 10, 10);
                            layparam.gravity = Gravity.CENTER_VERTICAL;
                            lane.setLayoutParams(layparam);
                            lane.setTextSize(15);
                            lane.setText("line " + caseSubPathArr[i][j][1]);
                            lane.setTextColor(Color.parseColor("#ffffff"));
                            switch (caseSubPathArr[i][j][2]) {
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

                            }

                            ll3.addView(lane_pic);
                            ll3.addView(lane);

                            if(subwaybuscnt != caseInfoArr[i][3]) {
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

                        if (caseSubPathArr[i][j][0] == 2) {//버스
                            lane_pic.setImageDrawable(getResources().getDrawable(R.drawable.bus));
                            subwaybuscnt++;
                            TextView lane = new TextView(context);
                            layparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            layparam.setMargins(10, 10, 10, 10);
                            layparam.gravity = Gravity.CENTER_VERTICAL;
                            lane.setLayoutParams(layparam);
                            lane.setTextSize(15);
                            lane.setText(""+caseSubPathArr[i][j][1]);
                            lane.setTextColor(Color.parseColor("#ffffff"));
                            switch (caseSubPathArr[i][j][2]) {
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

                            Log.i("subwaybustransittotal"+i+j,""+caseInfoArr[i][3] );
                            Log.i("cnt", ""+subwaybuscnt);
                            if(subwaybuscnt != caseInfoArr[i][3]) {
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


                    linearLayout.addView(ll);

                }





                }

            catch(Exception e){

            }
        }

        @Override
        public void onError(int i, String errorMessage, API api) {

        }
    };






}


