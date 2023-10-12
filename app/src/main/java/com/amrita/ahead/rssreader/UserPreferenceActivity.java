package com.amrita.ahead.rssreader;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class UserPreferenceActivity extends AppCompatActivity {
    public static final String SHARED_PREFS_SETTINGS = "SHARED_PREFS_SETTINGS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_preference);
        setTitle("Settings");

        SharedPreferences sharedpreferences = getSharedPreferences(SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE);
        Spinner numberOfNewsDisplay = findViewById(R.id.spinner_NoOfNews);
        Spinner syncIntervalNews = findViewById(R.id.spinner_SyncInterval);
        EditText rssInput = findViewById(R.id.edit_rss_insert);
        TextView rssShow = findViewById(R.id.text_show_rss);
        Button btnApplyChanges = findViewById(R.id.btn_apply);

        ArrayList<Integer> displayItemsList = new ArrayList<>();
        ArrayList<String> syncIntervalList = new ArrayList<>();

        displayItemsList.add(10);
        displayItemsList.add(20);
        displayItemsList.add(50);
        displayItemsList.add(70);
        displayItemsList.add(100);

        syncIntervalList.add("5 min");
        syncIntervalList.add("10 min");
        syncIntervalList.add("30 min");
        syncIntervalList.add("1 hour");
        syncIntervalList.add("6 hour");
        syncIntervalList.add("12 hour");

        // https://rss.nytimes.com/services/xml/rss/nyt/HomePage.xml


        ArrayAdapter<Integer> newsDisplayAdapter = new ArrayAdapter<>(UserPreferenceActivity.this,
                android.R.layout.simple_list_item_1, displayItemsList);
        newsDisplayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numberOfNewsDisplay.setAdapter(newsDisplayAdapter);

        ArrayAdapter<String> syncItemsAdapter = new ArrayAdapter<>(UserPreferenceActivity.this,
                android.R.layout.simple_list_item_1, syncIntervalList);
        syncItemsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        syncIntervalNews.setAdapter(syncItemsAdapter);

        int numberOfNews = sharedpreferences.getInt("numberOfNewsToShow", 0);
        String syncInterval = sharedpreferences.getString("syncIntervalNews", "");

        numberOfNewsDisplay.setSelection(displayItemsList.indexOf(numberOfNews));
        syncIntervalNews.setSelection(syncIntervalList.indexOf(syncInterval));

        rssShow.setText(sharedpreferences.getString("rss", ""));


        rssInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                rssShow.setText(rssInput.getText().toString());
                rssInput.setText("");

                InputMethodManager imm =
                        (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return true;
            }
            return false;
        });


        btnApplyChanges.setOnClickListener(v -> {

            SharedPreferences.Editor editor = sharedpreferences.edit();

            editor.putInt("numberOfNewsToShow",
                    Integer.parseInt(numberOfNewsDisplay.getSelectedItem().toString()));

            editor.putString("syncIntervalNews",
                    syncIntervalNews.getSelectedItem().toString());

            editor.putString("rss", rssShow.getText().toString());
            editor.apply();


            Toast toast = Toast.makeText(UserPreferenceActivity.this,
                    "changes applied successfully", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            MainActivity.startBackgroundService(this);
        });
    }
}
