package com.example.shonandtomer.hilik;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button settingsBtn;
    private Button reportBtn;
    private TextView estimatedTxt;
    private TextView salaryTxt;
    private Address selectedAddress = null;

    private final int SETTING_ACTIVITY = 1;
    private final int REPORT_ACTIVITY = 2;
    private final String ADDRESS = "Address";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI(); //Init all constants

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(settingIntent, SETTING_ACTIVITY);
            }


        });

        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reportIntent = new Intent(MainActivity.this, ReportActivity.class);
                startActivity(reportIntent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case (SETTING_ACTIVITY):
                if (resultCode == Activity.RESULT_OK) {
                    selectedAddress = data.getParcelableExtra(ADDRESS);
                    Toast.makeText(MainActivity.this, "Address: " + selectedAddress.getAddressLine(0), Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(this, "Setting activity ended with an error", Toast.LENGTH_SHORT).show();

            case (REPORT_ACTIVITY):
                if (resultCode == Activity.RESULT_OK) {

                }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /*
    Initiate all constants
     */
    private void initUI() {

        settingsBtn = (Button) findViewById(R.id.settingsBtn);
        reportBtn = (Button) findViewById(R.id.reportBtn);
        estimatedTxt = (TextView) findViewById(R.id.estimatedTxt);
        salaryTxt = (TextView) findViewById(R.id.salaryTxt);
    }
}
