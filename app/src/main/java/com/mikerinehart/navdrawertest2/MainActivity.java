package com.mikerinehart.navdrawertest2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.Uri;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends ActionBarActivity implements
        HomeFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener,
        RidesFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        AboutFragment.OnFragmentInteractionListener {

    private Toolbar toolbar;

    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    DrawerLayout Drawer;
    ActionBarDrawerToggle mDrawerToggle;

    String[] TITLES;
    int ICONS[] = {R.drawable.ic_home, R.drawable.ic_profile, R.drawable.ic_rides, R.drawable.ic_settings, R.drawable.ic_about, R.drawable.ic_logout};
    String EMAIL = "mrrineh@ilstu.edu";
    int PROFILE = R.drawable.ic_launcher;

    String TAG = "MainActivity";

    private GraphUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String fbUser = intent.getStringExtra("com.mikerinehart.navdrawertest2.USER");

        try {
            JSONObject obj = new JSONObject(fbUser);
            user = GraphObject.Factory.create(obj, GraphUser.class);
        } catch (JSONException e) {
            Log.i(TAG, "JSON Exception :(");
        }

        TITLES = getResources().getStringArray(R.array.nav_drawer_items);

        toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.container, HomeFragment.newInstance("Test", "HomeFragment")).commit();

        mRecyclerView = (RecyclerView)findViewById(R.id.RecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new MyAdapter(TITLES, ICONS, user.getName(), EMAIL, user.getId(), getBaseContext());
        mRecyclerView.setAdapter(mAdapter);


        final GestureDetector mGestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                //View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                ViewGroup child = (ViewGroup)recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {
                    Drawer.closeDrawers();
                    android.support.v4.app.FragmentManager fm = getSupportFragmentManager();

                    int itemClicked = recyclerView.getChildPosition(child);

                    if (itemClicked == 1) {
                        fm.beginTransaction().replace(R.id.container, HomeFragment.newInstance("Test", "HomeFragment")).commit();
                    }
                    else if (itemClicked == 2) fm.beginTransaction().replace(R.id.container, ProfileFragment.newInstance("Test", "ProfileFragment")).commit();
                    else if (itemClicked == 3) fm.beginTransaction().replace(R.id.container, RidesFragment.newInstance("Test", "RidesFragment")).commit();
                    else if (itemClicked == 4) fm.beginTransaction().replace(R.id.container, SettingsFragment.newInstance("Test", "SettingsFragment")).commit();
                    else if (itemClicked == 5) fm.beginTransaction().replace(R.id.container, AboutFragment.newInstance("Test", "AboutFragment")).commit();
                    else if (itemClicked == 6) {
                        new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Confirm Logout")
                        .setMessage("Do you wish to logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getBaseContext(), "Logged Out!", Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("No", null).show();
                        return true;
                    }
                    return true;
                }
                return false;
            }
            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                Log.i(TAG, "TouchEvent");
            }
        });


        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        Drawer = (DrawerLayout)findViewById(R.id.DrawerLayout);
        mDrawerToggle = new ActionBarDrawerToggle(this, Drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // Execute code here when drawer opened
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Execute code here when drawer closed
            }
        };
        Drawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
