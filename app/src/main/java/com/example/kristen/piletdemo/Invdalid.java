package com.example.kristen.piletdemo;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Invdalid extends AppCompatActivity {
    private Button reset;
    private Button scan;
    private Typeface ticketfont;
    private TextView invalid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invalid);
        reset = (Button) findViewById(R.id.btnResetInValid);
        scan = (Button) findViewById(R.id.btnScanInValid);
        invalid = (TextView) findViewById(R.id.invalidInvalid);

        ticketfont = Typeface.createFromAsset(getAssets(), "ticketfont2.ttf");
        reset.setTypeface(ticketfont);
        scan.setTypeface(ticketfont);
        invalid.setTypeface(ticketfont);
        invalid.setTextColor(Color.WHITE);


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
