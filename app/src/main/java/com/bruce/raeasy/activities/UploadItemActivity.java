package com.bruce.raeasy.activities;

import android.content.Intent;
import android.net.Uri;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bruce.raeasy.R;
import com.bruce.raeasy.adapters.ImagesUriPagerAdapter;
import com.bruce.raeasy.fragments.ImageItemUriFragment;
import com.bruce.raeasy.models.ImageUri;
import com.bruce.raeasy.models.Item;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import static com.bruce.raeasy.utils.Constants.GALLERY_PICK;

public class UploadItemActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageView expand;
    private LinearLayout expandOptions;
    private TextView camera, gallery;
    private EditText itemName, itemDesc, itemPrice, tradeInItem;
    private TabLayout mTabLayout;
    private ViewPager imgViewPager;
    private RadioGroup rgTradeType, rgAdvertDur;
    private RadioButton rbSell, rbBarter, rbTwo, rbFour, rbEight;
    private Button btnProceed;
    private List<ImageUri> mImageUris = new ArrayList<>();
    private String name, desc, tradeType, price, tradeIn, duration, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_item);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

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

        listenToTradeTypeSelection();

        listenToAdvertDurChange();
    }

    private void listenToAdvertDurChange() {
        rgAdvertDur.setOnCheckedChangeListener(((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rbTwo:
                    duration = rbTwo.getText().toString();
                    break;
                case R.id.rbFour:
                    duration = rbFour.getText().toString();
                    break;
                case R.id.rbEight:
                    duration = rbEight.getText().toString();
            }
        }));
    }

    private void listenToTradeTypeSelection() {
        rgTradeType.setOnCheckedChangeListener(((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rbSale:
                    tradeType = rbSell.getText().toString();
                    break;
                case R.id.rbBarter:
                    tradeType = rbBarter.getText().toString();
                    break;
            }
        }));
    }

    private void showPaymentDialog() {
        name = itemName.getText().toString().trim();
        desc = itemDesc.getText().toString().trim();
        price = itemPrice.getText().toString().trim();
        tradeIn = tradeInItem.getText().toString().trim();
        if (mImageUris.size() > 0) {
            if (!name.isEmpty()) {
                if (!desc.isEmpty()) {
                    if (!tradeType.isEmpty()) {
                        if (!duration.isEmpty()) {
                            if (!price.isEmpty()) {
                                proceedToPayment();
                            } else {
                                itemPrice.setError("Item Price required");
                                itemPrice.requestFocus();
                            }
                        } else {
                            Toast.makeText(this, "Duration required", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Trade type required", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    itemDesc.setError("Item description required");
                    itemDesc.requestFocus();
                }
            } else {
                itemName.setError("Item name required");
                itemName.requestFocus();
            }
        } else {
            Toast.makeText(this, "No Images selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void proceedToPayment() {
        Item item = new Item("", name, desc, tradeType, duration, price, tradeIn, "",
                "", userId, mImageUris);
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra("item", item);
        startActivity(intent);
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GALLERY_PICK);
    }

    private void openCamera() {
        Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                int totalImages = data.getClipData().getItemCount();
                for (int i = 0; i < totalImages; i++) {
                    Uri uri = data.getClipData().getItemAt(i).getUri();
                    ImageUri imageUri = new ImageUri(uri);
                    mImageUris.add(imageUri);
                }
                expandOptions.setVisibility(View.GONE);
                loadSelectedImages(mImageUris);
            } else if (data.getData() != null) {
                Toast.makeText(this, "Please select at least two images", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No images selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadSelectedImages(List<ImageUri> imageUris) {
        ArrayList<Fragment> fragments = new ArrayList<>();
        for (ImageUri imageUri : imageUris) {
            ImageItemUriFragment fragment = ImageItemUriFragment.getInstance(imageUri);
            fragments.add(fragment);
        }
        ImagesUriPagerAdapter adapter = new ImagesUriPagerAdapter(getSupportFragmentManager(),
                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, fragments);
        imgViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(imgViewPager, true);
    }

    private void initViews() {
        mToolbar = findViewById(R.id.upload_toolbar);
        mTabLayout = findViewById(R.id.tabIndicators);
        expand = findViewById(R.id.expand);
        expandOptions = findViewById(R.id.optionsLayout);
        camera = findViewById(R.id.camera);
        gallery = findViewById(R.id.gallery);
        imgViewPager = findViewById(R.id.imagesViewPager);
        itemName = findViewById(R.id.itemName);
        itemDesc = findViewById(R.id.itemDesc);
        rgTradeType = findViewById(R.id.tradeTypeRg);
        rbSell = findViewById(R.id.rbSale);
        rbBarter = findViewById(R.id.rbBarter);
        btnProceed = findViewById(R.id.btn_proceed);
        itemPrice = findViewById(R.id.itemPrice);
        tradeInItem = findViewById(R.id.tradeInItem);
        rgAdvertDur = findViewById(R.id.advDuration);
        rbTwo = findViewById(R.id.rbTwo);
        rbFour = findViewById(R.id.rbFour);
        rbEight = findViewById(R.id.rbEight);
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
