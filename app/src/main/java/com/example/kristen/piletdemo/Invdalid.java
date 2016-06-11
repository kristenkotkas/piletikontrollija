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
    public static TextView invalid;
    private TextView longText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invalid);

        reset = (Button) findViewById(R.id.btnResetInValid);
        scan = (Button) findViewById(R.id.btnScanInValid);
        invalid = (TextView) findViewById(R.id.invalidInvalid);
        longText = (TextView) findViewById(R.id.resultInvalid);
        exists();
        ticketfont = Typeface.createFromAsset(getAssets(), "ticketfont2.ttf");
        reset.setTypeface(ticketfont);
        scan.setTypeface(ticketfont);
        invalid.setTypeface(ticketfont);
        invalid.setTextColor(Color.WHITE);
        longText.setTypeface(ticketfont);
        longText.setText(Result.getResult());


        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.scan.callOnClick();
                finish();
            }
        });
    }

    public static void exists() {
        System.out.println("jõuti exsists");
        System.out.println(Valid.exists);
        if (Valid.exists) {
            invalid.setText(R.string.exists);
            System.out.println("already excists");
        }
    }
}
