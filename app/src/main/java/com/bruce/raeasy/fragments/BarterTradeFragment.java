package com.bruce.raeasy.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bruce.raeasy.R;
import com.bruce.raeasy.activities.HomeActivity;
import com.bruce.raeasy.activities.ItemViewActivity;
import com.bruce.raeasy.adapters.ItemAdapter;
import com.bruce.raeasy.models.Favorite;
import com.bruce.raeasy.models.Item;
import com.bruce.raeasy.utils.FavoriteEvent;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.bruce.raeasy.utils.Constants.DATE;
import static com.bruce.raeasy.utils.Constants.FAVORITES;
import static com.bruce.raeasy.utils.Constants.ITEMS_REF;
import static com.bruce.raeasy.utils.Constants.SHORT_DATE;
import static com.bruce.raeasy.utils.Constants.TRADE_TYPE;

public class BarterTradeFragment extends Fragment implements ItemAdapter.ItemInteraction {

    private static final String tradeType = "barter";
    private RecyclerView barterRecycler;
    private ItemAdapter mItemAdapter;
    private ProgressBar barterLoader;
    private String userId, createdAt;
    private List<Favorite> mFavorites = new ArrayList<>();
    private List<Item> mItems = new ArrayList<>();

    //Firebase
    private CollectionReference itemsRef, favRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_barter_trade, container, false);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        itemsRef = database.collection(ITEMS_REF);
        favRef = database.collection(FAVORITES);
        createdAt = new SimpleDateFormat(SHORT_DATE, Locale.getDefault()).format(new Date());

        initViews(view);

        fetchItemTypeBarter();

        mItemAdapter = new ItemAdapter(requireContext(), this);
        barterRecycler.setAdapter(mItemAdapter);

        return view;
    }

    private void fetchItemTypeBarter() {
        barterLoader.setVisibility(View.VISIBLE);
        Query query = itemsRef.whereEqualTo(TRADE_TYPE, tradeType)
                .orderBy(DATE, Query.Direction.DESCENDING);
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            mItems.addAll(queryDocumentSnapshots.toObjects(Item.class));
            populateRecycler(mItems, mFavorites);
        }).addOnFailureListener(e -> {
            barterLoader.setVisibility(View.GONE);
            Snackbar.make(barterRecycler, "Unable to fetch items", Snackbar.LENGTH_SHORT).show();
        });
    }

    private void populateRecycler(List<Item> items, List<Favorite> favorites) {
        if (items != null) {
            barterLoader.setVisibility(View.GONE);
            GridLayoutManager manager = new GridLayoutManager(requireContext(), 2);
            barterRecycler.setHasFixedSize(true);
            barterRecycler.setLayoutManager(manager);

            mItemAdapter.setData(items, favorites);
            barterRecycler.setAdapter(mItemAdapter);
        } else {
            barterLoader.setVisibility(View.GONE);
            Snackbar.make(barterRecycler, "No Items found", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void observeFavorites(FavoriteEvent event) {
        mFavorites.clear();
        mFavorites.addAll(event.getFavorites());
        populateRecycler(mItems, mFavorites);
    }

    private void initViews(View view) {
        barterRecycler = view.findViewById(R.id.barterRecycler);
        barterLoader = view.findViewById(R.id.barterLoader);
    }

    @Override
    public void itemClicked(View view, Item item, ImageView favImg) {
        switch (view.getId()) {
            case R.id.itemCard:
                toItemViewActivity(item);
                break;
            case R.id.imgFavorite:
                favoriteItem(item, favImg);
                break;
        }
    }

    private void favoriteItem(Item item, ImageView favImg) {
        String favId = favRef.document().getId();
        Favorite favorite = new Favorite(favId, item.getItemId(), createdAt, userId);
        ((HomeActivity) requireActivity()).addFavorite(favImg, favorite, favRef);
    }

    private void toItemViewActivity(Item item) {
        Intent intent = new Intent(requireContext(), ItemViewActivity.class);
        intent.putExtra("item", item);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
