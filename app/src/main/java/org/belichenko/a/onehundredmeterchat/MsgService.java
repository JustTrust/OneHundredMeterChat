package org.belichenko.a.onehundredmeterchat;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import java.util.Date;

public class MsgService extends Service {

    MyBinder binder = new MyBinder();

    private LocationManager locationManager;
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
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
            showLocation(locationManager.getLastKnownLocation(provider));
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

    private void showLocation(Location location) {
        if (location == null) {
            return;
        }
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            //Выводим данные с gps gpsText.setText(formatLocation(location));
        } else if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
            //Выводим данные с network networkText.setText(formatLocation(location));
        }
    }

    private String formatLocation(Location location) {
        if (location == null) {
            return "";
        }

        return String.format("Coordinates: lat = %1$.4f, lon = %2$.4f, time = %3$tF %3$tT", +
                location.getLatitude(), location.getLongitude(), new Date(location.getTime()));

    }

    private void checkEnable() {
        //gps.setText("Enable: "+locationManager.isProviderEnabled(locationManager.GPS_PROVIDER));
        //net.setText("Enable: "+locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER));
    }
}
