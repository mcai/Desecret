package cc.mincai.android.desecret.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

public class User implements Identifiable {
    @Attribute
    private String id;
    @Attribute
    private String nickName;
    @Element
    private Location lastKnownLocation;

    public User(
            @Attribute(name = "id") String id,
            @Attribute(name = "nickName") String nickName,
            @Attribute(name = "lastKnownLocation") Location lastKnownLocation
    ) {
        this.id = id;
        this.nickName = nickName;
        this.lastKnownLocation = lastKnownLocation;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getNickName() {
        return nickName;
    }

    public Location getLastKnownLocation() {
        return lastKnownLocation;
    }
}
