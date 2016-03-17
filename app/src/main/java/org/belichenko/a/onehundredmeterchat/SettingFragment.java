package org.belichenko.a.onehundredmeterchat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.location.LocationRequest;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 *
 */
public class SettingFragment extends Fragment implements Constant{

    @Bind(R.id.logout)
    EditText mLogout;
    @Bind(R.id.setting_radius)
    EditText mRadius;
    @Bind(R.id.setting_limit)
    EditText mLimit;
    @Bind(R.id.rg_accuracy)
    RadioGroup rgAccuracy;

    private static SettingFragment ourInstance = new SettingFragment();
    protected static String name = "Setting";

    public static SettingFragment getInstance() {
        return ourInstance;
    }

    public SettingFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        ButterKnife.bind(this, view);
        updateSettings();
        setListeners();
        return view;
    }

    private void updateSettings() {

        SharedPreferences sharedPref = App.getAppContext()
                .getSharedPreferences(STORAGE_OF_SETTINGS, Context.MODE_PRIVATE);
        mLimit.setText(sharedPref.getString(MSG_LIMIT, "20"));
        mRadius.setText(sharedPref.getString(RADIUS, "100"));

        String nameAccuracy = sharedPref.getString(STORED_ACCURACY, getString(R.string.text_accuracy1));
        RadioButton rbAccuracy;
        if (nameAccuracy.equals(getString(R.string.text_accuracy1))) {
            rbAccuracy = (RadioButton) rgAccuracy.getChildAt(0);
        } else {
            rbAccuracy = (RadioButton) rgAccuracy.getChildAt(1);
        }
        if (rbAccuracy != null) {
            rbAccuracy.setChecked(true);
        }
    }

    private void setListeners() {

        rgAccuracy.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SharedPreferences mPrefs = App.getAppContext()
                        .getSharedPreferences(STORAGE_OF_SETTINGS, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = mPrefs.edit();
                RadioButton rb = (RadioButton) rgAccuracy.findViewById(checkedId);
                if (rb.getText().toString().equals(getString(R.string.text_accuracy1))) {
                    edit.putInt(ACCURACY, LocationRequest.PRIORITY_HIGH_ACCURACY);
                }else{
                    edit.putInt(ACCURACY, LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                }
                edit.putString(STORED_ACCURACY, rb.getText().toString());
                edit.apply();
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.logout)
    public void logout(View view) {
        // clear shared pref
        SharedPreferences.Editor edit = App.getAppContext()
                .getSharedPreferences(STORAGE_OF_SETTINGS, Context.MODE_PRIVATE)
                        .edit();
        edit.clear();
        edit.apply();
        // stop service
        Intent serviceIntent = new Intent(App.getAppContext(), MsgService.class);
        App.getAppContext().stopService(serviceIntent);
        getActivity().finish();
    }

    @OnTextChanged(value = R.id.setting_limit, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onLimitChange(CharSequence text) {
        SharedPreferences.Editor edit = App.getAppContext()
                .getSharedPreferences(STORAGE_OF_SETTINGS, Context.MODE_PRIVATE)
                .edit();
        edit.putString(MSG_LIMIT, mLimit.getText().toString());
        edit.apply();

    }


    @OnTextChanged(value = R.id.setting_radius, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onRadiusChange(CharSequence text) {
        SharedPreferences.Editor edit = App.getAppContext()
                .getSharedPreferences(STORAGE_OF_SETTINGS, Context.MODE_PRIVATE)
                .edit();
    edit.putString(RADIUS, mRadius.getText().toString());
        edit.apply();
    }
}