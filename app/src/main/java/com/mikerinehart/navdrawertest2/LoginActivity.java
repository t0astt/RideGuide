package com.mikerinehart.navdrawertest2;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import com.loopj.android.http.*;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends ActionBarActivity {

    private UiLifecycleHelper uiHelper;
    //private LoginButton loginButton;
    private FrameLayout fbContainer;
    private RelativeLayout emailContainer;
        private EditText emailField;
        //private Button emailButton;

    private String TAG = "LoginActivity";
    public final static String USER = "com.mikerinehart.navdrawertest2.USER";

    private String fbUid;
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
                public void onCompleted(final GraphUser user, Response response) {
                    if (user != null) {
                        fbUid = user.getId();

                        RequestParams params = new RequestParams("fb_uid", user.getId());
                        RestClient.post("users", params, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                try {
                                    int confirmedStatus = response.getInt("confirmed");
                                    if (confirmedStatus == 1) {
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                                        JSONObject obj = user.getInnerJSONObject();
                                        String jsonUserString = obj.toString();

                                        intent.putExtra(USER, jsonUserString);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                        startActivity(intent);
                                        finish();
                                    } else if (confirmedStatus == 0) {
                                        fbContainer = (FrameLayout)findViewById(R.id.fb_container);
                                        emailContainer = (RelativeLayout)findViewById(R.id.email_container);
                                        fbContainer.setVisibility(FrameLayout.GONE);
                                        emailContainer.setVisibility(RelativeLayout.VISIBLE);
                                        emailField = (EditText)findViewById(R.id.email_field);
                                        //emailButton = (Button)findViewById(R.id.email_button);
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
            params.put("email", email);
            params.put("fb_uid", fbUid);
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

    public void checkConfirmation() {
        RequestParams params = new RequestParams("fb_uid", fbUid);
        RestClient.get("users/" + fbUid, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    int confirmedStatus = response.getInt("confirmed");
                    if (confirmedStatus == 1) {
                        confCheckActive = false;
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                        finish();
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
