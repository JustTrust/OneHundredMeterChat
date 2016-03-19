package org.belichenko.a.onehundredmeterchat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.LinkedList;

/**
 *
 */
public class MapFragment extends Fragment implements Constant, OnMapReadyCallback, View.OnClickListener {

    private static final String TAG = "Map fragment";
    protected static String name = "Map";
    private static MapFragment ourInstance = new MapFragment();
    private GoogleMap map;
    private SupportMapFragment supportMapFragment;
    private ImageView imageMap;
    private ImageView imageSputnik;
    private ImageView imageEarth;
    private ImageView imageSearch;
    private EditText messegeText;

    public MapFragment() {
    }

    public static MapFragment getInstance() {
        return ourInstance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.gmap);
        supportMapFragment.getMapAsync(this);

        imageMap = (ImageView) view.findViewById(R.id.map_map);
        imageSputnik = (ImageView) view.findViewById(R.id.map_sputnic);
        imageEarth = (ImageView) view.findViewById(R.id.map_earth);
        imageSearch = (ImageView) view.findViewById(R.id.map_send_btn);
        messegeText = (EditText) view.findViewById(R.id.map_msg_text);

        imageMap.setOnClickListener(this);
        imageSputnik.setOnClickListener(this);
        imageEarth.setOnClickListener(this);
        imageSearch.setOnClickListener(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }

    public void updateMessages(LinkedList<Message> msgList) {
        if (map == null) {
            return;
        }
        if (msgList == null) {
            Log.d(TAG, "updateMessages() called with: " + "msgList = [" + null + "]");
            return;
        }
        map.clear();
        for (Message message : msgList) {
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(message.lat, message.lng))
                    .title(message.user_id)
                    .snippet(message.text));
        }
        // show last msg on the map
        if (msgList.size() > 0) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(msgList.getFirst().lat, msgList.getFirst().lng), 14));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateMessages(((MainActivity) getActivity()).getMsgList());
    }

    //Обработчик нажатия на кнопки
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
                ((MainActivity) getActivity()).sendNewMessage(messegeText.getText().toString());
                messegeText.setText("");
                break;
        }
    }

    public void showMsgOnMap(Message message) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(message.lat, message.lng), 18));
    }
}