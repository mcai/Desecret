package cc.mincai.android.desecret.model;

public class SmsSentEvent extends SmsEvent {
    public SmsSentEvent(String counterpartId, String time, String text) {
        super(counterpartId, time, text);
    }

    @Override
    public String toString() {
        return String.format("sent sms to %s (%s)", this.getCounterpartId(), this.getText());
    }
}
