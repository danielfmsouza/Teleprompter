package com.easyapps.teleprompter;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.easyapps.teleprompter.messages.Constants;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<String> fileNames = new ArrayList<>();
    private int selectedFile = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView lvFiles = (ListView) findViewById(R.id.lvFiles);
        onItemFileClickListener(lvFiles);
        listFiles(lvFiles);
    }

    private void onItemFileClickListener(ListView lvFiles) {
        lvFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                selectedFile = position;
                displayDecisionDialog(fileNames.get(position));
            }
        });
    }

    private void displayDecisionDialog(String s) {
        String message = MessageFormat.format(
                getResources().getString(R.string.start_prompt_question), s);

        String yes = getResources().getString(R.string.yes);
        String no = getResources().getString(R.string.no);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setPositiveButton(yes, dialogClickListener)
                .setNegativeButton(no, dialogClickListener).show();
    }

    public void createTextFile(View view) {
        Intent i = new Intent(this, CreateFileActivity.class);
        startActivity(i);

        finish();
    }

    private void listFiles(ListView lvFiles) {
        File workDirectory = this.getFilesDir();
        File[] files = workDirectory.listFiles();

        if (files != null && files.length > 0) {
            for (File f : files) {
                int indexBeforeFileExtension = f.getName().length() - 3;
                String fileExtension = f.getName().substring(indexBeforeFileExtension);

                if (indexBeforeFileExtension >= 0 && fileExtension.equals(Constants.FILE_EXTENSION))
                    fileNames.add(f.getName().substring(0, indexBeforeFileExtension));
            }

            ArrayAdapter<String> listAdapter = new ArrayAdapter<>(this,
                    R.layout.support_simple_spinner_dropdown_item, fileNames);
            lvFiles.setAdapter(listAdapter);
        }
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    startPrompter();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };

    private void startPrompter() {
        Intent i = new Intent(this, TestActivity.class);

        Bundle b = new Bundle();
        b.putString(Constants.FILE_NAME_PARAM, fileNames.get(selectedFile));
        i.putExtras(b);
        startActivity(i);

        finish();
    }
}
