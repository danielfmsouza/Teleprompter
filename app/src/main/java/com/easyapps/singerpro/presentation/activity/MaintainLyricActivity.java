package com.easyapps.singerpro.presentation.activity;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.easyapps.singerpro.R;
import com.easyapps.singerpro.domain.model.lyric.IQueueLyricRepository;
import com.easyapps.singerpro.domain.model.lyric.Lyric;
import com.easyapps.singerpro.presentation.fragment.MaintainLyricFragment;
import com.easyapps.singerpro.presentation.helper.ActivityUtils;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

/**
 * Created by Daniel on 2017-12-28.
 * Activity that controls the creation and update of a Lyric file.
 */
public class MaintainLyricActivity extends BaseActivity implements MaintainLyricFragment.OnSaveItemListener {

    private String mCurrentPlaylist;

    @Inject
    IQueueLyricRepository lyricQueue;

    @Inject
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        int orientation = getResources().getConfiguration().orientation;

        if (isTablet && orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ActivityUtils.backToMain(this);
        } else {
            setContentView(R.layout.activity_maintain_lyric);
            mCurrentPlaylist = sharedPref.getString(
                    getResources().getString(R.string.pref_key_currentPlaylistName), "");
        }

        EditText etTextFile = findViewById(R.id.etTextFile);
        if (etTextFile != null)
            etTextFile.setTypeface(Typeface.create(getFontFamily(), Typeface.BOLD));

        setTitle(getString(R.string.maintain_lyric));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_maintain_lyric, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        ActivityUtils.backToMain(this);
    }

    @Override
    public void onSaveItem(Lyric lyric) {
    }

    public void startSettings(MenuItem item) {
        if (!saveLyric()) return;
        ActivityUtils.startActivity(this, mCurrentPlaylist, SettingsActivity.class);
    }

    public void startTestPrompter(MenuItem item) {
        if (!saveLyric()) return;
        setLyricForPlaying();
        ActivityUtils.startActivity(this, mCurrentPlaylist, PrompterActivity.class);
    }

    private void setLyricForPlaying() {
        String lyricName = ActivityUtils.getLyricFileNameParameter(getIntent());
        lyricQueue.clearPlaylistQueue();
        lyricQueue.queueLyricForPlaying(lyricName);
    }

    private boolean saveLyric() {
        MaintainLyricFragment contentFragment = (MaintainLyricFragment) getFragmentManager()
                .findFragmentById(R.id.maintain_lyric_frag);

        return contentFragment != null && contentFragment.saveLyricFile();
    }

    private String getFontFamily() {
        String fontFamilyDefault = getResources().getString(R.string.pref_fontFamily_default);

        return sharedPref.getString(
                getResources().getString(R.string.pref_key_fontFamily), fontFamilyDefault);
    }
}
