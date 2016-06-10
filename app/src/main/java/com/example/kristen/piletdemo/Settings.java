package com.example.kristen.piletdemo;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Settings extends AppCompatActivity {
    private Typeface ticketfont;
    private TextView settingsTitle;
    private Button delete;
    private Context ctx = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ticketfont = Typeface.createFromAsset(getAssets(), "ticketfont2.ttf");

        settingsTitle = (TextView) findViewById(R.id.settingsTitle);
        settingsTitle.setTypeface(ticketfont);
        delete = (Button) findViewById(R.id.deleteEntries);
        delete.setTypeface(ticketfont);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseOperations DB = new DatabaseOperations(ctx);
                DB.getWritableDatabase().delete(TableData.TableInfo.TABLE_NAME, null, null);
            }
        });


    }

}
