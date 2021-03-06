package org.belichenko.a.onehundredmeterchat;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;

import java.util.LinkedHashMap;
import java.util.LinkedList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends AppCompatActivity implements
        ListFragment.OnListFragmentInteractionListener
        , Constant {

    private static final String TAG = "Main activity";
    protected UpdateMsgBroadcastReceiver mBroadcastReceiver;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private MsgService msgService;
    private ViewPager mViewPager;
    private boolean isServiceRunning = false;
    private ServiceConnection serviceConnection;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // start service
        Intent serviceIntent = new Intent(this, MsgService.class);
        startService(serviceIntent);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        /*
      The {@link android.support.v4.view.PagerAdapter} that will provide
      fragments for each of the sections. We use a
      {@link FragmentPagerAdapter} derivative, which will keep every
      loaded fragment in memory. If this becomes too memory intensive, it
      may be best to switch to a
      {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1);

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                isServiceRunning = true;
                msgService = ((MsgService.MyBinder) service).getService();
                LinkedList<Message> messageList = msgService.messageList;
                MapFragment.getInstance().updateMessages(messageList);
                ListFragment.getInstance().updateMessages(messageList);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                isServiceRunning = false;
            }
        };

        // Get a receiver for broadcasts from ActivityDetectionIntentService.
        mBroadcastReceiver = new UpdateMsgBroadcastReceiver();

        checkLocationServiceEnabled();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isServiceRunning) {
            bindService(new Intent(this, MsgService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        }
        // Register the broadcast receiver that informs this activity of the DetectedActivity
        // object broadcast sent by the intent service.
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter(BROADCAST_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isServiceRunning) {
            unbindService(serviceConnection);
            isServiceRunning = false;
        }
        // Unregister the broadcast receiver that was registered during onResume().
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    public void onListFragmentInteraction(Message message) {
        if (message == null) {
            return;
        }
        mViewPager.setCurrentItem(1);
        MapFragment.getInstance().showMsgOnMap(message);
    }

    public LinkedList<Message> getMsgList() {
        if (msgService != null && isServiceRunning) {
            LinkedList<Message> messageList = msgService.messageList;
            MapFragment.getInstance().updateMessages(messageList);
            ListFragment.getInstance().updateMessages(messageList);
            return messageList;
        }
        return null;
    }

    public void sendNewMessage(String msgText) {

        SharedPreferences sharedPref = getSharedPreferences(STORAGE_OF_SETTINGS, Context.MODE_PRIVATE);
        String storedName = sharedPref.getString(STORED_NAME, "");
        if (msgText.isEmpty() || storedName.isEmpty() || msgService == null) {
            Log.d(TAG, "sendMessage() called with: empty parameters ");
            return;
        }
        Location currentLocation = msgService.currentLocation;
        if (currentLocation == null) {
            Log.d(TAG, "sendMessage() called with: currentLocation == null ");
            Toast.makeText(MainActivity.this, "Message don't sent. Check GPS connection", Toast.LENGTH_LONG).show();
            return;
        }

        LinkedHashMap<String, String> filter = new LinkedHashMap<>();
        filter.put("lat", String.valueOf(currentLocation.getLatitude()));
        filter.put("lng", String.valueOf(currentLocation.getLongitude()));
        filter.put("user_id", storedName);
        filter.put("text", msgText);
        Retrofit.sendMessage(filter, new Callback<Void>() {
            @Override
            public void success(Void aVoid, Response response) {
                Toast.makeText(MainActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(MainActivity.this, "Message don't sent. Check internet connection", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void checkLocationServiceEnabled() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean netWork = false;
        boolean geolocationEnabled = false;
        try {
            geolocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            netWork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "checkLocationServiceEnabled() called with Exception: " + ex.toString());
        }
        SharedPreferences sharedPref = getSharedPreferences(STORAGE_OF_SETTINGS, Context.MODE_PRIVATE);
        int requestedAccuracy = sharedPref.getInt(ACCURACY, LocationRequest.PRIORITY_HIGH_ACCURACY);
        if ((requestedAccuracy == LocationRequest.PRIORITY_HIGH_ACCURACY) & (!geolocationEnabled)) {
            buildAlertMessageNoLocationService(R.string.msg_switch_gps);
        }
        if ((requestedAccuracy == LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY) & (!netWork)) {
            buildAlertMessageNoLocationService(R.string.msg_switch_network);
        }
    }

    private void buildAlertMessageNoLocationService(final int provider) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true)
                .setMessage(getString(provider))
                .setPositiveButton(getString(R.string.msg_turn_on), new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog
                            , @SuppressWarnings("unused") final int id) {
                        if (provider == R.string.msg_switch_gps){
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }else{
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_setting, container, false);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return ListFragment.getInstance();
                case 1:
                    return MapFragment.getInstance();
                case 2:
                    return SettingFragment.getInstance();
            }
            return null;

        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return MapFragment.name;
                case 1:
                    return ListFragment.name;
                case 2:
                    return SettingFragment.name;
            }
            return null;
        }
    }

    /**
     * Receiver for intents sent by DetectedActivitiesIntentService via a sendBroadcast().
     * Receives a list of one or more DetectedActivity objects associated with the current state of
     * the device.
     */
    public class UpdateMsgBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (msgService == null) {
                return;
            }
            Bundle bundle = intent.getExtras();
            String updateType = bundle.getString(ACTIVITY_EXTRA);
            if (updateType != null && updateType.equals("getList")) {
                if (isServiceRunning) {
                    LinkedList<Message> messageList = msgService.messageList;
                    MapFragment.getInstance().updateMessages(messageList);
                    ListFragment.getInstance().updateMessages(messageList);
                }
            }
        }
    }
}
