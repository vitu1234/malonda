package com.example.malonda.maphelpers;

import android.location.Location;

public class DistanceBetween {
    public DistanceBetween() {
    }
    public double distance(double lat1, double lon1, double lat2, double lon2) {
        Location startPoint=new Location("locationA");
        startPoint.setLatitude(lat1);
        startPoint.setLongitude(lon1);

        Location endPoint=new Location("locationA");
        endPoint.setLatitude(lat2);
        endPoint.setLongitude(lon2);

        double distance=startPoint.distanceTo(endPoint);
        return (distance/1000);//return distance in km
    }


}
