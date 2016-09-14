package com.easyapps.teleprompter;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.easyapps.teleprompter.components.PlayableCustomAdapter;
import com.easyapps.teleprompter.messages.Constants;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ActivityCallback {

    private List<String> fileNames = new ArrayList<>();
    private Menu mOptionsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        Intent i = new Intent(this, CreateFileActivity.class);
        startActivity(i);

        finish();
    }

    private void listFiles(final ListView lvFiles) {
        File workDirectory = this.getFilesDir();
        File[] files = workDirectory.listFiles();

        if (files != null && files.length > 0) {
            for (File f : files) {
                int indexBeforeFileExtension = f.getName().length() - 3;
                String fileExtension = f.getName().substring(indexBeforeFileExtension);

                if (indexBeforeFileExtension >= 0 && fileExtension.equals(Constants.FILE_EXTENSION))
                    fileNames.add(f.getName().substring(0, indexBeforeFileExtension));
            }

            ArrayAdapter<String> listAdapter = new PlayableCustomAdapter(this, this, fileNames);
            lvFiles.setAdapter(listAdapter);
        }
    }

    public void startSettings(MenuItem item) {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
        finish();
    }

    public void startAbout(MenuItem item) {
    }

    public void deleteSelectedFiles(MenuItem item) {
        displayDecisionDialog();
    }

    private void displayDecisionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_files_question).
                setPositiveButton(Constants.YES, dialogClickListener).show();
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
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
        ListAdapter adapter = lvFiles.getAdapter();

        for (int i = 0; i < adapter.getCount(); i++) {
            PlayableCustomAdapter row = (PlayableCustomAdapter) adapter.getItem(i);
            if (row.isChecked(i))
                deleteFileFromDisk(i, lvFiles);
        }
    }

    private void deleteFileFromDisk(int i, ListView lvFiles) {
        File workDirectory = this.getFilesDir();
        File[] files = workDirectory.listFiles();
        for (File f : files) {
            if (f.getName().equals(fileNames.get(i) + Constants.FILE_EXTENSION))
                f.delete();
        }
        listFiles(lvFiles);
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
