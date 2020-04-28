package com.bruce.raeasy.activities;

import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bruce.raeasy.R;
import com.bruce.raeasy.models.Favorite;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import static com.bruce.raeasy.utils.Constants.ITEM_ID;
import static com.bruce.raeasy.utils.Constants.USER_ID;

class BaseActivity extends AppCompatActivity {

    public void addFavorite(ImageView favImg, Favorite favorite, CollectionReference favRef) {
        Query query = favRef.whereEqualTo(ITEM_ID, favorite.getItemId())
                .whereEqualTo(USER_ID, favorite.getUserId()).limit(1);
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                    Favorite mFavorite = snapshot.toObject(Favorite.class);
                    checkForMatch(mFavorite, favorite, favImg, favRef);
                }
            } else {
                doFavorite(favorite, favImg, favRef);
            }
        });
    }

    private void checkForMatch(Favorite mFavorite, Favorite favorite, ImageView favImg,
                               CollectionReference favRef) {
        if (mFavorite != null) {
            undoFavorite(favImg, favRef, mFavorite.getFavId());
        } else {
            doFavorite(favorite, favImg, favRef);
        }
    }

    private void undoFavorite(ImageView favImg, CollectionReference favRef, String favId) {
        favRef.document(favId).delete().addOnSuccessListener(aVoid -> {
            favImg.setImageResource(R.drawable.ic_favorite_border);
            Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
        });
    }

    private void doFavorite(Favorite favorite, ImageView favImg, CollectionReference favRef) {
        favRef.document(favorite.getFavId()).set(favorite).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                favImg.setImageResource(R.drawable.ic_favorite_filled);
                Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(
                this, "Check your internet connection", Toast.LENGTH_SHORT).show()
        );
    }
}
