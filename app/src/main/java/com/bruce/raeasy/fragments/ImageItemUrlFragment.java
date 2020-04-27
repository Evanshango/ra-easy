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
import com.bruce.raeasy.models.ImageUrl;
import com.bumptech.glide.Glide;

import java.util.Objects;

public class ImageItemUrlFragment extends Fragment {

    private ImageView mImage;
    private ImageUrl mImageUrl;

    public static ImageItemUrlFragment getInstance(ImageUrl imageUrl){
        ImageItemUrlFragment fragment = new ImageItemUrlFragment();
        if (imageUrl != null){
            Bundle bundle = new Bundle();
            bundle.putParcelable("imageUrl", imageUrl);
            fragment.setArguments(bundle);
        }
        return  fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            mImageUrl = getArguments().getParcelable("imageUrl");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.images_url_placeholder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mImage = view.findViewById(R.id.itemImagesUrl);
        init();
    }

    private void init(){
        if (mImageUrl != null){
            Glide.with(requireContext())
                    .load(mImageUrl.getImgUrl())
                    .into(mImage);
        }
    }
}
