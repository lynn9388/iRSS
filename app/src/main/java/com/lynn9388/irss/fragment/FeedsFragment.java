package com.lynn9388.irss.fragment;


import android.app.Dialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.lynn9388.irss.R;
import com.lynn9388.irss.blog.Blog;
import com.lynn9388.irss.blog.IPC;
import com.lynn9388.irss.dialog.AddFeedDialogFragment;
import com.lynn9388.irss.util.Article;
import com.lynn9388.irss.util.Feed;
import com.lynn9388.irss.util.RSS;
import com.lynn9388.irss.util.RSSDatabaseHelper;


/**
 * A simple {@link Fragment} subclass.
 */
public class FeedsFragment extends Fragment {
    public static final String TAG = FeedsFragment.class.getSimpleName();

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabsAdapter adapter;

    public FeedsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feeds, container, false);
        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        viewPager = (ViewPager) view.findViewById(R.id.view_pager);

        adapter = new TabsAdapter(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_feed:
                AddFeedDialogFragment addFeedDialogFragment = new AddFeedDialogFragment();
                addFeedDialogFragment.show(getActivity().getSupportFragmentManager(), null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_feeds, menu);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void addTab(DialogFragment dialogFragment) {
        Dialog dialog = dialogFragment.getDialog();
        EditText editName = (EditText) dialog.findViewById(R.id.edit_add_feed_dialog_feed_name);
        EditText editURL = (EditText) dialog.findViewById(R.id.edit_add_feed_dialog_feed_url);
        String name = editName.getText().toString();
        String url = editURL.getText().toString();
        adapter.addFeed(name, url);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private class TabsAdapter extends FragmentStatePagerAdapter {
        private RSSDatabaseHelper databaseHelper;
        private Cursor cursor;

        public TabsAdapter(FragmentManager fm) {
            super(fm);
            databaseHelper = new RSSDatabaseHelper(FeedsFragment.this.getActivity());
            cursor = RSS.FeedEntry.queryAll(databaseHelper, null);
        }

        @Override
        public Fragment getItem(int position) {
            ArticleListFragment fragment = new ArticleListFragment();
            cursor.moveToPosition(position);
            Bundle bundle = new Bundle();
            bundle.putSerializable(Blog.TYPE, new IPC());
            bundle.putInt(RSS.FeedEntry._ID, cursor.getInt(cursor.getColumnIndex(RSS.FeedEntry._ID)));
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return cursor.getCount();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            cursor.moveToPosition(position);
            return cursor.getString(cursor.getColumnIndex(RSS.FeedEntry.COLUMN_NAME_NAME));
        }

        public void addFeed(String name, String url) {
            String iconURL = Feed.getIconURL(url);
            RSS.FeedEntry.insert(databaseHelper, name, url, iconURL);
            cursor = RSS.FeedEntry.queryAll(databaseHelper, null);
        }
    }


}
