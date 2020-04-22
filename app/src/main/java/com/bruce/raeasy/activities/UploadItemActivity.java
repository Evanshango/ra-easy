package com.bruce.raeasy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.bruce.raeasy.R;

public class UploadItemActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageView expand;
    private LinearLayout expandOptions;
    private TextView camera, gallery;
    private EditText itemName, itemDesc;
    private ViewPager imgViewPager;
    private RadioGroup tradeType;
    private RadioButton rbSale, rbBarter;
    private Button btnProceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_item);

        initViews();

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Upload Item");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        expand.setOnClickListener(v -> {
            if (expandOptions.getVisibility() == View.GONE) {
                expandOptions.setVisibility(View.VISIBLE);
            } else {
                expandOptions.setVisibility(View.GONE);
            }
        });

        btnProceed.setOnClickListener(v -> showPaymentDialog());

        camera.setOnClickListener(v -> openCamera());
        gallery.setOnClickListener(v -> openGallery());
    }

    private void showPaymentDialog() {
        startActivity(new Intent(this, PaymentActivity.class));
    }

    private void openGallery() {
        Toast.makeText(this, "Opening Gallery", Toast.LENGTH_SHORT).show();
    }

    private void openCamera() {
        Toast.makeText(this, "Opening Camera", Toast.LENGTH_SHORT).show();
    }

    private void initViews() {
        mToolbar = findViewById(R.id.upload_toolbar);
        expand = findViewById(R.id.expand);
        expandOptions = findViewById(R.id.optionsLayout);
        camera = findViewById(R.id.camera);
        gallery = findViewById(R.id.gallery);
        imgViewPager = findViewById(R.id.imagesViewPager);
        itemName = findViewById(R.id.itemName);
        itemDesc = findViewById(R.id.itemDesc);
        tradeType = findViewById(R.id.tradeTypeRg);
        rbSale = findViewById(R.id.rbSale);
        rbBarter = findViewById(R.id.rbBarter);
        btnProceed = findViewById(R.id.btn_proceed);
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
