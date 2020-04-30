package com.bruce.raeasy.utils;

import com.bruce.raeasy.models.Item;

import java.util.List;

public class SellEvent {

    private List<Item> mItems;

    public SellEvent() {
    }

    public List<Item> getItems() {
        return mItems;
    }

    public void setItems(List<Item> items) {
        mItems = items;
    }
}
