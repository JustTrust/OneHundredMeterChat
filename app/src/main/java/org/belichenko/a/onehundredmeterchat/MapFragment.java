package org.belichenko.a.onehundredmeterchat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 *
 */
public class MapFragment extends Fragment implements Constant, OnMapReadyCallback {
    protected static String name = "Map";
    private static MapFragment ourInstance = new MapFragment();
    String massage;
    GoogleMap map;

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
        map = googleMap;
        updateMessage();
    }

    public void updateMessage() {
        if (map == null) {
            return;
        }
        if (ListFragment.getInstance().messagesList == null) {
            return;
        }
        map.clear();
        LatLng lastPosition = new LatLng(0,0);
        for (Message message : ListFragment.getInstance().messagesList) {
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(message.latLng)
                    .title(message.userName)
                    .snippet(message.content));
            lastPosition = message.latLng;
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(lastPosition, 14));
    }
}
