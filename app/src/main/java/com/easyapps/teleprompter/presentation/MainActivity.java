package com.easyapps.teleprompter.presentation;

import android.content.DialogInterface;
import android.content.Intent;
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
import com.easyapps.teleprompter.domain.model.lyric.ILyricRepository;
import com.easyapps.teleprompter.infrastructure.persistence.lyric.AndroidFileSystemLyricFinder;
import com.easyapps.teleprompter.infrastructure.persistence.lyric.AndroidFileSystemLyricRepository;
import com.easyapps.teleprompter.infrastructure.persistence.lyric.FileSystemException;
import com.easyapps.teleprompter.presentation.components.PlayableCustomAdapter;
import com.easyapps.teleprompter.query.model.lyric.ILyricFinder;

import java.io.FileNotFoundException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ActivityCallback {

    private Menu mOptionsMenu;
    private LyricApplicationService mAppService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ILyricRepository mLyricRepository = new AndroidFileSystemLyricRepository(getApplicationContext());
        ILyricFinder lyricFinder = new AndroidFileSystemLyricFinder(getApplicationContext());
        mAppService = new LyricApplicationService(mLyricRepository, lyricFinder);

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

    public void createTextFile(View view) {
        Intent i = new Intent(this, CreateLyricActivity.class);
        startActivity(i);

        finish();
    }

    private void listFiles(final ListView lvFiles) {
        lvFiles.setAdapter(new PlayableCustomAdapter(this, this, mAppService.getAllLyrics()));
    }

    public void startAbout(MenuItem item) {
        Intent i = new Intent(this, AboutActivity.class);
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
