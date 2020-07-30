package com.example.coderescue;

import java.util.Comparator;

public class VictimLocationCardModel {

    private String title, description;
    private String rescueUsername;
    private  String latitude, longitude;
    private double distance;
    private String disaster_id;
    private int img;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRescueUsername() {return rescueUsername; }

    public void setRescueUsername(String rescueUsername) { this.rescueUsername = rescueUsername; }

    public String getLatitude(){ return latitude; }

    public void setLatitude(String latitude) { this.latitude = latitude; }

    public String getLongitude(){ return longitude; }

    public void setLongitude(String longitude) { this.longitude = longitude; }
//
    public double getDistance() { return distance;}

    public void setDistance(double distance) {this.distance = distance; }

    public String getDisaster_id(){ return disaster_id; }

    public void setDisaster_id(String disaster_id){ this.disaster_id = disaster_id; }

    public static Comparator<VictimLocationCardModel> DistSort = new Comparator<VictimLocationCardModel>() {

        public int compare(VictimLocationCardModel s1, VictimLocationCardModel s2) {

            double dist1 = s1.getDistance();
            double dist2 = s2.getDistance();

            /*For ascending order*/
            return (int)(dist1-dist2);
        }};


}
