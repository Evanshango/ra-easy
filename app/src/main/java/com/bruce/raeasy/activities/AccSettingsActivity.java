package com.bruce.raeasy.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bruce.raeasy.R;
import com.bruce.raeasy.models.User;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.bruce.raeasy.utils.Constants.GALLERY_PICK;
import static com.bruce.raeasy.utils.Constants.PROFILE;
import static com.bruce.raeasy.utils.Constants.USERS_REF;

public class AccSettingsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private String userId;
    private CollectionReference usersRef;
    private EditText fullName, email, institute, phone, regDate, regNo;
    private String sFullName, sEmail, sInstitute, sPhone, sImage;
    private Button btnUpdate, btnEnableEditing;
    private CircleImageView profileImg;
    private ProgressBar accSettingsLoader;
    private Uri mImageUri;

    //Firebase
    private StorageReference mStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc_settings);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference(PROFILE);
        usersRef = database.collection(USERS_REF);

        initViews();

        Intent intent = getIntent();
        if (intent != null) {
            userId = intent.getStringExtra("userId");
        }

        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        getUserDetails();

        btnUpdate.setOnClickListener(v -> updateUserDetails());

        btnEnableEditing.setOnClickListener(v -> enableViews(true, View.VISIBLE));

        profileImg.setOnClickListener(v -> {
            if (profileImg.isEnabled()) {
                openGallery();
            }
        });

        enableViews(false, View.GONE);
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select an Option"), GALLERY_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null) {
            mImageUri = data.getData();
            profileImg.setImageURI(mImageUri);
        } else {
            profileImg.setImageURI(null);
        }
    }


    private void enableViews(boolean b, int visible) {
        btnUpdate.setVisibility(visible);

        fullName.setEnabled(b);
        email.setEnabled(b);
        institute.setEnabled(b);
        phone.setEnabled(b);
        profileImg.setEnabled(b);
    }

    private void updateUserDetails() {
        sFullName = fullName.getText().toString().trim();
        sEmail = email.getText().toString().trim();
        sInstitute = institute.getText().toString().trim();
        sPhone = phone.getText().toString().trim();

        if (mImageUri != null) {
            uploadWithNewImageUrl();
        } else {
            updateDetails(sImage);
        }
    }

    private void updateDetails(String imageUrl) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("fullName", sFullName);
        userMap.put("email", sEmail);
        userMap.put("institute", sInstitute);
        userMap.put("phone", sPhone);
        userMap.put("imageUrl", imageUrl);

        usersRef.document(userId).set(userMap, SetOptions.merge()).addOnSuccessListener(aVoid -> {
            accSettingsLoader.setVisibility(View.GONE);
            enableViews(false, View.GONE);
            Toast.makeText(this, "Details Updated Successfully", Toast.LENGTH_SHORT).show();
        });
    }

    private void uploadWithNewImageUrl() {
        accSettingsLoader.setVisibility(View.VISIBLE);
        StorageReference fileRef = mStorageReference.child(userId)
                .child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));
        fileRef.putFile(mImageUri).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> updateDetails(uri.toString()));
            } else {
                accSettingsLoader.setVisibility(View.GONE);
                Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            accSettingsLoader.setVisibility(View.GONE);
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        });
    }

    private String getFileExtension(Uri imageUri) {
        ContentResolver resolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(resolver.getType(imageUri));
    }

    private void getUserDetails() {
        accSettingsLoader.setVisibility(View.VISIBLE);
        usersRef.document(userId).get().addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            if (user != null) {
                accSettingsLoader.setVisibility(View.GONE);
                fullName.setText(user.getFullName());
                email.setText(user.getEmail());
                phone.setText(user.getPhone());
                institute.setText(user.getInstitute());
                regDate.setText(user.getRegDate());
                regNo.setText(user.getRegNo());
                sImage = user.getImageUrl();

                Glide.with(this).load(sImage).into(profileImg);

            } else {
                accSettingsLoader.setVisibility(View.GONE);
                Toast.makeText(this, "Failed to fetch records", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        mToolbar = findViewById(R.id.acc_settings_toolbar);
        fullName = findViewById(R.id.full_name);
        email = findViewById(R.id.email);
        institute = findViewById(R.id.institute);
        phone = findViewById(R.id.phone);
        regDate = findViewById(R.id.regDate);
        regNo = findViewById(R.id.regNo);
        btnUpdate = findViewById(R.id.btnUpdate);
        profileImg = findViewById(R.id.acc_prof_image);
        accSettingsLoader = findViewById(R.id.acc_setting_loader);
        btnEnableEditing = findViewById(R.id.btnEnableEditing);
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
