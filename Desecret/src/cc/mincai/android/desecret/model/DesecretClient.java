package cc.mincai.android.desecret.model;

import android.content.Context;
import android.content.SharedPreferences;
import cc.mincai.android.desecret.storage.AndroidFileSystemAccessProvider;
import cc.mincai.android.desecret.storage.FileSystemAccessProvider;
import cc.mincai.android.desecret.util.Action1;
import cc.mincai.android.desecret.util.Action2;
import cc.mincai.android.desecret.util.BlockingEvent;
import cc.mincai.android.desecret.util.BlockingEventDispatcher;

import java.util.List;
import java.util.UUID;

public class DesecretClient {
    private ServerTransport serverTransport;
    private BlockingEventDispatcher eventDispatcher;

    private String userId;

    public DesecretClient(Context context) {
        this(getUserId(context), new AndroidFileSystemAccessProvider(context));
    }

    public DesecretClient(String userId, FileSystemAccessProvider fileSystemAccessProvider) {
        this.userId = userId;

        this.serverTransport = new CloudStorageServerTransport(userId, new Action2<String, Message>() {
            @Override
            public void apply(String from, Message message) {
                eventDispatcher.dispatch(new MessageReceivedEvent(from, message));
            }
        }, new Action2<String, ActivityEvent>() {
            @Override
            public void apply(String from, ActivityEvent event) {
                eventDispatcher.dispatch(new TargetActivityEventOccurredEvent(from, event));
            }
        }, fileSystemAccessProvider);
        this.eventDispatcher = new BlockingEventDispatcher();
    }

    public void start() {
        this.serverTransport.connect();
    }

    public void stop() {
        this.serverTransport.disconnect();
    }

    public void sendMessage(String to, Message message) {
        this.serverTransport.sendMessage(to, message);
    }

    public void broadcastMessage(Message message) {
        for(String to : this.getUserIds()) {
            this.sendMessage(to, message);
        }
    }

    public void fireActivityEvent(ActivityEvent event) {
        this.serverTransport.fireActivityEvent(event);
    }

    public void addMessageReceivedEventListener(Action1<MessageReceivedEvent> listener) {
        this.eventDispatcher.addListener(MessageReceivedEvent.class, listener);
    }

    public void removeMessageReceivedEventListener(Action1<MessageReceivedEvent> listener) {
        this.eventDispatcher.removeListener(MessageReceivedEvent.class, listener);
    }

    public void addTargetActivityEventOccurredEventListener(Action1<TargetActivityEventOccurredEvent> listener) {
        this.eventDispatcher.addListener(TargetActivityEventOccurredEvent.class, listener);
    }

    public void removeTargetActivityEventOccurredEventListener(Action1<TargetActivityEventOccurredEvent> listener) {
        this.eventDispatcher.removeListener(TargetActivityEventOccurredEvent.class, listener);
    }

    public boolean authenticate(String passwordToCheck) {
        return this.serverTransport.getParam(String.class, this.getUserId(), "password").equals(passwordToCheck);
    }

    public void registerUser(String id, String password) {
        this.serverTransport.setParam(id, "password", password);
    }

    public void deregisterUser(String id) {
        this.serverTransport.removeParam(id, "password");
    }

    public List<String> getUserIds() {
        return this.serverTransport.getUserIds();
    }

    public String getUserId() {
        return userId;
    }

    public class MessageReceivedEvent implements BlockingEvent {
        private String from;
        private Message message;

        public MessageReceivedEvent(String from, Message message) {
            this.from = from;
            this.message = message;
        }

        public String getFrom() {
            return from;
        }

        public Message getMessage() {
            return message;
        }
    }

    public class TargetActivityEventOccurredEvent implements BlockingEvent {
        private String from;
        private ActivityEvent event;

        public TargetActivityEventOccurredEvent(String from, ActivityEvent event) {
            this.from = from;
            this.event = event;
        }

        public String getFrom() {
            return from;
        }

        public ActivityEvent getEvent() {
            return event;
        }
    }

    public static enum MonitoringEventType {
        SMS_RECEIVED,
        SMS_SENT,
        CALL_RECEIVED,
        CALL_SENT,
        LOCATION_CHANGED
    }

    public static final String USER_ID_ADMIN = "admin";
    public static final String USER_ID_GUEST = "guest";
    public static final String USER_ID_BROADCAST = "broadcast";

    private static final String PREF_UNIQUE_ID = "PREF_DEVICE_ID";

    private static String getUserId(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREF_UNIQUE_ID, Context.MODE_PRIVATE);
        String userId = sp.getString(PREF_UNIQUE_ID, null);
        if(userId == null) {
            userId = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(PREF_UNIQUE_ID, userId);
            editor.commit();
        }

        return userId;
    }
}
