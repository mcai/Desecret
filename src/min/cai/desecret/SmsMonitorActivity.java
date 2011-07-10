package min.cai.desecret;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ToggleButton;

public class SmsMonitorActivity extends Activity implements OnClickListener {
    private ToggleButton toggleButtonSmsMonitorService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main);

        this.toggleButtonSmsMonitorService = (ToggleButton) this.findViewById(R.id.toggleButtonSmsMonitorService);
        this.toggleButtonSmsMonitorService.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (this.toggleButtonSmsMonitorService.isChecked()) {
            this.startService(new Intent(this, SmsMonitorService.class));
        } else {
            this.stopService(new Intent(this, SmsMonitorService.class));
        }
    }
}