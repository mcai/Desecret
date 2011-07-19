package cc.mincai.android.desecret.model;

public class SmsReceivedEvent extends SmsEvent {
    public SmsReceivedEvent(String id, String userId, String counterpartId, String date, String text, String description) {
        super(id, userId, counterpartId, date, text, description);
    }
}
