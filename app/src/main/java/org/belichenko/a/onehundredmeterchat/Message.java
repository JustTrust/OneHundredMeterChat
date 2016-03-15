package org.belichenko.a.onehundredmeterchat;

import com.google.android.gms.maps.model.LatLng;

public class Message {

    public int id;
    public String user_id;
    public String text;
    public LatLng latLng;
    public double lng;
    public double lat;
    public String time;
    public double distance;


    public String getLatLng() {
        return String.format("%.4f, %.4f",lat, lng);
    }
}
