package com.bruce.raeasy.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bruce.raeasy.R;
import com.bruce.raeasy.dialogs.PaymentDialog;
import com.bruce.raeasy.models.ExtraImgUri;
import com.bruce.raeasy.models.ImageUri;
import com.bruce.raeasy.models.ImageUrl;
import com.bruce.raeasy.models.Item;
import com.bruce.raeasy.models.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.bruce.raeasy.utils.Constants.EIGHT_WEEKS;
import static com.bruce.raeasy.utils.Constants.FOUR_WEEKS;
import static com.bruce.raeasy.utils.Constants.ITEMS_REF;
import static com.bruce.raeasy.utils.Constants.LONG_DATE;
import static com.bruce.raeasy.utils.Constants.SHORT_DATE;
import static com.bruce.raeasy.utils.Constants.TWO_WEEKS;
import static com.bruce.raeasy.utils.Constants.UPLOADS;
import static com.bruce.raeasy.utils.Constants.USERS_REF;

public class PaymentActivity extends AppCompatActivity implements PaymentDialog.PaymentListener {

    private Toolbar mToolbar;
    private Button btnMpesa, btnAirtel, btnComplete;
    private TextView txtAmount, txtUsername;
    private String name, desc, tradeType, duration, price, tradeIn, ownerId, username;
    private String phone, itemId, date, advAmt, transCode, createdAt;
    private List<ImageUri> imageUris;
    private ProgressBar loaderProgress;
    private int looper = 0;

    //Firebase
    private CollectionReference usersRef, itemsRef;
    private StorageReference mStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference(UPLOADS);
        usersRef = database.collection(USERS_REF);
        itemsRef = database.collection(ITEMS_REF);
        itemId = itemsRef.document().getId();

        date = new SimpleDateFormat(LONG_DATE, Locale.getDefault()).format(new Date());
        createdAt = new SimpleDateFormat(SHORT_DATE, Locale.getDefault()).format(new Date());

        Intent intent = getIntent();
        Item item = intent.getParcelableExtra("item");
        ExtraImgUri imgUri = intent.getParcelableExtra("imageUris");
        if (item != null && imgUri != null) {
            ownerId = item.getOwnerId();
            duration = item.getDuration();
            name = item.getName();
            desc = item.getDesc();
            tradeType = item.getTradeType();
            price = item.getPrice();
            tradeIn = item.getTradeIn();
            imageUris = imgUri.getImageUris();
        }

        initViews();
        getUserName();

        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Payment");

        displayAmountToPay();

        btnMpesa.setOnClickListener(v -> openPaymentDialog("mpesa"));

        btnAirtel.setOnClickListener(v -> openPaymentDialog("airtel"));

        btnComplete.setOnClickListener(v -> {
            if (transCode != null) {
                btnComplete.setEnabled(false);
                disableButtons();
                uploadItem();
            } else {
                Toast.makeText(this, "Payment not received", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void disableButtons() {
        btnMpesa.setEnabled(false);
        btnAirtel.setEnabled(false);
    }

    private void uploadItem() {
        loaderProgress.setVisibility(View.VISIBLE);
        List<String> imageUrls = new ArrayList<>();
        for (int i = 0; i < imageUris.size(); i++) {
            StorageReference fileRef = mStorageReference.child(itemId).child(
                    System.currentTimeMillis() + "." + getFileExtension(imageUris.get(i).getUri()));
            fileRef.putFile(imageUris.get(i).getUri()).addOnSuccessListener(taskSnapshot ->
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        looper++;
                        imageUrls.add(uri.toString());
                        if (looper == imageUris.size()) {
                            loadImageLinks(imageUrls);
                        }
                    }).addOnFailureListener(e -> {
                        fileRef.delete();
                        loaderProgress.setVisibility(View.GONE);
                        Toast.makeText(this, "Error " + e, Toast.LENGTH_SHORT).show();
                    }));
        }
    }

    private void loadImageLinks(List<String> imageUrls) {
        List<ImageUrl> imageUrlList = new ArrayList<>();
        for (int i = 0; i < imageUrls.size(); i++) {
            String imgUrl = imageUrls.get(i);
            ImageUrl imageUrl = new ImageUrl(imgUrl);
            imageUrlList.add(imageUrl);
        }

        Item item = new Item(
                itemId, name, desc, tradeType.toLowerCase(), duration, price, tradeIn, advAmt,
                createdAt, ownerId, transCode, imageUrlList, null
        );

        itemsRef.document(itemId).set(item).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                loaderProgress.setVisibility(View.GONE);
                toHomeActivity();
            } else {
                loaderProgress.setVisibility(View.GONE);
                Toast.makeText(this, "Check your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void openPaymentDialog(String type) {
        disableButtons();
        PaymentDialog paymentDialog = new PaymentDialog();
        Bundle args = new Bundle();
        args.putString("type", type);
        args.putString("phone", phone);
        args.putString("amount", advAmt);
        args.putString("userId", ownerId);
        args.putString("date", date);
        args.putString("itemId", itemId);
        paymentDialog.setArguments(args);
        paymentDialog.show(getSupportFragmentManager(), "paymentDialog");
    }

    private void displayAmountToPay() {
        switch (duration) {
            case "2w":
                displayAmount(TWO_WEEKS);
                break;
            case "4w":
                displayAmount(FOUR_WEEKS);
                break;
            case "8w":
                displayAmount(EIGHT_WEEKS);
                break;
        }
    }

    private void displayAmount(String amount) {
        advAmt = amount;
        txtAmount.setText(String.format("Ksh. %s", amount));
    }

    private void getUserName() {
        loaderProgress.setVisibility(View.VISIBLE);
        btnComplete.setVisibility(View.GONE);
        usersRef.document(ownerId).get().addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            if (user != null) {
                loaderProgress.setVisibility(View.GONE);
                username = user.getFullName();
                phone = user.getPhone();
                txtUsername.setText(
                        String.format(getString(R.string.payment_page_info), username, name)
                );
            } else {
                loaderProgress.setVisibility(View.GONE);
            }
        });
    }

    private void initViews() {
        mToolbar = findViewById(R.id.payment_toolbar);
        btnMpesa = findViewById(R.id.mpesaBtn);
        btnAirtel = findViewById(R.id.airtelBtn);
        btnComplete = findViewById(R.id.btnComplete);
        txtAmount = findViewById(R.id.txtAmount);
        txtUsername = findViewById(R.id.txtUsername);
        loaderProgress = findViewById(R.id.loaderProgress);
    }

    @Override
    public void paymentMade(String code, AlertDialog dialog) {
        transCode = code;
        dialog.dismiss();
        btnComplete.setVisibility(View.VISIBLE);
        disableButtons();
    }
}
