package com.example.picobank;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Login extends AppCompatActivity {
    private Button loginButton;
    /* access modifiers changed from: private */
    public EditText passwordEditText;
    /* access modifiers changed from: private */
    public EditText usernameEditText;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), new Login$$ExternalSyntheticLambda0());
        this.usernameEditText = (EditText) findViewById(R.id.username);
        this.passwordEditText = (EditText) findViewById(R.id.password);
        this.loginButton = (Button) findViewById(R.id.loginBtn);
        this.loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String username = Login.this.usernameEditText.getText().toString();
                String password = Login.this.passwordEditText.getText().toString();
                if (!"johnson".equals(username) || !"tricky1990".equals(password)) {
                    Toast.makeText(Login.this, "Incorrect credentials", 0).show();
                    return;
                }
                Login.this.startActivity(new Intent(Login.this, OTP.class));
                Login.this.finish();
            }
        });
    }

    static /* synthetic */ WindowInsetsCompat lambda$onCreate$0(View v, WindowInsetsCompat insets) {
        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
        return insets;
    }
}
