package cc.mincai.android.desecret.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import cc.mincai.android.desecret.R;

public class UserRegisterActivity extends Activity {
    private EditText editTextRegisterId;

    private EditText editTextRegisterPassword;
    private EditText editTextRegisterReenterPassword;

    private Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_register);

        this.editTextRegisterId = (EditText) findViewById(R.id.editTextRegisterId);

        this.editTextRegisterPassword = (EditText) findViewById(R.id.editTextRegisterPassword);
        this.editTextRegisterReenterPassword = (EditText) findViewById(R.id.editTextRegisterReenterPassword);

        this.buttonRegister = (Button) findViewById(R.id.buttonRegister);

        this.buttonRegister.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }

    private void register() {
        final String id = this.editTextRegisterId.getText().toString();

        final String password = this.editTextRegisterPassword.getText().toString();
        String reenterPassword = this.editTextRegisterReenterPassword.getText().toString();

        if (password.equals(reenterPassword)) {
            showDialog(DIALOG_ID_WAITING_FOR_REGISTRATION);

            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        //TODO: perform the real registration process
                        sleep(5000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissDialog(DIALOG_ID_WAITING_FOR_REGISTRATION);
                            showDialog(DIALOG_ID_REGISTRATION_SUCCESSFUL);
                        }
                    });
                }
            };

            thread.start();
        } else {
            Toast.makeText(this, "Passwords should be identical", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_ID_WAITING_FOR_REGISTRATION:
                ProgressDialog dialog = new ProgressDialog(this);
                dialog.setMessage("Please wait...");
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                return dialog;
            case DIALOG_ID_REGISTRATION_SUCCESSFUL:
                return new AlertDialog.Builder(this)
                        .setMessage("Registration successful, please login to use Desecret.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Intent intent = new Intent(ACTION_REGISTER_SUCCESSFUL);
                                intent.putExtra("user_id", editTextRegisterId.getText().toString());
                                intent.putExtra("user_password", editTextRegisterPassword.getText().toString());
                                setResult(RESULT_OK, intent);

                                finish();
                            }
                        })
                        .create();
            default:
                throw new IllegalArgumentException();
        }
    }

    public static final String ACTION_REGISTER_SUCCESSFUL = "register_successful";

    private static final int DIALOG_ID_WAITING_FOR_REGISTRATION = 0;
    private static final int DIALOG_ID_REGISTRATION_SUCCESSFUL = 1;
}
