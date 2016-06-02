package com.example.kristen.piletdemo;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private LinearLayout linLay, grid;
    private TextView ticket, amount, totalAmount;
    private LinearLayout space;
    private Button scan;
    private List<LinearLayout> tickets;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scan = (Button) findViewById(R.id.btnScan);
        //loome scanneri vms //https://github.com/journeyapps/zxing-android-embedded
        //pmts saaks appi sisese ka teha
        //st barcodeview activity vms on olemas https://github.com/journeyapps/zxing-android-embedded/blob/master/EMBEDDING.md
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    public LinearLayout rowMaker(String info, int kogus) {
        this.linLay = new LinearLayout(this);
        this.ticket = new TextView(this);
        this.amount = new TextView(this);

        int dp40 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics()); //laseb määrata dp suurusi
        int dp20 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()); //laseb määrata dp suurusi
        int dp10 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()); //laseb määrata dp suurusi
        int dp8 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()); //laseb määrata dp suurusi


        this.linLay.setOrientation(LinearLayout.HORIZONTAL);
        this.linLay.setMinimumHeight(dp40);
        this.linLay.setBackgroundColor(Color.WHITE);
        this.linLay.setPadding(dp20,0,dp20,0); //left, top, right, bottom

        this.ticket.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.FILL_PARENT, Gravity.LEFT));
        this.ticket.setText(info);
        this.ticket.setTextSize(dp8);
        this.ticket.setBackgroundColor(Color.WHITE);

        this.amount.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT, Gravity.RIGHT));
        this.amount.setText(Integer.toString(kogus));
        this.amount.setTextSize(dp8);
        this.amount.setBackgroundColor(Color.WHITE);
        this.amount.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);

        this.linLay.addView(this.ticket);
        this.linLay.addView(this.amount);

        this.linLay.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, dp40));
        this.space = new LinearLayout(this);
        this.space.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, dp10));

        this.grid = new LinearLayout(this);
        this.grid.setOrientation(LinearLayout.VERTICAL);
        grid.addView(linLay);
        grid.addView(space);

        return grid;
    }

    public List<LinearLayout> ticketMaker(String ticket) {
        String eventName, dateTime;
        String[] texts;
        List<LinearLayout> result = new ArrayList<>();

        texts = ticket.split("\n");
        eventName = texts[0];
        dateTime = texts[1];

        int total = 0;
        totalAmount = (TextView) findViewById(R.id.totalAmount);

        for (int i = 2; i < texts.length-1; i++) {
            total = total + Integer.parseInt(texts[i].split(": ")[1]);
            result.add(rowMaker(texts[i].split(": ")[0], Integer.parseInt(texts[i].split(": ")[1])));
        }
        totalAmount.setText(Integer.toString(total));
        return result;
    }

    //scanneri vastus
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            String result = scanResult.getContents();
            Log.d("code", result);
            tickets = ticketMaker(result);
            TableLayout tableLay = (TableLayout) findViewById(R.id.tabelLay);
            tableLay.removeAllViews();
            for (LinearLayout elem: tickets) {
                tableLay.addView(elem);
            }
        }
    }
}
