package com.example.idolgo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class Categories extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        int permissionResult = checkSelfPermission(Manifest.permission.CALL_PHONE);

        if (permissionResult == PackageManager.PERMISSION_DENIED)
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1000);

        permissionResult = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionResult == PackageManager.PERMISSION_DENIED)
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);

        permissionResult = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionResult == PackageManager.PERMISSION_DENIED)
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);


        permissionResult = checkSelfPermission(Manifest.permission.WRITE_SETTINGS);
        if (permissionResult == PackageManager.PERMISSION_DENIED)
            requestPermissions(new String[]{Manifest.permission.WRITE_SETTINGS}, 1000);




    }
    public void next(View v){
        Intent it = new Intent(this, PlaceList.class);
        int id = v.getId();
        LinearLayout layout = (LinearLayout)findViewById(id);
        String tag = (String)layout.getTag();
        String cat= "";
        if(tag.equals("0"))
            cat = "Top10";
        else if(tag.equals("1"))
            cat = "Traditional Heritage";
        else if(tag.equals("2"))
            cat = "Hot Place";
        else if(tag.equals("3"))
            cat = "Food Streets";
        else if(tag.equals("4"))
            cat = "Shopping";
        else if(tag.equals("5"))
            cat = "Night Life";
        else if(tag.equals("6"))
            cat = "Korean Wave";
        else if(tag.equals("7"))
            cat = "Nature";
        else if(tag.equals("8"))
            cat = "Museums";

        it.putExtra("it_cat", cat);
        startActivity(it);


    }

}
