package com.bruce.raeasy.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bruce.raeasy.R;
import com.bruce.raeasy.adapters.ImagesUrlPagerAdapter;
import com.bruce.raeasy.fragments.ImageItemUrlFragment;
import com.bruce.raeasy.models.ImageUrl;
import com.bruce.raeasy.models.Item;
import com.bruce.raeasy.models.User;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.bruce.raeasy.utils.Constants.ITEMS_REF;
import static com.bruce.raeasy.utils.Constants.USERS_REF;

public class ItemViewActivity extends BaseActivity {

    private Toolbar mToolbar;
    private TabLayout itemTabLayout;
    private ViewPager itemViewPager;
    private String name, description, price, tradeIn, datePosted, traderId, userPhone;
    private String itemId, userId;
    private List<ImageUrl> imageUrls;
    private TextView itemViewName, itemViewDesc, itemViewPrice, itemViewTradeIn, itemViewDatePosted;
    private TextView memberSince;
    private ImageView imgFav;
    private TextView traderName, traderPhone;
    private CircleImageView traderProfileImg;
    private ConstraintLayout traderProfile;
    private boolean isFavorite;
    private ProgressBar favProgress, traderInfoLoader;

    //Firebase
    private CollectionReference usersRef, itemsRef;
    private Item mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        usersRef = database.collection(USERS_REF);
        itemsRef = database.collection(ITEMS_REF);

        Intent intent = getIntent();
        mItem = intent.getParcelableExtra("item");
        userId = intent.getStringExtra("userId");
        if (mItem != null) {
            itemId = mItem.getItemId();
            name = mItem.getName();
            description = mItem.getDesc();
            price = mItem.getPrice();
            tradeIn = mItem.getTradeIn();
            datePosted = mItem.getDate();
            traderId = mItem.getOwnerId();
            imageUrls = mItem.getImageUrls();
        }

        initViews();

        mToolbar.setTitle(name);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        loadItemImages();

        checkIfItemIsFavorite();

        populateItemInfo();

        fetchTraderDetails();

        imgFav.setOnClickListener(v -> addToFavorite());

        traderProfile.setOnClickListener(v -> openDialer());
    }

    private void checkIfItemIsFavorite() {
        favProgress.setVisibility(View.VISIBLE);
        itemsRef.document(itemId).get().addOnSuccessListener(documentSnapshot -> {
            Item item = documentSnapshot.toObject(Item.class);
            assert item != null;
            performCheck(item);
        });
    }

    private void performCheck(Item item) {
        List<String> userIds = item.getUserIds();
        if (userIds.size() > 0) {
            for (String id : userIds) {
                if (userId.equals(id)) {
                    favProgress.setVisibility(View.GONE);
                    isFavorite = true;
                    imgFav.setImageResource(R.drawable.ic_favorite_filled);
                } else {
                    favProgress.setVisibility(View.GONE);
                    isFavorite = false;
                    imgFav.setImageResource(R.drawable.ic_favorite_border);
                }
            }
        } else {
            favProgress.setVisibility(View.GONE);
            isFavorite = false;
            imgFav.setImageResource(R.drawable.ic_favorite_border);
        }
    }

    private void openDialer() {
        if (userPhone != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Your are about to make a phone call to: " + userPhone)
                    .setPositiveButton("Proceed", ((dialog, which) -> dialerIntent(dialog)))
                    .setNegativeButton("Cancel", ((dialog, which) -> dialog.dismiss()));
            builder.create().show();
        } else {
            Toast.makeText(this, "Trader Contact not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void dialerIntent(DialogInterface dialog) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", userPhone, null));
        startActivity(intent);
        dialog.dismiss();
    }

    private void fetchTraderDetails() {
        traderInfoLoader.setVisibility(View.VISIBLE);
        usersRef.document(traderId).get().addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            if (user != null) {
                traderInfoLoader.setVisibility(View.GONE);
                userPhone = user.getPhone();
                traderName.setText(user.getFullName());
                traderPhone.setText(userPhone);
                memberSince.setText(String.format("Member since %s", user.getRegDate()));

                Glide.with(this).load(user.getImageUrl()).into(traderProfileImg);

            } else {
                traderInfoLoader.setVisibility(View.GONE);
                traderName.setText(R.string.anonymous);
                traderPhone.setText(R.string.undefined);
            }
        });
    }

    private void addToFavorite() {
        List<String> userIds = new ArrayList<>();
        userIds.add(userId);
        if (isFavorite){
            removeFromList(userId, itemsRef, itemId, mItem.getUserIds());
            isFavorite = false;
            imgFav.setImageResource(R.drawable.ic_favorite_border);
        } else {
            addToList(userIds, itemsRef, itemId, "Added to Favorites");
            isFavorite = true;
            imgFav.setImageResource(R.drawable.ic_favorite_filled);
        }
    }

    private void populateItemInfo() {
        itemViewName.setText(name);
        itemViewDesc.setText(description);
        itemViewPrice.setText(String.format("Ksh. %s", price));
        itemViewTradeIn.setText(tradeIn);
        itemViewDatePosted.setText(datePosted);
    }

    private void loadItemImages() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        for (ImageUrl imageUrl : imageUrls) {
            ImageItemUrlFragment fragment = ImageItemUrlFragment.getInstance(imageUrl);
            fragments.add(fragment);
        }
        ImagesUrlPagerAdapter adapter = new ImagesUrlPagerAdapter(getSupportFragmentManager(),
                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, fragments);
        itemViewPager.setAdapter(adapter);
        itemTabLayout.setupWithViewPager(itemViewPager, true);
    }

    private void initViews() {
        mToolbar = findViewById(R.id.item_view_toolbar);
        itemViewPager = findViewById(R.id.itemImgViewPager);
        itemTabLayout = findViewById(R.id.itemTabIndicators);
        itemViewName = findViewById(R.id.itemViewName);
        itemViewDesc = findViewById(R.id.itemViewDesc);
        itemViewPrice = findViewById(R.id.itemViewPrice);
        itemViewTradeIn = findViewById(R.id.itemViewTradeIn);
        itemViewDatePosted = findViewById(R.id.itemViewDatePosted);
        imgFav = findViewById(R.id.imgFav);
        traderName = findViewById(R.id.traderName);
        traderPhone = findViewById(R.id.traderPhone);
        traderProfile = findViewById(R.id.traderProfile);
        traderProfileImg = findViewById(R.id.traderProfileImg);
        memberSince = findViewById(R.id.member_since);
        favProgress = findViewById(R.id.favProgress);
        traderInfoLoader = findViewById(R.id.traderInfoLoader);
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
