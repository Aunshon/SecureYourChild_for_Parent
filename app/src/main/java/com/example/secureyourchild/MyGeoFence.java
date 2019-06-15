package com.example.secureyourchild;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MyGeoFence extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_geo_fence);
    }

    @Override
    public void onBackPressed() {
        Intent mainint=new Intent(MyGeoFence.this,ParentMapActivity.class);
        mainint.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainint);
    }

    public void addBtnClicked(View view) {
        startActivity(new Intent(MyGeoFence.this,GeoFencePicker.class));
    }
}
