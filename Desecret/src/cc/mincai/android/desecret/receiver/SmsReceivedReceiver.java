package cc.mincai.android.desecret.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import cc.mincai.android.desecret.service.DesecretService;

public class SmsReceivedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(SMS_RECEIVED_ACTION)) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object messages[] = (Object[]) bundle.get("pdus");
                SmsMessage smsMessage[] = new SmsMessage[messages.length];
                for (int n = 0; n < messages.length; n++) {
                    smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);

                    String from = smsMessage[n].getOriginatingAddress();
                    String body = smsMessage[n].getMessageBody();

                    if (body.equals("#start_desecret")) {
                        context.startService(new Intent(context, DesecretService.class));
                    } else if (body.equals("#stop_desecret")) {
                        context.stopService(new Intent(context, DesecretService.class));
                    }
                }
            }
        }
    }

    public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
}
