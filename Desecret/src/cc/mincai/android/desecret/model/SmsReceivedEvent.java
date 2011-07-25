package cc.mincai.android.desecret.model;

public class SmsReceivedEvent extends SmsEvent {
    public SmsReceivedEvent(String counterpartId, String time, String text) {
        super(counterpartId, time, text);
    }

    @Override
    public String toString() {
        return String.format("received sms from %s (%s)", this.getCounterpartId(), this.getText());
    }
}
