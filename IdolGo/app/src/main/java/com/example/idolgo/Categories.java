package com.example.idolgo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class Categories extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

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
