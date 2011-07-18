package cc.mincai.android.desecret.model;

import org.simpleframework.xml.Attribute;

public class TextMessageContent implements MessageContent {
    @Attribute
    private String text;

    public TextMessageContent(
            @Attribute(name = "text") String text
    ) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
