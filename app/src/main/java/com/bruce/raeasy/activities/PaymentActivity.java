package com.bruce.raeasy.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.bruce.raeasy.R;
import com.bruce.raeasy.models.ImageUri;
import com.bruce.raeasy.models.Item;
import com.bruce.raeasy.models.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;

import static com.bruce.raeasy.utils.Constants.USERS_REF;

public class PaymentActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Button btnMpesa, btnAirtel, btnComplete;
    private TextView txtAmount;
    private Item mItem;
    private String name, desc, tradeType, duration, price, tradeIn, ownerId, username;
    private List<ImageUri> imageUris;

    //Firebase
    private CollectionReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        usersRef = database.collection(USERS_REF);

        Intent intent = getIntent();
        mItem = intent.getParcelableExtra("item");
        if (mItem != null){
            ownerId = mItem.getOwnerId();
            duration = mItem.getDuration();
        }

        getUserName();

        initViews();

        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Payment");
    }

    private void getUserName() {
        usersRef.document(ownerId).get().addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            if (user != null){
                username = user.getFullName();
            }
        });
    }

    private void initViews() {
        mToolbar = findViewById(R.id.payment_toolbar);
        btnMpesa = findViewById(R.id.mpesaBtn);
        btnAirtel = findViewById(R.id.airtelBtn);
        btnComplete = findViewById(R.id.btnComplete);
        txtAmount = findViewById(R.id.txtAmount);
    }
}
