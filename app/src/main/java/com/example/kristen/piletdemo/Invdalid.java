package com.example.kristen.piletdemo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class Invdalid extends AppCompatActivity {
    private Button reset;
    private Button scan;
    private Typeface ticketfont;
    public static TextView invalid;
    private TextView longText;
    private Locale myLocale;

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

        loadLocale();
    }

    public static void exists() {
        System.out.println("jõuti exsists");
        System.out.println(Valid.exists);
        if (Valid.exists) {
            invalid.setText(R.string.exists);
            System.out.println("already excists");
        }
    }

    public void changeLang(String lang) {
        if (lang.equalsIgnoreCase(""))
            return;
        myLocale = new Locale(lang);
        saveLocale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        updateTexts();
    }


    public void saveLocale(String lang) {
        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(langPref, lang);
        editor.apply();
    }

    public void loadLocale() {
        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        String language = prefs.getString(langPref, "");
        changeLang(language);
    }

    private void updateTexts() {
        reset.setText(R.string.btnReset);
        scan.setText(R.string.btnScan);
    }
}
