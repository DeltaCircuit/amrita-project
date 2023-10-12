package com.amrita.ahead.rssreader;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {News.class}, version = 1 ,exportSchema = false)
public abstract class NewsDB extends RoomDatabase {
    public abstract NewsDao daoAccess();
}