package cc.mincai.android.desecret.model;

public class CallReceivedEvent extends CallEvent {
    public CallReceivedEvent(String id, String userId, String counterpartId, String description) {
        super(id, userId, counterpartId, description);
    }
}
