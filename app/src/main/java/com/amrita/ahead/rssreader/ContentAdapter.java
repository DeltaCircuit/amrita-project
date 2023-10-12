package com.amrita.ahead.rssreader;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder> {
    private ArrayList<News> newsList;
    private DBWrapper mDB;

    public class ContentViewHolder extends RecyclerView.ViewHolder {
        public static final String NEWS_ARTICLE = "NEWS_ARTICLE ";
        public ImageView newsImageView;
        public TextView newsDateTextView;
        public TextView newsHeaderTextView;
        public TextView newsSummaryTextView;

        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            this.newsImageView = itemView.findViewById(R.id.newsImage);
            this.newsDateTextView = itemView.findViewById(R.id.newsDate);
            this.newsHeaderTextView = itemView.findViewById(R.id.newsHeader);
            this.newsSummaryTextView = itemView.findViewById(R.id.newsSummary);

            itemView.setOnClickListener(v -> {
                News clickedItem = ContentAdapter.this.newsList.get(getAdapterPosition());

                Intent intent = new Intent(itemView.getContext(), ArticleViewActivity.class);
                intent.putExtra(NEWS_ARTICLE, clickedItem.getUrl());
                itemView.getContext().startActivity(intent);

                mDB.markNewsAsRead(clickedItem.getUrl());
                this.newsHeaderTextView.setTextColor(Color.parseColor("#bdbdbd"));
                this.newsSummaryTextView.setTextColor(Color.parseColor("#bdbdbd"));
            });
        }
    }

    public ContentAdapter(ArrayList<News> newsList) {                            // Constructor, stores the ArrayList.
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public ContentAdapter.ContentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_view_content, viewGroup, false);

        ContentViewHolder newsContent = new ContentViewHolder(v);
        return newsContent;
    }

    @Override
    public void onBindViewHolder(@NonNull ContentViewHolder contentViewHolder, int i) {
        News currentNewsItem = newsList.get(i);

        mDB = new DBWrapper(contentViewHolder.itemView.getContext(), "news");
        if (mDB.isNewsRead(newsList.get(i).getUrl())) {
            contentViewHolder.newsHeaderTextView.setTextColor(Color.parseColor("#bdbdbd"));
            contentViewHolder.newsSummaryTextView.setTextColor(Color.parseColor("#bdbdbd"));
        } else {
            contentViewHolder.newsHeaderTextView.setTextColor(Color.parseColor("#ffffff"));
            contentViewHolder.newsSummaryTextView.setTextColor(Color.parseColor("#ffffff"));
        }

        Glide.with(contentViewHolder.itemView.getContext()).
                load(newsList.get(i).getThumbnailUrl()).
                into(contentViewHolder.newsImageView);

        contentViewHolder.newsDateTextView.setText(currentNewsItem.getDate());
        contentViewHolder.newsHeaderTextView.setText(currentNewsItem.getHeadline());
        contentViewHolder.newsSummaryTextView.setText(currentNewsItem.getSummary());
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }
}