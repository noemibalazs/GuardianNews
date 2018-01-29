package com.example.android.guardiannews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by Noemi on 1/26/2018.
 */

public class NewsAdapter extends ArrayAdapter<News> {

    public NewsAdapter(Context context, ArrayList<News> news){
        super(context, 0, news);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_item, parent,false);
        }

        News newsNews = getItem(position);

        TextView textViewTitle = listItemView.findViewById(R.id.news_title);
        textViewTitle.setText(newsNews.getTitle());

        TextView textViewText = listItemView.findViewById(R.id.news_text);
        textViewText.setText(newsNews.getText());

        TextView textViewDate = listItemView.findViewById(R.id.news_date);
        String date = (newsNews.getDate()).substring(0, 10);
        textViewDate.setText(date);

        ImageView imageViewImage = listItemView.findViewById(R.id.news_image);
        imageViewImage.setImageBitmap(newsNews.getThumbnail());

        return listItemView;
    }

}
