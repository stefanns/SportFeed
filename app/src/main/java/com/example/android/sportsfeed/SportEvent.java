package com.example.android.sportsfeed;

import android.graphics.Bitmap;

/**
 * Created by Stefan on 5/3/2018.
 */

public class SportEvent {
    // used for news title
    private String title;
    //used for date of the news
    private String date;
    //section of which category the news is (only sports)
    private String section;
    //url used to access the news
    private String url;
    //image of the news
    private Bitmap image;

    //constructor for the class
    public SportEvent(String titleEvent, String dateEvent, String sectionEvent,
                      String urlEvent, Bitmap imageEvent) {
        image = imageEvent;
        title = titleEvent;
        date = dateEvent;
        section = sectionEvent;
        url = urlEvent;
    }

    //get the title
    public String getTitle() {
        return title;
    }

    //get the date
    public String getDate() {
        return date;
    }

    //get the section
    public String getSection() {
        return section;
    }

    //get the url
    public String getUrl() {
        return url;
    }

    //get image of the news
    public Bitmap getImage() {
        return image;
    }
}
