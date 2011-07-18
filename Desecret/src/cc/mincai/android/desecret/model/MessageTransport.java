package cc.mincai.android.desecret.model;

import android.content.Context;
import android.content.SharedPreferences;
import cc.mincai.android.desecret.util.Action1;
import cc.mincai.android.desecret.util.BlockingEventDispatcher;

import java.util.UUID;

public abstract class MessageTransport {
    private Context context;
    private String userId;
    private BlockingEventDispatcher eventDispatcher;

    public MessageTransport(Context context) {
        this.context = context;
        this.eventDispatcher = new BlockingEventDispatcher();

        this.userId = getUserId(context);
    }

    public abstract void open();

    public abstract void close();

    public abstract void sendMessage(Message message);

    protected void dispatch(MessageReceivedEvent event) {
        this.eventDispatcher.dispatch(event);
    }

    public void addListener(Action1<MessageReceivedEvent> listener) {
        this.eventDispatcher.addListener(MessageReceivedEvent.class, listener);
    }

    public void removeListener(Action1<MessageReceivedEvent> listener) {
        this.eventDispatcher.removeListener(MessageReceivedEvent.class, listener);
    }

    public Context getContext() {
        return context;
    }

    public String getUserId() {
        return userId;
    }

    private static String uniqueId = null;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

    public static String getUserId(Context context) {
        if(uniqueId == null) {
            SharedPreferences sp = context.getSharedPreferences(PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            uniqueId = sp.getString(PREF_UNIQUE_ID, null);
            if(uniqueId == null) {
                uniqueId = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(PREF_UNIQUE_ID, uniqueId);
                editor.commit();
            }
        }

        return uniqueId;
    }
}
