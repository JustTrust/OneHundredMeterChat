package org.belichenko.a.onehundredmeterchat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 *
 */
public class MapFragment extends Fragment implements Constant, OnMapReadyCallback {
    private static MapFragment ourInstance = new MapFragment();

    public MapFragment() {
    }

    public static MapFragment getInstance() {
        return ourInstance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //  Marker marker = googleMap.addMarker(new MarkerOptions().position().title())

    }
}
