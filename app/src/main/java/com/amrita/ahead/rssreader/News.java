package com.amrita.ahead.rssreader;

import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "news")
public class News {
    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @ColumnInfo(name = "epochDate")
    private long epochDate;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "headline")
    private String headline;

    @ColumnInfo(name = "summary")
    private String summary;

    @ColumnInfo(name = "url")
    private String url;

    @ColumnInfo(name = "thumbnailUrl")
    private String thumbnailUrl;

    @ColumnInfo(name = "isRead")
    private boolean isRead;


    public News(String date, String headline, String summary, String url, String thumbnailUrl) {
        this.date = date;
        this.headline = headline;
        this.summary = summary;
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;
        this.epochDate = this.convertDateToEpoch(date);
        this.isRead = false;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public long getEpochDate() {
        return this.epochDate;
    }

    public void setEpochDate(long epochDate) {
        this.epochDate = epochDate;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHeadline() {
        return this.headline;
    }

    public void setHeadline(String header) {
        this.headline = header;
    }

    public String getSummary() {
        return this.summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String newsLink) {
        this.url = newsLink;
    }

    public String getThumbnailUrl() {
        return this.thumbnailUrl;
    }

    public void setThumbnailUrl(String imageLink) {
        this.thumbnailUrl = imageLink;
    }

    public void setIsRead(boolean bol) {
        this.isRead = bol;
    }

    public boolean getIsRead() {
        return this.isRead;
    }

    public long convertDateToEpoch(String newsDate) {
        long tempDate = 0;

        try {
            DateFormat formatter =
                    new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
            Date date = formatter.parse(newsDate);
            tempDate = date.getTime();
        } catch (ParseException e) {
            Log.d("Date", e.getMessage());
        }

        return tempDate;
    }
}
