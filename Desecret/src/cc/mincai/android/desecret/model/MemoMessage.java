package cc.mincai.android.desecret.model;

public class MemoMessage extends Message {
    private String text;

    public MemoMessage(String userId, String text) {
        super();
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
