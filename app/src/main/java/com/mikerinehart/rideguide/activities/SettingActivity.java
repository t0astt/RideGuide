package com.mikerinehart.rideguide.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.mikerinehart.rideguide.R;
import com.mikerinehart.rideguide.main_fragments.SettingsFragment;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import de.psdev.licensesdialog.LicensesDialog;
import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20;
import de.psdev.licensesdialog.licenses.MITLicense;
import de.psdev.licensesdialog.model.Notice;
import de.psdev.licensesdialog.model.Notices;

public class SettingActivity extends ActionBarActivity {

    public static Toolbar settings_toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setTintColor(getResources().getColor(R.color.ColorPrimary));

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

        SharedPreferences sp;
        SharedPreferences.Editor editor;
        Context c;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.prefs);
            sp = getActivity().getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
            editor = sp.edit();

            c = getActivity();

            com.jenzz.materialpreference.Preference resetTutorialScreens = (com.jenzz.materialpreference.Preference)findPreference("resetTutorialScreens");
            com.jenzz.materialpreference.Preference ossLicense = (com.jenzz.materialpreference.Preference)findPreference("ossLicense");
            com.jenzz.materialpreference.Preference aboutApp = (com.jenzz.materialpreference.Preference)findPreference("aboutApp");

            resetTutorialScreens.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    editor.remove("drawerShowcase");
                    MainActivity.showDrawerShowcase = true;
                    editor.remove("profileShowcase");
                    editor.remove("shiftsShowcase");
                    editor.remove("ridesShowcase");
                    editor.remove("reservationsShowcase");
                    editor.commit();
                    Toast.makeText(getActivity().getBaseContext(), "Tutorial screens reset!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });

            ossLicense.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    final Notices notices = new Notices();

                    notices.addNotice(new Notice("Android Async HTTP", "http://loopj.com/android-async-http/", "James Smith", new ApacheSoftwareLicense20()));
                    notices.addNotice(new Notice("Picasso", "http://square.github.io/picasso/", "Square, Inc.", new ApacheSoftwareLicense20()));
                    notices.addNotice(new Notice("Gson", "https://code.google.com/p/google-gson/", null, new ApacheSoftwareLicense20()));
                    notices.addNotice(new Notice("Parceler", "https://github.com/johncarl81/parceler", "John Ericksen", new ApacheSoftwareLicense20()));
                    notices.addNotice(new Notice("Snackbar", "https://github.com/nispok/snackbar", null, new MITLicense()));
                    notices.addNotice(new Notice("PagerSlidingTabStrip", "https://github.com/jpardogo/PagerSlidingTabStrip", "Andreas Stuetz", new ApacheSoftwareLicense20()));
                    notices.addNotice(new Notice("Material Design Library", "https://github.com/navasmdc/MaterialDesignLibrary", null, new ApacheSoftwareLicense20()));
                    notices.addNotice(new Notice("Material Dialogs", "https://github.com/afollestad/material-dialogs", "Aiden Follestad", new MITLicense()));
                    notices.addNotice(new Notice("SystemBarTint", "https://github.com/jgilfelt/SystemBarTint", "Jeff Gilfelt", new ApacheSoftwareLicense20()));
                    notices.addNotice(new Notice("Sticky Headers Recyclerview", "https://github.com/timehop/sticky-headers-recyclerview", "Timehop", new ApacheSoftwareLicense20()));
                    notices.addNotice(new Notice("MaterialEditText", "https://github.com/rengwuxian/MaterialEditText", "rengwuxian", new ApacheSoftwareLicense20()));
                    notices.addNotice(new Notice("Material Range Bar", "https://github.com/oli107/material-range-bar", "AppyVet, Inc.", new ApacheSoftwareLicense20()));
                    notices.addNotice(new Notice("ShowcaseView", "https://github.com/amlcurran/ShowcaseView", "Alex Curran", new ApacheSoftwareLicense20()));
                    notices.addNotice(new Notice("Android-MaterialPreference", "https://github.com/jenzz/Android-MaterialPreference", "Jens Driller", new MITLicense()));
                    notices.addNotice(new Notice("SlideDateTimePicker", "https://github.com/jjobes/SlideDateTimePicker", null, new ApacheSoftwareLicense20()));

                    new LicensesDialog.Builder(c).setNotices(notices).setIncludeOwnLicense(true).build().show();


                    return false;
                }
            });
        }
    }
}
