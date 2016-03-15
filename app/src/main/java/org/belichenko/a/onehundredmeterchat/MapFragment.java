package org.belichenko.a.onehundredmeterchat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 *
 */
public class MapFragment extends Fragment implements Constant, OnMapReadyCallback, View.OnClickListener {

    protected static String name = "Map";
    private static MapFragment ourInstance = new MapFragment();
    private GoogleMap map;
    private SupportMapFragment supportMapFragment;
    private ImageView imageMap;
    private ImageView imageSputnik;
    private ImageView imageEarth;
    private ImageView imageSearch;

    public MapFragment() {
    }

    public static MapFragment getInstance() {
        return ourInstance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        imageMap = (ImageView) view.findViewById(R.id.map_map);
        imageSputnik = (ImageView) view.findViewById(R.id.map_sputnic);
        imageEarth = (ImageView) view.findViewById(R.id.map_earth);
        imageSearch = (ImageView) view.findViewById(R.id.map_send_btn);

        imageMap.setOnClickListener(this);
        imageSputnik.setOnClickListener(this);
        imageEarth.setOnClickListener(this);
        imageSearch.setOnClickListener(this);

        if (supportMapFragment == null) {
            supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.gmap);
        }
        if (supportMapFragment != null) {
            supportMapFragment.getMapAsync(this);
        }
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
        LatLng lastPosition = new LatLng(0, 0);
        for (Message message : ListFragment.getInstance().messagesList) {
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(message.latLng)
                    .title(message.user_id)
                    .snippet(message.text));
            lastPosition = message.latLng;
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(lastPosition, 14));
    }


    //Обработчик нажатия на картинку
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.map_earth:
                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.map_map:
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.map_sputnic:
                map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case R.id.map_send_btn:
                //Кнопка писка должна что-то делать
                break;
        }
    }
}
