package com.mikerinehart.rideguide;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.apache.commons.lang.BooleanUtils;

public class DecisionActivity extends ActionBarActivity {

    private static String TAG = "DecisionActivity";

    ConnectivityManager cm;

    private User me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decision);
        cm = (ConnectivityManager)getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
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
                                        me = new User (response.getInt("id"),
                                                response.getString("fb_uid"),
                                                response.getString("email"),
                                                response.getString("first_name"),
                                                response.getString("last_name"),
                                                BooleanUtils.toBoolean(response.getInt("confirmed")));
                                        if (me.getConfirmationStatus()) {
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
    }

    private void launchMainActivity(User me) {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.putExtra("me", me);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }

    private void launchLoginActivity() {
        Intent intent = new Intent(DecisionActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_decision, menu);
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
}
