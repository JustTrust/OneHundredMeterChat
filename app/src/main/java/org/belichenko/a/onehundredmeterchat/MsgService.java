package org.belichenko.a.onehundredmeterchat;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MsgService extends Service implements Constant {

    private static final String TAG = "Service";
    public MyBinder binder = new MyBinder();
    public Location currentLocation;
    public List<Message> messageList = new ArrayList<>();
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


            if (ActivityCompat.checkSelfPermission(App.getAppContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(App.getAppContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            showNotification(locationManager.getLastKnownLocation(provider));
        }

        @Override
        public void onProviderDisabled(String provider) {

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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TEN_SECONDS, TEN_METERS, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, TEN_SECONDS, TEN_METERS, locationListener);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(locationListener);
    }

    private void showNotification(Location location) {
        if (location == null) {
            return;
        }
        updateMessage();
        Intent notificationIntent = new Intent(App.getAppContext(), MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(App.getAppContext(),
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


    protected void updateMessage() {

        if (currentLocation == null) {
            return;
        }

        SharedPreferences sharedPref = getSharedPreferences(STORAGE_OF_SETTINGS, Context.MODE_PRIVATE);
        String radius = sharedPref.getString(RADIUS, "100");
        String limit = sharedPref.getString(MSG_LIMIT, "20");
        LinkedHashMap<String, String> filter = new LinkedHashMap<>();
        filter.put("lat", String.valueOf(currentLocation.getLatitude()));
        filter.put("lng", String.valueOf(currentLocation.getLongitude()));
        filter.put("radius", radius);
        filter.put("limit", limit);

        Retrofit.getMessage(filter, new Callback<List<Message>>() {
            @Override
            public void success(List<Message> messages, Response response) {
                // Broadcast the list of detected activities.
                if (messages == null) {
                    Log.d(TAG, "success() called with: "
                            + "messages = [" + messages + "], response = [" + response + "]");
                    return;
                }
                Intent localIntent = new Intent(BROADCAST_ACTION);
                localIntent.putExtra(ACTIVITY_EXTRA, "getList");
                LocalBroadcastManager.getInstance(App.getAppContext()).sendBroadcast(localIntent);
                messageList.clear();
                messageList.addAll(messages);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getApplicationContext(), "something went wrong :(", Toast.LENGTH_SHORT).show();
            }
        });
    }

    class MyBinder extends Binder {
        MsgService getService() {
            return MsgService.this;
        }
    }
}
