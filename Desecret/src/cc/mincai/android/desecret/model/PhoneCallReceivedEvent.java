package cc.mincai.android.desecret.model;

public class PhoneCallReceivedEvent extends PhoneCallEvent {
    public PhoneCallReceivedEvent(String counterpartId, String time, double durationInMinutes) {
        super(counterpartId, time, durationInMinutes);
    }
}
