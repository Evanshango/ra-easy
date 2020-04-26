package com.bruce.raeasy.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.bruce.raeasy.R;
import com.bruce.raeasy.models.Item;

public class ItemViewActivity extends AppCompatActivity {

    private static final String TAG = "ItemViewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);

        Intent intent = getIntent();
        Item item = intent.getParcelableExtra("item");
        if (item != null){
            Log.d(TAG, "onCreate: item :" + item.toString());
        }
    }
}
