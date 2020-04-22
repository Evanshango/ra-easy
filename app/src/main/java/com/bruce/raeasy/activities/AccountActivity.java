package com.bruce.raeasy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bruce.raeasy.R;
import com.bruce.raeasy.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.bruce.raeasy.utils.Constants.USERS_REF;

public class AccountActivity extends AppCompatActivity {

    private static final String TAG = "AccountActivity";
    private Toolbar mToolbar;
    private TextView txtUsername, txtEmail;
    private String userId;
    private Button btnLogout;
    private CircleImageView profileImg;
    private ImageView addImg;

    //Firebase
    private CollectionReference usersRef;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        usersRef = database.collection(USERS_REF);
        mUser = mAuth.getCurrentUser();

        initViews();

        if (mUser != null) {
            userId = mUser.getUid();
            btnLogout.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "onCreate: Not logged in");
        }

        fetchUserDetails();

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Account");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        btnLogout.setOnClickListener(v -> logoutUser());

        profileImg.setOnClickListener(v -> openGallery());
        addImg.setOnClickListener(v -> openGallery());
    }

    private void openGallery() {
        Toast.makeText(this, "Opening Gallery", Toast.LENGTH_SHORT).show();
    }

    private void logoutUser() {
        if (mUser != null){
            mAuth.signOut();
            toWelcomeScreen();
        }
    }

    private void toWelcomeScreen() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void fetchUserDetails() {
        usersRef.document(userId).get().addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            displayUserDetails(user);
        });
    }

    private void displayUserDetails(User user) {
        if (user != null) {
            txtUsername.setText(user.getFullName());
            txtEmail.setText(user.getEmail());
        } else {
            txtUsername.setText(R.string.not_logged_in);
            txtEmail.setText(R.string.email_placeholder);
        }
    }

    private void initViews() {
        mToolbar = findViewById(R.id.account_toolbar);
        txtUsername = findViewById(R.id.txtUsername);
        txtEmail = findViewById(R.id.txtEmail);
        btnLogout = findViewById(R.id.btnLogout);
        profileImg = findViewById(R.id.profileImage);
        addImg = findViewById(R.id.addImg);
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
