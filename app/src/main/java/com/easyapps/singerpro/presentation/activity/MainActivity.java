package com.easyapps.singerpro.presentation.activity;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.easyapps.singerpro.R;
import com.easyapps.singerpro.application.LyricApplicationService;
import com.easyapps.singerpro.domain.model.lyric.Lyric;
import com.easyapps.singerpro.infrastructure.communication.bluetooth.BluetoothScreenShareServer;
import com.easyapps.singerpro.infrastructure.persistence.lyric.FileSystemException;
import com.easyapps.singerpro.presentation.component.ChecklistCustomAdapter;
import com.easyapps.singerpro.presentation.fragment.MainListFragment;
import com.easyapps.singerpro.presentation.fragment.MaintainLyricFragment;
import com.easyapps.singerpro.presentation.helper.ActivityUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class MainActivity extends BaseActivity implements
        MainListFragment.OnListChangeListener,
        MaintainLyricFragment.OnSaveItemListener {

    private static final int PICK_FILE_RESULT_CODE = 1;

    @Inject
    LyricApplicationService mAppService;

    @Inject
    BluetoothScreenShareServer mBluetoothScreenShare;

    private String mCurrentPlaylist = "";
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mCurrentPlaylist = ActivityUtils.getCurrentPlaylistName(this);
        handleIntent(getIntent());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMaintainLyricFeature();
            }
        });
        verifyDetailsFragment();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchByLyricName(query);
        }
    }

    private void searchByLyricName(String query) {
        String normalizedQuery = query != null ? query.toLowerCase() : "";
        if (normalizedQuery.isEmpty()) {
            return;
        }

        MainListFragment listFragment = getMainListFragment();
        if (listFragment != null) {
            listFragment.filterLyrics(normalizedQuery);
        }
    }

    private void verifyDetailsFragment() {
        MaintainLyricFragment contentFragment = (MaintainLyricFragment) getFragmentManager()
                .findFragmentById(R.id.details_frag);
        if (contentFragment != null) {
            MainListFragment listFragment = getMainListFragment();
            if (!listFragment.selectCurrentItem()) {
                showMaintainLyricFeature();
            }
        }
    }

    public void startSettingsFromMain(MenuItem item) {
        MaintainLyricFragment contentFragment = (MaintainLyricFragment) getFragmentManager()
                .findFragmentById(R.id.details_frag);
        if (contentFragment != null)
            contentFragment.saveLyricFile();

        String lyricName = ActivityUtils.getLyricFileNameParameter(getIntent());
        startActivity(SettingsActivity.class, lyricName);
    }

    private void setConfigurationButtonVisible(boolean visibility) {
        if (mMenu != null) {
            MenuItem menuConfig = mMenu.findItem(R.id.menu_lyric_settings_main);
            menuConfig.setVisible(visibility);
        }
    }

    private void setScreenShareButtonVisible(boolean visibility) {
        if (mMenu != null) {
//            MenuItem menuConfig = mMenu.findItem(R.id.menu_share_screen);
//            menuConfig.setVisible(visibility);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_settings, menu);
        mMenu = menu;
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        boolean isNewLyric = ActivityUtils.isNewLyric(this);
        int orientation = getResources().getConfiguration().orientation;

        if (!isNewLyric && isTablet && orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setConfigurationButtonVisible(true);
        } else {
            setConfigurationButtonVisible(false);
        }

        if (mBluetoothScreenShare.isScreenShareAvailable()) {
            setScreenShareButtonVisible(true);
        } else {
            setScreenShareButtonVisible(false);
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_general_options) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startAbout(MenuItem item) {
        startActivity(AboutActivity.class);
    }

    public void startGlobalSettings(MenuItem item) {
        startActivity(GlobalSettingsActivity.class);
    }

    public void startExport(MenuItem item) {
        ArrayList allUris = mAppService.exportAll();

        if (allUris != null && !allUris.isEmpty()) {

            Intent backupIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            backupIntent.setType("vnd.android.cursor.dir/email");

            DateFormat df = DateFormat.getDateInstance();
            backupIntent.putExtra(Intent.EXTRA_SUBJECT,
                    getResources().getString(R.string.export_description) + " " + df.format(new Date()));

            backupIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, allUris);

            try {
                startActivity(backupIntent);
            } catch (ActivityNotFoundException ex) {
                Toast.makeText(getBaseContext(), R.string.export_import_error_no_intent,
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getBaseContext(), R.string.export_error_no_files,
                    Toast.LENGTH_LONG).show();
        }
    }

    public void startImport(MenuItem item) {
        Intent intent;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");

        try {
            startActivityForResult(intent, PICK_FILE_RESULT_CODE);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(getBaseContext(), R.string.export_import_error_no_intent,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == PICK_FILE_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                ClipData data = intent.getClipData();

                if (data != null) {
                    importFiles(data);
                } else {
                    importFile(intent);
                }
                showAllLyrics(null);
            }
        }
    }

    private void importFile(Intent intent) {
        Uri uri = intent.getData();
        try {
            mAppService.importFile(uri, this);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void importFiles(ClipData data) {
        try {
            mAppService.importAll(data, this);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void startActivity(Class activity, String lyricName) {
        Intent i = new Intent(this, activity);
        ActivityUtils.setCurrentPlaylistName(mCurrentPlaylist, this);
        ActivityUtils.setLyricFileNameParameter(lyricName, i);
        startActivity(i);
        finish();
    }

    private void startActivity(Class activity) {
        startActivity(activity, null);
    }

    public void renameSetList(MenuItem item) {
        final EditText input = new EditText(this);
        input.setText(mCurrentPlaylist);
        input.setSelected(true);

        Dialog d = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.rename_playlist))
                .setNegativeButton(getResources().getString(R.string.cancel), null)
                .setView(input)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String value = input.getText().toString();
                        if (value.trim().isEmpty())
                            input.setError(getString(R.string.set_list_name_required));
                        else {
                            try {
                                mAppService.updatePlaylistName(mCurrentPlaylist, value);
                                mCurrentPlaylist = value;
                                setTitle(mCurrentPlaylist);
                                ActivityUtils.setCurrentPlaylistName(value, getApplicationContext());
                            } catch (FileSystemException | FileNotFoundException e) {
                                Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                })
                .create();
        d.show();
    }

    private void showMaintainLyricFeature() {
        MaintainLyricFragment contentFragment = (MaintainLyricFragment) getFragmentManager()
                .findFragmentById(R.id.details_frag);

        setConfigurationButtonVisible(false);

        if (contentFragment != null && getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE) {
            contentFragment.newContent();
            getMainListFragment().removeSelection();
        } else {
            startActivity(MaintainLyricActivity.class);
        }
    }

    @Override
    public void onItemSelected(String lyricName) {
        if (lyricName == null) return;

        MaintainLyricFragment contentFragment = (MaintainLyricFragment) getFragmentManager()
                .findFragmentById(R.id.details_frag);

        if (contentFragment != null && getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE) {
            contentFragment.updateContent(lyricName);
            setConfigurationButtonVisible(true);
        } else {
            startActivity(MaintainLyricActivity.class, lyricName);
        }
    }

    @Override
    public void onRemovedAllItems() {
        MaintainLyricFragment contentFragment = (MaintainLyricFragment) getFragmentManager()
                .findFragmentById(R.id.details_frag);
        if (contentFragment != null) {
            setConfigurationButtonVisible(false);
            contentFragment.newContent();
        }
    }

    /**
     * If this method is called it's because this activity is holding both fragments
     * (on large screens). Hence, it has to update the list after one it's saved
     */
    @Override
    public void onSaveItem(Lyric lyric) {
        MainListFragment listFragment = getMainListFragment();
        listFragment.refresh();
        setConfigurationButtonVisible(true);
    }

    private MainListFragment getMainListFragment() {
        return (MainListFragment) getFragmentManager()
                .findFragmentById(R.id.list_frag);
    }

    public void showAllLyrics(MenuItem item) {
        MainListFragment frag = getMainListFragment();
        if (frag != null)
            frag.showAllLyrics();
        mCurrentPlaylist = "";
        MenuItem menuRenamePlaylist = mMenu.findItem(R.id.menu_rename_playlist);
        menuRenamePlaylist.setVisible(false);
        clearSearchQuery();
    }

    public void shareScreen(MenuItem item) {
        if (mBluetoothScreenShare.isProtocolEnabled()) {
            showPairedDevicesDialog();
        } else {
            showEnableBluetoothDialog();
        }
    }

    private void showEnableBluetoothDialog() {
        final AlertDialog d = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.bluetooth_enable_dialog_title))
                .setNegativeButton(getResources().getString(R.string.cancel), null)
                .setPositiveButton(android.R.string.ok, null)
                .create();
        LayoutInflater inflater = this.getLayoutInflater();
//        View dialogView = inflater.inflate(R.layout.alert_dialog_with_progress_bar, null);
//        d.setView(dialogView);
        d.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        mBluetoothScreenShare.enable();

                        if (mBluetoothScreenShare.enable()) {

                            showPairedDevicesDialog();
                        }

                    }
                });
            }
        });
        d.show();
    }

    private void showPairedDevicesDialog() {
        ListView lvPairedDevices = new ListView(this);
        lvPairedDevices.setAdapter(new ChecklistCustomAdapter(this,
                R.layout.row_list_simple_item,
                mBluetoothScreenShare.getPairedDevicesNames(), R.id.tvDescription));

        Dialog d = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.bluetooth_paired_devices_dialog_title))
                .setNegativeButton(getResources().getString(R.string.cancel), null)
                .setView(lvPairedDevices)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        mBluetoothScreenShare.connect();
                    }
                })
                .create();
        d.show();
    }

    public void loadPlaylist(MenuItem item) {
        String[] playlistNames = mAppService.getAllPlaylistNames();
        final String[] items = new String[playlistNames.length];
        System.arraycopy(playlistNames, 0, items, 0, playlistNames.length);

        final MainListFragment listFragment = getMainListFragment();
        final MenuItem menuRenamePlaylist = mMenu.findItem(R.id.menu_rename_playlist);

        Dialog d = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.load_existing_playlist))
                .setNegativeButton(getResources().getString(R.string.cancel), null)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dlg, int position) {
                        tryLoadLyricsFromPlaylist(listFragment, items[position]);
                        menuRenamePlaylist.setVisible(true);

                    }
                })
                .create();
        d.show();
    }

    private void tryLoadLyricsFromPlaylist(MainListFragment listFragment, String playListName) {
        boolean loaded = listFragment.loadLyricsFromPlaylist(playListName);
        if (loaded) {
            mCurrentPlaylist = playListName;
            ActivityUtils.setCurrentPlaylistName(mCurrentPlaylist, getApplicationContext());
        } else
            mCurrentPlaylist = "";

        clearSearchQuery();
    }

    private void clearSearchQuery() {
        Intent intent = getIntent();
        if (intent != null) {
            intent.setAction(Intent.ACTION_MAIN);
        }
    }

    public void search(MenuItem item) {
        onSearchRequested();
    }
}
