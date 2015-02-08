package com.mikerinehart.rideguide;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;

import com.loopj.android.http.*;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.apache.commons.lang.BooleanUtils;


public class LoginActivity extends ActionBarActivity {

    private UiLifecycleHelper uiHelper;
    private FrameLayout fbContainer;
    private RelativeLayout emailContainer;
    private EditText emailField;

    private String TAG = "LoginActivity";
    public final static String USER = "com.mikerinehart.rideguide.USER";

    private User me;
    private GraphUser user;

    private boolean confCheckActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
    }

    // Called when session changes
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state,
                         Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    // When session is changed, this method is called from callback method
    private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
        // When Session is successfully opened (User logged-in)
        if (state.isOpened()) {
            Log.i(TAG, "Logged into Facebook");
            Request.newMeRequest(session, new Request.GraphUserCallback() {
                // callback after Graph API response with user object
                @Override
                public void onCompleted(final GraphUser fbUser, Response response) {
                    if (fbUser != null) {
                        user = fbUser;
                        Log.i(TAG, "Facebook user object set");

                        RequestParams params = new RequestParams("fb_uid", fbUser.getId());
                        // checkExistence returns false if user does not exist, returns user model if user exists
                        RestClient.post("users/checkExistence", params, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                try {
                                    Log.i(TAG, "Checking if user exists in database");
                                    //If the exists key is present, it means they DO NOT EXIST
                                    if (response.has("exists")) {
                                        Log.i(TAG, "User does not exist, show email registration screen");
                                        fbContainer = (FrameLayout)findViewById(R.id.fb_container);
                                        emailContainer = (RelativeLayout)findViewById(R.id.email_container);
                                        fbContainer.setVisibility(FrameLayout.GONE);
                                        emailContainer.setVisibility(RelativeLayout.VISIBLE);
                                        emailField = (EditText)findViewById(R.id.email_field);

                                    } else {
                                        Log.i(TAG, "User exists, checking confirmation status");

                                        if (BooleanUtils.toBoolean(response.getInt("confirmed"))) {
                                            Log.i(TAG, "User is confirmed, starting MainActivity");
                                            me = new User (response.getInt("id"),
                                                    response.getString("fb_uid"),
                                                    response.getString("email"),
                                                    response.getString("first_name"),
                                                    response.getString("last_name"),
                                                    BooleanUtils.toBoolean(response.getInt("confirmed")));
                                            launchMainActivity(me);
                                        } else {
                                            Log.i(TAG, "User is not confirmed, show email registration screen");
                                            fbContainer = (FrameLayout)findViewById(R.id.fb_container);
                                            emailContainer = (RelativeLayout)findViewById(R.id.email_container);
                                            fbContainer.setVisibility(FrameLayout.GONE);
                                            emailContainer.setVisibility(RelativeLayout.VISIBLE);
                                            emailField = (EditText)findViewById(R.id.email_field);
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
                }
            }).executeAsync();
        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
        }
    }

    public void submitEmail(View v) {
        String email = emailField.getText().toString();


        if (!email.equals("") && email.length() > 4 && email.substring(email.length()-4).equalsIgnoreCase(".edu"))
        {
            Snackbar.with(getApplicationContext()).text("Sending email...").show(LoginActivity.this);
            RequestParams params = new RequestParams();

            params.put("fb_uid", user.getId());
            params.put("email", email);
            params.put("first_name", user.getFirstName());
            params.put("last_name", user.getLastName());

            RestClient.post("registration/sendValidationEmail", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    confCheckActive = true;
                    Snackbar.with(getApplicationContext()).text("Email Sent!")
                            .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                            .actionLabel("Resend Email")
                            .actionListener(new ActionClickListener() {
                                @Override
                                public void onActionClicked(Snackbar snackbar) {
                                    submitEmail(findViewById(R.id.email_container));
                                }
                            })
                            .show(LoginActivity.this);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Snackbar.with(getApplicationContext()).text("Error sending email: " + statusCode)
                            .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                            .actionLabel("Try again")
                            .actionListener(new ActionClickListener() {
                                @Override
                                public void onActionClicked(Snackbar snackbar) {
                                    submitEmail(findViewById(R.id.email_container));
                                }
                            })
                            .show(LoginActivity.this);
                }
            });
        } else {
            Snackbar.with(getApplicationContext()).text("Please enter a valid .edu email address!").show(LoginActivity.this);
        }
    }

//    public void checkConfirmation() {
//        RequestParams params = new RequestParams("fb_uid", fbUid);
//        RestClient.get("users/" + fbUid, params, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                try {
//                    int confirmedStatus = response.getInt("confirmed");
//                    if (confirmedStatus == 1) {
//                        confCheckActive = false;
//                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                        startActivity(intent);
//                        finish();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                Log.i(TAG, "Error: " + statusCode);
//            }
//        });
//    }

    private void launchMainActivity(User me) {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.putExtra("me", me);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
        Log.i(TAG, "onResume reached");
        onSessionStateChange(Session.getActiveSession(), SessionState.OPENED, new Exception());
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }
}
