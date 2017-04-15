package com.cambrian.android.ganarticles.fragments;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.cambrian.android.ganarticles.R;

/**
 * Created by S.J.Xiong.
 */

public class ImageDialogFragment extends DialogFragment {

    private static final String IMAGE_URL = "image_url";
    private String mImageUrl;

    public static ImageDialogFragment newInstance(String imageUrl) {
        ImageDialogFragment fragment = new ImageDialogFragment();
        Bundle args = new Bundle();
        args.putString(IMAGE_URL, imageUrl);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mImageUrl = getArguments().getString(IMAGE_URL);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_dialog, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_image_dialog);
        Glide.with(getActivity())
                .load(mImageUrl)
                .centerCrop()
                .into(imageView);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        super.onActivityCreated(savedInstanceState);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
    }
}
