package com.example.shonandtomer.hilik;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ListViewAdapter extends BaseAdapter     {

    private ArrayList<ReportItem> reportList;
    private Activity activity;
    private ImageView dateImg;
    private TextView dateTxt;
    private TextView hoursTxt;

    public ListViewAdapter(Activity activity,ArrayList<ReportItem> list){
        super();
        this.activity=activity;
        this.reportList=list;
    }

    @Override
    public int getCount() {
        return reportList.size();
    }

    @Override
    public Object getItem(int position) {
        return reportList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater=activity.getLayoutInflater();

        if(convertView == null){
            convertView=inflater.inflate(R.layout.report_item, null);
            dateImg=(ImageView) convertView.findViewById(R.id.reportItemImg);
            dateTxt=(TextView) convertView.findViewById(R.id.reportItemDate);
            hoursTxt=(TextView) convertView.findViewById(R.id.reportItemHours);
        }
        ReportItem reportItem = reportList.get(position);

        //Get the Date
        Date entry = reportItem.getEntry();
        int day = entry.getDate();


        int id = activity.getResources().getIdentifier(
                "com.example.shonandtomer.hilik:drawable/d" + day, null, null);
        dateImg.setImageResource(id);

        //date format
        DateFormat dateFormat = DateFormat.getDateInstance();

        dateTxt.setText(dateFormat.format(entry));
        hoursTxt.setText(reportItem.getTotalHoursSting());

        return convertView;
    }
}
