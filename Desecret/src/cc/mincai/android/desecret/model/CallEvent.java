package cc.mincai.android.desecret.model;

import org.simpleframework.xml.Attribute;

public abstract class CallEvent extends UserEvent {
    @Attribute
    private String counterpartId;

    public CallEvent(
            @Attribute(name = "id") String id,
            @Attribute(name = "userId") String userId,
            @Attribute(name = "counterpartId") String counterpartId,
            @Attribute(name = "description") String description
    ) {
        super(id, userId, description);
        this.counterpartId = counterpartId;
    }

    public String getCounterpartId() {
        return counterpartId;
    }
}
