package com.example.shonandtomer.hilik;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.util.Date;

public class LocationService extends Service {

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private boolean inRange = false;
    private DatabaseHelper db;
    private ReportItem item;

    private static final long LOCATION_REFRESH_TIME = 1000;
    private static final float LOCATION_REFRESH_DISTANCE = 0;
    private static final int RADIUS = 100;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        mLocationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        final Address chosenAddress = intent.getParcelableExtra("chosen address");
        final Location chosenLocation = setLatLong(chosenAddress.getLongitude(), chosenAddress.getLatitude());
        db = new DatabaseHelper(this);

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location)
            {
                float distance = location.distanceTo(chosenLocation);
                Log.i("Service", "location changed, distance: " + Float.toString(distance));

                //Entering workplace radius
                if (distance < RADIUS && !inRange) {
                    inRange = true;
                    makeNotificationAndReturnApproval(Float.toString(distance), chosenAddress);
                    item = new ReportItem();
                }
                //Leaving workplace radius
                else if (distance > RADIUS && inRange) { //out of range
                    inRange = false;
                    item.setExit(new Date());
                    makeNotificationAndReturnApproval(Float.toString(distance), chosenAddress);
                    db.createReport(item);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            @Override
            public void onProviderEnabled(String provider) {}
            @Override
            public void onProviderDisabled(String provider) {}
        };

        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        mLocationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE,
                mLocationListener
        );

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        this.mLocationManager.removeUpdates(this.mLocationListener);
        super.onDestroy();
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
    private boolean makeNotificationAndReturnApproval(String distance, Address chosenAddress) {

        NotificationCompat.Builder builder = NotificationCompatBuilder();

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(contentIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());

        return true; //TODO: return from notification
    }

    private NotificationCompat.Builder NotificationCompatBuilder()
    {
        long[] pattern = {500, 500, 500, 500, 500, 500, 500, 500, 500};
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        DateFormat timeFormat = DateFormat.getTimeInstance();
        String currentTime = timeFormat.format(new Date());

        String startEnd = inRange ? "start" : "end";
        String enterLeave = inRange ? "Entering" : "Leaving";

        NotificationCompat.Builder builder =
            new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.d1)
                .setContentTitle(enterLeave + "your workplace's radius")
                .setContentText("Want to " + startEnd + " your shift at " + currentTime + "?")
                .setSound(alarmSound)         //configure a sound to notification
                .setAutoCancel(true)
                .setLights(Color.BLUE, 500, 500)
                .setStyle(new NotificationCompat.InboxStyle()) // Add as notification
                .setVibrate(pattern);

        return builder;
    }
}
