package com.example.shonandtomer.hilik;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by sotmazgi on 2/20/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 4;


    // Table Create Statements
    // ReportingList table create statement
    private static final String CREATE_TABLE_REPORTINGLIST = "CREATE TABLE "
            + Constants.reports.TABLE_REPORTS + "(" + Constants.reports.KEY_ID + " INTEGER PRIMARY KEY," + Constants.reports.DAY
            + " TEXT," + Constants.reports.MONTH + " TEXT," + Constants.reports.YEAR
            + " TEXT," + Constants.reports.ENTRY + " TEXT," + Constants.reports.EXIT
            + " TEXT" + ")";


    public DatabaseHelper(Context context) {
        super(context, Constants.DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_REPORTINGLIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + Constants.reports.TABLE_REPORTS);

        // create new tables
        onCreate(db);
    }

    /*
* Creating a report.
*/
    public long createReport(ReportItem report) {
        SQLiteDatabase db = this.getWritableDatabase();

        Date entry = report.getEntry();
        Date exit = report.getExit();

        int day = entry.getDate();
        int month = entry.getMonth();
        int year = entry.getYear();

        SimpleDateFormat formatter = new SimpleDateFormat(
                "dd-MM-yyyy HH:mm:ss" , Locale.ENGLISH);
        String entryString = formatter.format(entry);
        String exitString = formatter.format(exit);

        ContentValues values = new ContentValues();
        values.put(Constants.reports.DAY, Integer.toString(day));
        values.put(Constants.reports.MONTH, Integer.toString(month));
        values.put(Constants.reports.YEAR, Integer.toString(year));
        values.put(Constants.reports.ENTRY, entryString);
        values.put(Constants.reports.EXIT, exitString);

        // insert row
        long report_id = db.insert(Constants.reports.TABLE_REPORTS, null, values);

        return report_id;
    }

    /*
 * getting all reports
 * */
    public ArrayList<ReportItem> getAllReports() {
        ArrayList<ReportItem> reports = new ArrayList<ReportItem>();
        String selectQuery = "SELECT  * FROM " + Constants.reports.TABLE_REPORTS;

        Log.d(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                ReportItem report = new ReportItem();
                String entryString = c.getString(c.getColumnIndex(Constants.reports.ENTRY));
                String exitString = c.getString(c.getColumnIndex(Constants.reports.EXIT));
                Log.d(LOG, "entry: " + entryString + " exit: " + exitString);
                SimpleDateFormat formatter = new SimpleDateFormat(
                        "dd-MM-yyyy HH:mm:ss" , Locale.ENGLISH);
                try {
                    Date entry = formatter.parse(entryString);
                    Date exit = formatter.parse(exitString);
                    report.setEntry(entry);
                    report.setExit(exit);
                    Log.d(LOG, "Parse succeed");
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // adding to todo list
                reports.add(report);
            } while (c.moveToNext());
        }

        return reports;
    }

    /*
 * getting all reports under single month
 * */
    public ArrayList<ReportItem> getAllReportByMonth(int month) {
        ArrayList<ReportItem> reports = new ArrayList<ReportItem>();

        String selectQuery = "SELECT  * FROM " + Constants.reports.TABLE_REPORTS
                + " rp WHERE rp."
                + Constants.reports.MONTH + " = '" + month + "'" ;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                ReportItem report = new ReportItem();
                String entryString = c.getString(c.getColumnIndex(Constants.reports.ENTRY));
                String exitString = c.getString(c.getColumnIndex(Constants.reports.EXIT));
                Log.d(LOG, "entry: " + entryString + " exit: " + exitString);
                SimpleDateFormat formatter = new SimpleDateFormat(
                        "dd-MM-yyyy HH:mm:ss" , Locale.ENGLISH);
                try {
                    Date entry = formatter.parse(entryString);
                    Date exit = formatter.parse(exitString);
                    report.setEntry(entry);
                    report.setExit(exit);
                    Log.d(LOG, "Parse succeed");
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // adding to todo list
                reports.add(report);
            } while (c.moveToNext());
        }

        return reports;
    }

    /*
* getting all available months
* */
    public ArrayList<String> getAllAvailableMonths() {
        ArrayList<String> months = new ArrayList<String>();

        String selectQuery = "SELECT  * FROM " + Constants.reports.TABLE_REPORTS;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {

                String monthString = c.getString(c.getColumnIndex(Constants.reports.MONTH));
                // adding to months list
                months.add(intMonthToStingMonth(Integer.parseInt(monthString)));

            } while (c.moveToNext());
        }

        return months;
    }

    public static String intMonthToStingMonth(int month){
        switch(month) {
            case 0:
                return "January";
            case 1:
                return "February";
            case 2:
                return "March";
            case 3:
                return "April";
            case 4:
                return "May";
            case 5:
                return "June";
            case 6:
                return "July";
            case 7:
                return "August";
            case 8:
                return "September";
            case 9:
                return "October";
            case 10:
                return "November";
            case 11:
                return "December";
            default:
                return "January";
        }
    }

    public static int stringMonthToIntMonth(String month){
        switch(month) {
            case "January":
                return 0;
            case "February":
                return 1;
            case "March":
                return 2;
            case "April":
                return 3;
            case "May":
                return 4;
            case "June":
                return 5;
            case "July":
                return 6;
            case "August":
                return 7;
            case "September":
                return 8;
            case "October":
                return 9;
            case "November":
                return 10;
            case "December":
                return 11;
            default:
                return 0;
        }
    }
}
