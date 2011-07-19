package cc.mincai.android.desecret.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import cc.mincai.android.desecret.model.LocationChangedEvent;
import cc.mincai.android.desecret.service.LocationMonitorService;
import cc.mincai.android.desecret.service.MasterService;

public class LocationChangedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction().equals(LocationMonitorService.ACTION_LOCATION_CHANGED)) {
            Location loc = (Location) intent.getExtras().get(android.location.LocationManager.KEY_LOCATION_CHANGED);

            if(loc != null) {
                Intent launchIntent = new Intent(context, MasterService.class);

                cc.mincai.android.desecret.model.Location location = new cc.mincai.android.desecret.model.Location(loc.getLongitude(), loc.getLatitude(), "desc");

                LocationChangedEvent locationChangedEvent = new LocationChangedEvent("id", "userId", location, "desc");

                launchIntent.putExtra("event", locationChangedEvent);

                context.startService(launchIntent);
            }
        }
    }
}
