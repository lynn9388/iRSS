package com.lynn9388.irss.blog;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.lynn9388.irss.util.Article;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Lynn on 2015/9/14.
 */
public class IPC implements Blog {
    public static final String WESITE_NAME = "ipc.me";
    public static final String WEBSITE_ADDRESS = "http://www.ipc.me/";

    @Override
    public String getFirstArticleURL() {
        String firstLink = null;
        try {
            Document doc = Jsoup.connect(WEBSITE_ADDRESS).get();
            Elements titles = doc.select("[class$=entry-title]");
            firstLink = titles.get(1).attr("href");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return firstLink;
    }

    @Override
    public String getPreviousArticleURL(String url) {
        String previousURL = null;
        int page = 1;
        if (url == null) {
            previousURL = getFirstArticleURL();
        } else {
            try {
                String link = url;
                Document doc = Jsoup.connect(url).get();
                Element entryLinks = doc.select("ul.entry-relate-links").first();
                Element element = entryLinks.select("a[href]").first();
                if (element.text().equals("木有了")) {
                    previousURL = url;
                } else {
                    previousURL = element.attr("href");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return previousURL;
    }

    @Override
    public String getNextArticleURL(String link) {
        String nextLink = null;
        if (link == null) {
            nextLink = getFirstArticleURL();
        } else {
            try {
                Document doc = Jsoup.connect(link).get();
                Element entryLinks = doc.select("ul.entry-relate-links").first();
                nextLink = entryLinks.select("a[href]").get(1).attr("href");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return nextLink;
    }

    @Override
    public Article getArticle(String url) {
        String title = null;
        String subtitle = null;
        String photoURL = null;
        String content = null;
        String date = null;
        long timestamp = 0;
        try {
            Document doc = Jsoup.connect(url).get();
            Element post = doc.select("div.post").first();
            title = post.select("h1#post-title").first().text();

            Element pageContent = post.select("div.post_content").first();
            subtitle = pageContent.select("p").first().text();

            photoURL = pageContent.select("img").first().attr("src");

            pageContent.select("p").get(1).remove();
            content = pageContent.toString();

            Element detail = post.select("div.post_detail").first().select("ul").first();
            date = detail.select("li").last().text()
                    .replace("生产日期：", "").replace("年", " ").replace("月", " ").replace("日", " ")
                    .replace("-", " ").replace("时", " ").replace("分", " ").replace("秒", " ");
            String[] strings = date.split("\\s+");
            int year = Integer.valueOf(strings[0]);
            int month = Integer.valueOf(strings[1]);
            int day = Integer.valueOf(strings[2]);
            int hour = Integer.valueOf(strings[3]);
            int minute = Integer.valueOf(strings[4]);
            int second = Integer.valueOf(strings[5]);
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day, hour, minute, second);
            timestamp = calendar.getTimeInMillis();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Article(url, title, subtitle, photoURL, content, timestamp);
    }
}
