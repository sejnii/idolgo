package com.example.idolgo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;


public class NearByInfo extends AppCompatActivity implements OnConnectionFailedListener{

    Context context = this;
   Double it_endlat, it_endlon;
   private GoogleApiClient mGoogleApiClient;

    LinearLayout listLayout;
    final int nll = 10000;
    final int nmore = 20000;
    final int ngo = 30000;

    Vector placeNameVector = new Vector<String>();
    Integer[] categories;
    String[] endX;
    String [] endY;

    LinearLayout nearbylinear;

   List<PlaceLikelihood> fp;

   Set<Integer> mAllowedTypes;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_near_by_info);


      nearbylinear = (LinearLayout) findViewById(R.id.nearbylinear);

       Intent it = getIntent();
       it_endlat = it.getDoubleExtra("it_endlat", 37.5537722);
       it_endlon = it.getDoubleExtra("it_endlon", 126.9806958);


       mGoogleApiClient = new GoogleApiClient
               .Builder(this)
               .addApi(Places.PLACE_DETECTION_API)
               .enableAutoManage(this, this)
               .build();




       mAllowedTypes = new HashSet<>();
       mAllowedTypes.add(Place.TYPE_ATM);
       mAllowedTypes.add(Place.TYPE_BAKERY);
       mAllowedTypes.add(Place.TYPE_BAR);
       mAllowedTypes.add(Place.TYPE_CAFE);
       mAllowedTypes.add(Place.TYPE_CLOTHING_STORE);
       mAllowedTypes.add(Place.TYPE_CONVENIENCE_STORE);
       mAllowedTypes.add(Place.TYPE_GROCERY_OR_SUPERMARKET);
       mAllowedTypes.add(Place.TYPE_PHARMACY);
       mAllowedTypes.add(Place.TYPE_RESTAURANT);



           @SuppressWarnings({"MissingPermission"})
          PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                   .getCurrentPlace(mGoogleApiClient, null);//두번째가 필터
           result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
               @Override
               public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                   for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                       Log.i("ss", String.format("Place '%s' has likelihood: %g",
                               placeLikelihood.getPlace().getName(),
                               placeLikelihood.getLikelihood()));
                   }
                   fp = filteredPlaces(likelyPlaces);
                   categories = new Integer[fp.size()];
                   endX = new String[fp.size()];
                   endY = new String[fp.size()];
                   for(int i=0;i<fp.size();i++) {
                       placeNameVector.add(fp.get(i).getPlace().getName());
                       endX[i] = Double.toString(fp.get(i).getPlace().getLatLng().longitude);
                       endY[i] = Double.toString(fp.get(i).getPlace().getLatLng().latitude);
                       List<Integer> types = fp.get(i).getPlace().getPlaceTypes();


                       for (int type : types) {

                           if (type == Place.TYPE_ATM) {
                               categories[i] = 0;
                               break;
                           } else if (type == Place.TYPE_BAKERY) {
                               categories[i] = 1;
                               break;
                           } else if (type == Place.TYPE_BAR) {
                               categories[i] = 2;
                               break;
                           } else if (type == Place.TYPE_CAFE) {
                               categories[i] = 3;
                               break;
                           } else if (type == Place.TYPE_CLOTHING_STORE) {
                               categories[i] = 4;
                               break;
                           } else if (type == Place.TYPE_CONVENIENCE_STORE) {
                               categories[i] = 5;
                               break;
                           } else if (type == Place.TYPE_GROCERY_OR_SUPERMARKET) {
                               categories[i] = 6;
                               break;
                           } else if (type == Place.TYPE_PHARMACY) {
                               categories[i] = 7;
                               break;
                           } else if (type == Place.TYPE_RESTAURANT) {
                               categories[i] = 8;
                               break;
                           }
                       }
                   }



                       for (int i = 0; i < placeNameVector.size(); i++) {



                           LinearLayout ll = new LinearLayout(context);
                           ll.setId(nll + i);
                           ll.setOrientation(LinearLayout.HORIZONTAL);
                           LinearLayout.LayoutParams layparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 300);
                           layparam.setMargins(10, 10, 10, 10);
                           ll.setBackground(getResources().getDrawable(R.drawable.information_border));
                               ll.setLayoutParams(layparam);


                               ImageView pic = new ImageView(context);
                       if(categories[i] == 0){
                             pic.setImageDrawable(getResources().getDrawable(R.drawable.atm_machine));

                       }
                       else if(categories[i]==2){
                           pic.setImageDrawable(getResources().getDrawable(R.drawable.winetoast));

                       }
                       else if(categories[i]==3){

                           pic.setImageDrawable(getResources().getDrawable(R.drawable.coffee_cup));
                       }
                       else if(categories[i]==4){
                           pic.setImageDrawable(getResources().getDrawable(R.drawable.shirts));

                       }
                       else if(categories[i]==5){
                           pic.setImageDrawable(getResources().getDrawable(R.drawable.icon));
                       }
                       else if(categories[i]==6){
                           pic.setImageDrawable(getResources().getDrawable(R.drawable.shopping_cart));
                       }
                       else if(categories[i]==7){
                           pic.setImageDrawable(getResources().getDrawable(R.drawable.hospital));
                       }
                       else if(categories[i]==8){
                           pic.setImageDrawable(getResources().getDrawable(R.drawable.plate_fork_and_knife));
                       }
                       else if(categories[i]==1){
                           pic.setImageDrawable(getResources().getDrawable(R.drawable.bread_basket));
                       }

                        layparam = new LinearLayout.LayoutParams(150, 150);
                           layparam.setMargins(30, 10, 30, 10);
                          layparam.gravity = Gravity.CENTER_VERTICAL;

                          pic.setLayoutParams(layparam);


////////////////////////////////////////////////////////////////////////

                           TextView place = new TextView(context);
                       layparam = new LinearLayout.LayoutParams(170, LinearLayout.LayoutParams.MATCH_PARENT);
                       layparam.setMargins(50, 5, 10, 5);
                       layparam.weight = 5;
                       place.setLayoutParams(layparam);
                       place.setGravity(Gravity.CENTER_VERTICAL);
                       place.setTextSize(20);
                       place.setText((String) placeNameVector.elementAt(i));





                           LinearLayout ll2 = new LinearLayout(context);
                           ll2.setOrientation(LinearLayout.VERTICAL);
                            layparam = new LinearLayout.LayoutParams(200, LinearLayout.LayoutParams.WRAP_CONTENT);
                           layparam.setMargins(10, 10, 10, 10);
                            layparam.gravity = Gravity.CENTER_VERTICAL;

                           ll2.setLayoutParams(layparam);

                           TextView more = new TextView(context);
                           more.setId(nmore+i);
                           layparam = new LinearLayout.LayoutParams(200, 130);
                           layparam.setMargins(5, 5, 5, 5);
                           layparam.gravity = Gravity.CENTER_VERTICAL;
                           more.setLayoutParams(layparam);
                           more.setGravity(Gravity.CENTER);
                           more.setTextSize(20);
                           more.setBackgroundColor(getResources().getColor(R.color.mainPink2));
                           more.setTextColor(Color.parseColor("#ffffff"));
                           more.setText("MORE");

                           more.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View view) {
                                   int id = view.getId();
                                   String name = (String)placeNameVector.get(id-nmore);
                                   Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse( "http://www.google.com/maps/search/"+ name + "?hl=en" ));
                                   startActivity(intent);


                               }
                           });


                           TextView go = new TextView(context);
                           go.setId(ngo+i);
                           layparam = new LinearLayout.LayoutParams(200, 130);
                           layparam.setMargins(5, 5, 5, 5);
                           layparam.gravity = Gravity.CENTER_VERTICAL;
                           go.setLayoutParams(layparam);
                           go.setGravity(Gravity.CENTER);
                           go.setTextSize(20);
                           go.setBackgroundColor(getResources().getColor(R.color.mainPink2));
                           go.setTextColor(Color.parseColor("#ffffff"));
                           go.setText("GO");


                           go.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View view) {
                                   int id = view.getId();
                                   Intent it = new Intent(context,PedestrianAr.class);
                                   Log.i("id", ""+(id-ngo));
                                 it.putExtra("it_endX", endX[id-ngo]);
                                 it.putExtra("it_endY", endY[id-ngo]);
                                 it.putExtra("it_isfinal", true);
                                   startActivity(it);

                               }
                           });



                           ll2.addView(more);
                           ll2.addView(go);

                       ll.addView(pic);
                       ll.addView(place);
                       ll.addView(ll2);

                       nearbylinear.addView(ll);



                   }






                   likelyPlaces.release();
               }
           });





   }
   public void onConnectionFailed(ConnectionResult cr){

   }


   public  boolean hasMatchingType(Place place) {
       List<Integer> types = place.getPlaceTypes();

       for (int type : types) {
           if (mAllowedTypes.contains(type)) {
               return true;
           }
       }
       return false;
   }



   public List<PlaceLikelihood> filteredPlaces(PlaceLikelihoodBuffer places) {
       List<PlaceLikelihood> results = new ArrayList<>();
       for(PlaceLikelihood likelihood : places) {
           if(hasMatchingType(likelihood.getPlace())) {
               results.add(likelihood);
           }
       }
       return results;
   }
}

