package cc.mincai.android.desecret.model;

public class User implements Identifiable {
    private String id;
    private String nickName;
    private Location lastKnownLocation;

    public User(String id, String nickName, Location lastKnownLocation) {
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
