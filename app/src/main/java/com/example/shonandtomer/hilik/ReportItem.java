package com.example.shonandtomer.hilik;

import java.util.Date;

/**
 * Created by sotmazgi on 2/14/2017.
 */
public class ReportItem {
    private  int id;
    private Date entry;
    private Date exit;


    public ReportItem() {
        this.id = 0;
        this.entry = new Date();
        this.exit = new Date();
    }

    public ReportItem(Date entry, Date exit) {
        this.entry = entry;
        this.exit = exit;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getEntry() {

        return entry;
    }

    public void setEntry(Date entry) {
        this.entry = entry;
    }

    public Date getExit() {
        return exit;
    }

    public void setExit(Date exit) {
        this.exit = exit;
    }

    public String getTotalHoursSting()  {
        long[] elapsedTime =  timeDifference(entry, exit);
        return "Total " + elapsedTime[0] + " Hours and " + elapsedTime[1] + " Minutes";
    }
    public long[] getTotalHours(){
        return timeDifference(entry, exit);
    }

    private  long[] timeDifference(Date startDate, Date endDate){

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

        long[] times = new long[3];
        times[0] = elapsedHours;
        times[1] = elapsedMinutes;
        times[2] = elapsedSeconds;

        return times;

       /* Log.d("timeTag", elapsedDays + " days, " +
                        elapsedHours + " hours, " +
                        elapsedMinutes + " minutes, " +
                        elapsedSeconds + " seconds");
                        */
    }
}