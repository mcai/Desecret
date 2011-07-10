package min.cai.desecret;

import android.app.Service;
import android.content.*;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.telephony.SmsMessage;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public abstract class AbstractSmsMonitorService extends Service {
    private SmsReceiveBroadcastReceiver smsReceiveBroadcastReceiver;
    private SmsSendObserver smsSendObserver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "SMS monitor service started", Toast.LENGTH_SHORT).show();

        this.smsReceiveBroadcastReceiver = new SmsReceiveBroadcastReceiver();
        this.smsSendObserver = new SmsSendObserver();

        this.registerReceiver(this.smsReceiveBroadcastReceiver, new IntentFilter(SMS_RECEIVED_ACTION));
        this.getContentResolver().registerContentObserver(Uri.parse(URI_SMS_ALL), true, this.smsSendObserver);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "SMS monitor service stopped", Toast.LENGTH_SHORT).show();

        this.unregisterReceiver(this.smsReceiveBroadcastReceiver);
        this.getContentResolver().unregisterContentObserver(this.smsSendObserver);
    }

    private class SmsReceiveBroadcastReceiver extends BroadcastReceiver {
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
                        long date = smsMessage[n].getTimestampMillis();

                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(date);

                        onSmsReceived(from, body, new SimpleDateFormat().format(cal.getTime()));
                    }
                }
            }
        }
    }

    private class SmsSendObserver extends ContentObserver {
        private SmsSendObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            Cursor cur = AbstractSmsMonitorService.this.getContentResolver().query(Uri.parse(URI_SMS_ALL), null, " date > ?", new String[]{getSmsUpdateTime().getTimeInMillis() + ""}, "date desc");

            while (cur.moveToNext()) {
                String to = cur.getString(cur.getColumnIndex("address"));
                String body = cur.getString(cur.getColumnIndex("body"));
                Long date = cur.getLong(cur.getColumnIndex("date"));
                Integer type = cur.getInt(cur.getColumnIndex("type"));

//		if (type == 4) //TODO: filter sent message
                {
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(date);

                    onSmsSent(to, body, new SimpleDateFormat().format(cal.getTime()));
                }
            }

            setSmsUpdateTime(Calendar.getInstance());
        }
    }

    private static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    private static final String URI_SMS_ALL = "content://sms";
    public static final String PREFS_NAME = "MyPrefsFile";

    public static final int INCOMING_TYPE = CallLog.Calls.INCOMING_TYPE;
    public static final int OUTGOING_TYPE = CallLog.Calls.OUTGOING_TYPE;

    private Calendar smsUpdateTime = null;

    private Calendar getSmsUpdateTime() {
        if (this.smsUpdateTime == null) {
            Calendar cal = Calendar.getInstance();

            SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            long storeSmsUpdateTime = settings.getLong("smsUpdateTime", 0L);

            if (storeSmsUpdateTime != 0L) {
                cal.setTimeInMillis(storeSmsUpdateTime);
            } else {
                cal.set(2000, 1, 1);
            }

            this.smsUpdateTime = cal;
        }

        return this.smsUpdateTime;
    }

    private void setSmsUpdateTime(Calendar now) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        SharedPreferences.Editor editor = settings.edit();
        editor.putLong("smsUpdateTime", now.getTimeInMillis());
        editor.commit();

        this.smsUpdateTime = now;
    }

    protected abstract void onSmsReceived(String from, String body, String date);

    protected abstract void onSmsSent(String to, String body, String date);
}
