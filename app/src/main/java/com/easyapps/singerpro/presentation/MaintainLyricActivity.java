package com.easyapps.singerpro.presentation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.easyapps.singerpro.presentation.fragments.MaintainLyricFragment;
import com.easyapps.singerpro.presentation.helper.ActivityUtils;
import com.easyapps.teleprompter.R;

/**
 * Created by Daniel on 2017-12-28.
 * Activity that controls the creation and update of a Lyric file.
 */

public class MaintainLyricActivity extends AppCompatActivity implements MaintainLyricFragment.OnSaveItemListener{

    private String mCurrentPlaylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintain_lyric);

        mCurrentPlaylist = ActivityUtils.getPlaylistNameParameter(this.getIntent());
    }

    @Override
    public void onBackPressed() {
        ActivityUtils.backToMain(this, mCurrentPlaylist);
    }

    @Override
    public void onSaveItem() {
        onBackPressed();
    }
}
