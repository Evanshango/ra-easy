package com.bruce.raeasy.activities;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bruce.raeasy.models.Item;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class BaseActivity extends AppCompatActivity {

    public void addOrRemoveFavorite(Item item, String userId, CollectionReference itemsRef) {

        List<String> userIds = new ArrayList<>();
        userIds.add(userId);

        List<String> userIdList = item.getUserIds();
        if (userIdList.size() > 0) {
            for (String toFavUserId : userIdList) {
                if (toFavUserId.equals(userId)) {
                    removeFromList(userId, itemsRef, item.getItemId(), userIdList);
                } else {
                    addToList(userIds, itemsRef, item.getItemId(), "Added to Favorites");
                }
            }
        } else {
            addToList(userIds, itemsRef, item.getItemId(), "Added to Favorites");
        }
    }

    public void addToList(
            List<String> userIds, CollectionReference itemsRef, String itemId, String message
    ) {
        Map<String, Object> favMap = new HashMap<>();
        favMap.put("userIds", userIds);
        itemsRef.document(itemId)
                .set(favMap, SetOptions.merge()).addOnSuccessListener(aVoid -> Toast.makeText(
                this, message, Toast.LENGTH_SHORT
        ).show());
    }

    public void removeFromList(
            String userId, CollectionReference itemsRef, String itemId, List<String> userIdList
    ) {
        userIdList.remove(userId);
        addToList(userIdList, itemsRef, itemId, "Removed from Favorites");
    }
}
