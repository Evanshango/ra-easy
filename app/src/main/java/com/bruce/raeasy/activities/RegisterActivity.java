package com.bruce.raeasy.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bruce.raeasy.R;
import com.bruce.raeasy.models.User;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static com.bruce.raeasy.utils.Constants.SHORT_DATE;
import static com.bruce.raeasy.utils.Constants.USERS_REF;

public class RegisterActivity extends AppCompatActivity {

    private Toolbar regToolbar;
    private Button btnContinue;
    private EditText edPhone, edEmail, edName, edInstitute, edRegNo, edPassword;
    private String phone, email, name, institute, regNo, password, createdAt;
    private TextView txtTerms;
    private TextInputLayout txtPhone, txtEmail, txtName, txtInstitute, txtRegNo, txtPassword;
    private ProgressBar regProgress;

    //Firebase
    private FirebaseAuth mAuth;
    private CollectionReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        usersRef = database.collection(USERS_REF);

        createdAt = new SimpleDateFormat(SHORT_DATE, Locale.getDefault()).format(new Date());

        initViews();

        setSupportActionBar(regToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        btnContinue.setOnClickListener(v -> register());

        txtTerms.setOnClickListener(v -> Toast.makeText(this, "Terms", Toast.LENGTH_SHORT).show());
    }

    private void register() {
        phone = edPhone.getText().toString().trim();
        email = edEmail.getText().toString().trim();
        name = edName.getText().toString().trim();
        institute = edInstitute.getText().toString().trim();
        regNo = edRegNo.getText().toString().trim();
        password = edPassword.getText().toString().trim();

        if (!phone.isEmpty()){
            if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                if (!name.isEmpty()){
                    if (!institute.isEmpty()){
                        if (!regNo.isEmpty()){
                            if (!password.isEmpty()){
                                if (password.length() >=6){
                                    doRegister();
                                } else {
                                    txtPassword.setError("Password too short");
                                }
                            } else {
                                txtPassword.setError("Password required");
                            }
                        } else {
                            txtRegNo.setError("Registration number required");
                        }
                    } else {
                        txtInstitute.setError("Institute required");
                    }
                } else {
                    txtName.setError("Full name required");
                }
            } else {
                txtEmail.setError("Valid Email required");
            }
        } else {
            txtPhone.setError("Phone number required");
        }
    }

    private void doRegister() {
        regProgress.setVisibility(View.VISIBLE);
        btnContinue.setEnabled(false);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                FirebaseUser aUser = mAuth.getCurrentUser();
                saveExtraUserInfo(aUser);
                regProgress.setVisibility(View.GONE);
            } else {
                String errMsg = Objects.requireNonNull(task.getException()).getMessage();
                Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show();
                btnContinue.setEnabled(true);
                regProgress.setVisibility(View.GONE);
            }
        }).addOnFailureListener(e -> {
            btnContinue.setEnabled(true);
            regProgress.setVisibility(View.GONE);
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void saveExtraUserInfo(FirebaseUser aUser) {
        if (aUser != null){
            User user = new User(aUser.getUid(), phone, email, name, institute, regNo, createdAt);
            usersRef.document(aUser.getUid()).set(user).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    toHomeActivity();
                } else {
                    Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Unable to Save user details", Toast.LENGTH_SHORT).show();
        }
    }

    private void toHomeActivity() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra("userId", user.getUid());
            startActivity(intent);
            finish();
        }
    }

    private void initViews() {
        regToolbar = findViewById(R.id.reg_toolbar);
        btnContinue = findViewById(R.id.btnContinue);
        txtTerms = findViewById(R.id.txtTerms);
        edPhone = findViewById(R.id.regPhone);
        edEmail = findViewById(R.id.regEmail);
        edName = findViewById(R.id.regName);
        edInstitute = findViewById(R.id.regInstitute);
        edRegNo = findViewById(R.id.regNumber);
        edPassword = findViewById(R.id.regPassword);
        txtPhone = findViewById(R.id.textPhone);
        txtEmail = findViewById(R.id.textEmail);
        txtName = findViewById(R.id.textName);
        txtInstitute = findViewById(R.id.textInstitute);
        txtRegNo = findViewById(R.id.textRegNo);
        txtPassword = findViewById(R.id.textPassword);
        regProgress = findViewById(R.id.regProgress);
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
