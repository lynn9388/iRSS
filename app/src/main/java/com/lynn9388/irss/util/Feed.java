package com.lynn9388.irss.util;

import android.os.AsyncTask;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Lynn on 2015/9/11.
 */
public class Feed {
    public static final String TAG = "com.lynn9388.irss.util.Feed";

    public static String getIconURL(String urlString) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        String iconURL = url.getProtocol() + "://" + url.getHost() + "/favicon.ico";
        return iconURL;
    }

//    public class DownloadIconTask extends AsyncTask<String, > {
//
//    }
}
