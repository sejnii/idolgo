package com.example.idolgo;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class WebInfo extends AppCompatActivity {

    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_info);

        Intent intent = getIntent();
        name = intent.getExtras().getString("name");

    }

    public void click(View v) {

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com/maps/search/" + name + "?hl=en"));
        startActivity(intent);

    }
}
