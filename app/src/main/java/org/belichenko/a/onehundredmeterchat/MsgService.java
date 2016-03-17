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
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MsgService extends Service implements Constant
        , GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener
        , com.google.android.gms.location.LocationListener {

    private static final String TAG = "Service";
    public MyBinder binder = new MyBinder();
    public Location currentLocation;
    public List<Message> messageList = new ArrayList<>();
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    Handler handler;

    public MsgService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        readData();
        buildGoogleApiClient();
        createLocationRequest();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        addHandler();
        updateMessage();
    }

    private void addHandler() {
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(android.os.Message msg) {
                updateMessage();
                handler.sendEmptyMessageDelayed(1, TWENTY_SECONDS);
                return true;
            }
        });
        handler.sendEmptyMessageDelayed(1, TWENTY_SECONDS);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeMessages(1);
        stopLocationUpdates();
        saveData();
    }

    private void readData() {
        SharedPreferences sharedPref = getSharedPreferences(STORAGE_OF_SETTINGS, Context.MODE_PRIVATE);
        Gson gson = new Gson();

        if (sharedPref.contains(CURRENT_LOCATION)) {
            String jsonLoc = sharedPref.getString(CURRENT_LOCATION, null);
            currentLocation = gson.fromJson(jsonLoc, Location.class);
        }
        if (sharedPref.contains(MESSAGE_LIST)) {
            String jsonMsg = sharedPref.getString(MESSAGE_LIST, null);
            Message[] tempList = gson.fromJson(jsonMsg, Message[].class);
            messageList = Arrays.asList(tempList);
        }
    }

    protected void stopLocationUpdates() {

        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
            Log.d(TAG, "stopLocationUpdates()");
        } else {
            Log.d(TAG, "stopLocationUpdates() mGoogleApiClient not connected");
        }

        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    private void buildGoogleApiClient() {
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    protected void createLocationRequest() {
        SharedPreferences sharedPref = getSharedPreferences(STORAGE_OF_SETTINGS, Context.MODE_PRIVATE);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(TWENTY_SECONDS);
        mLocationRequest.setFastestInterval(TEN_SECONDS);
        mLocationRequest.setPriority(sharedPref.getInt(ACCURACY, LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY));
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this
                , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this
                , Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d(TAG, "onConnected() called with: " + "not permissions");
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        currentLocation = mLastLocation;
        showNotification();

        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended() called with: " + "i = [" + i + "]");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed() called with: " + "connectionResult = [" + connectionResult + "]");
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        showNotification();
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this
                , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this
                , Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d(TAG, "startLocationUpdates() called with: " + "not permissions");
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                Log.d(TAG, "startLocationUpdates() called with: " + "status = [" + status + "]");
            }
        });

    }

    private void saveData() {
        if (currentLocation != null) {
            SharedPreferences mPrefs = App.getAppContext()
                    .getSharedPreferences(STORAGE_OF_SETTINGS, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = mPrefs.edit();
            Gson gson = new Gson();

            edit.putString(CURRENT_LOCATION, gson.toJson(currentLocation));
            edit.putString(MESSAGE_LIST, gson.toJson(messageList));
            edit.apply();
        }
    }

    private void showNotification() {
        if (currentLocation == null) {
            return;
        }
        Intent notificationIntent = new Intent(App.getAppContext(), MainActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(App.getAppContext(),
                NOTIFY_ID, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentTitle("Location changed")
                .setContentText(currentLocation.toString());

        Notification n = builder.build();
        nm.notify(NOTIFY_ID, n);
    }


    private void updateMessage() {

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
                            + "messages = [" + null + "], response = [" + response + "]");
                    return;
                }
                messageList.clear();
                messageList.addAll(messages);
                Intent localIntent = new Intent(BROADCAST_ACTION);
                localIntent.putExtra(ACTIVITY_EXTRA, "getList");
                LocalBroadcastManager.getInstance(App.getAppContext()).sendBroadcast(localIntent);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getApplicationContext(), "Check your internet connection", Toast.LENGTH_LONG).show();
            }
        });
    }

    class MyBinder extends Binder {
        MsgService getService() {
            return MsgService.this;
        }
    }
}
