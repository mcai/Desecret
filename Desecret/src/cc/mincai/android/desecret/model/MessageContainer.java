package cc.mincai.android.desecret.model;

public class MessageContainer {
    private String from;
    private String to;
    private Message message;

    public MessageContainer(String from, String to, Message message) {
        this.from = from;
        this.to = to;
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public Message getMessage() {
        return message;
    }
}
