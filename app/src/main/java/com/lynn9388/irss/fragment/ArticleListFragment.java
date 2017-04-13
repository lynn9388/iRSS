package com.lynn9388.irss.fragment;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lynn9388.irss.ArticleDetailActivity;
import com.lynn9388.irss.R;
import com.lynn9388.irss.blog.Blog;
import com.lynn9388.irss.blog.IPC;
import com.lynn9388.irss.util.Article;
import com.lynn9388.irss.util.DividerItemDecoration;
import com.lynn9388.irss.util.RSS;
import com.lynn9388.irss.util.RSSDatabaseHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArticleListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static RSSDatabaseHelper databaseHelper;
    private static boolean isLoading;
    private static Cursor cursor;
    private static String firstURL;
    private static String lastURL;

    private static Blog blog;
    private static int feedId;

    private static SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView.Adapter adapter;

    public ArticleListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        databaseHelper = new RSSDatabaseHelper(getActivity());
        isLoading = false;

        Bundle bundle = getArguments();
        blog = (Blog) bundle.getSerializable(Blog.TYPE);
        feedId = bundle.getInt(RSS.FeedEntry._ID);

        View view = inflater.inflate(R.layout.fragment_article_list, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout_article_list);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycle_view_article_list);
        layoutManager = new LinearLayoutManager(getActivity());

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.primary_color, R.color.accent_color);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                int totalItemCount = ((LinearLayoutManager) layoutManager).getItemCount();
                if (lastVisibleItem == totalItemCount - 2 && dy > 0) {
                    if (!isLoading) {
                        new LoadOldArticlesTask().execute();
                    }
                }
            }
        });
        adapter = new ArticleListAdapter();
        recyclerView.setAdapter(adapter);
        onRefresh();
        return view;
    }

    @Override
    public void onRefresh() {
        if (cursor.getCount() > 5) {
            new LoadNewArticlesTask().execute();
        } else {
            new LoadOldArticlesTask().execute();
        }
    }

    public static void updateCursor() {
        String[] projection = {RSS.ArticleEntry.COLUMN_NAME_URL,
                RSS.ArticleEntry.COLUMN_NAME_TITLE, RSS.ArticleEntry.COLUMN_NAME_PHOTO_URL};
        cursor = RSS.ArticleEntry.query(databaseHelper, projection, feedId);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            firstURL = cursor.getString(cursor.getColumnIndex(RSS.ArticleEntry.COLUMN_NAME_URL));
            cursor.moveToLast();
            lastURL = cursor.getString(cursor.getColumnIndex(RSS.ArticleEntry.COLUMN_NAME_URL));
        }
    }

    public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListAdapter.ViewHolder> {
        private DisplayImageOptions options;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView title;
            public ImageView image;

            public ViewHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.text_article_item_title);
                image = (ImageView) itemView.findViewById(R.id.image_article_item);
            }
        }

        public ArticleListAdapter() {
            updateCursor();

            ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(ArticleListFragment.this.getActivity());
            ImageLoader.getInstance().init(configuration);
            options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_item, parent, false);
            final ViewHolder viewHolder = new ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cursor.moveToPosition(viewHolder.getAdapterPosition());
                    String link = cursor.getString(cursor.getColumnIndex(RSS.ArticleEntry.COLUMN_NAME_URL));
                    Intent intent = new Intent(getActivity(), ArticleDetailActivity.class);
                    intent.putExtra(RSS.ArticleEntry.COLUMN_NAME_URL, link);
                    startActivity(intent);
                }
            });
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            cursor.moveToPosition(position);
            holder.title.setText(cursor.getString(cursor.getColumnIndex(RSS.ArticleEntry.COLUMN_NAME_TITLE)));
            String photoURL = cursor.getString(cursor.getColumnIndex(RSS.ArticleEntry.COLUMN_NAME_PHOTO_URL));
            ImageLoader.getInstance().displayImage(photoURL, holder.image, options);
        }

        @Override
        public int getItemCount() {
            return cursor.getCount();
        }
    }

    private static class LoadNewArticlesTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            int previousArticlesAccount = cursor.getCount();
            updateCursor();
            int currentArticlesAccount = cursor.getCount();
            adapter.notifyItemRangeChanged(0, currentArticlesAccount - previousArticlesAccount);
            isLoading = false;
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected Void doInBackground(Void... params) {
            isLoading = true;
            String newURL;
            int index;
            while ((newURL = blog.getPreviousArticleURL(firstURL)) != null
                    && (firstURL == null || !newURL.equals(firstURL))) {
                Article article = blog.getArticle(newURL);
                Log.d("lynn", article.getTitle());
                Log.d("lynn", " " + article.getTimestamp());
                if (firstURL == null) {
                    index = 5000;
                } else {
                    Cursor articleCursor = RSS.ArticleEntry.query(databaseHelper, firstURL);
                    index = articleCursor.getInt(articleCursor.getColumnIndex(RSS.ArticleEntry.COLUMN_NAME_PAGE_INDEX)) - 1;
                }
                RSS.ArticleEntry.insert(
                        databaseHelper,
                        newURL,
                        feedId,
                        article.getTitle(),
                        article.getSubtitle(),
                        article.getPhotoURL(),
                        article.getContent(),
                        article.getTimestamp(),
                        index
                );
                firstURL = newURL;
            }
            return null;
        }
    }

    private static class LoadOldArticlesTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            int previousArticlesAccount = cursor.getCount();
            updateCursor();
            int currentArticlesAccount = cursor.getCount();
            adapter.notifyItemRangeChanged(previousArticlesAccount - 1, currentArticlesAccount - previousArticlesAccount);
            isLoading = false;
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected Void doInBackground(Void... params) {
            isLoading = true;
            String newURL;
            int index;
            int i = 0;
            while ((newURL = blog.getNextArticleURL(lastURL)) != null
                    && i < 5
                    && (lastURL == null || !newURL.equals(lastURL))) {
                Article article = blog.getArticle(newURL);
                Log.d("lynn", article.getTitle() + "  " + article.getURL());
                Log.d("lynn", " " + article.getTimestamp());
                if (lastURL == null) {
                    index = 5000;
                } else {
                    Cursor articleCursor = RSS.ArticleEntry.query(databaseHelper, lastURL);
                    index = articleCursor.getInt(articleCursor.getColumnIndex(RSS.ArticleEntry.COLUMN_NAME_PAGE_INDEX)) + 1;
                }
                RSS.ArticleEntry.insert(
                        databaseHelper,
                        newURL,
                        feedId,
                        article.getTitle(),
                        article.getSubtitle(),
                        article.getPhotoURL(),
                        article.getContent(),
                        article.getTimestamp(),
                        index
                );
                lastURL = newURL;
                i++;
            }
            return null;
        }
    }

}
