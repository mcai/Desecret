package cc.mincai.android.desecret.model;

public class PhoneCallSentEvent extends PhoneCallEvent {
    public PhoneCallSentEvent(String counterpartId, String time, double durationInMinutes) {
        super(counterpartId, time, durationInMinutes);
    }
}
