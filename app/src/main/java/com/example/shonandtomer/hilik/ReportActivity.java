package com.example.shonandtomer.hilik;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ReportActivity extends AppCompatActivity {

    ListView listViewReport;

    public static final String FIRST_COLUMN="First";
    public static final String SECOND_COLUMN="Second";
    public static final String THIRD_COLUMN="Third";
    public static final String FOURTH_COLUMN="Fourth";

    private ArrayList<ReportItem> reportList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        this.listViewReport = (ListView) findViewById(R.id.listViewReport);

        reportList = new ArrayList<ReportItem>();
        ReportItem reportItem1 = new ReportItem(new Date() , 8 , 5);
        ReportItem reportItem2 = new ReportItem(new Date() , 8 , 7);
        ReportItem reportItem3 = new ReportItem(new Date() , 8 , 9);

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
