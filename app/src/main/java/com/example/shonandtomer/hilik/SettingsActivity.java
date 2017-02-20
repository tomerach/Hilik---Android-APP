package com.example.shonandtomer.hilik;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {


    private EditText addressInput;
    private EditText salaryInput;
    private EditText transportInput;
    private EditText extraTimeInput;
    private EditText precentageInput;
    private TextView addressToPresent;
    private Switch extraHoursSwitch;
    private Spinner currencySpinner;
    private Button submitBtn;
    private Button clearBtn;
    private ImageButton searchBtn;
    private ArrayList<String> dialogList;

    private static final String NO_ADDRESS_ENTERED = "No Address Entered Yet.";
    private static final String ADDRESS = "Address";
    private static final String MY_PREFS_NAME = "SettingsFile";
    private Address selectedAddress = null;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initUI(); //initiates all components of the activity

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAddressUsingGeocoderThread();
            }
        });

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAllViews();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                if(!checkIfAllFieldsAreValid())
                    Toast.makeText(SettingsActivity.this, "Please fill all fileds", Toast.LENGTH_SHORT).show();
                else {
                    saveToSharedPreferencesFile();
                    resultIntent.putExtra(ADDRESS, selectedAddress);
                    setResult(SettingsActivity.RESULT_OK, resultIntent);
                    finish();
                }
            }
        });
    }

    private boolean checkIfAllFieldsAreValid() {
        boolean isExtraTimeChecked = extraHoursSwitch.isChecked();

        if(isExtraTimeChecked)
            if(extraTimeInput.getText().toString().isEmpty() || precentageInput.getText().toString().isEmpty())
                return false;

        if(selectedAddress == null || salaryInput.getText().toString().isEmpty() ||
            transportInput.getText().toString().isEmpty() || salaryInput.getText().toString().isEmpty()) {
            return false;
        }

        return true;
    }

    private void initUI() {
        addressInput = (EditText) findViewById(R.id.addressInput);
        salaryInput = (EditText) findViewById(R.id.salaryInput);
        transportInput = (EditText) findViewById(R.id.transportInput);
        extraTimeInput = (EditText) findViewById(R.id.extraTimeInput);
        precentageInput = (EditText) findViewById(R.id.precentageInput);
        addressToPresent = (TextView) findViewById(R.id.addressToPresent);
        extraHoursSwitch = (Switch) findViewById(R.id.extraHoursSwitch);
        currencySpinner = (Spinner) findViewById(R.id.currencySpinner);
        submitBtn = (Button) findViewById(R.id.submitSettings);
        clearBtn = (Button) findViewById(R.id.clearBtn);
        searchBtn = (ImageButton) findViewById(R.id.searchBtn);
        dialogList = new ArrayList<>();
        gson = new Gson();

        extraTimeInput.setEnabled(false);
        precentageInput.setEnabled(false);

        extraHoursSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(!isChecked) {
                    extraTimeInput.setEnabled(false);
                    precentageInput.setEnabled(false);
                }
                else
                {
                    extraTimeInput.setEnabled(true);
                    precentageInput.setEnabled(true);
                }
            }
        });

        retrieveSharedPreferences();
    }

    private void retrieveSharedPreferences() {
        final SharedPreferences settings = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        String stringifiedAddress = settings.getString("selectedAddress", null);

        if(stringifiedAddress != null) {

            new AsyncTask<String, Void, Void>() {
                @Override
                protected Void doInBackground(String... stringifiedAddress) {
                    selectedAddress = gson.fromJson(stringifiedAddress[0],Address.class);
                    return null;
                }
            }.execute(stringifiedAddress);

            addressToPresent.setText(settings.getString("addressToPresent", null));
            salaryInput.setText(settings.getString("salaryInput", null));
            transportInput.setText(settings.getString("transportInput", null));
            currencySpinner.setSelection(settings.getInt("currencySpinnerPos", -1));

            boolean isExtraChecked = settings.getBoolean("extraHoursSwitch", false);
            extraHoursSwitch.setChecked(isExtraChecked);

            if(isExtraChecked) {
                extraTimeInput.setText(settings.getString("extraTimeInput", null));
                precentageInput.setText(settings.getString("precentageInput", null));
            }
        }
    }

    private void saveToSharedPreferencesFile() {
        final ProgressDialog progress = ProgressDialog.show(this, "","Saving...", true);

        new AsyncTask<String, Void, String>()
        {
            @Override
            protected String doInBackground(String... strings) {
                return gson.toJson(selectedAddress);
            }

            @Override
            protected void onPostExecute(String stringifiedAddress) {
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

                editor.putString("selectedAddress", stringifiedAddress);
                editor.putString("addressToPresent", addressToPresent.getText().toString());
                editor.putString("salaryInput", salaryInput.getText().toString());
                editor.putString("transportInput", transportInput.getText().toString());
                editor.putString("currencySpinnerVal", currencySpinner.getSelectedItem().toString());
                editor.putInt("currencySpinnerPos", currencySpinner.getSelectedItemPosition());
                editor.putBoolean("extraHoursSwitch", extraHoursSwitch.isChecked());

                if(extraHoursSwitch.isChecked()) {
                    editor.putString("extraTimeInput", extraTimeInput.getText().toString());
                    editor.putString("precentageInput", precentageInput.getText().toString());
                }
                editor.commit();
                progress.dismiss();
            }
        }.execute();
    }

    private void clearAllViews()
    {
        addressInput.setText("");
        addressInput.requestFocus();
        salaryInput.setText("");
        transportInput.setText("");
        extraTimeInput.setText("");
        precentageInput.setText("");
        addressToPresent.setText(NO_ADDRESS_ENTERED);
        extraHoursSwitch.setChecked(false);
        selectedAddress = null;
    }

    private void setAddressUsingGeocoderThread() {
        String addressSearch = addressInput.getText().toString();
        final ProgressDialog progress = ProgressDialog.show(this, "","Loading...", true);
        new AsyncTask<String, Void, List<Address>>() {
            @Override
            protected List<Address> doInBackground(String... addressSearch) {
                Geocoder geoCoder = new Geocoder(SettingsActivity.this);
                List<Address> addressList;
                try {
                    addressList = geoCoder.getFromLocationName(addressSearch[0], 5); //get address by searchable name
                    if (addressList.isEmpty())
                        return null;
                    return addressList;
                } catch (IOException ex) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<Address> addressList) {
                progress.dismiss();
                if (addressList == null)
                    Toast.makeText(SettingsActivity.this, "No results found", Toast.LENGTH_SHORT).show();

                else if (addressList.size() == 1)  //Just one result from Geocoder
                {
                    addressToPresent.setText(getAddressInOneLine(selectedAddress = addressList.get(0)));
                    dialogList.clear();
                }
                else //More than 1 address results
                    invokeDialog(addressList);
            }
        }.execute(addressSearch); //start the Asynctask and pass search address
    }

    /*
        Returns a flattened address name
    */
    private String getAddressInOneLine(Address address) {

        String addressLine = "";
        int maxAddressLineIndex = address.getMaxAddressLineIndex();

        for (int i = 0; i < maxAddressLineIndex; i++)
            addressLine += address.getAddressLine(i) + ". ";
        return addressLine;
    }

    /*
         Invokes a dialog which will show up to 5 addresses results from Geocoder function.
         Choosing an address will pring it to the textViews
    */
    private void invokeDialog(final List<Address> addressList) {
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.dialog, null);
        ListView listView = (ListView) convertView.findViewById(R.id.listView);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
        alertDialogBuilder.setView(convertView);
        alertDialogBuilder.setTitle("Select specific address");

        alertDialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                dialogList.clear();
            }
        });

        //add all results as flattened addresses to a list
        for (Address addressInstance : addressList)
            dialogList.add(getAddressInOneLine(addressInstance));

        //Create and set the an adapter to the listView of the dialog
        ArrayAdapter<String> arrayAdapter = new DialogAdapter(SettingsActivity.this);
        listView.setAdapter(arrayAdapter);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        //When an address name clicked
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                addressToPresent.setText(getAddressInOneLine(selectedAddress = addressList.get(i)));
                dialogList.clear();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }


    /*
  private Adapter class which inflates instances of addresses (to textView)
   */
    private class DialogAdapter extends ArrayAdapter<String> {

        private DialogAdapter(Context context) {
            super(context, R.layout.item_as_text,R.id.textItem, dialogList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View inflater = getLayoutInflater().inflate(R.layout.item_as_text, null);
            return super.getView(position, inflater, parent);
        }
    }


}

