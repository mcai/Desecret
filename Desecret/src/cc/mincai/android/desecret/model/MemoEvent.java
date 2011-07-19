package cc.mincai.android.desecret.model;

public class MemoEvent extends Event {
    private String text;

    public MemoEvent(String id, String userId, String text, String description) {
        super(id, userId, description);
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
