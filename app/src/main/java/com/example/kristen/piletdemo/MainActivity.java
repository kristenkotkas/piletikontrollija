package com.example.kristen.piletdemo;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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
                startActivity(actSettings);
            }
        });

        //loome scanneri vms //https://github.com/journeyapps/zxing-android-embedded
        //pmts saaks appi sisese ka teha
        //st barcodeview activity vms on olemas https://github.com/journeyapps/zxing-android-embedded/blob/master/EMBEDDING.md
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Valid.exists = false;
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Skänni!");
                integrator.setCameraId(0);  // Use a specific camera of the device // vb pole vaja
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(true);
                integrator.setOrientationLocked(true); //kuidas portrait lock saada? või kaamerale pole vaja?
                integrator.initiateScan();

            }
        });
    }

    public void validator(String ticket) {
        String eventName, dateTime;
        String[] texts;

        texts = ticket.split("\n");
        eventName = texts[0];
        dateTime = texts[1];

        int total = 0;
        totalAmount = (TextView) findViewById(R.id.totalAmount);
        totalAmount.setText(Integer.toString(total));
    }



    //scanneri vastus
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult.getContents() != null && scanResult != null) {
            String result = scanResult.getContents();
            Log.d("code", result);
            Result.setResult(result);
            try {
                validator(result);
                startActivity(actValid);

            } catch (RuntimeException e) {
                startActivity(actInvalid);
            }
        }
    }
}
