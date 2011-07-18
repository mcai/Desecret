package cc.mincai.android.desecret.model;

import org.simpleframework.xml.Element;

public class UserEventMessageContent implements MessageContent {
    @Element
    private UserEvent userEvent;

    public UserEventMessageContent(
            @Element(name = "userEvent") UserEvent userEvent
    ) {
        this.userEvent = userEvent;
    }

    public UserEvent getUserEvent() {
        return userEvent;
    }
}
