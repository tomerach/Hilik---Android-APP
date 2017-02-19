package com.example.shonandtomer.hilik;

import java.util.Date;

/**
 * Created by sotmazgi on 2/14/2017.
 */
public class ReportItem {
    private Date inDate;
    private Date outDate;

    public ReportItem(Date inDate, Date outDate) {

        this.inDate = inDate;
        this.outDate = outDate;
    }
    public void setInDate(Date inDate) {
        this.inDate = inDate;
    }

    public void setOutDate(Date outDate) {
        this.outDate = outDate;
    }

    public Date getInDate() {
        return inDate;
    }

    public Date getOutDate() { return outDate; }

    public String getTotalHours()  {
        return timeDifference(inDate, outDate);
    }

    private String timeDifference(Date startDate, Date endDate){

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        return "Total " + elapsedHours + " Hours and " + elapsedMinutes + " Minutes";

       /* Log.d("timeTag", elapsedDays + " days, " +
                        elapsedHours + " hours, " +
                        elapsedMinutes + " minutes, " +
                        elapsedSeconds + " seconds");
                        */
    }
}