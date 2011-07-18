package cc.mincai.android.desecret.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import cc.mincai.android.desecret.model.SmsReceivedEvent;

public class SmsMonitorService extends AbstractSmsMonitorService {
    private MasterService masterService;

    @Override
    public void onCreate() {
        super.onCreate();

        this.bindService(
                new Intent(this, MasterService.class),
                new ServiceConnection() {
                    public void onServiceConnected(ComponentName className, IBinder binder) {
                        masterService = ((MasterService.MyBinder) binder).getService();
                    }

                    public void onServiceDisconnected(ComponentName className) {
                        masterService = null;
                    }
                },
                Context.BIND_AUTO_CREATE
        );
    }

    protected void onNewSmsReceivedOrSent(String address, String body, String date) {
        this.masterService.addUserEvent(new SmsReceivedEvent("id", "userId", address, date, body, "desc")); //TODO: fill the fields
    }
}
