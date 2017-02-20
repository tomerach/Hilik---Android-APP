package com.example.shonandtomer.hilik;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ReportActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String LOG = "ReportActivityLOG";
    private ListView listViewReport;
    private Spinner dropdown;
    private DatabaseHelper db;
    private ArrayList<ReportItem> reportList;
    private ListViewAdapter reportListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        db = new DatabaseHelper(this);

        listViewReport = (ListView) findViewById(R.id.listViewReport);
        dropdown = (Spinner) findViewById(R.id.monthSppiner);

        ArrayList<String> months = db.getAllAvailableMonths();
        ArrayAdapter<String> monthsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, months);
        dropdown.setAdapter(monthsAdapter);
        dropdown.setOnItemSelectedListener(this);

        listViewReport.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id)
            {
                int pos=position+1;
                Toast.makeText(ReportActivity.this, Integer.toString(pos)+" Clicked", Toast.LENGTH_SHORT).show();
            }

        });
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // An item was selected. You can retrieve the selected item using
        String monthString = (String) parent.getItemAtPosition(position);
        reportList = db.getAllReportByMonth(DatabaseHelper.stringMonthToIntMonth(monthString));
        reportListAdapter = new ListViewAdapter(this, reportList);
        listViewReport.setAdapter(reportListAdapter);
        //Log.d(LOG, "month: " + monthString);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
