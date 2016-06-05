package com.example.kristen.piletdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private static Button scantValid;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OnClickButtonLIstener();

    }

    public void OnClickButtonLIstener() { //ise tehtud suvaline meetod
        scantValid = (Button) findViewById(R.id.scanValid);
        scantValid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.example.kristen.piletdemo.ScanActivity");
                startActivity(intent);
            }
        });
    }
}
