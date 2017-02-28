package com.easyapps.teleprompter.presentation;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.easyapps.teleprompter.R;
import com.easyapps.teleprompter.application.LyricApplicationService;
import com.easyapps.teleprompter.application.command.AddLyricCommand;
import com.easyapps.teleprompter.domain.model.lyric.IConfigurationRepository;
import com.easyapps.teleprompter.domain.model.lyric.ILyricRepository;
import com.easyapps.teleprompter.infrastructure.persistence.lyric.AndroidFileSystemLyricFinder;
import com.easyapps.teleprompter.infrastructure.persistence.lyric.AndroidFileSystemLyricRepository;
import com.easyapps.teleprompter.infrastructure.persistence.lyric.AndroidPreferenceConfigurationRepository;
import com.easyapps.teleprompter.infrastructure.persistence.lyric.FileSystemException;
import com.easyapps.teleprompter.infrastructure.persistence.lyric.FileSystemRepository;
import com.easyapps.teleprompter.presentation.components.PlayableCustomAdapter;
import com.easyapps.teleprompter.query.model.lyric.ILyricFinder;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ILyricRepository lyricRepository = new AndroidFileSystemLyricRepository(getApplicationContext());
        IConfigurationRepository configRepository = new AndroidPreferenceConfigurationRepository(getApplicationContext());
        ILyricFinder lyricFinder = new AndroidFileSystemLyricFinder(getApplicationContext());
        mAppService = new LyricApplicationService(lyricRepository, lyricFinder, configRepository);

        ListView lvFiles = (ListView) findViewById(R.id.lvFiles);
        listFiles(lvFiles);
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

    private void listFiles(final ListView lvFiles) {
        lvFiles.setAdapter(new PlayableCustomAdapter(this, this, mAppService.getAllLyrics()));
    }

    public void startAbout(MenuItem item) {
        startActivity(AboutActivity.class);
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
                Toast.makeText(getBaseContext(), R.string.export_error_no_intent,
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getBaseContext(), R.string.export_error_no_files,
                    Toast.LENGTH_LONG).show();
        }
    }

    public void startImport(MenuItem item) {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("*/*");

        startActivityForResult(intent, PICK_FILE_RESULT_CODE);
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

    private void deleteFiles() {
        ListView lvFiles = (ListView) findViewById(R.id.lvFiles);
        PlayableCustomAdapter adapter = (PlayableCustomAdapter) lvFiles.getAdapter();

        List<String> lyricsToDelete = adapter.getAllCheckedItems();
        try {
            mAppService.removeLyrics(lyricsToDelete);
            adapter.removeAllCheckedItems();
        } catch (FileNotFoundException | FileSystemException e) {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Show the trash button when called from some child component.
     */
    @Override
    public void showContent() {
        MenuItem deleteItemMenu = mOptionsMenu.getItem(0);
        deleteItemMenu.setVisible(true);
    }

    /**
     * Hide the trash button when called from some child component.
     */
    @Override
    public void hideContent() {
        MenuItem deleteItemMenu = mOptionsMenu.getItem(0);
        deleteItemMenu.setVisible(false);
    }
}
