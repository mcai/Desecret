package cc.mincai.android.desecret.model;

import org.simpleframework.xml.Attribute;

public class CallSentEvent extends CallEvent {
    public CallSentEvent(
            @Attribute(name = "id") String id,
            @Attribute(name = "userId") String userId,
            @Attribute(name = "counterpartId") String counterpartId,
            @Attribute(name = "description") String description
    ) {
        super(id, userId, counterpartId, description);
    }
}
