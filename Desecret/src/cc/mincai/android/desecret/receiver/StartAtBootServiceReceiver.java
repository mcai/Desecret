package cc.mincai.android.desecret.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cc.mincai.android.desecret.service.DesecretService;

public class StartAtBootServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            context.startService(new Intent(context, DesecretService.class));
        }
    }
}
