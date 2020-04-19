package com.bruce.raeasy.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bruce.raeasy.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BarterTradeFragment extends Fragment {

    public BarterTradeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_barter_trade, container, false);
    }
}
