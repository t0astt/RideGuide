package com.mikerinehart.rideguide.main_fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mikerinehart.rideguide.R;
import com.mikerinehart.rideguide.RestClient;
import com.mikerinehart.rideguide.RoundedTransformation;
import com.mikerinehart.rideguide.activities.MainActivity;
import com.mikerinehart.rideguide.adapters.ProfileCommentListAdapter;
import com.mikerinehart.rideguide.models.Review;
import com.mikerinehart.rideguide.models.User;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private static User ARG_PARAM1;
    private static User ARG_PARAM2;

    private User me;
    private User user;
    private Review myReview;

    private ImageView coverPhoto;
    private ImageView profilePicture;
    private TextView firstName;
    private TextView lastName;
    private ImageView thumbUpButton;
    private ImageView thumbDownButton;
    private TextView thumbsUpButtonCount;
    private TextView thumbsDownButtonCount;
    RecyclerView commentList;
    private String coverPhotoSource;
    private final String TAG = "ProfileFragment";

    private Gson gson;

    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(User param1, User param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable("USER", param1);
        args.putParcelable("ME", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            user = getArguments().getParcelable("USER");
            me = getArguments().getParcelable("ME");
        }
        gson = new Gson();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        MainActivity.toolbar.setTitle("Profile");

        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        commentList = (RecyclerView)v.findViewById(R.id.profile_comments_list);
        commentList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(commentList.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        commentList.setLayoutManager(llm);

        profilePicture = (ImageView) v.findViewById(R.id.profile_picture);
        coverPhoto = (ImageView) v.findViewById(R.id.cover_photo);
        firstName = (TextView) v.findViewById(R.id.first_name);
        lastName = (TextView) v.findViewById(R.id.last_name);
        thumbUpButton = (ImageView)v.findViewById(R.id.profile_thumb_up_button);
        thumbDownButton = (ImageView)v.findViewById(R.id.profile_thumb_down_button);
        thumbsUpButtonCount = (TextView)v.findViewById(R.id.profile_thumb_up_count);
        thumbsDownButtonCount = (TextView)v.findViewById(R.id.profile_thumb_down_count);

        refreshContent();

        // Get cover photo with the jankass Graph API call.
        RestClient.fbGet(user.getFbUid() + "?fields=cover", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    coverPhotoSource = response.getJSONObject("cover").getString("source");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Picasso.with(coverPhoto.getContext())
                        .load(coverPhotoSource)
                        .into(coverPhoto);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.i("ProfileFragment", "Error: " + errorResponse);
            }
        });

        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());
        Picasso.with(profilePicture.getContext())
                .load("https://graph.facebook.com/" + user.getFbUid() + "/picture?height=1000&type=large&width=1000")
                .transform(new RoundedTransformation(600, 5))
                .into(profilePicture);




        return v;
    }

    private void refreshContent() {
        RequestParams params = new RequestParams("user_id", user.getId());
        params.put("me", me.getId());
        RestClient.post("reviews/getUserReviews", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                List<Review> result;
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                Type listType = new TypeToken<List<Review>>() {
                }.getType();

                result = (List<Review>)gson.fromJson(response.toString(), listType);
                int thumbsUpCount = 0;
                int thumbsDownCount = 0;

                for (Review r : result) {
                    if(r.getType() == 1) {
                        thumbsUpCount++;
                    } else {
                        thumbsDownCount++;
                    }
                    if (r.getReviewer_user_id() == me.getId()) {
                        myReview = r;
                    }
                }

                if (myReview != null) {
                    if (myReview.getType() == 1) {
                        Log.i(TAG, myReview.toString());
                        thumbsUpButtonCount.setTextColor(getResources().getColor(R.color.ColorPrimary));
                        Picasso.with(thumbUpButton.getContext())
                                .load(R.drawable.ic_thumb_up_blue)
                                .into(thumbUpButton);

                        Log.i(TAG, myReview.toString());
                        thumbUpButton.setOnClickListener(null);
                        thumbUpButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                updateReview(1);
                            }
                        });
                        thumbDownButton.setOnClickListener(null);
                        thumbDownButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                updateReview(0);
                            }
                        });
                    } else {
                        Log.i(TAG, myReview.toString());
                        thumbsDownButtonCount.setTextColor(getResources().getColor(R.color.ColorPrimaryDark));
                        Picasso.with(thumbDownButton.getContext())
                                .load(R.drawable.ic_thumb_down_blue)
                                .into(thumbDownButton);

                        thumbUpButton.setOnClickListener(null);
                        thumbUpButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                leaveReview(1);
                            }
                        });
                        thumbDownButton.setOnClickListener(null);
                        thumbDownButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                leaveReview(0);
                            }
                        });
                    }
                }
                thumbsUpButtonCount.setText(String.valueOf(thumbsUpCount));
                thumbsDownButtonCount.setText(String.valueOf(thumbsDownCount));

                ProfileCommentListAdapter pcAdapter = new ProfileCommentListAdapter(result, me);
                commentList.setAdapter(pcAdapter);
                if (commentList.getAdapter() != null) {
                    Log.i(TAG, "Adapter set");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.i(TAG, "Error: " + errorResponse);
                Toast.makeText(getActivity().getApplicationContext(), "Error retrieving reviews", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String errorMsg, Throwable error) {
                Toast.makeText(getActivity().getApplicationContext(), "Error retrieving reviews", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateReview(int type) {
        Log.i(TAG, "In updateReview");
        final int reviewType = type;
        final LayoutInflater inflater = LayoutInflater.from(getActivity().getBaseContext());
        View thumbUpLayout = inflater.inflate(R.layout.review_dialog, null);

        final EditText reviewTitleField = (EditText)thumbUpLayout.findViewById(R.id.review_dialog_review_title);
        final TextView titleCharactersLeft = (TextView)thumbUpLayout.findViewById(R.id.review_dialog_title_characters_left_count);
        final int titleMaxCharacters = 50;
        titleCharactersLeft.setText(String.valueOf(titleMaxCharacters));

        reviewTitleField.setFilters(new InputFilter[] {new InputFilter.LengthFilter(titleMaxCharacters)});
        final TextWatcher titleWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                titleCharactersLeft.setText(String.valueOf(titleMaxCharacters - s.length()));
            }
        };
        reviewTitleField.addTextChangedListener(titleWatcher);

        final EditText reviewField = (EditText)thumbUpLayout.findViewById(R.id.review_dialog_review);
        final TextView reviewCharactersLeft = (TextView)thumbUpLayout.findViewById(R.id.review_dialog_review_characters_count);
        final int reviewMaxCharacters = 255;
        reviewCharactersLeft.setText(String.valueOf(reviewMaxCharacters));

        reviewField.setFilters(new InputFilter[]{new InputFilter.LengthFilter(reviewMaxCharacters)});
        final TextWatcher reviewWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                reviewCharactersLeft.setText(String.valueOf(reviewMaxCharacters - s.length()));
            }
        };
        reviewField.addTextChangedListener(reviewWatcher);

        reviewTitleField.setText(myReview.getTitle());
        reviewField.setText(myReview.getTitle());

        final MaterialDialog thumbUpDialog = new MaterialDialog.Builder(ProfileFragment.this.getActivity())
                .title("Update Review")
                .customView(thumbUpLayout)
                .neutralColor(getResources().getColor(R.color.ColorNegative))
                .positiveText("Ok")
                .negativeText("Cancel")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        RequestParams params = new RequestParams("user_id", user.getId());
                        params.put("me", me.getId());
                        params.put("review_title", reviewField.getText().toString());
                        params.put("review_comment", reviewField.getText().toString());
                        params.put("review_type", reviewType);
                        params.put("review_id", myReview.getId());
                        RestClient.post("reviews/updateReview", params, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                //thumbUpDialog.dismiss();
                                refreshContent();
                                Toast.makeText(getActivity().getApplicationContext(), "Review successful!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                                Log.i(TAG, "Error " + statusCode + ": " + response);
                                if (statusCode == 200) {
                                    refreshContent();
                                    Toast.makeText(getActivity().getApplicationContext(), "Review successful!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity().getApplicationContext(), "Error, please try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }).build();
        thumbUpDialog.show();
    }

    private void leaveReview(int type) {
        final int reviewType = type;
        final LayoutInflater inflater = LayoutInflater.from(getActivity().getBaseContext());
        View thumbUpLayout = inflater.inflate(R.layout.review_dialog, null);

        final EditText reviewTitleField = (EditText)thumbUpLayout.findViewById(R.id.review_dialog_review_title);
        final TextView titleCharactersLeft = (TextView)thumbUpLayout.findViewById(R.id.review_dialog_title_characters_left_count);
        final int titleMaxCharacters = 50;
        titleCharactersLeft.setText(String.valueOf(titleMaxCharacters));

        reviewTitleField.setFilters(new InputFilter[] {new InputFilter.LengthFilter(titleMaxCharacters)});
        final TextWatcher titleWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                titleCharactersLeft.setText(String.valueOf(titleMaxCharacters - s.length()));
            }
        };
        reviewTitleField.addTextChangedListener(titleWatcher);

        final EditText reviewField = (EditText)thumbUpLayout.findViewById(R.id.review_dialog_review);
        final TextView reviewCharactersLeft = (TextView)thumbUpLayout.findViewById(R.id.review_dialog_review_characters_count);
        final int reviewMaxCharacters = 255;
        reviewCharactersLeft.setText(String.valueOf(reviewMaxCharacters));

        reviewField.setFilters(new InputFilter[]{new InputFilter.LengthFilter(reviewMaxCharacters)});
        final TextWatcher reviewWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                reviewCharactersLeft.setText(String.valueOf(reviewMaxCharacters - s.length()));
            }
        };
        reviewField.addTextChangedListener(reviewWatcher);

        String title;
        if (type == 1) {
            title = "Leave a Positive Review";
        } else {
            title = "Leave a Negative Review";
        }
        final MaterialDialog thumbUpDialog = new MaterialDialog.Builder(ProfileFragment.this.getActivity())
                .title(title)
                .customView(thumbUpLayout)
                .neutralColor(getResources().getColor(R.color.ColorNegative))
                .positiveText("Ok")
                .negativeText("Cancel")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        RequestParams params = new RequestParams("user_id", user.getId());
                        params.put("me", me.getId());
                        params.put("review_title", reviewField.getText().toString());
                        params.put("review_comment", reviewField.getText().toString());
                        params.put("review_type", reviewType);
                        RestClient.post("reviews/leaveReview", params, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                //thumbUpDialog.dismiss();
                                refreshContent();
                                Toast.makeText(getActivity().getApplicationContext(), "Review successful!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                                Log.i(TAG, "Error " + statusCode + ": " + response);
                                if (statusCode == 200) {
                                    refreshContent();
                                    Toast.makeText(getActivity().getApplicationContext(), "Review successful!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity().getApplicationContext(), "Error, please try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }).build();
        thumbUpDialog.show();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i("Profile", "In onAttach");
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
