package cc.mincai.android.desecret.model;

public abstract class PhoneCallEvent extends ActivityEvent {
    private String counterpartId;
    private String time;
    private double durationInSeconds;

    public PhoneCallEvent(String counterpartId, String time, double durationInSeconds) {
        this.counterpartId = counterpartId;
        this.time = time;
        this.durationInSeconds = durationInSeconds;
    }

    public String getCounterpartId() {
        return counterpartId;
    }

    public String getTime() {
        return time;
    }

    public double getDurationInSeconds() {
        return durationInSeconds;
    }
}
