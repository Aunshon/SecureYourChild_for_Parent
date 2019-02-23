package com.example.secureyourchild;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class QrAndChildIdResult extends AppCompatActivity {

    TextView childid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_and_child_id_result);
        childid=findViewById(R.id.resultview);
        childid.setText(getIntent().getStringExtra("qrresult"));
    }

}
