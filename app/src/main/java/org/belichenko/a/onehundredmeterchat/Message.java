package org.belichenko.a.onehundredmeterchat;

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
        if (!(other instanceof Message)) {
            return false;
        }
        Message otherMessage = (Message)other;
        return this.user_id.equals(otherMessage.user_id)
                && this.text.equals(otherMessage.text)
                && this.time.equals(otherMessage.time)
                && this.lat == otherMessage.lat
                && this.lng == otherMessage.lng;
    }
}
