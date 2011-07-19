package cc.mincai.android.desecret.model;

public class LocationChangedEvent extends Event {
    private Location newLocation;

    public LocationChangedEvent(String id, String userId, Location newLocation, String description) {
        super(id, userId, description);
        this.newLocation = newLocation;
    }

    public Location getNewLocation() {
        return newLocation;
    }
}
