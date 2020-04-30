package com.bruce.raeasy.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bruce.raeasy.R;
import com.bruce.raeasy.adapters.MyItemsAdapter;
import com.bruce.raeasy.models.Item;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.bruce.raeasy.utils.Constants.DATE;
import static com.bruce.raeasy.utils.Constants.ITEMS_REF;
import static com.bruce.raeasy.utils.Constants.OWNER_ID;

public class MyItemsActivity extends AppCompatActivity implements MyItemsAdapter.MyItemInteraction {

    private RecyclerView myAdsRecycler;
    private MyItemsAdapter mAdapter;
    private Toolbar mToolbar;
    private TextView noItem;
    private String userId;
    private ProgressBar adProgressLoader;

    //Firebase
    private CollectionReference itemsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_items);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        itemsRef = database.collection(ITEMS_REF);

        Intent intent = getIntent();
        if (intent != null) {
            userId = intent.getStringExtra("userId");
        }

        initViews();

        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        mAdapter = new MyItemsAdapter(this, this);
        myAdsRecycler.setAdapter(mAdapter);
    }

    private void populateRecycler(List<Item> items) {
        List<Item> itemList = new ArrayList<>();
        if (items.size() > 0){
            itemList.clear();
            itemList.addAll(items);
            loadItems(itemList);
        } else {
            itemList.clear();
            loadItems(itemList);
            adProgressLoader.setVisibility(View.GONE);
            noItem.setVisibility(View.VISIBLE);
        }
    }

    private void loadItems(List<Item> itemList) {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        myAdsRecycler.setHasFixedSize(true);
        myAdsRecycler.setLayoutManager(manager);
        noItem.setVisibility(View.GONE);
        adProgressLoader.setVisibility(View.GONE);
        mAdapter.setData(itemList);
    }

    private void initViews() {
        myAdsRecycler = findViewById(R.id.myAdsRecycler);
        mToolbar = findViewById(R.id.my_add_toolbar);
        adProgressLoader = findViewById(R.id.adProgressLoader);
        noItem = findViewById(R.id.txtNoItem);
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

    @Override
    public void itemClicked(Item item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You are about to delete item : " + item.getName() + "\n" + "" +
                "remember this action is irreversible. Click Proceed to continue with action, " +
                "otherwise Cancel")
                .setPositiveButton("Proceed", ((dialog, which) -> deleteItem(dialog, item)))
                .setNegativeButton("Cancel", ((dialog, which) -> dialog.dismiss()));
        builder.create().show();
    }

    private void deleteItem(DialogInterface dialog, Item item) {
        itemsRef.document(item.getItemId()).delete().addOnSuccessListener(aVoid -> {
            dialog.dismiss();
            mAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adProgressLoader.setVisibility(View.VISIBLE);
        Query q = itemsRef.whereEqualTo(OWNER_ID, userId).orderBy(DATE, Query.Direction.DESCENDING);
        q.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Toast.makeText(this, "An unexpected error occurred", Toast.LENGTH_SHORT).show();
                return;
            }
            if (queryDocumentSnapshots != null) {
                List<Item> items = queryDocumentSnapshots.toObjects(Item.class);
                populateRecycler(items);
            }
        });
    }
}
