package com.easyapps.teleprompter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listFiles();
    }

    public void createTextFile(View view) {
        Intent i = new Intent(this, CreateFileActivity.class);
        startActivity(i);

        finish();
    }

    private void listFiles() {
        File workDirectory = this.getFilesDir();
        File[] files = workDirectory.listFiles();

        if (files != null && files.length > 0) {
            ListView lvFiles = (ListView) findViewById(R.id.lvFiles);

            ArrayList<String> fileNames = new ArrayList<>();
            for (File f : files) {
                fileNames.add(f.getName());
            }

            ArrayAdapter<String> listAdapter = new ArrayAdapter<>(this,
                    R.layout.support_simple_spinner_dropdown_item, fileNames);
            lvFiles.setAdapter(listAdapter);
        }
    }
}
