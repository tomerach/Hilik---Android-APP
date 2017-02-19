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

public class LocationService extends Service {

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private boolean inRange = false;

    private static final long LOCATION_REFRESH_TIME = 1000;
    private static final float LOCATION_REFRESH_DISTANCE = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        mLocationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        final long maxDistance = intent.getLongExtra("maxDistance", 0);
        final Address chosenAddress = intent.getParcelableExtra("chosen address");
        final Location chosenLocation = setLatLong(chosenAddress.getLongitude(), chosenAddress.getLatitude());

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location)
            {
                float distance = location.distanceTo(chosenLocation);

                //Entering workplace radius
                if (distance < maxDistance && !inRange) {
                    inRange = true;
                    makeNotification(Float.toString(distance), chosenAddress);
                }
                //Leaving workplace radius
                else if (distance > maxDistance && inRange) { //out of range
                    inRange = false;
                    makeNotification(Float.toString(distance), chosenAddress);
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
    private void makeNotification(String distance, Address chosenAddress) {

        NotificationCompat.Builder builder = NotificationCompatBuilder();

//        //Create String formats that will pop Google Maps with a maker of the target
//        String label = "Target";
//        String uriBegin = "geo:" + chosenAddress.getLatitude() + "," + chosenAddress.getLongitude();
//        String query = chosenAddress.getLatitude() + "," + chosenAddress.getLongitude() + "(" + label + ")";
//        String encodedQuery = Uri.encode(query);
//        String uri = uriBegin + "?q=" + encodedQuery + "?z=10";
//
//        Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //configure a sound to notification
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);

      //  builder.setContentIntent(contentIntent);
        builder.setAutoCancel(true);
        builder.setLights(Color.BLUE, 500, 500);
        long[] pattern = {500, 500, 500, 500, 500, 500, 500, 500, 500};
        builder.setVibrate(pattern);
        builder.setStyle(new NotificationCompat.InboxStyle()); // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());
    }

    private NotificationCompat.Builder NotificationCompatBuilder()
    {
        TimePicker timePicker = new TimePicker(this);
        String hour = Integer.toString(timePicker.getHour());
        String minute = Integer.toString(timePicker.getMinute());

        String startEnd = inRange ? "start" : "end";

        NotificationCompat.Builder builder =
            new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Entering your workplace's radius")
                .setContentText("Want to " + startEnd + " your shift at " + hour + ":" + minute + "?");

        return builder;
    }
}
