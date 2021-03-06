package org.belichenko.a.onehundredmeterchat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazon.device.ads.Ad;
import com.amazon.device.ads.AdError;
import com.amazon.device.ads.AdLayout;
import com.amazon.device.ads.AdListener;
import com.amazon.device.ads.AdProperties;
import com.amazon.device.ads.AdRegistration;
import com.amazon.device.ads.AdTargetingOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LoginActivity extends AppCompatActivity implements Constant {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private static final String TAG = "AmazonAd";
    private final Handler mHideHandler = new Handler();
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };
    View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private AdLayout mAdView;
    private TextView mStartBt;
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    private AutoCompleteTextView usersName;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> usersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AdRegistration.enableLogging(false);
        AdRegistration.enableTesting(true);
        AdRegistration.setAppKey("ba43cb42ea6c4650a5be17803d99d5ce");

        mVisible = true;
        mContentView = findViewById(R.id.cover_layout);
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mStartBt = (TextView) findViewById(R.id.start_bt);
        usersName = (AutoCompleteTextView) findViewById(R.id.editName);

        mAdView = (AdLayout) findViewById(R.id.adview);
        AdTargetingOptions adOptions = new AdTargetingOptions();
        mAdView.loadAd();
        mAdView.setListener(new AdListener() {
            @Override
            public void onAdLoaded(Ad ad, AdProperties adProperties) {
                Log.d(TAG, "onAdLoaded() called with: " + "ad = [" + ad + "], adProperties = [" + adProperties + "]");
            }

            @Override
            public void onAdFailedToLoad(Ad ad, AdError adError) {
                Log.d(TAG, "onAdFailedToLoad() called with: "
                        + "code = [" + adError.getCode() + "], msg = [" + adError.getMessage() + "]");
            }

            @Override
            public void onAdExpanded(Ad ad) {
                Log.d(TAG, "onAdExpanded() called with: " + "ad = [" + ad + "]");
            }

            @Override
            public void onAdCollapsed(Ad ad) {
                Log.d(TAG, "onAdCollapsed() called with: " + "ad = [" + ad + "]");
            }

            @Override
            public void onAdDismissed(Ad ad) {
                Log.d(TAG, "onAdDismissed() called with: " + "ad = [" + ad + "]");
            }
        });

        getListOfUsers();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, usersList);
        usersName.setAdapter(adapter);
        usersName.setThreshold(2);

        SharedPreferences sharedPref = getSharedPreferences(STORAGE_OF_SETTINGS, Context.MODE_PRIVATE);
        String storedName = sharedPref.getString(STORED_NAME, "");
        if (!storedName.isEmpty()) {
            // start main activity if we have name
            Intent activityIntent = new Intent(this, MainActivity.class);
            startActivity(activityIntent);
            this.finish();
        }

        mStartBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usersName.getText().toString().length() < 6) {
                    Toast.makeText(App.getAppContext(),
                            getString(R.string.no_valid_name), Toast.LENGTH_LONG).show();
                    return;
                }
                storeNewName(usersName.getText().toString());
                usersName.setText("");
                Intent activityIntent = new Intent(App.getAppContext(), MainActivity.class);
                startActivity(activityIntent);
            }
        });
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getListOfUsers();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdView.destroy();
    }

    private void storeNewName(String name) {
        SharedPreferences.Editor edit =
                getSharedPreferences(STORAGE_OF_SETTINGS, Context.MODE_PRIVATE)
                        .edit();
        if (!usersList.contains(name)){
            usersList.add(name);
            Gson gson = new Gson();
            edit.putString(LIST_OF_USERS, gson.toJson(usersList));
        }
        edit.putString(STORED_NAME, usersName.getText().toString());
        edit.apply();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void getListOfUsers() {
        SharedPreferences sharedPref = getSharedPreferences(STORAGE_OF_SETTINGS, Context.MODE_PRIVATE);
        Gson lgson = new Gson();
        if (sharedPref.contains(LIST_OF_USERS)) {
            String jsonMsg = sharedPref.getString(LIST_OF_USERS, "");
            String[] tempList = lgson.fromJson(jsonMsg, String[].class);
            usersList.clear();
            usersList.addAll(Arrays.asList(tempList));
        }
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
