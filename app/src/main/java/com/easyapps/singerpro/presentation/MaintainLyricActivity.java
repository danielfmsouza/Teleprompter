package com.easyapps.singerpro.presentation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.easyapps.singerpro.domain.model.lyric.Lyric;
import com.easyapps.singerpro.presentation.fragments.MaintainLyricFragment;
import com.easyapps.singerpro.presentation.helper.ActivityUtils;
import com.easyapps.teleprompter.R;

/**
 * Created by Daniel on 2017-12-28.
 * Activity that controls the creation and update of a Lyric file.
 */

public class MaintainLyricActivity extends AppCompatActivity implements MaintainLyricFragment.OnSaveItemListener {

    private String mCurrentPlaylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        int orientation = getResources().getConfiguration().orientation;

        if (isTablet && orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ActivityUtils.backToMain(this);
        } else {
            setContentView(R.layout.activity_maintain_lyric);

            SharedPreferences sharedPref =
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            mCurrentPlaylist = sharedPref.getString(
                    getResources().getString(R.string.pref_key_currentPlaylistName), "");
        }
    }

    @Override
    public void onBackPressed() {
        ActivityUtils.backToMain(this);
    }

    @Override
    public void onSaveItem(Lyric lyric) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_maintain_lyric, menu);
        return true;
    }

    public void startSettings(MenuItem item) {
        MaintainLyricFragment contentFragment = (MaintainLyricFragment) getFragmentManager()
                .findFragmentById(R.id.maintain_lyric_frag);

        if (contentFragment != null)
            contentFragment.saveLyricFile();

        Intent i = new Intent(getApplicationContext(), SettingsActivity.class);

        String lyricName = ActivityUtils.getFileNameParameter(getIntent());
        ActivityUtils.setLyricFileNameParameter(lyricName, i);
        ActivityUtils.setCurrentPlaylistName(mCurrentPlaylist, getApplicationContext());
        startActivity(i);

        finish();
    }
}
