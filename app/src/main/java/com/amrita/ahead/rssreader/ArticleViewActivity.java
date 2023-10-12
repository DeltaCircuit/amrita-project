package com.amrita.ahead.rssreader;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class ArticleViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_view);

        String newsLink = getIntent().
                getStringExtra(ContentAdapter.ContentViewHolder.NEWS_ARTICLE);

        WebView articleWebView = findViewById(R.id.articleWebView);
        articleWebView.setWebViewClient(new WebViewClient());
        articleWebView.loadUrl(newsLink);
    }
}
