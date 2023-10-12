package com.amrita.ahead.rssreader;

import android.content.Context;
import android.os.AsyncTask;

import androidx.room.Room;

import java.util.concurrent.ExecutionException;

public class DBWrapper {
    private NewsDB newsDB;
    private String name;

    public DBWrapper(Context context, String dbName) {
        name = dbName;
        newsDB = Room.databaseBuilder(context, NewsDB.class, name).build();
    }

    public void insertNews(News... news) {
        new AsyncTask<News, Void, Void>() {
            @Override
            protected Void doInBackground(News... news) {
                newsDB.daoAccess().insert(news);
                return null;
            }
        }.execute(news);
    }

    public News[] getNews(Integer num) {
        News[] temp = null;
        try {
            temp = new AsyncTask<Integer, Void, News[]>() {
                @Override
                protected News[] doInBackground(Integer... integers) {
                    return newsDB.daoAccess().getNews(integers[0]);
                }
            }.execute(num).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return temp;
    }

    public News[] getFilteredNews(String query) {
        String useQuery = "%" + query + "%";

        News[] temp = null;
        try {
            temp = new AsyncTask<String, Void, News[]>() {
                @Override
                protected News[] doInBackground(String... strings) {
                    return newsDB.daoAccess().getFilteredNews(strings[0]);
                }
            }.execute(useQuery).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return temp;
    }

    public void markNewsAsRead(String link) {
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... strings) {
                newsDB.daoAccess().markAsRead(strings[0]);
                return null;
            }
        }.execute(link);
    }

    public boolean isNewsRead(String link) {
        Integer temp = 0;
        try {
            temp = new AsyncTask<String, Void, Integer>() {
                @Override
                protected Integer doInBackground(String... strings) {
                    return newsDB.daoAccess().isRead(strings[0]);
                }
            }.execute(link).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (temp == 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean hasNewsExists(String link) {
        Integer temp = 0;
        try {
            temp = new AsyncTask<String, Void, Integer>() {
                @Override
                protected Integer doInBackground(String... strings) {
                    return newsDB.daoAccess().articleExistsAlready(strings[0]);
                }
            }.execute(link).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (temp == 0) {
            return false;
        } else {
            return true;
        }
    }

    public void deleteStaleNews(String days) {

        String dateStatement = "'%s', 'now' '-" + days + " day'";

        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... strings) {
                newsDB.daoAccess().deleteNewsOlderThen(dateStatement);
                return null;
            }
        }.execute(days);
    }
}