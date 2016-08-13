package com.easyapps.teleprompter;

import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Xml;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.easyapps.teleprompter.messages.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class TestActivity extends AppCompatActivity {

    private boolean isStarted = false;
    private PausableAnimation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);

        Bundle b = getIntent().getExtras();
        String fileName = null;
        if (b != null)
            fileName = b.getString(Constants.FILE_NAME_PARAM);

//        XmlResourceParser parser = getResources().getAnimation(R.anim.text_prompter);
//        animation = new PausableAnimation(this, Xml.asAttributeSet(parser));

        animation = (PausableAnimation) AnimationUtils.loadAnimation(this, R.anim.text_prompter);

        final TextView prompter = (TextView) findViewById(R.id.fullscreen_content);

        if (fileName != null) {
            prompter.setText(getFileContent(fileName));
            prompter.startAnimation(animation);
        }
    }

    private String getFileContent(String fileName) {
        String completeFileName = fileName + Constants.FILE_EXTENSION;
        File[] files = this.getFilesDir().listFiles();
        for (File f : files) {
            if (f.getName().equals(completeFileName))
                return readFile(f);
        }
        return null;
    }

    private String readFile(File f) {
        StringBuilder text = new StringBuilder();
        String line;
        try {
            BufferedReader br = new BufferedReader((new FileReader(f)));
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text.toString();
    }

    public void startStop(View view) {
        if (isStarted) {
            animation.pause();
            isStarted = false;
        } else {
            animation.resume();
            isStarted = true;
        }
    }
}
