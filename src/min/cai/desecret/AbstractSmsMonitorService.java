package min.cai.desecret;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public abstract class AbstractSmsMonitorService extends Service {
    private SmsSendObserver smsSendObserver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "SMS monitor service started", Toast.LENGTH_SHORT).show();

        this.smsSendObserver = new SmsSendObserver();
        this.getContentResolver().registerContentObserver(Uri.parse(URI_SMS_ALL), true, this.smsSendObserver);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "SMS monitor service stopped", Toast.LENGTH_SHORT).show();

        this.getContentResolver().unregisterContentObserver(this.smsSendObserver);
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

                    onNewSmsReceivedOrSent(to, body, new SimpleDateFormat().format(cal.getTime()));
                }
            }

            setSmsUpdateTime(Calendar.getInstance());
        }
    }

    public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";

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

    protected abstract void onNewSmsReceivedOrSent(String to, String body, String date);
}
