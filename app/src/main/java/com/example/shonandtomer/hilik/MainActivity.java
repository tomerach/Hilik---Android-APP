package com.example.shonandtomer.hilik;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;

import java.net.URISyntaxException;
import java.security.Permission;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private static final String LOG = "MainActivityLOG";
    private DatabaseHelper db;
    private mehdi.sakout.fancybuttons.FancyButton settingsBtn;
    private mehdi.sakout.fancybuttons.FancyButton reportBtn;
    private mehdi.sakout.fancybuttons.FancyButton startServiceBtn;
    private mehdi.sakout.fancybuttons.FancyButton stopServiceBtn;
    private TextView estimatedTxt;
    private TextView salaryTxt;
    private Spinner dropdown;
    private Address selectedAddress = null;
    private Intent serviceIntent = null;
    private Gson gson;
    private float salaryPerHour;
    private float transportation;
    private String currencySymbol;
    private boolean isExtraChecked;
    private float extraFromHour;
    private float extraPrecentage;
    private static final int SETTING_ACTIVITY = 1;
    private static final int REPORT_ACTIVITY = 2;
    private static final String ADDRESS = "Address";
    private static final String MY_PREFS_NAME = "SettingsFile";
    private ArrayList<String> months;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);
        initUI(); //Init all constants


        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(settingIntent, SETTING_ACTIVITY);
                retrieveSharedPreferences();
            }


        });

        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reportIntent = new Intent(MainActivity.this, ReportActivity.class);
                startActivityForResult(reportIntent, REPORT_ACTIVITY);
            }
        });

        startServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(retrieveSharedPreferences()) {
                    if(serviceIntent != null)
                        stopService(serviceIntent);

                    createServiceIntent(selectedAddress);
                    startService(serviceIntent);
                    Toast.makeText(MainActivity.this, "Service started", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(MainActivity.this, "Please go to settings and configure your workplace's address.", Toast.LENGTH_SHORT).show();
            }


        });

        stopServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (serviceIntent == null)
                    Toast.makeText(MainActivity.this, "Service is not running", Toast.LENGTH_SHORT).show();
                else
                {
                    stopService(serviceIntent);
                    serviceIntent = null;
                    Toast.makeText(MainActivity.this, "Service stopped", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void saveIntentIntoSharedPreferences(Intent serviceIntent)
    {
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString("serviceIntent", serviceIntent.toURI()).commit();
    }

    private void createServiceIntent(Address selectedAddress) {
        serviceIntent = new Intent(MainActivity.this, LocationService.class);
        serviceIntent.putExtra("chosen address", selectedAddress);
        saveIntentIntoSharedPreferences(serviceIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case (SETTING_ACTIVITY):
                if (resultCode == Activity.RESULT_OK) {
                    selectedAddress = data.getParcelableExtra(ADDRESS);
                    retrieveSharedPreferences();
                    if(serviceIntent != null) {
                        createServiceIntent(selectedAddress);
                        startService(serviceIntent);
                    }
                    estimatedTxt.setText("Start your service or add shifts manually.");
                }

            case (REPORT_ACTIVITY):
                if (resultCode == Activity.RESULT_OK) {

                }
        }
        setDropDown();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    private void setDropDown(){
        months = db.getAllAvailableMonths();
        ArrayAdapter<String> monthsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, months);
        dropdown.setAdapter(monthsAdapter);
        dropdown.setOnItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        setDropDown();
        super.onResume();
    }

    /*
        Initiate all constants
         */
    private void initUI() {
        dropdown = (Spinner) findViewById(R.id.monthSppiner);
        setDropDown();
        settingsBtn = (mehdi.sakout.fancybuttons.FancyButton) findViewById(R.id.settingsBtn);
        reportBtn = (mehdi.sakout.fancybuttons.FancyButton) findViewById(R.id.reportBtn);
        startServiceBtn = (mehdi.sakout.fancybuttons.FancyButton) findViewById(R.id.startServiceBtn);
        stopServiceBtn = (mehdi.sakout.fancybuttons.FancyButton) findViewById(R.id.stopServiceBtn);
        estimatedTxt = (TextView) findViewById(R.id.estimatedTxt);
        salaryTxt = (TextView) findViewById(R.id.salaryTxt);

        gson = new Gson();

        if(!retrieveSharedPreferences())
            estimatedTxt.setText("Please go to 'Settings' and configure your workplace's address.");
        else
            estimatedTxt.setText("Please start your location service or add shifts manually.");

    }

    private String calculateSalary(String month){
        ArrayList<ReportItem> reportList = db.getAllReportByMonth(DatabaseHelper.stringMonthToIntMonth(month));

        float hours = 0;
        float sumRegularHours = 0;
        float sumExtraHours = 0;
        for(ReportItem report: reportList){
            hours = report.getTotalHours();
            if(isExtraChecked && hours > extraFromHour){
                float extraHours = hours - extraFromHour;
                sumExtraHours += extraHours;
                sumRegularHours += hours - extraHours;
            }
            else
                sumRegularHours += hours;
        }

        int days = reportList.size();
        float salary = (sumRegularHours * salaryPerHour) + (sumExtraHours * (extraPrecentage/100) * salaryPerHour) + (days * transportation) ;
        return Float.toString(salary);
    }

    private boolean retrieveSharedPreferences() {
        final SharedPreferences settings = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);

        try {
            String uri = settings.getString("serviceIntent", null);
            if(uri != null)
                serviceIntent = Intent.getIntent(uri);
        } catch (URISyntaxException e) {
            Toast.makeText(this, "Intent parser exeption", Toast.LENGTH_SHORT).show();
        }

        String stringifiedAddress = settings.getString("selectedAddress", null);

        if (stringifiedAddress != null) {
            selectedAddress = gson.fromJson(stringifiedAddress, Address.class);
            salaryPerHour = Integer.parseInt(settings.getString("salaryInput", null));
            transportation = Float.parseFloat(settings.getString("transportInput", null));
            currencySymbol = settings.getString("currencySpinnerVal", "₪");
            isExtraChecked = settings.getBoolean("extraHoursSwitch", false);

            if(isExtraChecked) {
                extraFromHour = Float.parseFloat(settings.getString("extraTimeInput", null));
                extraPrecentage = Integer.parseInt(settings.getString("precentageInput", null));
            }
            return true;
        }
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String monthString = (String) parent.getItemAtPosition(position);
        estimatedTxt.setText("Your Estimated Salary for " + monthString + " Is:");
        salaryTxt.setText(calculateSalary(monthString) + currencySymbol);
        Log.d(LOG, "month: " + monthString);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
