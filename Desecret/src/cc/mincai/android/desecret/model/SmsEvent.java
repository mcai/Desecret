package cc.mincai.android.desecret.model;

import org.simpleframework.xml.Attribute;

public abstract class SmsEvent extends UserEvent {
    @Attribute
    private String counterpartId;
    @Attribute
    private String date;
    @Attribute
    private String text;

    public SmsEvent(
            @Attribute(name = "id") String id,
            @Attribute(name = "userId") String userId,
            @Attribute(name = "counterpartId") String counterpartId,
            @Attribute(name = "date") String date,
            @Attribute(name = "text") String text,
            @Attribute(name = "description") String description
    ) {
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
