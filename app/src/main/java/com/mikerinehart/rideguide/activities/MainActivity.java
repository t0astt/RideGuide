package com.mikerinehart.rideguide.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
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
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.google.gson.Gson;
import com.mikerinehart.rideguide.VenmoLibrary;
import com.mikerinehart.rideguide.main_fragments.AboutFragment;
import com.mikerinehart.rideguide.adapters.DrawerAdapter;
import com.mikerinehart.rideguide.main_fragments.HomeFragment;
import com.mikerinehart.rideguide.main_fragments.MyHistoryFragment;
import com.mikerinehart.rideguide.main_fragments.MyReservationsFragment;
import com.mikerinehart.rideguide.page_fragments.HomePageFragment;
import com.mikerinehart.rideguide.main_fragments.ProfileFragment;
import com.mikerinehart.rideguide.R;
import com.mikerinehart.rideguide.main_fragments.RidesFragment;
import com.mikerinehart.rideguide.main_fragments.SettingsFragment;
import com.mikerinehart.rideguide.models.User;
import com.mikerinehart.rideguide.page_fragments.MyReservationsPageFragment;
import com.mikerinehart.rideguide.page_fragments.MyShiftsPageFragment;
import com.mikerinehart.rideguide.page_fragments.ReservationsHistoryPageFragment;
import com.mikerinehart.rideguide.page_fragments.ShiftsHistoryPageFragment;
import com.readystatesoftware.systembartint.SystemBarTintManager;

public class MainActivity extends ActionBarActivity implements
        HomeFragment.OnFragmentInteractionListener,
        HomePageFragment.OnFragmentInteractionListener,
        MyReservationsPageFragment.OnFragmentInteractionListener,
        MyShiftsPageFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener,
        RidesFragment.OnFragmentInteractionListener,
        MyReservationsFragment.OnFragmentInteractionListener,
        MyHistoryFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        AboutFragment.OnFragmentInteractionListener,
        ShiftsHistoryPageFragment.OnFragmentInteractionListener,
        ReservationsHistoryPageFragment.OnFragmentInteractionListener {

    RecyclerView mRecyclerView;
    DrawerAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    DrawerLayout Drawer;
    ActionBarDrawerToggle mDrawerToggle;
    String[] TITLES;
    int ICONS[] = {R.drawable.ic_home_gray,
            R.drawable.ic_search_gray,
            R.drawable.ic_event_gray,
            R.drawable.ic_shift_gray,
            R.drawable.ic_history_gray,
            R.drawable.ic_settings_gray,
            R.drawable.ic_exit_gray};

    public User me;

    String TAG = "MainActivity";
    public static Toolbar toolbar;
    private GraphUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setTintColor(getResources().getColor(R.color.ColorPrimary));

        TITLES = getResources().getStringArray(R.array.nav_drawer_items);

        me = getIntent().getExtras().getParcelable("me");

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Rides Available Now");

        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.container, HomePageFragment.newInstance(me, "HomePageFragment")).commit();

        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        final GestureDetector mGestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                ViewGroup child = (ViewGroup) recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {
                    Drawer.closeDrawers();
                    android.support.v4.app.FragmentManager fm = getSupportFragmentManager();

                    int itemClicked = recyclerView.getChildPosition(child);
                    mAdapter.selectPosition(itemClicked);

                    if (itemClicked == 0) {
                        fm.beginTransaction()
                                .replace(R.id.container, ProfileFragment.newInstance(me, me))
                                .addToBackStack("Profile")
                                .commit();
                    } else if (itemClicked == 1) {
                        toolbar.setTitle("Home");
                        fm.beginTransaction()
                                .replace(R.id.container, HomePageFragment.newInstance(me, "HomePageFragment"))
                                .addToBackStack("Home")
                                .commit();
                    } else if (itemClicked == 2) {
                        toolbar.setTitle("Find a Ride");
                        fm.beginTransaction().replace(R.id.container, RidesFragment.newInstance(me, "RidesFragment"))
                                .addToBackStack("Rides")
                                .commit();
                    } else if (itemClicked == 3) {
                        toolbar.setTitle("My Reservations");
                        fm.beginTransaction().replace(R.id.container, MyReservationsFragment.newInstance(me, "MyReservationsFragment"))
                                .addToBackStack("MyReservations")
                                .commit();
                    } else if (itemClicked == 4) {
                        toolbar.setTitle("My Shifts");
                        fm.beginTransaction().replace(R.id.container, MyShiftsPageFragment.newInstance(me, "MyShiftsPageFragment"))
                                .addToBackStack("MyShifts")
                                .commit();
                    } else if (itemClicked == 5) {
                        toolbar.setTitle("My History");
                        fm.beginTransaction().replace(R.id.container, MyHistoryFragment.newInstance(me, "MyHistoryFragment"))
                                .addToBackStack("MyHistory")
                                .commit();
                    } else if (itemClicked == 6) {
                            toolbar.setTitle("Settings");
                            fm.beginTransaction().replace(R.id.container, SettingsFragment.newInstance("Test", "SettingsFragment"))
                                    .addToBackStack("Settings")
                                    .commit();
                    } else if (itemClicked == 7) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Confirm Logout")
                                .setMessage("Do you wish to logout?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Session.getActiveSession().closeAndClearTokenInformation();
                                        finish();
                                        System.exit(0);
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

        mAdapter = new DrawerAdapter(TITLES, ICONS, me, me.getFirstName() + " " + me.getLastName(), me.getEmail(), me.getFbUid(), getBaseContext());
        mRecyclerView.setAdapter(mAdapter);
        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case 1: {
                if (resultCode == RESULT_OK) {
                    String signedrequest = data.getStringExtra("signedrequest");
                    if (signedrequest != null) {
                        VenmoLibrary.VenmoResponse response = (new VenmoLibrary()).validateVenmoPaymentResponse(signedrequest, Constants.getVenmoSecret());
                        if (response.getSuccess().equals("1")) {
                            //Payment was successful
                            String note = response.getNote();
                            String amount = response.getAmount();
                            Toast.makeText(this.getApplicationContext(), "Donation of $" + amount + " successful", Toast.LENGTH_LONG);
                        }
                    } else {
                        String error_message = data.getStringExtra("error_message");
                        Toast.makeText(this.getApplicationContext(), "Error in donation process", Toast.LENGTH_LONG);
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this.getApplicationContext(), "Donation canceled", Toast.LENGTH_LONG);
                }
            }
        }
    }

    public void onPause() {
        super.onPause();
        Log.i(TAG, "Pausing from MainActivity");
        Context c = this.getBaseContext();
        SharedPreferences sharedPref = c.getSharedPreferences("com.mikerinehart.rideguide.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String jsonMe = gson.toJson(me);
        editor.putString("CURRENT_USER", jsonMe);
        editor.putInt("CURRENT_FRAGMENT", 1);
        editor.commit();
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
