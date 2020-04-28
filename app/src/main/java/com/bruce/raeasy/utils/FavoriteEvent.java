package com.bruce.raeasy.utils;

import com.bruce.raeasy.models.Favorite;

import java.util.List;

public class FavoriteEvent {

    private List<Favorite> mFavorites;

    public FavoriteEvent() {
    }

    public List<Favorite> getFavorites() {
        return mFavorites;
    }

    public void setFavorites(List<Favorite> favorites) {
        mFavorites = favorites;
    }
}
