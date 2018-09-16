package com.minus.occupancychecker;

public class Building {
    private double latitute;
    private double longitude;
    private int occupancy;

    public double getLatitute() {
        return latitute;
    }

    public void setLatitute(double latitute) {
        this.latitute = latitute;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getOccupancy() {
        return occupancy;
    }

    @Override
    public String toString() {
        return "Building{" +
                "latitute=" + latitute +
                ", longitude=" + longitude +
                ", occupancy=" + occupancy +
                '}';
    }

    public void setOccupancy(int occupancy) {
        this.occupancy = occupancy;
    }
}
