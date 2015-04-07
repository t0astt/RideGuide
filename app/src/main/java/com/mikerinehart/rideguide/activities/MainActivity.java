package com.mikerinehart.rideguide.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.gson.Gson;
import com.mikerinehart.rideguide.VenmoLibrary;
import com.mikerinehart.rideguide.main_fragments.AboutFragment;
import com.mikerinehart.rideguide.adapters.DrawerAdapter;
import com.mikerinehart.rideguide.main_fragments.MyHistoryFragment;
import com.mikerinehart.rideguide.main_fragments.MyReservationsFragment;
import com.mikerinehart.rideguide.main_fragments.HomePageFragment;
import com.mikerinehart.rideguide.main_fragments.MyShiftsFragment;
import com.mikerinehart.rideguide.main_fragments.ProfileFragment;
import com.mikerinehart.rideguide.R;
import com.mikerinehart.rideguide.main_fragments.RidesFragment;
import com.mikerinehart.rideguide.main_fragments.SettingsFragment;
import com.mikerinehart.rideguide.models.Notification;
import com.mikerinehart.rideguide.models.User;
import com.mikerinehart.rideguide.page_fragments.ReservationsHistoryPageFragment;
import com.mikerinehart.rideguide.page_fragments.ShiftsHistoryPageFragment;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends ActionBarActivity implements
        HomePageFragment.OnFragmentInteractionListener,
        MyShiftsFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener,
        RidesFragment.OnFragmentInteractionListener,
        MyReservationsFragment.OnFragmentInteractionListener,
        MyHistoryFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        AboutFragment.OnFragmentInteractionListener,
        ShiftsHistoryPageFragment.OnFragmentInteractionListener,
        ReservationsHistoryPageFragment.OnFragmentInteractionListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    RecyclerView mRecyclerView;
    public static DrawerAdapter drawerAdapter;
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

    private MenuItem notificationsIcon;

    Activity mainActivity;
    SharedPreferences sp;

    ShowcaseView s;
    public static boolean showDrawerShowcase;
    public static boolean showDrawerHandleShowcase;

    SharedPreferences spNotifications;

    public User me;

    String TAG = "MainActivity";
    public static Toolbar toolbar;
    private GraphUser user;

    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        Log.i(TAG, "Notifications changed");

        boolean newNotifications = false;
        Gson gson = new Gson();
        Map<String, ?> map = spNotifications.getAll();
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            Notification n = gson.fromJson(entry.getValue().toString(), Notification.class);
            if (n.isSeen() == null || !n.isSeen()) {
                newNotifications = true;
            }
        }
        if (newNotifications) {
            Log.i(TAG, "New notifications");
            notificationsIcon.setIcon(getResources().getDrawable(R.drawable.ic_actions_notifications));
        } else {
            Log.i(TAG, "No new notificastions");
            notificationsIcon.setIcon(getResources().getDrawable(R.drawable.ic_action_notifications_none));
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = this;
        setContentView(R.layout.activity_main);

        spNotifications = this.getSharedPreferences(Constants.NOTIFICATIONS, Context.MODE_PRIVATE);
        spNotifications.registerOnSharedPreferenceChangeListener(this);

        sp = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
        showDrawerShowcase = sp.getBoolean(Constants.SHOWDRAWERSHOWCASE, true); // True if need to show
        showDrawerHandleShowcase = sp.getBoolean(Constants.SHOWDRAWERHANDLESHOWCASE, true);

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
                    //drawerAdapter.selectPosition(itemClicked);

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
                        fm.beginTransaction().replace(R.id.container, MyShiftsFragment.newInstance(me, "MyShiftsFragment"))
                                .addToBackStack("MyShifts")
                                .commit();
                    } else if (itemClicked == 5) {
                        toolbar.setTitle("My History");
                        fm.beginTransaction().replace(R.id.container, MyHistoryFragment.newInstance(me, "MyHistoryFragment"))
                                .addToBackStack("MyHistory")
                                .commit();
                    } else if (itemClicked == 6) {
                            Intent settingIntent = new Intent(getApplicationContext(), SettingActivity.class);
                            startActivity(settingIntent);

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

        drawerAdapter = new DrawerAdapter(TITLES, ICONS, me, me.getFirstName() + " " + me.getLastName(), me.getEmail(), me.getFbUid(), getBaseContext());
        mRecyclerView.setAdapter(drawerAdapter);
        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);
        mDrawerToggle = new ActionBarDrawerToggle(this, Drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (showDrawerShowcase) {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean(Constants.SHOWDRAWERSHOWCASE, false);
                    editor.commit();
                    showDrawerShowcase = false;
                    showcase(0);
                }
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

    private void showcase(int which) {
        switch(which) {
            case(0):
                ViewTarget target = new ViewTarget(R.id.imageView, mainActivity);
                new ShowcaseView.Builder(mainActivity, true)
                        .setTarget(target)
                        .setContentTitle("Profile")
                        .setContentText("Clicking your profile icon will open your RideGuide profile.")
                        .hideOnTouchOutside()
                        .setStyle(R.style.CustomShowcaseTheme2)
                        .build();
                break;
            case(1):
                ImageButton drawerToggleIcon = null;

                for (int i=0; i < toolbar.getChildCount(); i++) {
                    if (toolbar.getChildAt(i) instanceof ImageButton) drawerToggleIcon = (ImageButton)toolbar.getChildAt(i);
                }

                ViewTarget target2 = new ViewTarget(drawerToggleIcon);
                s = new ShowcaseView.Builder(mainActivity, true)
                        .setTarget(target2)
                        .setContentTitle("Welcome!")
                        .setContentText("Welcome to RideGuide! To get started, click the button highlighted or swipe from the left of the screen.")
                        .hideOnTouchOutside()
                        .setStyle(R.style.CustomShowcaseTheme2)
                        .setOnClickListener(new ShowcaseView.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                s.hide();
                                //HomePageFragment.showcase(mainActivity);
                            }
                        })
                        .build();
                break;
        }
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
                            Toast.makeText(this.getApplicationContext(), "Donation of $" + amount + " successful", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        String error_message = data.getStringExtra("error_message");
                        Toast.makeText(this.getApplicationContext(), "Error in donation process", Toast.LENGTH_LONG).show();
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this.getApplicationContext(), "Donation canceled", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void onResume() {
        super.onResume();
        Log.i(TAG, "OnResume");

        if (showDrawerHandleShowcase) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(Constants.SHOWDRAWERHANDLESHOWCASE, false);
            editor.commit();
            showDrawerHandleShowcase = false;
            showcase(1);
        }

    }

    public void onPause() {
        super.onPause();
        Log.i(TAG, "Pausing from MainActivity");
        Context c = this.getBaseContext();
        SharedPreferences userPref = c.getSharedPreferences("CURRENT_USER", Context.MODE_PRIVATE);
        SharedPreferences.Editor userEditor = userPref.edit();
        Gson gson = new Gson();
        String jsonMe = gson.toJson(me);
        userEditor.putString("CURRENT_USER", jsonMe);
        userEditor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        notificationsIcon = menu.getItem(0);

        boolean newNotifications = false;
        Gson gson = new Gson();
        Map<String, ?> map = spNotifications.getAll();
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            Notification n = gson.fromJson(entry.getValue().toString(), Notification.class);
            if (n.isSeen() == null || !n.isSeen()) {
                newNotifications = true;
            }
        }
        if (newNotifications) {
            if (notificationsIcon != null) notificationsIcon.setIcon(getResources().getDrawable(R.drawable.ic_actions_notifications));
        } else {
            if (notificationsIcon != null) notificationsIcon.setIcon(getResources().getDrawable(R.drawable.ic_action_notifications_none));
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.action_notifications:
                item.setIcon(getResources().getDrawable(R.drawable.ic_action_notifications_none));
                Intent notificationIntent = new Intent(getApplicationContext(), NotificationCenterActivity.class);
                startActivity(notificationIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
