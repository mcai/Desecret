package cc.mincai.android.desecret.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

public class LocationChangedEvent extends UserEvent {
    @Element
    private Location newLocation;

    public LocationChangedEvent(
            @Attribute(name = "id") String id,
            @Attribute(name = "userId") String userId,
            @Element(name = "newLocation") Location newLocation,
            @Attribute(name = "description") String description
    ) {
        super(id, userId, description);
        this.newLocation = newLocation;
    }

    public Location getNewLocation() {
        return newLocation;
    }
}
