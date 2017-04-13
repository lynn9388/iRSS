package com.lynn9388.irss.util;

/**
 * Created by Lynn on 2015/9/14.
 */
public class Article {
    private String URL;
    private String title;
    private String subtitle;
    private String photoURL;
    private String content;
    private long timestamp;


    public String getURL() {
        return URL;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Article(String URL, String title, String subtitle, String photoURL, String content, long timestamp) {
        this.URL = URL;
        this.title = title;
        this.subtitle = subtitle;
        this.photoURL = photoURL;
        this.content = content;
        this.timestamp = timestamp;
    }
}
