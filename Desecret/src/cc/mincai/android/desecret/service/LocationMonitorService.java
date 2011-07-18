package cc.mincai.android.desecret.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;

public class LocationMonitorService extends Service {
    private LocationManager locationManager;

    private boolean registered;
    private PendingIntent launchIntent;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        this.register();
    }

    @Override
    public void onDestroy() {
        this.deregister();
    }

    private void register() {
        if (!this.registered) {
            this.launchIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_LOCATION_CHANGED), PendingIntent.FLAG_CANCEL_CURRENT);
            this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 0, this.launchIntent);

            this.registered = true;
        }
    }

    private void deregister() {
        if (this.registered) {
            this.locationManager.removeUpdates(this.launchIntent);

            this.registered = false;
        }
    }

    public static final String ACTION_LOCATION_CHANGED = "cc.mincai.android.LOCATION_CHANGED";
}
