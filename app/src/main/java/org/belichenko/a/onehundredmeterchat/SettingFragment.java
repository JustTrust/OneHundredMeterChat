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
        return view;
    }

    private void updateSettings() {
        SharedPreferences sharedPref = App.getAppContext()
                .getSharedPreferences(STORAGE_OF_SETTINGS, Context.MODE_PRIVATE);
        mLimit.setText(sharedPref.getString(MSG_LIMIT, "20"));
        mRadius.setText(sharedPref.getString(RADIUS, "100"));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.logout)
    public void logout(View view) {
        SharedPreferences.Editor edit = App.getAppContext()
                .getSharedPreferences(STORAGE_OF_SETTINGS, Context.MODE_PRIVATE)
                        .edit();
        edit.putString(STORED_NAME, "");
        edit.apply();
        Intent activityIntent = new Intent(App.getAppContext(), LoginActivity.class);
        startActivity(activityIntent);
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