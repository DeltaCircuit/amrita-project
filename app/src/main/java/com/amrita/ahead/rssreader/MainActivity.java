package com.amrita.ahead.rssreader;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;


import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;


import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String SERVICE_ACTION_RSS = "SERVICE_ACTION_RSS";
    private RecyclerView recyclerView;

    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private SearchView searchView;
    private DBWrapper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("News Reader");

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);

        recyclerView = findViewById(R.id.contentView);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadNewsOntoAdapter(false, "");
        startPendingBackgroundService();
    }

    public static void startBackgroundService(Context context) {
        Intent intent = new Intent(context, RSSPullService.class);
        context.startService(intent);
    }

    public void startPendingBackgroundService() {
        SharedPreferences sharedPref =
                getSharedPreferences(UserPreferenceActivity.SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE);

        String syncInterval =
                sharedPref.getString("syncIntervalNews", "10 min");


        int interval = 5 * 60 * 1000;

        switch (syncInterval) {
            case "5 min":
                interval = 5 * 60 * 1000;
                break;
            case "10 min":
                interval = 10 * 60 * 1000;
                break;
            case "30 min":
                interval = 30 * 60 * 1000;
                break;
            case "1 hour":
                interval = 1 * 60 * 60 * 1000;
                break;
            case "6 hour":
                interval = 6 * 60 * 60 * 1000;
                break;
            case "12 hour":
                interval = 12 * 60 * 60 * 1000;
                break;

        }

        Intent intent = new Intent(this, RSSPullService.class);
        PendingIntent pIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_MUTABLE);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);

        alarm.setRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(), interval, pIntent);
    }

    public void loadNewsOntoAdapter(boolean isFiltered, String query) {
        db = new DBWrapper(this, "news");
        SharedPreferences sharedPreferences =
                getSharedPreferences(UserPreferenceActivity.SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE);

        int numberOfNews = sharedPreferences.getInt("numberOfNewsToShow", 10);

        News[] tempNews;
        if (isFiltered) {
            tempNews = db.getFilteredNews(query);
        } else {
            tempNews = db.getNews(numberOfNews);
        }


        ArrayList<News> news = new ArrayList<>(Arrays.asList(tempNews));
        adapter = new ContentAdapter(news);
        recyclerView.setAdapter(adapter);
    }

    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(bReceiver, new IntentFilter(SERVICE_ACTION_RSS));
    }

    private BroadcastReceiver bReceiver = new BroadcastReceiver() {                             // Broadcast receiver to receive from Service.
        @Override
        public void onReceive(Context context, Intent intent) {
            loadNewsOntoAdapter(false, "");
        }
    };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadNewsOntoAdapter(true, query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.equals("")) {
                    loadNewsOntoAdapter(false, "");
                }
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.home) {
            return true;
        } else if (id == R.id.settings) {
            Intent intent = new Intent(this, UserPreferenceActivity.class);
            startActivity(intent);
            return true;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}