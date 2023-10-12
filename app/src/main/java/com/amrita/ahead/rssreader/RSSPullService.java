package com.amrita.ahead.rssreader;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


public class RSSPullService extends IntentService {
    private SharedPreferences sharedPreferences;
    private String newsDate;
    private String newsHeadline;
    private String newsSummary;
    private String thumbnailUrl;
    private String url;
    private DBWrapper db;

    public RSSPullService() {
        super("RSSPullService");
    }

    public RSSPullService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        sharedPreferences = getSharedPreferences(UserPreferenceActivity.SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE);
        db = new DBWrapper(getBaseContext(), "news") {
        };
        String rssURL = sharedPreferences.getString("rss", "");

        try {
            URL url = new URL(rssURL);
            ProcessRSSFeed(url);
            db.deleteStaleNews("3");
            sendBroadcast();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


    }

    private void sendBroadcast() {
        Intent intent = new Intent(MainActivity.SERVICE_ACTION_RSS);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void ProcessRSSFeed(URL url) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(url.openConnection().getInputStream(), "UTF_8");

            boolean insideItem = false;
            String thumbnail = new String();
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {

                if (eventType == XmlPullParser.START_TAG) {
                    Log.d("Loop", xpp.getName().toString());
                    if (xpp.getName().equals("item")) {
                        insideItem = true;

                    } else if (xpp.getName().equalsIgnoreCase("title")) {
                        if (insideItem) {
                            newsHeadline = xpp.nextText();
                        }
                    } else if (xpp.getName().equalsIgnoreCase("description")) {
                        if (insideItem) {
                            newsSummary = xpp.nextText();
                        }
                    } else if (xpp.getName().equalsIgnoreCase("enclosure")) {
                        if (insideItem) {
                            thumbnailUrl = xpp.getAttributeValue(null, "url");
                        }
                    } else if (xpp.getName().equalsIgnoreCase("link")) {
                        if (insideItem) {
                            this.url = xpp.nextText();
                        }
                    } else if (xpp.getName().equalsIgnoreCase("media:content")) {
                        if (thumbnail.isEmpty()) {
                            thumbnail = xpp.getAttributeValue(null, "url");
                            thumbnailUrl = thumbnail;
                            Log.d("thumbnail is", thumbnail.toString());
                        }
                    } else if (xpp.getName().equalsIgnoreCase("pubDate")) {
                        if (insideItem) {
                            newsDate = xpp.nextText();

                            if (!db.hasNewsExists(this.url)) {
                                db.insertNews(new News(newsDate, newsHeadline,
                                        newsSummary, this.url, thumbnailUrl));
                            }
                            thumbnailUrl = "";
                            newsDate = "";
                        }
                    }
                } else if (eventType == XmlPullParser.END_TAG &&
                        xpp.getName().equalsIgnoreCase("item")) {
                    insideItem = false;
                } else if (eventType == XmlPullParser.END_TAG &&
                        xpp.getName().equalsIgnoreCase("media:content")) {
                    thumbnail = new String();
                }
                eventType = xpp.next();
            }
        } catch (MalformedURLException ex) {
            Log.d("RSS service", "ProcessRSSFeed: " + ex);
        } catch (XmlPullParserException ex) {
            Log.d("RSS service", "ProcessRSSFeed: " + ex);
        } catch (IOException ex) {
            Log.d("RSS service", "ProcessRSSFeed: " + ex);
        }
    }
}
