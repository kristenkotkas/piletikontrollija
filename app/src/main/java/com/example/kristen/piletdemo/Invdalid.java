package com.example.kristen.piletdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Invdalid extends AppCompatActivity {
    private Button reset;
    private Button scan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invalid);
        reset = (Button) findViewById(R.id.btnResetInValid);
        scan = (Button) findViewById(R.id.btnScanInValid);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                MainActivity.scan.callOnClick();
            }
        });
    }
}
