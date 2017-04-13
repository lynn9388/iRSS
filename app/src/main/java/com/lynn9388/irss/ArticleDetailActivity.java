package com.lynn9388.irss;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lynn9388.irss.util.RSS;
import com.lynn9388.irss.util.RSSDatabaseHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class ArticleDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private static RSSDatabaseHelper databaseHelper;

    private CollapsingToolbarLayout toolbarLayout;
    private ImageView imageView;
    private Toolbar toolbar;
    private TextView titleView;
    private WebView webView;
    private FloatingActionButton button;

    private String url;
    private String title;
    private String photoURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        databaseHelper = new RSSDatabaseHelper(this);

        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout_article_detail);
        imageView = (ImageView) findViewById(R.id.image_article_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar_article_detail);
        titleView = (TextView)findViewById(R.id.text_title_article_detail);
        webView = (WebView) findViewById(R.id.web_view_article_detail);
        button = (FloatingActionButton)findViewById(R.id.button_article_detail);

        url = getIntent().getStringExtra(RSS.ArticleEntry.COLUMN_NAME_URL);
        Cursor articleCursor = RSS.ArticleEntry.query(databaseHelper, url);
        photoURL = articleCursor.getString(articleCursor.getColumnIndex(RSS.ArticleEntry.COLUMN_NAME_PHOTO_URL));
        loadImage();

        int feedId = articleCursor.getInt(articleCursor.getColumnIndex(RSS.ArticleEntry.COLUMN_NAME_FEED_ID));
        Cursor feedCursor = RSS.FeedEntry.query(databaseHelper, feedId);
        String feedName = feedCursor.getString(feedCursor.getColumnIndex(RSS.FeedEntry.COLUMN_NAME_NAME));
        toolbarLayout.setTitle(feedName);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = articleCursor.getString(articleCursor.getColumnIndex(RSS.ArticleEntry.COLUMN_NAME_TITLE));
        titleView.setText(title);

        String content = articleCursor.getString(articleCursor.getColumnIndex(RSS.ArticleEntry.COLUMN_NAME_CONTENT));
        content = "<link rel=\"stylesheet\" href=\"file:///android_asset/style.css\" type=\"text/css\" />" + content;
        //webView.getSettings().setJavaScriptEnabled(true);
        webView.loadDataWithBaseURL("", content, "text/html", "UTF-8", "");

        button.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        RSS.FavoriteEntry.insert(databaseHelper, url, title, photoURL);
        Snackbar.make(v, getString(R.string.message_added_to_favorite), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.reload();
    }

    private void loadImage() {
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(ArticleDetailActivity.this);
        ImageSize imageSize = new ImageSize(450, 250);
        ImageLoader.getInstance().init(configuration);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        ImageLoader.getInstance().loadImage(photoURL, imageSize, options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                imageView.setImageBitmap(loadedImage);
                Palette palette = Palette.from(loadedImage).generate();
                Palette.from(loadedImage).generate(new Palette.PaletteAsyncListener() {

                    @Override
                    public void onGenerated(Palette palette) {
                        Palette.Swatch vibrant = palette.getVibrantSwatch();
                        if (vibrant != null) {
                            toolbarLayout.setStatusBarScrimColor(vibrant.getRgb());
                            toolbarLayout.setContentScrimColor(vibrant.getRgb());
                        } else {
                            toolbarLayout.setStatusBarScrimColor(ContextCompat.getColor(ArticleDetailActivity.this, R.color.primary_color));
                        }
                    }
                });
            }
        });

    }
}
