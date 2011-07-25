package cc.mincai.android.desecret.core;

import cc.mincai.android.desecret.util.Action2;

import java.util.List;

public abstract class ServerTransport {
    private String userId;
    private Action2<String, Message> onMessageReceivedCallback;
    private Action2<String, ActivityEvent> onTargetActivityEventOccurredCallback;

    public ServerTransport(String userId, Action2<String, Message> onMessageReceivedCallback, Action2<String, ActivityEvent> onTargetActivityEventOccurredCallback) {
        this.userId = userId;
        this.onMessageReceivedCallback = onMessageReceivedCallback;
        this.onTargetActivityEventOccurredCallback = onTargetActivityEventOccurredCallback;
    }

    public abstract void connect();

    public abstract void disconnect();

    public abstract <T> T getParam(Class<? extends T> clz, String userId, String key);

    public abstract void setParam(String userId, String key, Object value);

    public abstract void removeParam(String userId, String key);

    public abstract void fireActivityEvent(ActivityEvent event);

    public abstract void addTargetUserIdToMonitor(String userId);

    public abstract void removeTargetUserIdToMonitor(String userId);

    public abstract void sendMessage(String to, Message message);

    public void onMessageReceived(String from, Message message) {
        this.onMessageReceivedCallback.apply(from, message);
    }

    public void onTargetActivityEventOccurred(String from, ActivityEvent event) {
        this.onTargetActivityEventOccurredCallback.apply(from, event);
    }

    public String getUserId() {
        return userId;
    }

    public abstract List<String> getUserIds();
}
