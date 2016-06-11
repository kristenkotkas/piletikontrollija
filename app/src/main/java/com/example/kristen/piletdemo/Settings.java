package com.example.kristen.piletdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Locale;

import static com.example.kristen.piletdemo.MainActivity.closeCursor;

public class Settings extends AppCompatActivity implements View.OnClickListener{
    private Typeface ticketfont;
    private TextView settingsTitle;
    private Button delete, btnEng, btnEst, btnScan;
    private Context ctx = this;
    private Locale myLocale;
    private Intent actMain;

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
        this.btnScan = (Button) findViewById(R.id.settingsScan);
        this.btnScan.setTypeface(ticketfont);

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

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(Settings.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Skänni!"); // TODO: 11.06.2016 tõlge
                integrator.setCameraId(0);  // Use a specific camera of the device // vb pole vaja
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(true);
                integrator.setOrientationLocked(true); //kuidas portrait lock saada? või kaamerale pole vaja?
                integrator.initiateScan();
            }
        });

    }

    public void setAuthKey(String result) {
        DatabaseOperations DB = new DatabaseOperations(this);
        Cursor cursor = DB.getAuthKey(DB);
        cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            DB.putAuthKey(DB,result);
            closeCursor(cursor);
        }
        else {
            DB.getWritableDatabase().delete(TableData.TableInfo.TABLE_AUTH, null, null);
            DB.putAuthKey(DB,result);
            closeCursor(cursor);
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null && scanResult.getContents() != null) {
            String result = scanResult.getContents();
            Log.d("code", result);
            if (result.split(" ")[0].contains("Auth")) {
                setAuthKey(result);
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        actMain = new Intent(this, MainActivity.class);
        this.startActivity(actMain);
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
        btnScan.setText(R.string.btnScan);
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
