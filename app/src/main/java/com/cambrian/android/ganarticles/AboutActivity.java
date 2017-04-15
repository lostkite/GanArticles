package com.cambrian.android.ganarticles;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.cambrian.android.ganarticles.fragments.AboutFragment;

/**
 * Created by S.J.Xiong.
 */

public class AboutActivity extends AppCompatActivity {
    public static Intent newIntent(Context context) {
        return new Intent(context, AboutActivity.class);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);

        FragmentManager fm = getSupportFragmentManager();
        Fragment aboutFragment = fm.findFragmentById(R.id.single_fragment_container);
        if (aboutFragment == null) {
            aboutFragment = AboutFragment.newInstance();
        }
        fm.beginTransaction()
                .add(R.id.single_fragment_container, aboutFragment)
                .commit();
    }
}
