package com.bruce.raeasy.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bruce.raeasy.R;
import com.bruce.raeasy.adapters.MyAdsAdapter;
import com.bruce.raeasy.models.Favorite;
import com.bruce.raeasy.models.Item;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.bruce.raeasy.utils.Constants.DATE;
import static com.bruce.raeasy.utils.Constants.FAVORITES;
import static com.bruce.raeasy.utils.Constants.ITEMS_REF;
import static com.bruce.raeasy.utils.Constants.ITEM_ID;
import static com.bruce.raeasy.utils.Constants.OWNER_ID;

public class MyAdsActivity extends AppCompatActivity implements MyAdsAdapter.AdInteraction {

    private static final String TAG = "MyAdsActivity";
    private RecyclerView myAdsRecycler;
    private MyAdsAdapter mAdapter;
    private Toolbar mToolbar;
    private String userId;
    private ProgressBar adProgressLoader;
    private List<Item> mItems = new ArrayList<>();

    //Firebase
    private CollectionReference itemsRef, favRef;
    private Query mQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ads);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        itemsRef = database.collection(ITEMS_REF);
        favRef = database.collection(FAVORITES);

        Intent intent = getIntent();
        if (intent != null) {
            userId = intent.getStringExtra("userId");
        }

        initViews();

        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        getUserPostedItems();

        mAdapter = new MyAdsAdapter(this, this);
        myAdsRecycler.setAdapter(mAdapter);
    }

    private void getUserPostedItems() {
        adProgressLoader.setVisibility(View.VISIBLE);
        mQuery = itemsRef.whereEqualTo(OWNER_ID, userId).orderBy(DATE, Query.Direction.DESCENDING);
        mQuery.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Item> items = queryDocumentSnapshots.toObjects(Item.class);
            populateRecycler(items);
        });
    }

    private void populateRecycler(List<Item> items) {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        myAdsRecycler.setHasFixedSize(true);
        myAdsRecycler.setLayoutManager(manager);

        if (items != null) {
            adProgressLoader.setVisibility(View.GONE);
            mItems.clear();
            mItems.addAll(items);
            mAdapter.setData(mItems);
        } else {
            adProgressLoader.setVisibility(View.GONE);
            Toast.makeText(this, "You haven't posted any ad yet", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {
        myAdsRecycler = findViewById(R.id.myAdsRecycler);
        mToolbar = findViewById(R.id.my_add_toolbar);
        adProgressLoader = findViewById(R.id.adProgressLoader);
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
    public void adClicked(Item item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You are about to delete item : " + item.getName() + "\n" + "" +
                "remember this action is irreversible. Click Proceed to continue with action, " +
                "otherwise Cancel")
                .setPositiveButton("Proceed", ((dialog, which) -> deleteItem(dialog, item)))
                .setNegativeButton("Cancel", ((dialog, which) -> dialog.dismiss()));
        builder.create().show();
    }

    private void deleteItem(DialogInterface dialog, Item item) {
        Query query = favRef.whereEqualTo(ITEM_ID, item.getItemId()).limit(1);
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                Favorite favorite = snapshot.toObject(Favorite.class);
                checkIfExists(favorite, dialog, item);
            }
        });
    }

    private void checkIfExists(Favorite favorite, DialogInterface dialog, Item item) {
        if (favorite != null) {
            deleteFavorite(favorite.getFavId(), item, dialog);
        } else {
            doDeleteItem(item, dialog);
        }
    }

    private void deleteFavorite(String favId, Item item, DialogInterface dialog) {
        favRef.document(favId).delete().addOnSuccessListener(aVoid -> Log.d(
                TAG, "deleteFavorite: favorites deleted")
        );
        doDeleteItem(item, dialog);
    }

    private void doDeleteItem(Item item, DialogInterface dialog) {
        itemsRef.document(item.getItemId()).delete().addOnSuccessListener(aVoid -> {
            dialog.dismiss();
            mAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mQuery.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Toast.makeText(this, "An unexpected error occurred", Toast.LENGTH_SHORT).show();
                return;
            }
            if (queryDocumentSnapshots != null) {
                mItems.clear();
                mItems.addAll(queryDocumentSnapshots.toObjects(Item.class));
            }
        });
    }
}
