package com.example.shonandtomer.hilik;

import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ReportActivity extends AppCompatActivity {

    ListView listViewReport;

    private ArrayList<ReportItem> reportList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        this.listViewReport = (ListView) findViewById(R.id.listViewReport);



        reportList = new ArrayList<ReportItem>();

        Date syntheticDate = new Date();
        Date date = new Date();
        syntheticDate.setHours(17);

        //Log.d("timeTag", "date: " + date.toString());
        //Log.d("timeTag", "syntheticDate: " + syntheticDate.toString());

        ReportItem reportItem1 = new ReportItem(date , syntheticDate);
        ReportItem reportItem2 = new ReportItem(date , syntheticDate);
        ReportItem reportItem3 = new ReportItem(date , syntheticDate);

        reportList.add(reportItem1);
        reportList.add(reportItem2);
        reportList.add(reportItem3);

        ListViewAdapter adapter=new ListViewAdapter(this, reportList);
        listViewReport.setAdapter(adapter);

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
}
