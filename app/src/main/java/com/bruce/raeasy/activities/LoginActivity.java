package com.bruce.raeasy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bruce.raeasy.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private Toolbar loginToolbar;
    private Button btnLogin;
    private EditText edEmail, edPassword;
    private String email, password;
    private TextInputLayout txtEmail, txtPassword;

    //Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        initViews();

        setSupportActionBar(loginToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        btnLogin.setOnClickListener(v -> login());
    }

    private void login() {
        email = edEmail.getText().toString().trim();
        password = edPassword.getText().toString().trim();

        if (!email.isEmpty()){
            if (!password.isEmpty()){
                doLogin();
            } else {
                txtPassword.setError("Password cannot be empty");
            }
        } else {
            txtEmail.setError("Email cannot be empty");
        }
    }

    private void doLogin() {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                toHomeActivity();
            } else {
                String errMsg = Objects.requireNonNull(task.getException()).toString();
                Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(
                this, "Check your internet connection", Toast.LENGTH_SHORT
        ).show());
    }

    private void toHomeActivity() {
        finish();
        startActivity(new Intent(this, HomeActivity.class));
    }

    private void initViews() {
        loginToolbar = findViewById(R.id.login_toolbar);
        btnLogin = findViewById(R.id.btnLogin);
        edEmail = findViewById(R.id.loginEmail);
        edPassword = findViewById(R.id.loginPassword);
        txtEmail = findViewById(R.id.txtLoginEmail);
        txtPassword = findViewById(R.id.txtLoginPassword);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
