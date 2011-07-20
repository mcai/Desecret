package cc.mincai.android.desecret.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import cc.mincai.android.desecret.R;

public class SplashActivity extends Activity {
    private long splashTime = 3000;
    private boolean paused = false;
    private boolean splashActive = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash);

        Thread splashTimer = new Thread() {
            @Override
            public void run() {
                try {
                    long ms = 0;

                    while (splashActive && ms < splashTime) {
                        sleep(100);

                        if (!paused) {
                            ms += 100;
                        }
                    }

                    startActivity(new Intent("cc.mincai.android.desecret.action.CLEAR_SPLASH"));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    finish();
                }
            }
        };
        splashTimer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        this.paused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.paused = false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);

        this.splashActive = false;

        return true;
    }
}

