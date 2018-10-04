package com.job.atdonate.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.job.atdonate.R;

public class MainActivity extends AppCompatActivity {

    private DonateFragment donateFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        donateFragment = new DonateFragment();

        //call this on button donate in main screen
        donateFragment.show(getSupportFragmentManager(), DonateFragment.TAG);
    }
}
