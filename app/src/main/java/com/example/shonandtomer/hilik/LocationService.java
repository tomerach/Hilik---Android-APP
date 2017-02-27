package com.example.shonandtomer.hilik;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;

public class LocationService extends Service {

    private LocationManager mLocationManager;
    private boolean inRange = false;
    private boolean isNotificationPosted = false;
    private DatabaseHelper db;
    private ReportItem item;
    private float lastKnownDistance;
    private Location chosenLocation;

    private static final long LOCATION_REFRESH_TIME = 10000;
    private static final float LOCATION_REFRESH_DISTANCE = 10;
    private static final int RADIUS = 100;
    private static final float INITIAL_DISTANCE = 1000;

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location)
        {
            float distance = location.distanceTo(chosenLocation);
            Log.i("Service", "lase known location, distance: " + Float.toString(lastKnownDistance));
            Log.i("Service", "location changed, distance: " + Float.toString(distance));
            Log.i("Service", "isNotificationPosed: " + isNotificationPosted);
            Log.i("Service", "Time: " + new Date().toString());

            if(!isNotificationPosted)
            {
                if (lastKnownDistance > distance) {

                    //Entering workplace radius
                    if (distance < RADIUS && !inRange) {
                        Log.i("Service", "in range with notification");

                        inRange = true;
                        item = new ReportItem();
                        makeNotification();
                        isNotificationPosted = true;

                    }
                }
            }

            else
            {
                if (lastKnownDistance < distance) {

                    //Leaving workplace radius
                    if (distance > RADIUS && inRange) { //out of range
                        Log.i("Service", "out of range with notification");

                        inRange = false;
                        item.setExit(new Date());
                        db.createReport(item);
                        makeNotification();
                        isNotificationPosted = false;
                    }
                }
            }

            lastKnownDistance = distance;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        @Override
        public void onProviderEnabled(String provider) {}
        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText( getApplicationContext(), "Please turn on your location services", Toast.LENGTH_SHORT ).show();
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId)
    {
        mLocationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        final Address chosenAddress = intent.getParcelableExtra("chosen address");
        chosenLocation = setLatLong(chosenAddress.getLongitude(), chosenAddress.getLatitude());
        lastKnownDistance = INITIAL_DISTANCE;
        Log.i("Service", "lase known location, distance: " + Float.toString(lastKnownDistance));

        db = new DatabaseHelper(this);

        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        mLocationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE,
                mLocationListener
        );

        //TODO: check sticky service
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        this.mLocationManager.removeUpdates(this.mLocationListener);

    }

    /*
    Returns a location og an address using latitude and longitude
    */
    public static Location setLatLong(double longitude, double latitude) {
        Location loc = new Location("Chosen Address");
        loc.setLatitude(latitude);
        loc.setLongitude(longitude);

        return loc;
    }

    /*
    Makes a notification which shows the distance from target.
    pressing the notification will trigger google maps.
     */
    private void makeNotification()
    {
        NotificationCompat.Builder builder = NotificationCompatBuilder();
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());
    }

    private NotificationCompat.Builder NotificationCompatBuilder()
    {
        long[] pattern = {500, 500, 500, 500, 500, 500, 500, 500, 500};
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        DateFormat timeFormat = DateFormat.getTimeInstance();
        String currentTime = timeFormat.format(new Date());

        String startEnd = inRange ? "Starting" : "Ending";
        String enterLeave = inRange ? "Entering" : "Leaving";

        NotificationCompat.Builder builder =
            new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.location_shekel)
                    .setColor(Color.rgb(163,0,0))
                .setContentTitle(enterLeave + " your workplace's radius")
                .setContentText(startEnd + " your shift at " + currentTime)
                .setSound(alarmSound)         //configure a sound to notification
                .setAutoCancel(true)
                .setLights(Color.BLUE, 500, 500)
                .setStyle(new NotificationCompat.InboxStyle()) // Add as notification
                .setVibrate(pattern);

        return builder;
    }
}
