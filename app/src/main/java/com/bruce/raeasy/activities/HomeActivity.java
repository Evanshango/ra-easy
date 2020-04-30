package com.bruce.raeasy.activities;

import android.content.Intent;
import android.os.Bundle;
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
import com.bruce.raeasy.models.Item;
import com.bruce.raeasy.utils.BarterEvent;
import com.bruce.raeasy.utils.SellEvent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.bruce.raeasy.utils.Constants.DATE;
import static com.bruce.raeasy.utils.Constants.ITEMS_REF;
import static com.bruce.raeasy.utils.Constants.TRADE_TYPE;

public class HomeActivity extends BaseActivity {

    private static final String tradeTypeBarter = "barter";
    private static final String tradeTypeSale = "sell";
    private ViewPager homeViewPager;
    private TabLayout homeTabs;
    private Toolbar mToolbar;
    private FloatingActionButton uploadItem;
    private String userId;

    //Firebase
    private CollectionReference itemsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        itemsRef = database.collection(ITEMS_REF);

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
        observerBarterItems();
        observerSaleItems();
    }

    private void observerSaleItems() {
        Query query = itemsRef.whereEqualTo(TRADE_TYPE, tradeTypeSale)
                .orderBy(DATE, Query.Direction.DESCENDING);
        query.addSnapshotListener(this, (queryDocumentSnapshots, e) -> {
            if (e != null){
                Toast.makeText(this, "Fetching Items error", Toast.LENGTH_SHORT).show();
                return;
            }
            if (queryDocumentSnapshots != null){
                List<Item> items = new ArrayList<>();
                items.clear();
                items.addAll(queryDocumentSnapshots.toObjects(Item.class));
                emitSaleItems(items);
            }
        });
    }

    private void emitSaleItems(List<Item> items) {
        SellEvent event = new SellEvent();
        event.setItems(items);
        EventBus.getDefault().post(event);
    }

    private void observerBarterItems() {
        Query query = itemsRef.whereEqualTo(TRADE_TYPE, tradeTypeBarter)
                .orderBy(DATE, Query.Direction.DESCENDING);
        query.addSnapshotListener(this, (queryDocumentSnapshots, e) -> {
            if (e != null){
                Toast.makeText(this, "Fetching Items error", Toast.LENGTH_SHORT).show();
                return;
            }
            if (queryDocumentSnapshots != null){
                List<Item> items = new ArrayList<>();
                items.clear();
                items.addAll(queryDocumentSnapshots.toObjects(Item.class));
                emitBarterItems(items);
            }
        });
    }

    private void emitBarterItems(List<Item> items) {
        BarterEvent event = new BarterEvent();
        event.setItems(items);
        EventBus.getDefault().post(event);
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
        if (item.getItemId() == R.id.action_settings) {
            toAccount();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toAccount() {
        Intent intent = new Intent(this, AccountActivity.class);
        startActivity(intent);
    }
}
