package org.belichenko.a.onehundredmeterchat;

import com.google.android.gms.maps.model.LatLng;

public class Message {

    public String id;
    public String userName;
    public String content;
    public LatLng latLng;
    public long timeStamp;

    public String getLatLng() {
        return String.format("%.4f, %.4f",latLng.latitude, latLng.longitude);
    }
}
