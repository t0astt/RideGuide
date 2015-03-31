package com.mikerinehart.rideguide.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mikerinehart.rideguide.R;
import com.mikerinehart.rideguide.RestClient;
import com.mikerinehart.rideguide.models.User;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.apache.commons.lang.BooleanUtils;

public class DecisionActivity extends ActionBarActivity {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static String TAG = "DecisionActivity";
    ConnectivityManager cm;
    String SENDER_ID = "690520067451";
    GoogleCloudMessaging gcm;
    String regid;

    private User me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "DecisionActivity OnCreate");

        SharedPreferences userPref = this.getSharedPreferences("CURRENT_USER", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonMe = userPref.getString("CURRENT_USER", "not_found");
        if (jsonMe.equalsIgnoreCase("not_found")) {
            Log.i(TAG, "User wasn't stored");
        } else {
            me = gson.fromJson(jsonMe, User.class);
            launchMainActivity(me);
            finish();
            return;
        }


        setContentView(R.layout.activity_decision);

        if (checkPlayServices() == true) {
            cm = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {

                Session session = Session.getActiveSession();

                // If active Facebook session exists
                if (session != null && session.isOpened()) {

                    Request.newMeRequest(session, new Request.GraphUserCallback() {
                        // callback after Graph API response with user object
                        @Override
                        public void onCompleted(final GraphUser user, Response response) {
                            Log.i(TAG, "Session active, checking if user exists in db");

                            RequestParams params = new RequestParams("fb_uid", user.getId());
                            // checkExistence returns false if user does not exist, returns user model if user exists
                            RestClient.post("users/checkExistence", params, new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    try {
                                        if (response.has("exists")) {
                                            Log.i(TAG, "User does not exist, launching LoginActivity");
                                            launchLoginActivity();
                                        } else {
                                            Log.i(TAG, "User exists, creating local User object");
                                            me = new User(response.getInt("id"),
                                                    response.getString("fb_uid"),
                                                    response.getString("email"),
                                                    response.getString("first_name"),
                                                    response.getString("last_name"),
                                                    response.getString("phone"),
                                                    BooleanUtils.toBoolean(response.getInt("email_confirmed")),
                                                    BooleanUtils.toBoolean(response.getInt("phone_confirmed")));
                                            if (me.getEmailConfirmationStatus() && me.getPhoneConfirmationStatus()) {
                                                Log.i(TAG, "User is confirmed, starting MainActivity");
                                                launchMainActivity(me);
                                            } else {
                                                Log.i(TAG, "User is not confirmed, launching LoginActivity");
                                                launchLoginActivity();
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                    Log.i(TAG, "Error: " + statusCode);
                                }
                            });
                        }
                    }).executeAsync();
                } else {
                    Log.i(TAG, "No Facebook session active, start LoginActivity");
                    launchLoginActivity();
                }
            } else {
                new AlertDialog.Builder(DecisionActivity.this)
                        .setTitle("Error")
                        .setMessage("RideGuide requires an active data connection. Please enable data before continuing.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                                System.exit(0);
                            }
                        }).show();
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found");
            GooglePlayServicesUtil.getErrorDialog(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this), this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
        }
    }

    private void launchMainActivity(User me) {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.putExtra("me", me);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
        return;
    }

    private void launchLoginActivity() {
        Intent intent = new Intent(DecisionActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void onResume() {
        super.onResume();
        Log.i(TAG, "DecisionActivity was resumed!");
    }

    public void onPause() {
        super.onPause();
        Context c = this.getBaseContext();
        SharedPreferences sharedPref = c.getSharedPreferences("com.mikerinehart.rideguide.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String jsonMe = gson.toJson(me);
        editor.putString("CURRENT_USER", jsonMe);
        editor.commit();
    }

    public void onDestroy() {
        super.onDestroy();
        finish();
        return;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_decision, menu);
        return true;
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
