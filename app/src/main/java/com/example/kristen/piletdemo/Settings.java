package com.example.kristen.piletdemo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class Settings extends AppCompatActivity implements View.OnClickListener{
    private Typeface ticketfont;
    private TextView settingsTitle;
    private Button delete, btnEng, btnEst;
    private Context ctx = this;
    private Locale myLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ticketfont = Typeface.createFromAsset(getAssets(), "ticketfont2.ttf");

        this.settingsTitle = (TextView) findViewById(R.id.settingsTitle);
        this.settingsTitle.setTypeface(ticketfont);
        this.delete = (Button) findViewById(R.id.deleteEntries);
        this.delete.setTypeface(ticketfont);
        this.btnEng = (Button) findViewById(R.id.engLangBtn);
        this.btnEng.setTypeface(ticketfont);
        this.btnEst = (Button) findViewById(R.id.estLangBtn);
        this.btnEst.setTypeface(ticketfont);

        this.btnEng.setOnClickListener(this);
        this.btnEst.setOnClickListener(this);

        loadLocale();

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseOperations DB = new DatabaseOperations(ctx);
                DB.getWritableDatabase().delete(TableData.TableInfo.TABLE_NAME, null, null);
            }
        });

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
        settingsTitle.setText(R.string.settings);
        btnEng.setText(R.string.engLang);
        btnEst.setText(R.string.estLang);
        delete.setText(R.string.deleteEntries);
    }

    @Override
    public void onClick(View v) {
        String lang = "en";
        switch (v.getId()) {
            case R.id.engLangBtn:
                lang = "en";
                break;
            case R.id.estLangBtn:
                lang = "est";
                break;
            default:
                break;
        }
        changeLang(lang);
    }
}
