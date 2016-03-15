package org.belichenko.a.onehundredmeterchat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class MsgService extends Service implements Constant{

    public MyBinder binder = new MyBinder();
    Location currentLocation;
    private LocationManager locationManager;
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            currentLocation = location;
            showNotification(currentLocation);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                //gps.setText("Status: "+String.valueOf(status))
            } else if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                //net.setText("Status: "+String.valueOf(status))
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            checkEnable();
            showNotification(locationManager.getLastKnownLocation(provider));
        }

        @Override
        public void onProviderDisabled(String provider) {
            checkEnable();
        }
    };

    public MsgService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    class MyBinder extends Binder {
        MsgService getService() {
            return MsgService.this;
        }
    }

    private void showNotification(Location location) {
        if (location == null) {
            return;
        }
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                NOTIFY_ID, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentTitle("Location changet")
                .setContentText(location.toString());

        Notification n = builder.build();
        nm.notify(NOTIFY_ID, n);
    }


    private void checkEnable() {
        //gps.setText("Enable: "+locationManager.isProviderEnabled(locationManager.GPS_PROVIDER));
        //net.setText("Enable: "+locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER));
    }
}
