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
import android.widget.ListView;
import android.widget.Toast;

import com.easyapps.teleprompter.components.PlayableCustomAdapter;
import com.easyapps.teleprompter.messages.Constants;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ActivityCallback {

    private final List<String> fileNames = new ArrayList<>();
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
        for (File f : getAppFiles()) {
            int indexBeforeFileExtension = f.getName().length() - 3;
            fileNames.add(f.getName().substring(0, indexBeforeFileExtension));
        }
        lvFiles.setAdapter(new PlayableCustomAdapter(this, this, fileNames));
        ((PlayableCustomAdapter) lvFiles.getAdapter()).notifyDataSetChanged();
    }

    public void startSettings(MenuItem item) {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
        finish();
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
                setPositiveButton(Constants.YES, dialogClickListener)
                .setNegativeButton(Constants.NO, dialogClickListener).show();
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

        List<Integer> positionsToDelete = new ArrayList<>();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.isChecked(i)) {
                positionsToDelete.add(i);
            }
        }

        deleteFilesFromDisk(positionsToDelete, adapter);
    }

    private void deleteFilesFromDisk(List<Integer> positionsToDelete, ArrayAdapter adapter) {
        File[] files = getAppFiles();
        for (int position : positionsToDelete)
            if (files[position].delete()) {
                adapter.remove(fileNames.get(position));
                fileNames.remove(position);
            } else
                showMessage(fileNames.get(position));
        recreate(); // Horrible workaround, but it works!
    }

    private void showMessage(String... parameters) {
        String message = getResources().getString(R.string.delete_file_error, parameters);
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
    }

    private File[] getAppFiles() {
        File workDirectory = this.getFilesDir();
        File[] files = workDirectory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File directory, String fileName) {
                return fileName.endsWith(Constants.FILE_EXTENSION);
            }
        });
        return files == null ? new File[]{} : files;
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
