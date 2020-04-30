package com.bruce.raeasy.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bruce.raeasy.R;
import com.bruce.raeasy.dialogs.RatingDialog;
import com.bruce.raeasy.models.User;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.bruce.raeasy.utils.Constants.ABOUT_US;
import static com.bruce.raeasy.utils.Constants.USERS_REF;

public class AccountActivity extends AppCompatActivity implements RatingDialog.RatingListener {

    private static final String TAG = "AccountActivity";
    private Toolbar mToolbar;
    private TextView txtUsername, txtEmail, myAds, accountSettings, aboutUs, contactUs, shareApp;
    private TextView rateUs;
    private String userId;
    private Button btnLogout;
    private CircleImageView profileImg;

    //Firebase
    private CollectionReference usersRef, aboutUsRef;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        usersRef = database.collection(USERS_REF);
        aboutUsRef = database.collection(ABOUT_US);
        mUser = mAuth.getCurrentUser();

        initViews();

        if (mUser != null) {
            userId = mUser.getUid();
            btnLogout.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "onCreate: Not logged in");
        }

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Account");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        btnLogout.setOnClickListener(v -> logoutUser());

        myAds.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyAdsActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        accountSettings.setOnClickListener(v -> toAccountSettings());

        aboutUs.setOnClickListener(v -> openAboutUs());

        contactUs.setOnClickListener(v -> openContactUs());

        shareApp.setOnClickListener(v -> openShareOptions());

        rateUs.setOnClickListener(v -> openRatingDialog());
    }

    private void openRatingDialog() {
        RatingDialog dialog = new RatingDialog();
        Bundle args = new Bundle();
        args.putString("userId", userId);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "ratingDialog");
    }

    private void openContactUs() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        String[] recipients = {"info@raeasy.ac.ke"};
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Problem with RaEasy for Android");
        intent.putExtra(Intent.EXTRA_TEXT, deviceDetails());
        intent.setType("text/html");
        intent.setPackage("com.google.android.gm");
        startActivity(Intent.createChooser(intent, "Send mail"));
    }

    private String deviceDetails() {
        return Build.MANUFACTURER + " " + Build.MODEL + " Android Version " + Build.VERSION.RELEASE;
    }

    private void openAboutUs() {
        aboutUsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null) {
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                    String about = snapshot.getString("about");
                    showAlertDialog(about);
                }
            } else {
                Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(
                this, "An error occurred", Toast.LENGTH_SHORT
        ).show());
    }

    private void showAlertDialog(String about) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ABOUT US").setMessage(about)
                .setPositiveButton("Ok", ((dialog, which) -> dialog.dismiss()));
        builder.create().show();
    }

    private void openShareOptions() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String app_url = "https://google.com";
        intent.putExtra(Intent.EXTRA_TEXT, app_url);
        startActivity(Intent.createChooser(intent, "Share via:"));
    }

    private void toAccountSettings() {
        Intent intent = new Intent(this, AccSettingsActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }

    private void logoutUser() {
        if (mUser != null) {
            mAuth.signOut();
            toWelcomeScreen();
        }
    }

    private void toWelcomeScreen() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void displayUserDetails(User user) {
        if (user != null) {
            txtUsername.setText(user.getFullName());
            txtEmail.setText(user.getEmail());
            Glide.with(this).load(user.getImageUrl()).into(profileImg);
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
        myAds = findViewById(R.id.myAdds);
        accountSettings = findViewById(R.id.accountSettings);
        aboutUs = findViewById(R.id.aboutUs);
        contactUs = findViewById(R.id.contactUs);
        shareApp = findViewById(R.id.shareApp);
        rateUs = findViewById(R.id.rateUs);
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

    @Override
    public void ratingMade(String message, AlertDialog dialog) {
        dialog.dismiss();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        usersRef.document(userId).addSnapshotListener(this, (documentSnapshot, e) -> {
            if (e != null){
                Toast.makeText(this, "An unexpected error occurred", Toast.LENGTH_SHORT).show();
                return;
            }
            if (documentSnapshot != null){
                User user = documentSnapshot.toObject(User.class);
                displayUserDetails(user);
            }
        });
    }
}
