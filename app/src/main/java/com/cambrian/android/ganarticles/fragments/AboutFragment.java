package com.cambrian.android.ganarticles.fragments;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cambrian.android.ganarticles.R;
import com.cambrian.android.ganarticles.utils.UrlStringUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends Fragment {

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        // Toolbar 的初始化
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.tb_fragment_about);
        toolbar.setTitle(getString(R.string.about_zh));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        // 显示返回按钮
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_fragment_about_icon);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProjectWebsite();
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_frangment_about, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.action_git:
                openProjectWebsite();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openProjectWebsite() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(UrlStringUtil.getProjectUrl()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage("com.android.chrome");
        try {
            getActivity().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            intent.setPackage(null);
            getActivity().startActivity(intent);
        }
    }
}
