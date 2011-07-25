package cc.mincai.android.desecret.model;

public class PhoneCallSentEvent extends PhoneCallEvent {
    public PhoneCallSentEvent(String counterpartId, String time, double durationInMinutes) {
        super(counterpartId, time, durationInMinutes);
    }

    @Override
    public String toString() {
        return String.format("sent call to %s (%f seconds)", this.getCounterpartId(), this.getDurationInSeconds());
    }
}
