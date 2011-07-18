package cc.mincai.android.desecret.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;
import cc.mincai.android.desecret.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MasterService extends Service {
    private IBinder binder = new MyBinder();
    private boolean registered;
    private MessageTransport messageTransport;

    private List<UserEvent> pendingUserEvents;

    private Timer timer = new Timer();

    @Override
    public IBinder onBind(Intent intent) {
        return this.binder;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Master service started", Toast.LENGTH_SHORT).show();

        this.messageTransport = new DropboxMessageTransport(this);

        this.pendingUserEvents = new ArrayList<UserEvent>();

        this.register();
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Master service stopped", Toast.LENGTH_SHORT).show();

        this.deregister();
    }

    private void pollForUpdates() {
        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (UserEvent userEvent : pendingUserEvents) {
                    getMessageTransport().sendMessage(new Message("id", "from", "to", new UserEventMessageContent(userEvent))); //TODO: fill the fields
                }

                pendingUserEvents.clear();
            }
        }, 0, UPDATE_INTERVAL);
    }

    private void register() {
        if (!this.registered) {
            this.messageTransport.open();
            this.pollForUpdates();

            this.startService(new Intent(this, LocationMonitorService.class));
            this.startService(new Intent(this, SmsMonitorService.class));

            this.registered = true;
        }
    }

    private void deregister() {
        if (this.registered) {
            this.stopService(new Intent(this, LocationMonitorService.class));
            this.stopService(new Intent(this, SmsMonitorService.class));

            this.timer.cancel();
            this.messageTransport.close();

            this.registered = false;
        }
    }

    public void addUserEvent(UserEvent userEvent) {
        this.pendingUserEvents.add(userEvent);
    }

    public MessageTransport getMessageTransport() {
        return this.messageTransport;
    }

    public class MyBinder extends Binder {
        public MasterService getService() {
            return MasterService.this;
        }
    }

    private static final long UPDATE_INTERVAL = 5000;
}
