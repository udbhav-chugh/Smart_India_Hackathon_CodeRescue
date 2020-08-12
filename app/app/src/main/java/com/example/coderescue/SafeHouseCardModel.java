package com.example.coderescue;

import java.util.Comparator;

public class SafeHouseCardModel {

    private String name;
    private  String latitude, longitude;
    private double distance;
    private String state;
    private int img;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLatitude(){ return latitude; }

    public void setLatitude(String latitude) { this.latitude = latitude; }

    public String getLongitude(){ return longitude; }

    public void setLongitude(String longitude) { this.longitude = longitude; }
//
    public double getDistance() { return distance;}

    public void setDistance(double distance) {this.distance = distance; }

    public static Comparator<SafeHouseCardModel> DistSort = new Comparator<SafeHouseCardModel>() {

        public int compare(SafeHouseCardModel s1, SafeHouseCardModel s2) {

            double dist1 = s1.getDistance();
            double dist2 = s2.getDistance();

            /*For ascending order*/
            return (int)(dist1-dist2);
        }};


}
