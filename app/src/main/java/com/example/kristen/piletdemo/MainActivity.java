package com.example.kristen.piletdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    public static Button scan;
    private LinearLayout drawer;
    private Intent actValid;
    private Intent actInvalid;
    private Typeface ticketfont;
    private String code;
    private boolean isValid = false;
    private Locale myLocale;
    private TextView settingsTitle;
    private Button delete, btnEng, btnEst, btnScan, btnVõru;
    private Context ctx = this;
    private boolean isKeyScan;
    private int deletePressed = 0;
    private Toast delPressed;
    private Toast deleted;
    private String scanPrompt;
    private String keyIsScanned;
    private String notKey;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ticketfont = Typeface.createFromAsset(getAssets(), "ticketfont2.ttf");
        scan = (Button) findViewById(R.id.btnScan);
        drawer = (LinearLayout) findViewById(R.id.drawer);
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
        btnVõru = (Button) findViewById(R.id.võruLangBtn);
        btnVõru.setTypeface(ticketfont);
        delPressed = Toast.makeText(ctx, "", Toast.LENGTH_SHORT);
        deleted = Toast.makeText(ctx, "", Toast.LENGTH_SHORT);
        scanPrompt = getResources().getString(R.string.scanPrompt);
        keyIsScanned = getResources().getString(R.string.keyIsScanned);
        notKey = getResources().getString(R.string.notAKey);

        btnEng.setOnClickListener(this);
        btnEst.setOnClickListener(this);
        btnVõru.setOnClickListener(this);

        DatabaseOperations DB = new DatabaseOperations(this);
        Cursor cursor = DB.getAuthKey(DB);
        cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            scan.setEnabled(false);
        } else scan.setEnabled(true);

        loadLocale();
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Valid.exists = false;
                isKeyScan = false;
                scan();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            private Timer timer = new Timer();

            @Override
            public void onClick(View v) {
                deletePressed ++;
                deleted.setText(R.string.entriesDeleted);

                if (deletePressed == 3) {
                    deletePressed = 0;
                    DatabaseOperations DB = new DatabaseOperations(ctx);
                    DB.getWritableDatabase().delete(TableData.TableInfo.TABLE_NAME, null, null);
                    delPressed.cancel();
                    deleted.show();
                }
                else {
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            deletePressed = 0;
                        }
                    }, 4000);
                    deleted.cancel();
                    delPressed.setText(getResources().getString(R.string.pressesLeft) + " " + Integer.toString(3 - deletePressed));
                    delPressed.show();
                }
            }
        });

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isKeyScan = true;
                scan();
            }
        });


    }

    public void scan() {
        //https://github.com/journeyapps/zxing-android-embedded
        //https://github.com/journeyapps/zxing-android-embedded/blob/master/EMBEDDING.md
        IntentIntegrator integratorTicket = new IntentIntegrator(this);
        integratorTicket.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integratorTicket.setPrompt(scanPrompt);
        integratorTicket.setCameraId(0);  // Use a specific camera of the device // vb pole vaja
        integratorTicket.setBeepEnabled(false);
        integratorTicket.setBarcodeImageEnabled(true);
        integratorTicket.setOrientationLocked(true); //kuidas portrait lock saada? või kaamerale pole vaja? //done
        integratorTicket.initiateScan();
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
            DB.close();
            scan.setEnabled(true);
        }
        else {
            DB.getWritableDatabase().delete(TableData.TableInfo.TABLE_AUTH, null, null);
            DB.putAuthKey(DB,result);
            closeCursor(cursor);
            DB.close();
        }
    }

    public void getAuthKey() {
        DatabaseOperations DB = new DatabaseOperations(this);
        Cursor cursor = DB.getAuthKey(DB);
        cursor.moveToFirst();
        Encryption.setSecret(cursor.getString(0));
        closeCursor(cursor);
        DB.close();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null && scanResult.getContents() != null) {
            String result = scanResult.getContents();
            Log.d("code", result);
            if (result.split(" ")[0].contains("Auth")) {
                Result.setResult(result);
                if (!isKeyScan) {
                    startActivity(actInvalid);
                }
                else {
                    setAuthKey(result);
                    Toast keyScanned = Toast.makeText(ctx, keyIsScanned, Toast.LENGTH_SHORT);
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
                    } catch (Exception e) {
                        startActivity(actInvalid);
                    }
                }
                if (isKeyScan) {
                    Toast keyScanned = Toast.makeText(ctx, notKey, Toast.LENGTH_SHORT);
                    keyScanned.show();
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
        deleted.setText(R.string.entriesDeleted);
        scanPrompt = getResources().getString(R.string.scanPrompt);
        keyIsScanned = getResources().getString(R.string.keyIsScanned);
        notKey = getResources().getString(R.string.notAKey);
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
            case R.id.võruLangBtn:
                lang = "et";
                break;
            default:
                break;
        }
        changeLang(lang);
    }
}
