package com.bruce.raeasy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bruce.raeasy.R;
import com.bruce.raeasy.adapters.HomePagerAdapter;
import com.bruce.raeasy.fragments.BarterTradeFragment;
import com.bruce.raeasy.fragments.OnSaleFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private ViewPager homeViewPager;
    private TabLayout homeTabs;
    private Toolbar mToolbar;
    private FloatingActionButton uploadItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();

        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Home");

        setUpViewPager();

        uploadItem.setOnClickListener(v -> startActivity(new Intent(this, UploadItemActivity.class)));
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
