package cc.mincai.android.desecret.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cc.mincai.android.desecret.R;

public class UserLoginActivity extends Activity {
    private EditText editTextLoginId;
    private EditText editTextLoginPassword;

    private Button buttonLogin;
    private Button buttonGotoRegister;
    private Button buttonHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_login);

        this.editTextLoginId = (EditText) findViewById(R.id.editTextLoginId);
        this.editTextLoginPassword = (EditText) findViewById(R.id.editTextLoginPassword);

        this.buttonLogin = (Button) findViewById(R.id.buttonLogin);
        this.buttonGotoRegister = (Button) findViewById(R.id.buttonGotoRegister);
        this.buttonHelp = (Button) findViewById(R.id.buttonHelp);

        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        String id = prefs.getString("user_id", null);
        String password = prefs.getString("user_password", null);

        if(id != null) {
            this.editTextLoginId.setText(id, TextView.BufferType.EDITABLE);
        }

        if(password != null) {
            this.editTextLoginPassword.setText(password, TextView.BufferType.EDITABLE);
        }

        this.buttonLogin.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = editTextLoginId.getText().toString();
                String password = editTextLoginPassword.getText().toString();

                if (login(id, password)) {//TODO: should be asynchronous, see UserRegisterActivity; try to use Handle mechanism in Android
                    Intent intent = new Intent("cc.mincai.android.desecret.action.LOGIN_SUCCESSFUL");
                    intent.putExtra("user_id", id);
                    intent.putExtra("user_password", password);

                    startActivity(intent);

                    finish();
                }
            }
        });

        this.buttonGotoRegister.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent("cc.mincai.android.desecret.action.GOTO_REGISTER"), REQUEST_CODE_REGISTER_USER);
            }
        });

        this.buttonHelp.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent("cc.mincai.android.desecret.action.GOTO_HELP"));
            }
        });
    }

    private boolean login(String id, String password) {
        //TODO: perform the real login process

        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putString("user_id", id);
        editor.putString("user_password", password);
        editor.commit();

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_REGISTER_USER) {
            if(resultCode != RESULT_CANCELED) {
                if(data.getAction().equals(UserRegisterActivity.ACTION_REGISTER_SUCCESSFUL)) {
                    String id = data.getStringExtra("user_id");
                    String password = data.getStringExtra("user_password");

                    this.editTextLoginId.setText(id, TextView.BufferType.EDITABLE);
                    this.editTextLoginPassword.setText(password, TextView.BufferType.EDITABLE);
                }
            }
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_ID_WAITING_FOR_LOGIN:
                ProgressDialog dialog = new ProgressDialog(this);
                dialog.setMessage("Please wait...");
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                return dialog;
            default:
                throw new IllegalArgumentException();
        }
    }

    public static final int REQUEST_CODE_REGISTER_USER = 0;

    private static final int DIALOG_ID_WAITING_FOR_LOGIN = 0;
}
