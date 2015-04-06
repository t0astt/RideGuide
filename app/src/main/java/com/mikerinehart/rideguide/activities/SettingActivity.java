package com.mikerinehart.rideguide.activities;

import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mikerinehart.rideguide.R;
import com.mikerinehart.rideguide.main_fragments.SettingsFragment;

public class SettingActivity extends ActionBarActivity {

    public static Toolbar settings_toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        settings_toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.tool_bar);


        setSupportActionBar(settings_toolbar);
        settings_toolbar.setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        settings_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        new Handler().post(new Runnable() {
            @Override public void run() {
                getFragmentManager().beginTransaction()
                        .replace(R.id.settings_container, new SettingsFragment2())
                        .commit();
            }
        });

    }

    public void onBackPressed() {
        super.onBackPressed();
        super.finish();
    }

    public static class SettingsFragment2 extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.prefs);
        }
    }
}
