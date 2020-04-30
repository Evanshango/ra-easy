package com.bruce.raeasy.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bruce.raeasy.R;
import com.bruce.raeasy.activities.HomeActivity;
import com.bruce.raeasy.activities.ItemViewActivity;
import com.bruce.raeasy.adapters.ItemAdapter;
import com.bruce.raeasy.models.Item;
import com.bruce.raeasy.utils.BarterEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.bruce.raeasy.utils.Constants.ITEMS_REF;

public class BarterTradeFragment extends Fragment implements ItemAdapter.ItemInteraction {

    private RecyclerView barterRecycler;
    private ItemAdapter mItemAdapter;
    private ProgressBar barterLoader;
    private String userId;
    private TextView noItems;
    private CollectionReference itemsRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_barter_trade, container, false);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        itemsRef = database.collection(ITEMS_REF);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }

        initViews(view);

        mItemAdapter = new ItemAdapter(requireContext(), this);
        barterRecycler.setAdapter(mItemAdapter);

        return view;
    }

    private void populateRecycler(List<Item> items) {
        List<Item> itemList = new ArrayList<>();
        if (items.size() > 0) {
            barterLoader.setVisibility(View.GONE);
            noItems.setVisibility(View.GONE);
            itemList.clear();
            itemList.addAll(items);
            loadItems(itemList);
        } else {
            itemList.clear();
            loadItems(itemList);
            barterLoader.setVisibility(View.GONE);
            noItems.setVisibility(View.VISIBLE);
        }
    }

    private void loadItems(List<Item> itemList) {
        GridLayoutManager manager = new GridLayoutManager(requireContext(), 2);
        barterRecycler.setHasFixedSize(true);
        barterRecycler.setLayoutManager(manager);
        mItemAdapter.setData(itemList, userId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void observeBarterItems(BarterEvent event) {
        barterLoader.setVisibility(View.VISIBLE);
        populateRecycler(event.getItems());
    }

    private void initViews(View view) {
        barterRecycler = view.findViewById(R.id.barterRecycler);
        barterLoader = view.findViewById(R.id.barterLoader);
        noItems = view.findViewById(R.id.text_no_barter_items);
    }

    @Override
    public void itemClicked(View view, Item item) {
        switch (view.getId()) {
            case R.id.itemCard:
                toItemViewActivity(item);
                break;
            case R.id.favImg:
                favoriteItem(item);
                break;
        }
    }

    private void favoriteItem(Item item) {
        ((HomeActivity) requireActivity()).addOrRemoveFavorite(item, userId, itemsRef);
    }

    private void toItemViewActivity(Item item) {
        Intent intent = new Intent(requireContext(), ItemViewActivity.class);
        intent.putExtra("item", item);
        intent.putExtra("userId", userId);
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
