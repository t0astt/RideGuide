package com.mikerinehart.rideguide;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



        //String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, container, false);
        RecyclerView rideList = (RecyclerView)v.findViewById(R.id.ride_info_list);
        rideList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(rideList.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rideList.setLayoutManager(llm);

        RideInfoAdapter ra = new RideInfoAdapter(createRideList());
        rideList.addItemDecoration(new SimpleDividerItemDecoration(rideList.getContext()));
        rideList.setAdapter(ra);


        return v;
    }

//    private List<RideInfo> createList(int size) {
//        List<RideInfo> result = new ArrayList<RideInfo>();
//        for (int i = 1; i <= size; i++)
//        {
//            RideInfo r = new RideInfo();
//            r.name = r.NAME_PREFIX + i;
//            r.surname = r.SURNAME_PREFIX + i;
//            r.email = r.EMAIL_PREFIX + i + "@test.com";
//
//            result.add(r);
//        }
//
//        return result;
//    }

    private List<RideInfo> createRideList() {
        List<RideInfo> result;
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        Type listType = new TypeToken<List<RideInfo>>(){}.getType();

        String testJson = "[{\"id\":\"13\",\"user_id\":\"1\",\"start\":\"2015-02-02 18:30:00\",\"end\":\"2015-02-08 20:30:00\",\"user\":{\"id\":\"1\",\"fb_uid\":\"10205393671587549\",\"first_name\":\"Mike\",\"last_name\":\"Rinehart\"}},{\"id\":\"14\",\"user_id\":\"1\",\"start\":\"2015-02-02 10:42:00\",\"end\":\"2015-02-13 17:50:00\",\"user\":{\"id\":\"1\",\"fb_uid\":\"10205393671587549\",\"first_name\":\"Mike\",\"last_name\":\"Rinehart\"}},{\"id\":\"15\",\"user_id\":\"2\",\"start\":\"2015-02-03 00:00:00\",\"end\":\"2015-02-06 00:00:00\",\"user\":{\"id\":\"2\",\"fb_uid\":\"1493344104\",\"first_name\":\"Edward\",\"last_name\":\"Liu\"}}]";

        result = (List<RideInfo>)gson.fromJson(testJson, listType);

        return result;
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
