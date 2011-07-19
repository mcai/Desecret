package cc.mincai.android.desecret.model;

import com.google.android.maps.GeoPoint;

import java.io.Serializable;

public class Location implements Serializable {
    private double longitude;
    private double latitude;
    private String description;

    private transient GeoPoint point;

    public Location(double longitude, double latitude, String description) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.description = description;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public String getDescription() {
        return this.description;
    }

    public GeoPoint getPoint() {
        if (this.point == null) {
            this.point = new GeoPoint((int) (this.latitude * 1e6), (int) (this.longitude * 1e6));
        }

        return this.point;
    }
}
