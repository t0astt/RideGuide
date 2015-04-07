package com.mikerinehart.rideguide.activities;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mikerinehart.rideguide.R;
import com.mikerinehart.rideguide.SimpleDividerItemDecoration;
import com.mikerinehart.rideguide.adapters.NotificationsAdapter;
import com.mikerinehart.rideguide.models.Notification;
import com.mikerinehart.rideguide.models.User;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public class NotificationCenterActivity extends ActionBarActivity {

    public static Toolbar notifications_toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setTintColor(getResources().getColor(R.color.ColorPrimary));

        setContentView(R.layout.activity_notification_center);
        notifications_toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.tool_bar);



        setSupportActionBar(notifications_toolbar);
        notifications_toolbar.setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        notifications_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        new Handler().post(new Runnable() {
            @Override public void run() {
                getFragmentManager().beginTransaction()
                        .replace(R.id.notifications_container, new NotificationsFragment())
                        .commit();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notification_center, menu);
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

    public static class NotificationsFragment extends Fragment {

        SharedPreferences sp;
        private boolean showNotificationShowcase;
        SharedPreferences notificationSP;

        RecyclerView notificationList;
        TextView noNotifications;

        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_notifications, container, false);
            notificationList = (RecyclerView)v.findViewById(R.id.notification_center_notification_list);
            noNotifications = (TextView)v.findViewById(R.id.notification_center_no_notifications);
            notificationList.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(notificationList.getContext());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            notificationList.setLayoutManager(llm);

            return v;
        }

        public void onResume() {
            super.onResume();
            Log.i("HomePage", "onresume");
            refreshContent();
        }

        private void refreshContent() {
            Log.i("HomePage", "refresh");
            notificationSP = getActivity().getSharedPreferences(Constants.NOTIFICATIONS, Context.MODE_PRIVATE);


            Gson gson = new Gson();
            ArrayList<Notification> notificationMessageList = new ArrayList<Notification>();
            Map<String, ?> map = notificationSP.getAll();
            Log.i("Homepage", "map size is: " + map.size());
            for (Map.Entry<String, ?> entry : map.entrySet()) {
                notificationMessageList.add(gson.fromJson(entry.getValue().toString(), Notification.class));
            }
            if (notificationMessageList != null && notificationMessageList.size() > 0) {
                Collections.sort(notificationMessageList, new Comparator<Notification>() {
                    public int compare(Notification n1, Notification n2) {
                        return n2.getDate().compareTo(n1.getDate());
                    }
                });
                noNotifications.setVisibility(TextView.GONE);
                NotificationsAdapter na = new NotificationsAdapter(notificationMessageList);
                notificationList.setAdapter(na);
                notificationList.addItemDecoration(new SimpleDividerItemDecoration(notificationList.getContext()));
            } else {
                notificationList.setAdapter(null);
                noNotifications.setVisibility(TextView.VISIBLE);
            }
        }


    }
}
