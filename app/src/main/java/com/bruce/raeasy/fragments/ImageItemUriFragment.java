package com.bruce.raeasy.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bruce.raeasy.R;
import com.bruce.raeasy.models.ImageUri;

public class ImageItemUriFragment extends Fragment {

    private ImageView mImage;
    private ImageUri mImageUri;

    public static ImageItemUriFragment getInstance(ImageUri imageUri){
        ImageItemUriFragment fragment = new ImageItemUriFragment();
        if (imageUri != null){
            Bundle bundle = new Bundle();
            bundle.putParcelable("imageUri", imageUri);
            fragment.setArguments(bundle);
        }
        return  fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            mImageUri = getArguments().getParcelable("imageUri");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.images_placeholder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mImage = view.findViewById(R.id.itemImages);
        init();
    }

    private void init(){
        if (mImageUri != null){
            mImage.setImageURI(mImageUri.getUri());
        } else {
            mImage.setImageURI(null);
        }
    }
}
