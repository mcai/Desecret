package cc.mincai.android.desecret.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import cc.mincai.android.desecret.R;

public class LoginActivity extends Activity {
    private EditText editTextLoginId;
    private EditText editTextLoginPassword;

    private Button buttonLogin;
    private Button buttonRegister;
    private Button buttonHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);

        this.editTextLoginId = (EditText) findViewById(R.id.editTextLoginId);
        this.editTextLoginPassword = (EditText) findViewById(R.id.editTextLoginPassword);

        this.buttonLogin = (Button) findViewById(R.id.buttonLogin);
        this.buttonRegister = (Button) findViewById(R.id.buttonRegister);
        this.buttonHelp = (Button) findViewById(R.id.buttonHelp);

        this.buttonLogin.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                String id = editTextLoginId.getText().toString();
                String password = editTextLoginPassword.getText().toString();

                if(login(id, password)) {
                    startActivity(new Intent("cc.mincai.android.desecret.action.LOGIN_SUCCESSFUL"));
                }
            }
        });

        this.buttonRegister.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                //TODO
            }
        });

        this.buttonHelp.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                //TODO: go to about activity
            }
        });
    }

    private boolean login(String id, String password) {
        return true; //TODO
    }
}
