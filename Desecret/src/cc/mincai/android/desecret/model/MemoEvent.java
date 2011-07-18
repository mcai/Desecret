package cc.mincai.android.desecret.model;

import org.simpleframework.xml.Attribute;

public class MemoEvent extends UserEvent {
    @Attribute
    private String text;

    public MemoEvent(
            @Attribute(name = "id") String id,
            @Attribute(name = "userId") String userId,
            @Attribute(name = "text") String text,
            @Attribute(name = "description") String description
    ) {
        super(id, userId, description);
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
