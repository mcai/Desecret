package cc.mincai.android.desecret.receiver;

import android.content.*;
import android.location.Location;
import android.os.IBinder;
import cc.mincai.android.desecret.model.LocationChangedEvent;
import cc.mincai.android.desecret.service.LocationMonitorService;
import cc.mincai.android.desecret.service.MasterService;

public class LocationChangedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction().equals(LocationMonitorService.ACTION_LOCATION_CHANGED)) {
            final Location loc = (Location) intent.getExtras().get(android.location.LocationManager.KEY_LOCATION_CHANGED);
            context.bindService(
                    new Intent(context, MasterService.class),
                    new ServiceConnection() {
                        public void onServiceConnected(ComponentName className, IBinder binder) {
                            ((MasterService.MyBinder) binder).getService().addUserEvent(new LocationChangedEvent("id", "userId",
                                    new cc.mincai.android.desecret.model.Location(loc.getLongitude(), loc.getLatitude(), "desc"), "desc")); //TODO: fill the fields
                        }

                        public void onServiceDisconnected(ComponentName className) {
                        }
                    },
                    Context.BIND_AUTO_CREATE
            );
        }
    }
}
