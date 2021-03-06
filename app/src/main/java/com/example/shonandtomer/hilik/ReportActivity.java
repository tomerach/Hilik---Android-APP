package com.example.shonandtomer.hilik;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ReportActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ListView listViewReport;
    private Spinner dropdown;
    private DatabaseHelper db;
    private ArrayList<ReportItem> reportList;
    private ListViewAdapter reportListAdapter;
    private FloatingActionButton fab;

    private Dialog editReportDialog;
    private static TextView entryDateTxt;
    private static TextView entryTimeTxt;
    private static TextView exitDateTxt;
    private static TextView exitTimeTxt;
    private mehdi.sakout.fancybuttons.FancyButton saveBtn;
    private mehdi.sakout.fancybuttons.FancyButton deleteBtn;

    private Dialog addReportDialog;
    private static TextView newEntryDateTxt;
    private static TextView newEntryTimeTxt;
    private static TextView newExitDateTxt;
    private static TextView newExitTimeTxt;
    private mehdi.sakout.fancybuttons.FancyButton newSaveBtn;

    ReportActivity act;

    private ReportItem reportToEdit;


    private Date entry;
    private Date exit;
    // 0 = entry
    // 1 = exit
    private static int date;

    // 0 = entry
    // 1 = exit
    private static int time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.act = this;
        setContentView(R.layout.activity_report);
        db = new DatabaseHelper(this);

        listViewReport = (ListView) findViewById(R.id.listViewReport);
        dropdown = (Spinner) findViewById(R.id.monthSppiner);

        setDropDown();

        addReportDialog = new Dialog(ReportActivity.this);
        addReportDialog.setContentView(R.layout.add_shift_dialog);

        newEntryDateTxt = (TextView)addReportDialog.findViewById(R.id.entryDateBtn);
        newEntryTimeTxt = (TextView)addReportDialog.findViewById(R.id.entryTimeBtn);
        newExitDateTxt = (TextView)addReportDialog.findViewById(R.id.exitDateBtn);
        newExitTimeTxt = (TextView)addReportDialog.findViewById(R.id.exitTimeBtn);
        newSaveBtn = (mehdi.sakout.fancybuttons.FancyButton) addReportDialog.findViewById(R.id.addBtn);


        fab = (FloatingActionButton)  findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                newEntryDateTxt.setText("");
                newEntryTimeTxt.setText("");
                newExitDateTxt.setText("");
                newExitTimeTxt.setText("");

                newSaveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //date formatter
                        SimpleDateFormat dateFormatter = new SimpleDateFormat(
                                "dd-MM-yyyy" , Locale.ENGLISH);

                        //time formatter
                        SimpleDateFormat timeFormatter = new SimpleDateFormat(
                                "HH:mm:ss" , Locale.ENGLISH);

                        try {
                            Date entryDate = dateFormatter.parse((String) newEntryDateTxt.getText());
                            Date exitDate = dateFormatter.parse((String) newExitDateTxt.getText());
                            Date entryTime = timeFormatter.parse((String) newEntryTimeTxt.getText());
                            Date exitTime = timeFormatter.parse((String) newExitTimeTxt.getText());

                            entryDate.setHours(entryTime.getHours());
                            entryDate.setMinutes(entryTime.getMinutes());
                            entryDate.setSeconds(entryTime.getSeconds());

                            exitDate.setHours(exitTime.getHours());
                            exitDate.setMinutes(exitTime.getMinutes());
                            exitDate.setSeconds(exitTime.getSeconds());

                            if(exitDate.before(entryDate))
                                Toast.makeText(ReportActivity.this, "Please insert valid dates"
                                        , Toast.LENGTH_SHORT).show();
                            else{
                                ReportItem rp = new ReportItem(entryDate, exitDate);
                                if(rp.getTotalHours()[3] > 0) {
                                    Toast.makeText(ReportActivity.this, "Please insert valid dates"
                                            , Toast.LENGTH_SHORT).show();
                                }else{
                                    db.createReport(rp);
                                    addReportDialog.dismiss();
                                    setDropDown();
                                }
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }




                    }
                });


                newEntryDateTxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        date = 0;
                        showDatePickerDialog(v);
                    }
                });

                newEntryTimeTxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        time = 0;
                        showTimePickerDialog(v);
                    }
                });

                newExitDateTxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        date = 1;
                        showDatePickerDialog(v);
                    }
                });

                newExitTimeTxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        time = 1;
                        showTimePickerDialog(v);
                    }
                });

                addReportDialog.show();

            }
        });

        reportList = new ArrayList<>();


        //Dialogs:
        this.editReportDialog = new Dialog(this);
        this.editReportDialog.setContentView(R.layout.edit_dialog);

        this.entryDateTxt = (TextView)editReportDialog.findViewById(R.id.entryDateBtn);
        this.entryTimeTxt = (TextView)editReportDialog.findViewById(R.id.entryTimeBtn);
        this.exitDateTxt = (TextView)editReportDialog.findViewById(R.id.exitDateBtn);
        this.exitTimeTxt = (TextView)editReportDialog.findViewById(R.id.exitTimeBtn);
        this.saveBtn = (mehdi.sakout.fancybuttons.FancyButton) editReportDialog.findViewById(R.id.saveBtn);
        this.deleteBtn = (mehdi.sakout.fancybuttons.FancyButton) editReportDialog.findViewById(R.id.deleteBtn);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //date formatter
                SimpleDateFormat dateFormatter = new SimpleDateFormat(
                        "dd-MM-yyyy" , Locale.ENGLISH);

                //time formatter
                SimpleDateFormat timeFormatter = new SimpleDateFormat(
                        "HH:mm:ss" , Locale.ENGLISH);

                try {
                    Date entryDate = dateFormatter.parse((String) entryDateTxt.getText());
                    Date exitDate = dateFormatter.parse((String) exitDateTxt.getText());
                    Date entryTime = timeFormatter.parse((String) entryTimeTxt.getText());
                    Date exitTime = timeFormatter.parse((String) exitTimeTxt.getText());

                    entryDate.setHours(entryTime.getHours());
                    entryDate.setMinutes(entryTime.getMinutes());
                    entryDate.setSeconds(entryTime.getSeconds());

                    exitDate.setHours(exitTime.getHours());
                    exitDate.setMinutes(exitTime.getMinutes());
                    exitDate.setSeconds(exitTime.getSeconds());

                    if(exitDate.before(entryDate))
                        Toast.makeText(ReportActivity.this, "Please insert valid dates"
                                , Toast.LENGTH_SHORT).show();
                    else{
                        ReportItem rp = new ReportItem(entryDate, exitDate);
                        if(rp.getTotalHours()[3] > 0) {
                            Toast.makeText(ReportActivity.this, "Please insert valid dates"
                                    , Toast.LENGTH_SHORT).show();
                        }else {
                            reportToEdit.setEntry(entryDate);
                            reportToEdit.setExit(exitDate);
                            db.updateReport(reportToEdit);
                            editReportDialog.dismiss();
                            setDropDown();
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }




            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.deleteReport(reportToEdit.getId());
                editReportDialog.dismiss();
                setDropDown();
            }
        });

        this.entryDateTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date = 0;
                showDatePickerDialog(v);
            }
        });

        this.entryTimeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time = 0;
                showTimePickerDialog(v);
            }
        });

        this.exitDateTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date = 1;
                showDatePickerDialog(v);
            }
        });

        this.exitTimeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time = 1;
                showTimePickerDialog(v);
            }
        });



        listViewReport.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id)
            {
                reportToEdit = reportList.get(position);
                entry = (Date) reportToEdit.getEntry().clone();
                exit = (Date) reportToEdit.getExit().clone();

                //date formatter
                SimpleDateFormat dateFormatter = new SimpleDateFormat(
                        "dd-MM-yyyy" , Locale.ENGLISH);

                entryDateTxt.setText(dateFormatter.format(reportToEdit.getEntry()));
                exitDateTxt.setText(dateFormatter.format(reportToEdit.getExit()));

                //time formatter
                SimpleDateFormat timeFormatter = new SimpleDateFormat(
                        "HH:mm:ss" , Locale.ENGLISH);

                entryTimeTxt.setText(timeFormatter.format(reportToEdit.getEntry()));
                exitTimeTxt.setText(timeFormatter.format(reportToEdit.getExit()));

                editReportDialog.show();

                //int pos=position+1;
                //Toast.makeText(ReportActivity.this, Integer.toString(pos)+" Clicked", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void setDropDown(){
        ArrayList<String> months = db.getAllAvailableMonths();
        ArrayAdapter<String> monthsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, months);
        if(months.size() == 0)
        {
            reportList = new ArrayList<>();
            reportListAdapter = new ListViewAdapter(this, reportList);
            listViewReport.setAdapter(reportListAdapter);
        }
        dropdown.setAdapter(monthsAdapter);
        dropdown.setOnItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // An item was selected. You can retrieve the selected item using
        String monthAndYearString = (String) parent.getItemAtPosition(position);

        String[] tokens = monthAndYearString.split(" ");
        //if(tokens.length!=2){throw new IllegalArgumentException();}
        String month = tokens[0];
        String year = tokens[1];

        reportList = db.getAllReportByMonth(DatabaseHelper.stringMonthToIntMonth(month) , Integer.parseInt(year) - 1900);
        reportListAdapter = new ListViewAdapter(this, reportList);
        listViewReport.setAdapter(reportListAdapter);
        //Log.d(LOG, "month: " + monthString);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void closeDialog (Dialog myDialog){myDialog.dismiss();}

    //******** Start Time Picker on Fragment Dialog**********//
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        public static String timeString = "00:00";
        public static int hour;
        public static int min;


        //GET WARNINGS BECAUSE HIGHER API NEEDED.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final android.icu.util.Calendar c = android.icu.util.Calendar.getInstance();
            int hour = c.get(android.icu.util.Calendar.HOUR_OF_DAY);
            int minute = c.get(android.icu.util.Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    android.text.format.DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            hour = hourOfDay;
            min = minute;

            if(minute<=9)
                timeString = hourOfDay+":"+"0"+minute;
            else
                timeString = hourOfDay+":"+minute;

            Date d = new Date(117, 2, 3, hourOfDay, minute);
            SimpleDateFormat dateFormatter = new SimpleDateFormat(
                    "HH:mm:ss" , Locale.ENGLISH);
            if(time == 0){
                entryTimeTxt.setText(dateFormatter.format(d));
                newEntryTimeTxt.setText(dateFormatter.format(d));
            }
            else{
                exitTimeTxt.setText(dateFormatter.format(d));
                newExitTimeTxt.setText(dateFormatter.format(d));
            }

        }
    }
    //******** END Time Picker on Fragment Dialog**********//


    //******** Start Date Picker on Fragment Dialog**********//
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        public static String dateString = "2017-01-01";


        //GET WARNINGS BECAUSE HIGHER API NEEDED.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final android.icu.util.Calendar c = android.icu.util.Calendar.getInstance();
            int year = c.get(android.icu.util.Calendar.YEAR);
            int month = c.get(android.icu.util.Calendar.MONTH);
            int day = c.get(android.icu.util.Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            //Toast.makeText(getContext(), day+"/"+month+1+"/"+year, Toast.LENGTH_SHORT).show();
            Date d = new Date(year - 1900,month,day);
            SimpleDateFormat dateFormatter = new SimpleDateFormat(
                    "dd-MM-yyyy" , Locale.ENGLISH);
            if(date == 0){
                entryDateTxt.setText(dateFormatter.format(d));
                newEntryDateTxt.setText(dateFormatter.format(d));
            }

            else{
                exitDateTxt.setText(dateFormatter.format(d));
                newExitDateTxt.setText(dateFormatter.format(d));
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
//******** END Date Picker on Fragment Dialog**********//
}

