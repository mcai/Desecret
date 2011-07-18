package cc.mincai.android.desecret.model;

import org.simpleframework.xml.Attribute;

public class SmsSentEvent extends SmsEvent {
    public SmsSentEvent(
            @Attribute(name = "id") String id,
            @Attribute(name = "userId") String userId,
            @Attribute(name = "counterpartId") String counterpartId,
            @Attribute(name = "date") String date,
            @Attribute(name = "text") String text,
            @Attribute(name = "description") String description
    ) {
        super(id, userId, counterpartId, date, text, description);
    }
}
