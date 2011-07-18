package cc.mincai.android.desecret.model;

import cc.mincai.android.desecret.util.BlockingEvent;

public class MessageReceivedEvent implements BlockingEvent {
    private Message message;

    public MessageReceivedEvent(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }
}
