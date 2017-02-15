package com.example.shonandtomer.hilik;

import java.util.Date;

/**
 * Created by sotmazgi on 2/14/2017.
 */
public class ReportItem {
    private Date date;
    private int inTime;
    private int outTime;
    private float totalHours;

    public ReportItem(Date date, int inTime, int outTime) {
        this.date = date;
        this.inTime = inTime;
        this.outTime = outTime;
        this.totalHours = outTime - inTime;
    }

    public Date getDate() {

        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getInTime() {
        return inTime;
    }

    public void setInTime(int inTime) {
        this.inTime = inTime;
    }

    public int getOutTime() {
        return outTime;
    }

    public void setOutTime(int outTime) {
        this.outTime = outTime;
    }

    public float getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(float totalHours) {
        this.totalHours = totalHours;
    }
}
