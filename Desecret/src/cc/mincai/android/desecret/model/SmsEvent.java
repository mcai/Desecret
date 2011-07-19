package cc.mincai.android.desecret.model;

public abstract class SmsEvent extends Event {
    private String counterpartId;
    private String date;
    private String text;

    public SmsEvent(String id, String userId, String counterpartId, String date, String text, String description) {
        super(id, userId, description);
        this.counterpartId = counterpartId;
        this.date = date;
        this.text = text;
    }

    public String getCounterpartId() {
        return counterpartId;
    }

    public String getDate() {
        return date;
    }

    public String getText() {
        return text;
    }
}
