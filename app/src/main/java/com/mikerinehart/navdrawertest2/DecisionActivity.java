package com.mikerinehart.navdrawertest2;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;

import org.json.JSONObject;


public class DecisionActivity extends ActionBarActivity {

    private static String TAG = "DecisionActivity";
    public final static String USER = "com.mikerinehart.navdrawertest2.USER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Session session = Session.getActiveSession();

        if (session != null && session.isOpened()) {
            Log.i(TAG, "Session active");

            Request.newMeRequest(session, new Request.GraphUserCallback() {
                // callback after Graph API response with user object
                @Override
                public void onCompleted(final GraphUser user, Response response) {
                    Intent intent = new Intent(DecisionActivity.this, MainActivity.class);

                    JSONObject obj = user.getInnerJSONObject();
                    String jsonUserString = obj.toString();

                    intent.putExtra(USER, jsonUserString);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                    finish();
                }
            }).executeAsync();
        } else {
            Log.i(TAG, "No session active");
            Intent intent = new Intent(DecisionActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
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
