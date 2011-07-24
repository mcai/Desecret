package cc.mincai.android.desecret.model;

public class SmsReceivedEvent extends SmsEvent {
    public SmsReceivedEvent(String counterpartId, String time, String text) {
        super(counterpartId, time, text);
    }
}
