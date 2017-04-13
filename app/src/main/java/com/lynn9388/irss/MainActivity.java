package com.lynn9388.irss;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lynn9388.irss.dialog.AddFeedDialogFragment;
import com.lynn9388.irss.fragment.FavoritesFragment;
import com.lynn9388.irss.fragment.FeedsFragment;
import com.lynn9388.irss.util.RSS;
import com.lynn9388.irss.util.RSSDatabaseHelper;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AddFeedDialogFragment.AddFeedDialogListener {
    private RSSDatabaseHelper databaseHelper;

    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView drawerBackground;
    private ImageView drawerPhoto;
    private TextView drawerUsername;
    private TextView drawerEmailAddress;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new RSSDatabaseHelper(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        drawerBackground = (ImageView) findViewById(R.id.drawer_background);
        drawerPhoto = (ImageView) findViewById(R.id.drawer_photo);
        drawerUsername = (TextView)findViewById(R.id.text_drawer_username);
        drawerEmailAddress = (TextView)findViewById(R.id.text_drawer_email_address);

        configToolbar(getString(R.string.menu_feeds), false);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.describe_open_drawer, R.string.describe_close_drawer);
        drawerLayout.setDrawerListener(drawerToggle);

        navigationView.setNavigationItemSelectedListener(this);

        drawerPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });

        replaceFragment(new FeedsFragment(), FeedsFragment.TAG);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("profile", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(getString(R.string.username), "");
        if (!username.isEmpty()) {
            setDrawerPhoto();
            drawerUsername.setText(username);
            drawerEmailAddress.setText("lynn9388@gmail.com");
        }
    }

    private void setDrawerPhoto() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.user_photo);
        RoundedBitmapDrawable roundedBitmap = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        roundedBitmap.setCircular(true);
        drawerBackground.setImageBitmap(bitmap);
        drawerPhoto.setImageDrawable(roundedBitmap);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawers();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        drawerLayout.closeDrawers();
        switch (menuItem.getItemId()) {
            case R.id.menu_feeds:
                configToolbar(getString(R.string.menu_feeds), false);
                replaceFragment(new FeedsFragment(), FeedsFragment.TAG);
                return true;
            case R.id.menu_favorites:
                configToolbar(getString(R.string.menu_favorites), true);
                replaceFragment(new FavoritesFragment(), FavoritesFragment.TAG);
                return true;
            case R.id.menu_settings:
                return true;
            case R.id.menu_help:
                return true;
            default:
                return true;
        }
    }

    @Override
    public void onAddFeedDialogPositiveClick(DialogFragment dialogFragment) {
        FeedsFragment fragment = (FeedsFragment)getSupportFragmentManager().findFragmentByTag(FeedsFragment.TAG);
        fragment.addTab(dialogFragment);
    }

    @TargetApi(21)
    private void configToolbar(String title, boolean hasShadow) {
        toolbar.setTitle(title);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
            if (hasShadow) {
                final float scale = getResources().getDisplayMetrics().density;
                appBarLayout.setElevation((int) (4 * scale + 0.5f));
            } else {
                appBarLayout.setElevation(0);
            }
        }
    }

    private void replaceFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment, tag);
        fragmentTransaction.commit();
    }
}
