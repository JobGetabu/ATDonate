package com.job.atdonate.ui;

import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.job.atdonate.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.mainbtn)
    MaterialButton mainbtn;
    private DonateFragment donateFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        donateFragment = new DonateFragment();


    }

    @OnClick(R.id.mainbtn)
    public void onViewClicked() {
        //call this on button donate in main screen
        donateFragment.show(getSupportFragmentManager(), DonateFragment.TAG);
    }
}
