package com.bruce.raeasy.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
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
import com.bruce.raeasy.models.Favorite;
import com.bruce.raeasy.models.ImageUrl;
import com.bruce.raeasy.models.Item;
import com.bruce.raeasy.models.User;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.bruce.raeasy.utils.Constants.FAVORITES;
import static com.bruce.raeasy.utils.Constants.SHORT_DATE;
import static com.bruce.raeasy.utils.Constants.USERS_REF;

public class ItemViewActivity extends BaseActivity {

    private Toolbar mToolbar;
    private TabLayout itemTabLayout;
    private ViewPager itemViewPager;
    private String name, description, price, tradeIn, datePosted, traderId, userPhone, createdAt;
    private String itemId, userId;
    private List<ImageUrl> imageUrls;
    private TextView itemViewName, itemViewDesc, itemViewPrice, itemViewTradeIn, itemViewDatePosted;
    private ImageView imgFav;
    private TextView traderName, traderPhone;
    private CircleImageView downloadImg;
    private ConstraintLayout traderProfile;

    //Firebase
    private CollectionReference usersRef, favRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        usersRef = database.collection(USERS_REF);
        favRef = database.collection(FAVORITES);
        createdAt = new SimpleDateFormat(SHORT_DATE, Locale.getDefault()).format(new Date());

        Intent intent = getIntent();
        Item item = intent.getParcelableExtra("item");
        if (item != null) {
            itemId = item.getItemId();
            name = item.getName();
            description = item.getDesc();
            price = item.getPrice();
            tradeIn = item.getTradeIn();
            datePosted = item.getDate();
            traderId = item.getOwnerId();
            imageUrls = item.getImageUrls();
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

        downloadImg.setOnClickListener(v -> downloadItemImage());

        traderProfile.setOnClickListener(v -> openDialer());
    }

    private void checkIfItemIsFavorite() {
        favRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
           if (!queryDocumentSnapshots.isEmpty()){
               List<Favorite> favorites = queryDocumentSnapshots.toObjects(Favorite.class);
               for (Favorite favorite : favorites){
                   checkItemMatch(favorite);
               }
           }
        });
    }

    private void checkItemMatch(Favorite favorite) {
        if (itemId.equals(favorite.getItemId())){
            imgFav.setImageResource(R.drawable.ic_favorite_filled);
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
        usersRef.document(traderId).get().addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            if (user != null) {
                userPhone = user.getPhone();
                traderName.setText(user.getFullName());
                traderPhone.setText(userPhone);
                userId = user.getId();
            } else {
                traderName.setText(R.string.anonymous);
                traderPhone.setText(R.string.undefined);
            }
        });
    }

    private void downloadItemImage() {
        Toast.makeText(this, "Downloading image", Toast.LENGTH_SHORT).show();
    }

    private void addToFavorite() {
        String favId = favRef.document().getId();
        Favorite favorite = new Favorite(favId, itemId, createdAt, userId);
        addFavorite(imgFav, favorite, favRef);
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
        downloadImg = findViewById(R.id.downloadImg);
        traderName = findViewById(R.id.traderName);
        traderPhone = findViewById(R.id.traderPhone);
        traderProfile = findViewById(R.id.traderProfile);
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
