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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 *
 */
public class MapFragment extends Fragment implements Constant, OnMapReadyCallback {
    protected static String name = "Map";
    private static MapFragment ourInstance = new MapFragment();
    String massage;

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


    }

    public void updateMessege(GoogleMap map) {
        for (int i = 0; i < ListFragment.getInstance().messagesList.size(); i++) {
            massage = String.valueOf(ListFragment.getInstance().messagesList.get(i));
            Marker marker = map.addMarker(new MarkerOptions().position(ListFragment.getInstance().KHARKOV).title(massage));
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(ListFragment.getInstance().KHARKOV, 15)); // Move the camera instantly to Kharkov with a zoom of 15.
        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

    }
}
