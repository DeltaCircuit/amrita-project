package com.amrita.ahead.rssreader;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface NewsDao {
    @Insert
    public void insert(News... news);

    @Query("SELECT * FROM news ORDER BY epochDate DESC LIMIT :num")
    public News[] getNews(Integer num);

    @Query("SELECT * FROM news WHERE headline LIKE :query OR summary LIKE :query")
    public News[] getFilteredNews(String query);

    @Query("UPDATE news SET isRead = 1 WHERE url = :link")
    public void markAsRead(String link);

    @Query("SELECT isRead FROM news WHERE url = :link")
    public int isRead(String link);

    @Query("SELECT EXISTS(SELECT 1 FROM news WHERE url = :link)")
    public int articleExistsAlready(String link);

    @Query("DELETE FROM news WHERE epochDate >= strftime(:days)")
    public void deleteNewsOlderThen(String days);

    @Query("DELETE FROM news")
    public void dropTable();
}