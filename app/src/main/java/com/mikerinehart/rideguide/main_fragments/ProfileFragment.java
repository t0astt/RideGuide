package com.mikerinehart.rideguide.main_fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mikerinehart.rideguide.R;
import com.mikerinehart.rideguide.RestClient;
import com.mikerinehart.rideguide.RoundedTransformation;
import com.mikerinehart.rideguide.activities.MainActivity;
import com.mikerinehart.rideguide.models.User;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.apache.commons.lang.BooleanUtils;


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

    private User user;
    private String mParam2;

    private ImageView coverPhoto;
    private ImageView profilePicture;
    private TextView firstName;
    private TextView lastName;

    private String coverPhotoSource;

    //    private OkHttpClient client;
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

//        client = new OkHttpClient();
        gson = new Gson();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainActivity.toolbar.setTitle("Profile");
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        profilePicture = (ImageView) v.findViewById(R.id.profile_picture);
        coverPhoto = (ImageView) v.findViewById(R.id.cover_photo);
        firstName = (TextView) v.findViewById(R.id.first_name);
        lastName = (TextView) v.findViewById(R.id.last_name);

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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
