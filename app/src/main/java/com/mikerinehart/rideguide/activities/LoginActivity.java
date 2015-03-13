package com.mikerinehart.rideguide.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mikerinehart.rideguide.R;
import com.mikerinehart.rideguide.RestClient;
import com.mikerinehart.rideguide.models.User;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.apache.commons.lang.BooleanUtils;

import java.io.IOException;


public class LoginActivity extends ActionBarActivity {

    public final static String USER = "com.mikerinehart.rideguide.USER";
    private String TAG = "LoginActivity";

    private UiLifecycleHelper uiHelper;
    private FrameLayout fbContainer;

    private RelativeLayout emailContainer;
    private EditText emailField;

    private RelativeLayout phoneContainer;
    private EditText phoneField;
    private EditText phoneValidationField;

    private User me;
    private GraphUser user;
    GCMHelper gcm;
    private String gcmRegId;

    private boolean confCheckActive;
    // Called when session changes
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state,
                         Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get GCM registration ID
        gcm = new GCMHelper(getApplicationContext());
        getGCM();

        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
    }

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
                                        fbContainer = (FrameLayout) findViewById(R.id.fb_container);
                                        emailContainer = (RelativeLayout) findViewById(R.id.email_container);
                                        fbContainer.setVisibility(FrameLayout.GONE);
                                        emailContainer.setVisibility(RelativeLayout.VISIBLE);
                                        emailField = (EditText) findViewById(R.id.email_field);

                                    } else {
                                        Log.i(TAG, "User exists, checking email confirmation status");

                                        if (BooleanUtils.toBoolean(response.getInt("email_confirmed")) && BooleanUtils.toBoolean(response.getInt("phone_confirmed"))) {
                                            Log.i(TAG, "User is confirmed, starting MainActivity");
                                            me = new User(response.getInt("id"),
                                                    response.getString("fb_uid"),
                                                    response.getString("email"),
                                                    response.getString("first_name"),
                                                    response.getString("last_name"),
                                                    response.getString("phone"),
                                                    BooleanUtils.toBoolean(response.getInt("email_confirmed")),
                                                    BooleanUtils.toBoolean(response.getInt("phone_confirmed")));
                                            launchMainActivity(me);
                                        } else if (!BooleanUtils.toBoolean(response.getInt("email_confirmed"))) {
                                            Log.i(TAG, "User email is not confirmed, show email registration screen");
                                            fbContainer = (FrameLayout) findViewById(R.id.fb_container);
                                            emailContainer = (RelativeLayout) findViewById(R.id.email_container);
                                            fbContainer.setVisibility(FrameLayout.GONE);
                                            emailContainer.setVisibility(RelativeLayout.VISIBLE);
                                            emailField = (EditText) findViewById(R.id.email_field);
                                        } else if (!BooleanUtils.toBoolean(response.getInt("phone_confirmed"))) {
                                            Log.i(TAG, "User phone is not confirmed, show phone registration screen");
                                            fbContainer = (FrameLayout) findViewById(R.id.fb_container);
                                            emailContainer = (RelativeLayout) findViewById(R.id.email_container);
                                            fbContainer.setVisibility(FrameLayout.GONE);
                                            emailContainer.setVisibility(RelativeLayout.GONE);
                                            phoneContainer = (RelativeLayout)findViewById(R.id.login_phone_container);
                                            phoneField = (EditText) findViewById(R.id.login_phone_field);
                                            phoneValidationField = (EditText)findViewById(R.id.login_phone_validation_field);
                                            phoneContainer.setVisibility(RelativeLayout.VISIBLE);

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


        if (!email.equals("") && email.length() > 4 && email.substring(email.length() - 4).equalsIgnoreCase(".edu")) {
            Snackbar.with(getApplicationContext()).text("Sending email...").show(LoginActivity.this);

            //GCM REGISTRATION


            RequestParams params = new RequestParams();
            params.put("fb_uid", user.getId());
            params.put("email", email);
            params.put("first_name", user.getFirstName());
            params.put("last_name", user.getLastName());
            params.put("registration_type", "gcm");
            params.put("registration_id", gcmRegId);


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

    public void submitPhone(View v) {
        String phone = phoneField.getText().toString();


            Snackbar.with(getApplicationContext()).text("Sending validation text...").show(LoginActivity.this);
            RequestParams params = new RequestParams();

            params.put("fb_uid", user.getId());
            params.put("phone", phone);

            RestClient.post("registration/sendValidationText", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    Snackbar.with(getApplicationContext()).text("Text Sent!")
                            .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                            .actionLabel("Resend Text")
                            .actionListener(new ActionClickListener() {
                                @Override
                                public void onActionClicked(Snackbar snackbar) {
                                    submitEmail(findViewById(R.id.login_phone_container));
                                }
                            })
                            .show(LoginActivity.this);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Snackbar.with(getApplicationContext()).text("Error sending text: " + statusCode)
                            .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                            .actionLabel("Try again")
                            .actionListener(new ActionClickListener() {
                                @Override
                                public void onActionClicked(Snackbar snackbar) {
                                    submitEmail(findViewById(R.id.login_phone_container));
                                }
                            })
                            .show(LoginActivity.this);
                }
            });
    }

    public void checkPhoneValidation(View v) {
        String validationCode = phoneValidationField.getText().toString();


        Snackbar.with(getApplicationContext()).text("Checking validation code...").show(LoginActivity.this);
        RequestParams params = new RequestParams();

        params.put("fb_uid", user.getId());
        params.put("validation_code", validationCode);

        RestClient.post("registration/checkValidationTextCode", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (response.has("fb_uid") || response.getString("status").equalsIgnoreCase("success")) {
                        if (response.has("fb_uid")) {

                            me = new User(response.getInt("id"),
                                    response.getString("fb_uid"),
                                    response.getString("email"),
                                    response.getString("first_name"),
                                    response.getString("last_name"),
                                    response.getString("phone"),
                                    BooleanUtils.toBoolean(response.getInt("email_confirmed")),
                                    BooleanUtils.toBoolean(response.getInt("phone_confirmed")));



                            launchMainActivity(me);
                        } else if (response.getString("validation").equalsIgnoreCase("failed")) {
                            Snackbar.with(getApplicationContext()).text("Validation Code Incorrect")
                                    .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                                    .show(LoginActivity.this);
                        }
                    } else if (response.getString("status").equalsIgnoreCase("failed")) {
                        Snackbar.with(getApplicationContext()).text("Validation failed, please retry")
                                .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                                .show(LoginActivity.this);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                Log.i(TAG, "Error " + statusCode + ": " + response);
            }
        });
    }

    private void launchMainActivity(User me) {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.putExtra("me", me);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }

    private void getGCM() {

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    GCMHelper  gcmRegistrationHelper = new GCMHelper (
                            getApplicationContext());
                    gcmRegId = gcmRegistrationHelper.GCMRegister(Constants.getGoogleApiProjectNumber());

                } catch (Exception bug) {
                    bug.printStackTrace();
                }

            }
        });
        thread.start();
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
