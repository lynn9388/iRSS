 package com.lynn9388.irss.fragment;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lynn9388.irss.ArticleDetailActivity;
import com.lynn9388.irss.R;
import com.lynn9388.irss.util.DividerItemDecoration;
import com.lynn9388.irss.util.RSS;
import com.lynn9388.irss.util.RSSDatabaseHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

 /**
 * A simple {@link Fragment} subclass.
 */
public class FavoritesFragment extends Fragment {
    public static final String TAG = FavoritesFragment.class.getSimpleName();
     private static RSSDatabaseHelper databaseHelper;
     private static Cursor cursor;

     private RecyclerView recyclerView;
     private RecyclerView.LayoutManager layoutManager;
     private static RecyclerView.Adapter adapter;


    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        databaseHelper = new RSSDatabaseHelper(getActivity());

        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.recycle_view_favorites);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        adapter = new FavoritesAdapter();
        recyclerView.setAdapter(adapter);
        cursor = RSS.FavoriteEntry.queryAll(databaseHelper, null);
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search_favorites:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_favorites, menu);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


     public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {
         private DisplayImageOptions options;

         public class ViewHolder extends RecyclerView.ViewHolder {
             public TextView title;
             public ImageView image;

             public ViewHolder(View itemView) {
                 super(itemView);
                 title = (TextView) itemView.findViewById(R.id.text_favorites_item_title);
                 image = (ImageView) itemView.findViewById(R.id.image_favorites_item);
             }
         }


         public FavoritesAdapter() {
             cursor = RSS.FavoriteEntry.queryAll(databaseHelper, null);

             ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(FavoritesFragment.this.getActivity());
             ImageLoader.getInstance().init(configuration);
             options = new DisplayImageOptions.Builder()
                     .cacheInMemory(true)
                     .cacheOnDisk(true)
                     .bitmapConfig(Bitmap.Config.RGB_565)
                     .build();
         }

         @Override
         public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
             View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorites_item, parent, false);
             final ViewHolder viewHolder = new ViewHolder(view);
             view.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     cursor.moveToPosition(viewHolder.getAdapterPosition());
                     String link = cursor.getString(cursor.getColumnIndex(RSS.FavoriteEntry.COLUMN_NAME_URL));
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
             holder.title.setText(cursor.getString(cursor.getColumnIndex(RSS.FavoriteEntry.COLUMN_NAME_TITLE)));
             String photoURL = cursor.getString(cursor.getColumnIndex(RSS.FavoriteEntry.COLUMN_NAME_PHOTO_URL));
             ImageLoader.getInstance().displayImage(photoURL, holder.image, options);
         }

         @Override
         public int getItemCount() {
             return cursor.getCount();
         }

     }


}
