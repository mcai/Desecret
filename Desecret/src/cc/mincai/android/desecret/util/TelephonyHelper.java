package cc.mincai.android.desecret.util;

import android.content.Context;
import android.telephony.TelephonyManager;

public class TelephonyHelper {
    public static String getPhoneNumber(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getLine1Number();
    }
}
