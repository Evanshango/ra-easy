package com.bruce.raeasy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bruce.raeasy.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private Toolbar loginToolbar;
    private Button btnLogin;
    private EditText edEmail, edPassword;
    private String email, password;
    private TextInputLayout txtEmail, txtPassword;
    private ProgressBar loginProgress;

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

        if (!email.isEmpty()) {
            if (!password.isEmpty()) {
                doLogin();
            } else {
                txtPassword.setError("Password cannot be empty");
            }
        } else {
            txtEmail.setError("Email cannot be empty");
        }
    }

    private void doLogin() {
        loginProgress.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                loginProgress.setVisibility(View.GONE);
                toHomeActivity();
            } else {
                String errMsg = Objects.requireNonNull(task.getException()).getMessage();
                Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show();
                loginProgress.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
            }
        }).addOnFailureListener(e -> {
            loginProgress.setVisibility(View.GONE);
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void toHomeActivity() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra("userId", user.getUid());
            startActivity(intent);
            finish();
        }
    }

    private void initViews() {
        loginToolbar = findViewById(R.id.login_toolbar);
        btnLogin = findViewById(R.id.btnLogin);
        edEmail = findViewById(R.id.loginEmail);
        edPassword = findViewById(R.id.loginPassword);
        txtEmail = findViewById(R.id.txtLoginEmail);
        txtPassword = findViewById(R.id.txtLoginPassword);
        loginProgress = findViewById(R.id.loginProgress);
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
