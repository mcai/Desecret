package cc.mincai.android.desecret.core;

public abstract class SmsEvent extends ActivityEvent {
    private String counterpartId;
    private String time;
    private String text;

    public SmsEvent(String counterpartId, String time, String text) {
        this.counterpartId = counterpartId;
        this.time = time;
        this.text = text;
    }

    public String getCounterpartId() {
        return counterpartId;
    }

    public String getTime() {
        return time;
    }

    public String getText() {
        return text;
    }
}
