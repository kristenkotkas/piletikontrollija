package com.example.kristen.piletdemo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private LinearLayout linLayout;
    private LinearLayout totalLayout;
    private TextView totalAmount;
    public static Button scan;
    private Button settings;
    private RelativeLayout drawer;
    private Intent actSettings;
    private Intent actValid;
    private Intent actInvalid;
    private Typeface ticketfont;
    private String code;
    private boolean isValid = false;
    private Locale myLocale;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ticketfont = Typeface.createFromAsset(getAssets(), "ticketfont2.ttf");
        totalLayout = (LinearLayout) findViewById(R.id.totalLayout);
        linLayout = (LinearLayout) findViewById(R.id.linLayout);
        scan = (Button) findViewById(R.id.btnScan);
        drawer = (RelativeLayout) findViewById(R.id.drawer);
        settings = (Button) findViewById(R.id.toSettings);
        actSettings = new Intent("com.example.kristen.piletdemo.Settings");
        actInvalid = new Intent("com.example.kristen.piletdemo.Invdalid");
        actValid = new Intent("com.example.kristen.piletdemo.Valid");
        totalLayout.setVisibility(View.INVISIBLE);
        linLayout.setVisibility(View.INVISIBLE);
        scan.setTypeface(ticketfont);
        settings.setTypeface(ticketfont);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish();
                startActivity(actSettings);
            }
        });

        loadLocale();
        //https://github.com/journeyapps/zxing-android-embedded
        //https://github.com/journeyapps/zxing-android-embedded/blob/master/EMBEDDING.md
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Valid.exists = false;
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
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

    public void validator(String ticket) {
        System.out.println(ticket);
        String[] texts;
        texts = ticket.split("\n");
        code = texts[0];

        if (code.substring(0,3).equals("ID:")) {
            isValid = true;
            System.out.println("is valid code");
        }
    }

    public static void closeCursor(Cursor cursor) {
        if (!cursor.isClosed()) {
            cursor.close();
        }
    }

    public void authKeyAddition(String result) {
        DatabaseOperations DB = new DatabaseOperations(this);
        Cursor cursor = DB.getAuthKey(DB);
        if (cursor.getCount() == 0) {
            DB.putAuthKey(DB,result);
            closeCursor(cursor);
        }
        else {
            cursor.moveToFirst();
            Encryption.setSecret(cursor.getString(0));
            closeCursor(cursor);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null && scanResult.getContents() != null) {
            String result = scanResult.getContents();
            Log.d("code", result);
            if (result.split(" ")[0].contains("Auth")) {
                authKeyAddition(result);
            } else {
                authKeyAddition("");
                String[] encresult = Encryption.decrypt(result);
                if (encresult[0].equals("valid")) {
                    result = encresult[1];
                    Result.setResult(result);
                } else {
                    Result.setResult(encresult[1]);
                }
                try {
                    validator(result);
                    if (isValid) {
                        isValid = false;
                        System.out.println("väks Valid sisse");
                        startActivity(actValid);
                    }
                    else {
                        startActivity(actInvalid);
                    }
                } catch (Exception e) {
                    startActivity(actInvalid);
                }
            }
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
        scan.setText(R.string.btnScan);
        settings.setText(R.string.settings);
    }
}
