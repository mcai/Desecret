package cc.mincai.android.desecret.model;

public class LocationChangedEvent extends ActivityEvent {
    private Location newLocation;

    public LocationChangedEvent(Location newLocation) {
        this.newLocation = newLocation;
    }

    public Location getNewLocation() {
        return newLocation;
    }

    @Override
    public String toString() {
        return String.format("moved to: %s", newLocation);
    }
}
