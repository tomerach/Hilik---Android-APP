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

/**
 * Created by sotmazgi on 2/14/2017.
 */
public class ListViewAdapter extends BaseAdapter     {
    public ArrayList<ReportItem> reportList;
    Activity activity;

    ImageView dateImg;
    TextView dateTxt;
    TextView hoursTxt;

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
            dateImg=(ImageView) convertView.findViewById(R.id.reportItemImg);
            dateTxt=(TextView) convertView.findViewById(R.id.reportItemDate);
            hoursTxt=(TextView) convertView.findViewById(R.id.reportItemHours);

        }
        ReportItem reportItem = reportList.get(position);

        //Get or Generate Date
        Date inDate = reportItem.getInDate();
        Date outDate = reportItem.getInDate();

        //date format
        DateFormat dateFormat = DateFormat.getDateInstance();

        String dateString = dateFormat.format(inDate);
        dateTxt.setText(dateString);
        hoursTxt.setText(reportItem.getTotalHours());

        return convertView;
    }
}
