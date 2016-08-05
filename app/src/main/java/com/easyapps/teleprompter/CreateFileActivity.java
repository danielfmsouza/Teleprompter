package com.easyapps.teleprompter;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class CreateFileActivity extends AppCompatActivity {

    private static final String TEXT_WRITTEN = "TEXT_WRITTEN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_file);

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            EditText etTextFile = (EditText) findViewById(R.id.etTextFile);

            // Restore value of members from saved state
            String savedText = savedInstanceState.getString(TEXT_WRITTEN);
            etTextFile.setText(savedText);

            // Hide label that shows the information inside text box
            if (savedText != null && !savedText.equals(""))
                hideLabel(null);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        EditText etTextFile = (EditText) findViewById(R.id.etTextFile);

        savedInstanceState.putString(TEXT_WRITTEN, etTextFile.getText().toString());

        super.onSaveInstanceState(savedInstanceState);
    }

    public void hideLabel(View view) {
        TextView tvTextFile = (TextView) findViewById(R.id.tvTextFile);
        tvTextFile.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);

        finish();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            return true;
        } else
            return super.dispatchKeyEvent(event);
    }
}
