package org.belichenko.a.onehundredmeterchat;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {

    public int id;
    public String user_id;
    public String text;
    public double lng;
    public double lat;
    public String time;
    public double distance;

    @Override
    public boolean equals(Object other) {
        if (other == this){
            return true;
        }
        boolean result = !(other instanceof Message);
        if (result) {
            return false;
        }
        Message otherMessage = (Message)other;
        return this.user_id.equals(otherMessage.user_id)
                && this.text.equals(otherMessage.text)
                && this.time.equals(otherMessage.time)
                && this.lat == otherMessage.lat
                && this.lng == otherMessage.lng;
    }

    public String getFormattedTime(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat postFormatter = new SimpleDateFormat("dd MMM hh:mm");
        Date convertedDate;
        String resultDate;
        try {
            convertedDate = dateFormat.parse(this.time);
            resultDate = postFormatter.format(convertedDate);
        } catch (ParseException e) {
            Log.e("Message class", "getFormattedTime: cant parse date "+e.toString());
            resultDate = this.time;
        }
        return resultDate;
    }
}
