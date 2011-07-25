package cc.mincai.android.desecret.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import cc.mincai.android.desecret.core.DesecretClient;
import cc.mincai.android.desecret.core.LocationChangedEvent;
import cc.mincai.android.desecret.core.SmsReceivedEvent;
import com.google.android.maps.GeoPoint;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.LatLng;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;

//SplashActivity
//
//UserLoginActivity
//	user: <user id>
//	password: <password>
//
//UserRegisterActivity
//	user: <user id>
//	password: <password>
//			  <reenter password>
//	nickname: <nick name>
//	gender:   <male or female>
//	Birthday: <year-of-the-birth>
//
//HomeActivity
//	Contacts -> popup menu: contact profile; browse activities and messages
//  popup window: MapActivity
//
//ContactActivity
// list user's own or one contact's recent activities and messages
// send messages
//
//HelpActivity
public class DesecretService extends Service {
    private IBinder binder = new MyBinder();

    private DesecretClient desecretClient;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private SmsSendObserver smsSendObserver;

    @Override
    public IBinder onBind(Intent intent) {
        return this.binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Bundle bundle = intent.getExtras();
//        if (bundle != null) {
//            ActivityEvent event = (ActivityEvent) intent.getExtras().getSerializable("pendingActivityEvent");
//            if (event != null) {
//                this.desecretClient.fireActivityEvent(event);
//            }
//        }

        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        this.desecretClient = new DesecretClient(this);

        this.desecretClient.start();

        this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        this.locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                cc.mincai.android.desecret.core.Location loc = new cc.mincai.android.desecret.core.Location(location.getLongitude(), location.getLatitude());

                loc.setDescription(geocode(loc.getPoint()));

                desecretClient.fireActivityEvent(new LocationChangedEvent(loc));
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
            }
        };

        this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 0, this.locationListener);

        this.smsSendObserver = new SmsSendObserver();
        this.getContentResolver().registerContentObserver(Uri.parse(URI_SMS_ALL), true, this.smsSendObserver);
    }

    @Override
    public void onDestroy() {
        this.getContentResolver().unregisterContentObserver(this.smsSendObserver);

        this.locationManager.removeUpdates(this.locationListener);

        this.desecretClient.stop();
    }

    public DesecretClient getDesecretClient() {
        return this.desecretClient;
    }

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

    private static final String URI_SMS_ALL = "content://sms";
    public static final String PREFS_NAME = "MyPrefsFile";

    public String geocode(GeoPoint point) {
        com.google.code.geocoder.Geocoder geocoder = new com.google.code.geocoder.Geocoder();

        geocoder.getHttpClient().getHostConfiguration().setProxy("192.168.1.100", 3128);

        GeocoderRequest request = new GeocoderRequestBuilder()
                .setLocation(new LatLng( BigDecimal.valueOf(point.getLatitudeE6() / 1e6), BigDecimal.valueOf(point.getLongitudeE6() / 1e6)))
                .setLanguage("en").getGeocoderRequest();

        GeocodeResponse response = geocoder.geocode(request);
        return response.getResults().get(0).getFormattedAddress();
    }

    private class SmsSendObserver extends ContentObserver {
        private SmsSendObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            Cursor cur = DesecretService.this.getContentResolver().query(Uri.parse(URI_SMS_ALL), null, " date > ?", new String[]{getSmsUpdateTime().getTimeInMillis() + ""}, "date desc");

            while (cur.moveToNext()) {
                String to = cur.getString(cur.getColumnIndex("address"));
                String body = cur.getString(cur.getColumnIndex("body"));
                Long date = cur.getLong(cur.getColumnIndex("date"));
                Integer type = cur.getInt(cur.getColumnIndex("type"));

//		if (type == 4) //TODO: filter sent message
                {
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(date);

                    DesecretService.this.desecretClient.fireActivityEvent(new SmsReceivedEvent(to, new SimpleDateFormat().format(cal.getTime()), body));
                }
            }

            setSmsUpdateTime(Calendar.getInstance());
        }
    }

    public class MyBinder extends Binder {
        public DesecretService getService() {
            return DesecretService.this;
        }
    }
}
