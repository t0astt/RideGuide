package com.mikerinehart.rideguide.main_fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mikerinehart.rideguide.R;
import com.mikerinehart.rideguide.RestClient;
import com.mikerinehart.rideguide.RoundedTransformation;
import com.mikerinehart.rideguide.SimpleDividerItemDecoration;
import com.mikerinehart.rideguide.activities.MainActivity;
import com.mikerinehart.rideguide.adapters.MyShiftsAdapter;
import com.mikerinehart.rideguide.adapters.ProfileCommentListAdapter;
import com.mikerinehart.rideguide.models.Comment;
import com.mikerinehart.rideguide.models.Shift;
import com.mikerinehart.rideguide.models.User;
import com.squareup.picasso.Picasso;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.apache.commons.lang.BooleanUtils;

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
    private static final String ARG_PARAM2 = "param2";

    private User me;
    private User user;
    private String mParam2;

    private ImageView coverPhoto;
    private ImageView profilePicture;
    private TextView firstName;
    private TextView lastName;
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
    public static ProfileFragment newInstance(User param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable("USER", param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            user = getArguments().getParcelable("USER");
            mParam2 = getArguments().getString(ARG_PARAM2);
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
        RestClient.post("comments/getUserComments", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                List<Comment> result;
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                Type listType = new TypeToken<List<Comment>>() {
                }.getType();

                result = (List<Comment>)gson.fromJson(response.toString(), listType);

                ProfileCommentListAdapter pcAdapter = new ProfileCommentListAdapter(result);
                commentList.setAdapter(pcAdapter);
                if (commentList.getAdapter() != null) {
                    Log.i(TAG, "Adapter set");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.i(TAG, "Error: " + errorResponse);
                Toast.makeText(getActivity().getApplicationContext(), "Error retrieving comments", Toast.LENGTH_LONG).show();
            }
        });
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
