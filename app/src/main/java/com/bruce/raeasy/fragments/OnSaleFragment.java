package com.bruce.raeasy.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bruce.raeasy.R;
import com.bruce.raeasy.activities.HomeActivity;
import com.bruce.raeasy.activities.ItemViewActivity;
import com.bruce.raeasy.adapters.ItemAdapter;
import com.bruce.raeasy.models.Item;
import com.bruce.raeasy.utils.BarterEvent;
import com.bruce.raeasy.utils.SellEvent;
import com.google.android.material.snackbar.Snackbar;
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

public class OnSaleFragment extends Fragment implements ItemAdapter.ItemInteraction {

    private RecyclerView onSaleRecycler;
    private ItemAdapter mItemAdapter;
    private ProgressBar onSaleLoader;
    private String userId;
    private List<Item> mItems = new ArrayList<>();

    //Firebase
    private CollectionReference itemsRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_on_sale, container, false);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        itemsRef = database.collection(ITEMS_REF);

        initViews(view);

        mItemAdapter = new ItemAdapter(requireContext(), this);
        onSaleRecycler.setAdapter(mItemAdapter);

        return view;
    }

    private void populateRecycler(List<Item> items) {
        if (items != null) {
            onSaleLoader.setVisibility(View.GONE);
            GridLayoutManager manager = new GridLayoutManager(requireContext(), 2);
            onSaleRecycler.setHasFixedSize(true);
            onSaleRecycler.setLayoutManager(manager);
            mItemAdapter.setData(items, userId);
        } else {
            onSaleLoader.setVisibility(View.GONE);
            Snackbar.make(onSaleRecycler, "No Items found", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void observeBarterItems(SellEvent event){
        mItems.clear();
        mItems.addAll(event.getItems());
        populateRecycler(mItems);
    }

    private void initViews(View view) {
        onSaleRecycler = view.findViewById(R.id.onSaleRecycler);
        onSaleLoader = view.findViewById(R.id.onSaleLoader);
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
