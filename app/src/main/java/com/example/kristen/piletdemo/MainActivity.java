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
import android.widget.*;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
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
    private TextView settingsTitle;
    private Button delete, btnEng, btnEst, btnScan;
    private Context ctx = this;
    private boolean isKeyScan = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ticketfont = Typeface.createFromAsset(getAssets(), "ticketfont2.ttf");
        scan = (Button) findViewById(R.id.btnScan);
        drawer = (RelativeLayout) findViewById(R.id.drawer);
        actSettings = new Intent("com.example.kristen.piletdemo.Settings");
        actInvalid = new Intent("com.example.kristen.piletdemo.Invdalid");
        actValid = new Intent("com.example.kristen.piletdemo.Valid");
        scan.setTypeface(ticketfont);
        settingsTitle = (TextView) findViewById(R.id.settingsTitle);
        settingsTitle.setTypeface(ticketfont);
        delete = (Button) findViewById(R.id.deleteEntries);
        delete.setTypeface(ticketfont);
        btnEng = (Button) findViewById(R.id.engLangBtn);
        btnEng.setTypeface(ticketfont);
        btnEst = (Button) findViewById(R.id.estLangBtn);
        btnEst.setTypeface(ticketfont);
        btnScan = (Button) findViewById(R.id.settingsScan);
        btnScan.setTypeface(ticketfont);

        btnEng.setOnClickListener(this);
        btnEst.setOnClickListener(this);

        DatabaseOperations DB = new DatabaseOperations(this);
        Cursor cursor = DB.getAuthKey(DB);
        cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            scan.setEnabled(false);
        } else scan.setEnabled(true);

        loadLocale();
        //https://github.com/journeyapps/zxing-android-embedded
        //https://github.com/journeyapps/zxing-android-embedded/blob/master/EMBEDDING.md
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Valid.exists = false;
                IntentIntegrator integratorTicket = new IntentIntegrator(MainActivity.this);
                integratorTicket.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integratorTicket.setPrompt("Skänni!"); // TODO: 11.06.2016 tõlge
                integratorTicket.setCameraId(0);  // Use a specific camera of the device // vb pole vaja
                integratorTicket.setBeepEnabled(false);
                integratorTicket.setBarcodeImageEnabled(true);
                integratorTicket.setOrientationLocked(true); //kuidas portrait lock saada? või kaamerale pole vaja?
                integratorTicket.initiateScan();
            }
        });
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
                isKeyScan = true;
                scan.callOnClick();
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

    public void setAuthKey(String result) {
        DatabaseOperations DB = new DatabaseOperations(this);
        Cursor cursor = DB.getAuthKey(DB);
        cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            DB.putAuthKey(DB,result);
            closeCursor(cursor);
            scan.setEnabled(true);
        }
        else {
            DB.getWritableDatabase().delete(TableData.TableInfo.TABLE_AUTH, null, null);
            DB.putAuthKey(DB,result);
            closeCursor(cursor);
        }
    }

    public void getAuthKey() {
        DatabaseOperations DB = new DatabaseOperations(this);
        Cursor cursor = DB.getAuthKey(DB);
        cursor.moveToFirst();
        Encryption.setSecret(cursor.getString(0));
        closeCursor(cursor);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null && scanResult.getContents() != null) {
            String result = scanResult.getContents();
            Log.d("code", result);
            if (result.split(" ")[0].contains("Auth")) {
                Result.setResult(result);
                if (!isKeyScan) {
                    isKeyScan = false;
                    startActivity(actInvalid);
                }
                else {
                    setAuthKey(result);
                    isKeyScan = false;
                    Toast keyScanned = Toast.makeText(ctx, "key is scanned", Toast.LENGTH_LONG);
                    keyScanned.show();
                }
            }
            else {
                if (!isKeyScan) {
                    getAuthKey();
                    String[] encresult = Encryption.decrypt(result);
                    if (encresult[0].equals("valid")) {
                        result = encresult[1];
                        Result.setResult(result);
                    }
                    else {
                        Result.setResult(encresult[1]);
                        startActivity(actInvalid);
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
                if (isKeyScan) {
                    Toast keyScanned = Toast.makeText(ctx, "not a key", Toast.LENGTH_LONG);
                    keyScanned.show();
                    isKeyScan = false;
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
        settingsTitle.setText(R.string.settings);
        btnEng.setText(R.string.engLang);
        btnEst.setText(R.string.estLang);
        delete.setText(R.string.deleteEntries);
        btnScan.setText(R.string.btnScanKey);
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
