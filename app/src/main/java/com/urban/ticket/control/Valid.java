package com.urban.ticket.control;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Valid extends AppCompatActivity {
    private LinearLayout linLayout;
    private LinearLayout totalLayout;
    private LinearLayout linLay;
    private LinearLayout space;
    private LinearLayout grid;
    private TextView ticket;
    private TextView amount;
    private TextView totalAmount;
    private TableLayout tableLay;
    private List<LinearLayout> tickets;
    public static Button reset;
    private Button scan;
    private Typeface ticketfont;
    private TextView ticketType, quantity, total, valid;
    private Intent actInvalid;
    private String code;
    private Context ctx = this;
    public static boolean exists = false;
    private Locale myLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valid);

        totalLayout = (LinearLayout) findViewById(R.id.totalLayoutValid);
        linLayout = (LinearLayout) findViewById(R.id.linLayoutValid);
        reset = (Button) findViewById(R.id.btnResetValid);
        scan = (Button) findViewById(R.id.btnScanValid);
        ticketType = (TextView) findViewById(R.id.ticketTypeValid);
        quantity = (TextView) findViewById(R.id.quantityValid);
        total = (TextView) findViewById(R.id.totalValid);
        valid = (TextView) findViewById(R.id.validValid);

        ticketfont = Typeface.createFromAsset(getAssets(), "ticketfont2.ttf");
        actInvalid = new Intent("com.urban.ticket.control.Invdalid");

        reset.setTypeface(ticketfont);
        scan.setTypeface(ticketfont);
        ticketType.setTypeface(ticketfont);
        quantity.setTypeface(ticketfont);
        total.setTypeface(ticketfont);
        valid.setTypeface(ticketfont);

        printer();

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


    public LinearLayout rowMaker(String info, int kogus) {
        this.linLay = new LinearLayout(this);
        this.ticket = new TextView(this);
        this.amount = new TextView(this);

        int dp40 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics()); //laseb määrata dp suurusi
        int dp20 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()); //laseb määrata dp suurusi
        int dp10 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()); //laseb määrata dp suurusi
        int dp8 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()); //laseb määrata dp suurusi
        int dp4 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()); //laseb määrata dp suurusi


        this.linLay.setOrientation(LinearLayout.HORIZONTAL);
        this.linLay.setMinimumHeight(dp40);
        this.linLay.setBackgroundColor(Color.WHITE);
        this.linLay.setPadding(dp20,dp4,dp20,0); //left, top, right, bottom

        this.ticket.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.FILL_PARENT, Gravity.LEFT));
        this.ticket.setText(info);
        this.ticket.setTextSize(dp8);
        this.ticket.setBackgroundColor(Color.WHITE);
        this.ticket.setTypeface(ticketfont);
        this.ticket.setTextColor(Color.parseColor("#b0afb0"));

        this.amount.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT, Gravity.RIGHT));
        this.amount.setText(Integer.toString(kogus));
        this.amount.setTextSize(dp8);
        this.amount.setBackgroundColor(Color.WHITE);
        this.amount.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        this.amount.setTypeface(ticketfont);
        this.amount.setTextColor(Color.parseColor("#b0afb0"));

        this.linLay.addView(this.ticket);
        this.linLay.addView(this.amount);

        this.linLay.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, dp40));
        this.space = new LinearLayout(this);
        this.space.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, dp10));
        this.space.setBackgroundColor(Color.TRANSPARENT);

        this.grid = new LinearLayout(this);
        this.grid.setOrientation(LinearLayout.VERTICAL);
        grid.addView(linLay);
        grid.addView(space);

        return grid;
    }

    public List<LinearLayout> ticketMaker(String ticket) {
        String[] texts;
        List<LinearLayout> result = new ArrayList<>();

        texts = ticket.split("\n");

        int total = 0;
        totalAmount = (TextView) findViewById(R.id.totalAmountValid);
        totalAmount.setTypeface(ticketfont);
        totalAmount.setTextColor(Color.parseColor("#b0afb0"));
        code = texts[0];

        for (int i = 3; i < texts.length-1; i++) {
            total = total + Integer.parseInt(texts[i].split(": ")[1]);
            result.add(rowMaker(texts[i].split(": ")[0], Integer.parseInt(texts[i].split(": ")[1])));
        }
        totalAmount.setText(Integer.toString(total));
        return result;
    }

    public void printer() {
        try {
            tableLay = (TableLayout) findViewById(R.id.tabelLayValid);
            tableLay.removeAllViews(); //kui midagi ees on, siis tühjendab ära
            tickets = ticketMaker(Result.getResult());
            for (LinearLayout elem: tickets) {
                tableLay.addView(elem);
            }
            DatabaseOperations DB = new DatabaseOperations(ctx);
            Cursor cursor = DB.getInformation(DB);
            validation(cursor);
            DB.putInformation(DB, code);
            MainActivity.closeCursor(cursor);
            DB.close();

        } catch (Exception e) {
            onBackPressed();
            startActivity(actInvalid);
        }
    }

    public void validation(Cursor cursor) {
        cursor.moveToFirst();
        do {
            if (!(cursor.getCount() == 0)) {
                if (cursor.getString(0).equals(code)) {
                    System.out.println("FAIL FAIL");
                    exists = true;
                    MainActivity.closeCursor(cursor);
                    throw new RuntimeException();
                }
            }
        }
        while (cursor.moveToNext());
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
        ticketType.setText(R.string.tickettype);
        quantity.setText(R.string.quantity);
        total.setText(R.string.total);
        valid.setText(R.string.valid);
    }

}
