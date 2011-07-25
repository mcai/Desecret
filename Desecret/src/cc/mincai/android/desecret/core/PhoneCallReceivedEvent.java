package cc.mincai.android.desecret.core;

public class PhoneCallReceivedEvent extends PhoneCallEvent {
    public PhoneCallReceivedEvent(String counterpartId, String time, double durationInMinutes) {
        super(counterpartId, time, durationInMinutes);
    }

    @Override
    public String toString() {
        return String.format("received call from %s (%f seconds)", this.getCounterpartId(), this.getDurationInSeconds());
    }
}
