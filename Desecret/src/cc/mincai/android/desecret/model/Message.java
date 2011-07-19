package cc.mincai.android.desecret.model;

public class Message {
    private Event event;

    public Message(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }
}
