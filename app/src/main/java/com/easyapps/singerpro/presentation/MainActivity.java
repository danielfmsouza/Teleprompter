package com.easyapps.singerpro.presentation;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.easyapps.singerpro.domain.model.lyric.IConfigurationRepository;
import com.easyapps.singerpro.domain.model.lyric.ILyricRepository;
import com.easyapps.singerpro.domain.model.lyric.ISetListRepository;
import com.easyapps.singerpro.infrastructure.persistence.lyric.AndroidFileSystemLyricFinder;
import com.easyapps.singerpro.infrastructure.persistence.lyric.AndroidFileSystemSetListFinder;
import com.easyapps.singerpro.infrastructure.persistence.lyric.AndroidFileSystemSetListRepository;
import com.easyapps.singerpro.infrastructure.persistence.lyric.AndroidPreferenceConfigurationRepository;
import com.easyapps.singerpro.infrastructure.persistence.lyric.FileSystemException;
import com.easyapps.singerpro.infrastructure.persistence.lyric.FileSystemRepository;
import com.easyapps.singerpro.presentation.components.PlayableCustomAdapter;
import com.easyapps.singerpro.query.model.lyric.ILyricFinder;
import com.easyapps.singerpro.query.model.lyric.ISetListFinder;
import com.easyapps.singerpro.query.model.lyric.LyricQueryModel;
import com.easyapps.teleprompter.R;
import com.easyapps.singerpro.application.LyricApplicationService;
import com.easyapps.singerpro.application.command.AddLyricCommand;
import com.easyapps.singerpro.infrastructure.persistence.lyric.AndroidFileSystemLyricRepository;
import com.easyapps.singerpro.presentation.helper.ActivityUtils;

import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ActivityCallback {

    private static final int PICK_FILE_RESULT_CODE = 1;

    private Menu mOptionsMenu;
    private LyricApplicationService mAppService;
    private String currentSetList = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        currentSetList = ActivityUtils.getSetListNameParameter(this.getIntent());
        loadLyricsFromSetList(currentSetList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_settings, menu);
        mOptionsMenu = menu;

        // hiding the Delete button
        hideContent();
        return true;
    }

    public void createLyric(View view) {
        Intent i = new Intent(this, CreateLyricActivity.class);
        startActivity(i);

        finish();
    }

    public void listFiles(final ListView lvFiles) {
        lvFiles.setAdapter(new PlayableCustomAdapter(this, this, mAppService.getAllLyrics(),
                currentSetList));
    }

    public void startAbout(MenuItem item) {
        startActivity(AboutActivity.class);
    }

    public void startGlobalSettings(MenuItem item) {
        Intent i = new Intent(getBaseContext(), GlobalSettingsActivity.class);
        ActivityUtils.setSetListNameParameter(currentSetList, i);

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

                    ListView lvFiles = (ListView) findViewById(R.id.lvFiles);
                    listFiles(lvFiles);
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

    public void deleteSelectedFiles(MenuItem item) {
        displayDecisionDialog();
    }

    public void setListSelectedFiles(MenuItem item) {
        String[] setListsNames = mAppService.getAllSetListsNames();
        final String[] items = new String[setListsNames.length + 1];
        items[0] = getResources().getString(R.string.new_set_list);

        System.arraycopy(setListsNames, 0, items, 1, setListsNames.length);

        Dialog d = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.add_song_set_list))
                .setNegativeButton(getResources().getString(R.string.cancel), null)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dlg, int position) {
                        if (position == 0) {
                            createNewSetList();
                        } else {
                            addLyricsToSetList(items[position]);
                        }
                    }
                })
                .create();
        d.show();
    }

    private void unCheckAllSelectedItems() {
        ListView lvFiles = (ListView) findViewById(R.id.lvFiles);
        PlayableCustomAdapter adapter = (PlayableCustomAdapter) lvFiles.getAdapter();
        adapter.unCheckAllItems();
    }

    public void loadSetList(MenuItem item) {
        String[] setListsNames = mAppService.getAllSetListsNames();
        final String[] items = new String[setListsNames.length];
        System.arraycopy(setListsNames, 0, items, 0, setListsNames.length);

        Dialog d = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.load_existing_set_list))
                .setNegativeButton(getResources().getString(R.string.cancel), null)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dlg, int position) {
                        loadLyricsFromSetList(items[position]);
                    }
                })
                .create();
        d.show();
    }

    public void renameSetList(MenuItem item) {
        final EditText input = new EditText(this);
        input.setText(currentSetList);
        input.setSelected(true);

        Dialog d = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.rename_set_list))
                .setNegativeButton(getResources().getString(R.string.cancel), null)
                .setView(input)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String value = input.getText().toString();
                        if (value.trim().isEmpty())
                            input.setError(getString(R.string.set_list_name_required));
                        else {
                            try {
                                mAppService.updateSetListName(currentSetList, value);
                                unCheckAllSelectedItems();
                                currentSetList = value;
                                setTitle(getString(R.string.app_name) + " - " + currentSetList);
                            } catch (FileSystemException | FileNotFoundException e) {
                                Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                })
                .create();
        d.show();
    }

    public void showAllLyrics(MenuItem item) {
        currentSetList = "";
        ListView lvFiles = (ListView) findViewById(R.id.lvFiles);
        if (lvFiles != null)
            listFiles(lvFiles);
        setTitle(getString(R.string.app_name) + " - " + getString(R.string.app_name_all_songs));
        hideRenameSetListItemMenu();
    }

    private void loadLyricsFromSetList(String setListName) {
        if (setListName == null || setListName.isEmpty())
            showAllLyrics(null);

        else {
            List<LyricQueryModel> lyrics = null;
            try {
                lyrics = mAppService.loadLyricsFromSetList(setListName);
            } catch (FileSystemException e) {
                Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }

            if (lyrics == null || lyrics.isEmpty()) {
                removeSetList(setListName);
                showAllLyrics(null);
            } else {
                ListView lvFiles = (ListView) findViewById(R.id.lvFiles);
                lvFiles.setAdapter(new PlayableCustomAdapter(this, this, lyrics, setListName));
                setTitle(getString(R.string.app_name) + " - " + setListName);
                currentSetList = setListName;
                showRenameSetListItemMenu();
            }
        }
    }

    private void removeSetList(String setListName) {
        try {
            mAppService.removeSetList(setListName);
            hideRenameSetListItemMenu();
        } catch (FileNotFoundException | FileSystemException e) {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void addLyricsToSetList(String setListName) {
        try {
            mAppService.addLyricToSetList(setListName, getAllCheckedFiles());
        } catch (FileSystemException e) {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void createNewSetList() {
        final EditText input = new EditText(this);

        Dialog d = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.new_set_list))
                .setNegativeButton(getResources().getString(R.string.cancel), null)
                .setView(input)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String value = input.getText().toString();
                        if (value.trim().isEmpty())
                            input.setError(getString(R.string.set_list_name_required));
                        else {
                            try {
                                mAppService.addSetList(value, getAllCheckedFiles());
                                unCheckAllSelectedItems();
                            } catch (FileSystemException e) {
                                Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                })
                .create();
        d.show();
    }

    private void displayDecisionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_files_question).
                setPositiveButton(getResources().getString(R.string.yes), dialogClickListener)
                .setNegativeButton(getResources().getString(R.string.no), dialogClickListener).show();
    }

    private final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    deleteFiles();
                    break;
            }
        }
    };

    private List<String> getAllCheckedFiles() {
        ListView lvFiles = (ListView) findViewById(R.id.lvFiles);
        PlayableCustomAdapter adapter = (PlayableCustomAdapter) lvFiles.getAdapter();

        return adapter.getAllCheckedItems();
    }

    private void deleteFiles() {
        ListView lvFiles = (ListView) findViewById(R.id.lvFiles);
        PlayableCustomAdapter adapter = (PlayableCustomAdapter) lvFiles.getAdapter();

        List<String> lyricsToDelete = adapter.getAllCheckedItems();
        try {
            mAppService.removeLyrics(lyricsToDelete);
            adapter.removeAllCheckedItems();
            if (adapter.getCount() == 0 && currentSetList != null && !currentSetList.equals("")) {
                removeSetList(currentSetList);
                showAllLyrics(null);
            }
        } catch (FileNotFoundException | FileSystemException e) {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showRenameSetListItemMenu() {
        if (mOptionsMenu != null) {
            MenuItem renameSetListItemMenu = mOptionsMenu.getItem(2);
            renameSetListItemMenu.setVisible(true);
        }
    }

    private void hideRenameSetListItemMenu() {
        if (mOptionsMenu != null) {
            MenuItem renameSetListItemMenu = mOptionsMenu.getItem(2);
            renameSetListItemMenu.setVisible(false);
        }
    }

    /**
     * Show the trash button when called from some child component.
     */
    @Override
    public void showContent() {
        if (mOptionsMenu != null) {

            MenuItem setListItemMenu = mOptionsMenu.getItem(3);
            MenuItem deleteItemMenu = mOptionsMenu.getItem(4);

            deleteItemMenu.setVisible(true);
            setListItemMenu.setVisible(true);
        }
    }

    /**
     * Hide the trash button when called from some child component.
     */
    @Override
    public void hideContent() {
        if (mOptionsMenu != null) {

            MenuItem setListItemMenu = mOptionsMenu.getItem(3);
            MenuItem deleteItemMenu = mOptionsMenu.getItem(4);

            deleteItemMenu.setVisible(false);
            setListItemMenu.setVisible(false);
        }
    }

    @Override
    public void removeItem(String itemName) {
        if (currentSetList != null && !currentSetList.isEmpty()) {
            try {
                mAppService.removeLyricFromSetList(currentSetList, itemName);
                Toast.makeText(getBaseContext(), R.string.btn_remove_from_playlist_successful, Toast.LENGTH_LONG).show();
                loadLyricsFromSetList(currentSetList);
            } catch (FileSystemException e) {
                Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getBaseContext(), R.string.set_list_not_loaded, Toast.LENGTH_LONG).show();
        }
    }
}
