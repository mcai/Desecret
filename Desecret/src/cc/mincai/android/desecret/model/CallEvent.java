package cc.mincai.android.desecret.model;

public abstract class CallEvent extends Event {
    private String counterpartId;

    public CallEvent(String id, String userId, String counterpartId, String description) {
        super(id, userId, description);
        this.counterpartId = counterpartId;
    }

    public String getCounterpartId() {
        return counterpartId;
    }
}
