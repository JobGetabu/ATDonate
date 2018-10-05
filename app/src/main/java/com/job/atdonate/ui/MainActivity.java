package com.job.atdonate.ui;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.VideoView;

import com.job.atdonate.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.mVideoView)
    VideoView mVideoView;
    private DonateFragment donateFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        donateFragment = new DonateFragment();

        //call this on button donate in main screen
        donateFragment.show(getSupportFragmentManager(), DonateFragment.TAG);

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.clean);

        mVideoView.setVideoURI(uri);
        mVideoView.setSoundEffectsEnabled(false);
        mVideoView.playSoundEffect(0);


        mVideoView.start();

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
            }
        });
    }
}
