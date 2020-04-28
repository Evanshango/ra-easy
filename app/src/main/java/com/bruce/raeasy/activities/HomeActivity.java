package com.bruce.raeasy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bruce.raeasy.R;
import com.bruce.raeasy.adapters.HomePagerAdapter;
import com.bruce.raeasy.fragments.BarterTradeFragment;
import com.bruce.raeasy.fragments.OnSaleFragment;
import com.bruce.raeasy.models.Favorite;
import com.bruce.raeasy.utils.FavoriteEvent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.bruce.raeasy.utils.Constants.FAVORITES;

public class HomeActivity extends BaseActivity {

    private static final String TAG = "HomeActivity";
    private ViewPager homeViewPager;
    private TabLayout homeTabs;
    private Toolbar mToolbar;
    private FloatingActionButton uploadItem;
    private String userId;

    //Firebase
    private CollectionReference favRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        favRef = database.collection(FAVORITES);
        if (user != null){
            userId = user.getUid();
        } else {
            toWelcomeActivity();
        }

        initViews();

        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Home");

        setUpViewPager();

        uploadItem.setOnClickListener(v -> {
            Intent intent = new Intent(this, UploadItemActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        observerFavChanges();
    }

    private void observerFavChanges() {
        favRef.addSnapshotListener(this, (queryDocumentSnapshots, e) -> {
           if (e != null){
               Toast.makeText(this, "Fetching favorites error", Toast.LENGTH_SHORT).show();
               return;
           }
           if (queryDocumentSnapshots != null){
               List<Favorite> favorites = new ArrayList<>();
               favorites.clear();
               favorites.addAll(queryDocumentSnapshots.toObjects(Favorite.class));
               emitFavorites(favorites);
           }
        });
    }

    private void emitFavorites(List<Favorite> favorites) {
        FavoriteEvent event = new FavoriteEvent();
        event.setFavorites(favorites);
        EventBus.getDefault().post(event);
        Log.d(TAG, "emitFavorites: " + favorites.size());
    }

    private void toWelcomeActivity() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void setUpViewPager() {
        HomePagerAdapter pagerAdapter = new HomePagerAdapter(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        pagerAdapter.addFragment(new BarterTradeFragment(), "Barter Trade");
        pagerAdapter.addFragment(new OnSaleFragment(), "On Sale");
        homeViewPager.setAdapter(pagerAdapter);
        homeTabs.setupWithViewPager(homeViewPager);
    }

    private void initViews() {
        homeViewPager = findViewById(R.id.home_view_pager);
        homeTabs = findViewById(R.id.tab_layout);
        mToolbar = findViewById(R.id.home_toolbar);
        uploadItem = findViewById(R.id.fabUploadItem);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                toSettings();
                return true;
            case R.id.action_account:
                toAccount();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void toAccount() {
        Intent intent = new Intent(this, AccountActivity.class);
        startActivity(intent);
    }

    private void toSettings() {
        Toast.makeText(this, "To Settings", Toast.LENGTH_SHORT).show();
    }
}
