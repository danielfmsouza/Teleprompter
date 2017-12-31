package com.easyapps.singerpro.presentation;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.easyapps.singerpro.application.LyricApplicationService;
import com.easyapps.singerpro.application.command.AddLyricCommand;
import com.easyapps.singerpro.domain.model.lyric.IConfigurationRepository;
import com.easyapps.singerpro.domain.model.lyric.ILyricRepository;
import com.easyapps.singerpro.domain.model.lyric.ISetListRepository;
import com.easyapps.singerpro.infrastructure.persistence.lyric.AndroidFileSystemLyricFinder;
import com.easyapps.singerpro.infrastructure.persistence.lyric.AndroidFileSystemLyricRepository;
import com.easyapps.singerpro.infrastructure.persistence.lyric.AndroidFileSystemSetListFinder;
import com.easyapps.singerpro.infrastructure.persistence.lyric.AndroidFileSystemSetListRepository;
import com.easyapps.singerpro.infrastructure.persistence.lyric.AndroidPreferenceConfigurationRepository;
import com.easyapps.singerpro.infrastructure.persistence.lyric.FileSystemException;
import com.easyapps.singerpro.infrastructure.persistence.lyric.FileSystemRepository;
import com.easyapps.singerpro.presentation.fragments.MainListFragment;
import com.easyapps.singerpro.presentation.fragments.MaintainLyricFragment;
import com.easyapps.singerpro.presentation.helper.ActivityUtils;
import com.easyapps.singerpro.query.model.lyric.ILyricFinder;
import com.easyapps.singerpro.query.model.lyric.ISetListFinder;
import com.easyapps.teleprompter.R;

import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements
        MainListFragment.OnListChangeListener,
        MaintainLyricFragment.OnSaveItemListener {

    private static final int PICK_FILE_RESULT_CODE = 1;

    private LyricApplicationService mAppService;
    private String mCurrentSetList = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ILyricRepository lyricRepository
                = new AndroidFileSystemLyricRepository(getApplicationContext());
        IConfigurationRepository configRepository
                = new AndroidPreferenceConfigurationRepository(getApplicationContext());
        ISetListRepository setListRepository
                = new AndroidFileSystemSetListRepository(getApplicationContext());
        ILyricFinder lyricFinder = new AndroidFileSystemLyricFinder(getApplicationContext());
        ISetListFinder setListFinder = new AndroidFileSystemSetListFinder(getApplicationContext());
        mAppService = new LyricApplicationService(lyricRepository, lyricFinder, configRepository,
                setListFinder, setListRepository);

        mCurrentSetList = ActivityUtils.getPlaylistNameParameter(this.getIntent());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createLyric();
            }
        });

        verifyDetailsFragment();
    }

    private void verifyDetailsFragment() {
        MaintainLyricFragment contentFragment = (MaintainLyricFragment) getFragmentManager()
                .findFragmentById(R.id.details_frag);

        if (contentFragment != null) {
            MainListFragment listFragment = getMainListFragment();

            if (!listFragment.selectFirstItem()) {
                createLyric();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_settings, menu);
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
        Intent i = new Intent(getBaseContext(), GlobalSettingsActivity.class);
        ActivityUtils.setPlaylistNameParameter(mCurrentSetList, i);

        startActivity(i);
    }

    public void startExport(MenuItem item) {
        IConfigurationRepository ConfigRepository =
                new AndroidPreferenceConfigurationRepository(getApplicationContext());

        Uri configUri = ConfigRepository.getURIFromConfiguration();
        Uri[] lyricsUris = mAppService.exportAllLyrics();

        ArrayList<Uri> allUris = new ArrayList<>();

        if (lyricsUris != null)
            allUris.addAll(Arrays.asList(lyricsUris));

        if (configUri != null)
            allUris.add(configUri);

        if (!allUris.isEmpty()) {

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
        switch (requestCode) {
            case PICK_FILE_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    ClipData data = intent.getClipData();

                    if (data != null) {
                        importFiles(data);
                    } else {
                        Uri uri = intent.getData();
                        String fileName = FileSystemRepository.getFileName(uri, this);

                        if (uri != null) {
                            if (fileName.endsWith(mAppService.getConfigExtension()))
                                importConfigurationFile(uri);
                            else
                                importLyricFile(1, uri, fileName);
                        }
                    }
                    showAllLyrics(null);
                }
                break;
        }
    }

    private void importFiles(ClipData data) {
        Uri configFileUri = null;

        for (int i = 0; i < data.getItemCount(); i++) {
            ClipData.Item item = data.getItemAt(i);

            if (item != null) {
                String fileName = FileSystemRepository.getFileName(item.getUri(), this);

                if (fileName.endsWith(mAppService.getConfigExtension()))
                    configFileUri = item.getUri();
                else
                    importLyricFile(i, item.getUri(), fileName);
            }
        }
        importConfigurationFile(configFileUri);
    }

    private void importConfigurationFile(Uri configFileUri) {
        if (configFileUri != null) {
            try {
                mAppService.importAllConfigurationsFromFileUri(configFileUri);
            } catch (FileSystemException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void importLyricFile(int i, Uri uri, String fileName) {
        try {
            String content = FileSystemRepository.readFile(uri, this, fileName);

            if (fileName != null) {
                String shortFileName = fileName.substring(0, fileName.indexOf("."));
                AddLyricCommand cmd = new AddLyricCommand(shortFileName, content, String.valueOf(i));
                mAppService.addLyric(cmd);
            }

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void startActivity(Class activity) {
        Intent i = new Intent(this, activity);
        startActivity(i);

        finish();
    }

    public void renameSetList(MenuItem item) {
        final EditText input = new EditText(this);
        input.setText(mCurrentSetList);
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
                                mAppService.updateSetListName(mCurrentSetList, value);
//                                unCheckAllSelectedItems();
                                mCurrentSetList = value;
                                setTitle(getString(R.string.app_name) + " - " + mCurrentSetList);
                            } catch (FileSystemException | FileNotFoundException e) {
                                Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                })
                .create();
        d.show();
    }

    private void removeSetList(String setListName) {
        try {
            mAppService.removeSetList(setListName);
        } catch (FileNotFoundException | FileSystemException e) {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void createLyric() {
        MaintainLyricFragment contentFragment = (MaintainLyricFragment) getFragmentManager()
                .findFragmentById(R.id.details_frag);

        if (contentFragment == null) {
            Intent intent = new Intent(this, MaintainLyricActivity.class);
            ActivityUtils.setPlaylistNameParameter(mCurrentSetList, intent);
            startActivity(intent);
        } else {
            contentFragment.newContent();
        }
    }

    @Override
    public void onItemSelected(String lyricName) {
        MaintainLyricFragment contentFragment = (MaintainLyricFragment) getFragmentManager()
                .findFragmentById(R.id.details_frag);

        if (contentFragment == null) {
            Intent intent = new Intent(this, MaintainLyricActivity.class);
            ActivityUtils.setPlaylistNameParameter(mCurrentSetList, intent);
            ActivityUtils.setLyricFileNameParameter(lyricName, intent);
            startActivity(intent);
        } else {
            contentFragment.updateContent(lyricName);
        }
    }

    @Override
    public void onRemovedAllItems() {
        MaintainLyricFragment contentFragment = (MaintainLyricFragment) getFragmentManager()
                .findFragmentById(R.id.details_frag);
        if (contentFragment != null) {
            contentFragment.newContent();
        }
    }

    /**
     * If this method is called it's because this activity is holding both fragments
     * (on large screens). Hence, it has to update the list after one it's saved
     */
    @Override
    public void onSaveItem() {
        MainListFragment listFragment = getMainListFragment();
        listFragment.refresh();
    }

    private MainListFragment getMainListFragment() {
        return (MainListFragment) getFragmentManager()
                .findFragmentById(R.id.list_frag);
    }

    public void showAllLyrics(MenuItem item) {
        getMainListFragment().showAllLyrics();
    }

    public void loadPlaylist(MenuItem item) {
        String[] playlistNames = mAppService.getAllPlaylistNames();
        final String[] items = new String[playlistNames.length];
        System.arraycopy(playlistNames, 0, items, 0, playlistNames.length);

        final MainListFragment listFragment = getMainListFragment();

        Dialog d = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.load_existing_playlist))
                .setNegativeButton(getResources().getString(R.string.cancel), null)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dlg, int position) {
                        listFragment.loadLyricsFromPlaylist(items[position]);
                    }
                })
                .create();
        d.show();
    }
}
