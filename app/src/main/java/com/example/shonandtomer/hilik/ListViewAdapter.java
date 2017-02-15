package com.example.shonandtomer.hilik;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by sotmazgi on 2/14/2017.
 */
public class ListViewAdapter extends BaseAdapter     {
    public ArrayList<ReportItem> reportList;
    Activity activity;

    TextView dateTxt;
    TextView inTimeTxt;
    TextView outTimeTxt;
    TextView totalHoursTxt;

    public ListViewAdapter(Activity activity,ArrayList<ReportItem> list){
        super();
        this.activity=activity;
        this.reportList=list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return reportList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return reportList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        LayoutInflater inflater=activity.getLayoutInflater();

        if(convertView == null){

            convertView=inflater.inflate(R.layout.report_item, null);

            dateTxt=(TextView) convertView.findViewById(R.id.date);
            inTimeTxt=(TextView) convertView.findViewById(R.id.inTime);
            outTimeTxt=(TextView) convertView.findViewById(R.id.outTime);
            totalHoursTxt=(TextView) convertView.findViewById(R.id.totalHours);

        }

        ReportItem reportItem = reportList.get(position);
        dateTxt.setText(reportItem.getDate().toString());
        inTimeTxt.setText(Integer.toString(reportItem.getInTime()));
        outTimeTxt.setText(Integer.toString(reportItem.getOutTime()));
        totalHoursTxt.setText(Float.toString(reportItem.getTotalHours()));

        return convertView;
    }
}
