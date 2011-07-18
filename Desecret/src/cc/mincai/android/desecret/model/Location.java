package cc.mincai.android.desecret.model;

import com.google.android.maps.GeoPoint;
import org.simpleframework.xml.Attribute;

public class Location {
    @Attribute
    private double longitude;
    @Attribute
    private double latitude;
    @Attribute
    private String description;

    private GeoPoint point;

    public Location(
            @Attribute(name = "longitude") double longitude,
            @Attribute(name = "latitude") double latitude,
            @Attribute(name = "description") String description
    ) {
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
