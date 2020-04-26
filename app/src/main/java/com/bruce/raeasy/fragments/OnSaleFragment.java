package com.bruce.raeasy.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bruce.raeasy.R;
import com.bruce.raeasy.activities.ItemViewActivity;
import com.bruce.raeasy.adapters.ItemAdapter;
import com.bruce.raeasy.models.Item;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.bruce.raeasy.utils.Constants.DATE;
import static com.bruce.raeasy.utils.Constants.ITEMS_REF;
import static com.bruce.raeasy.utils.Constants.TRADE_TYPE;

public class OnSaleFragment extends Fragment implements ItemAdapter.ItemInteraction {

    private static final String tradeType = "sell";
    private RecyclerView onSaleRecycler;
    private ItemAdapter mItemAdapter;
    private ProgressBar onSaleLoader;

    //Firebase
    private CollectionReference itemsRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_on_sale, container, false);

        mItemAdapter = new ItemAdapter(requireContext(), this);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        itemsRef = database.collection(ITEMS_REF);

        initViews(view);

        fetchItemTypeOnSale();

        return view;
    }

    private void fetchItemTypeOnSale() {
        onSaleLoader.setVisibility(View.VISIBLE);
        Query query = itemsRef.whereEqualTo(TRADE_TYPE, tradeType)
                .orderBy(DATE, Query.Direction.DESCENDING);
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Item> itemList = new ArrayList<>();
            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                itemList.add(snapshot.toObject(Item.class));
            }
            populateRecycler(itemList);
        }).addOnFailureListener(e -> {
            onSaleLoader.setVisibility(View.GONE);
            Snackbar.make(onSaleRecycler, "Unable to fetch items", Snackbar.LENGTH_SHORT).show();
        });
    }

    private void populateRecycler(List<Item> itemList) {
        onSaleLoader.setVisibility(View.GONE);
        if (itemList != null){
            GridLayoutManager manager = new GridLayoutManager(requireContext(), 2);
            onSaleRecycler.setHasFixedSize(true);
            onSaleRecycler.setLayoutManager(manager);

            mItemAdapter.setData(itemList);
            onSaleRecycler.setAdapter(mItemAdapter);
        } else {
            Snackbar.make(onSaleRecycler, "No Items found", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void initViews(View view) {
        onSaleRecycler = view.findViewById(R.id.onSaleRecycler);
        onSaleLoader = view.findViewById(R.id.onSaleLoader);
    }

    @Override
    public void itemClicked(View view, Item item) {
        switch (view.getId()){
            case R.id.itemCard:
                toItemViewActivity(item);
                break;
            case R.id.imgFavorite:
                favoriteItem(item);
                break;
        }
    }

    private void favoriteItem(Item item) {
        Toast.makeText(requireContext(), item.getName() + " added to favorites", Toast.LENGTH_SHORT).show();
    }

    private void toItemViewActivity(Item item) {
        Intent intent = new Intent(requireContext(), ItemViewActivity.class);
        intent.putExtra("item", item);
        startActivity(intent);
    }
}
