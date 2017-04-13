package com.lynn9388.irss.blog;

import android.os.Parcelable;

import com.lynn9388.irss.util.Article;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Lynn on 2015/9/14.
 */
public interface Blog extends Serializable {
    public static final String TYPE = "BLOG_TYPE";
    public String getFirstArticleURL();
    public String getPreviousArticleURL(String url);
    public String getNextArticleURL(String url);
    public Article getArticle(String url);
}
