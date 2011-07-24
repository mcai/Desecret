package cc.mincai.android.desecret.model;

public abstract class PhoneCallEvent extends ActivityEvent {
    private String counterpartId;
    private String time;
    private double durationInMinutes;

    public PhoneCallEvent(String counterpartId, String time, double durationInMinutes) {
        this.counterpartId = counterpartId;
        this.time = time;
        this.durationInMinutes = durationInMinutes;
    }

    public String getCounterpartId() {
        return counterpartId;
    }

    public String getTime() {
        return time;
    }

    public double getDurationInMinutes() {
        return durationInMinutes;
    }
}
