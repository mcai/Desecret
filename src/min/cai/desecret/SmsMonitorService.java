package min.cai.desecret;

import android.widget.Toast;

public class SmsMonitorService extends AbstractSmsMonitorService {
    protected void onSmsReceived(String from, String body, String date) {
        //TODO: record it in an external text file
        Toast.makeText(SmsMonitorService.this, "New SMS message received, " + "from = " + from + ", body = " + body + ", date = " + date, Toast.LENGTH_LONG).show();
    }

    protected void onNewSmsReceivedOrSent(String address, String body, String date) {
        //TODO: record it in an external text file
        Toast.makeText(SmsMonitorService.this, "New SMS message received or sent, " + "address = " + address + ", body = " + body + ", date = " + date, Toast.LENGTH_LONG).show();
    }
}
