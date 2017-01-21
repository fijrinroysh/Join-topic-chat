package com.example.app.ourapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.app.ourapplication.database.DBHelper;
import com.example.app.ourapplication.pref.PreferenceEditor;
import com.example.app.ourapplication.rest.model.request.HomeFeedReqModel;
import com.example.app.ourapplication.rest.model.request.LocationModel;
import com.example.app.ourapplication.rest.model.response.FeedRespModel;
import com.example.app.ourapplication.rest.model.response.Person;
import com.example.app.ourapplication.util.Helper;
import com.example.app.ourapplication.wss.WebSocketClient;
import com.example.app.ourapplication.wss.WebSocketListener;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFeedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFeedFragment extends Fragment implements WebSocketListener{

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
        void onFragmentInteraction(Uri uri);
    }

    private final String TAG = HomeFeedFragment.class.getSimpleName();

    private WebSocketClient mWebSocketClient;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private OnFragmentInteractionListener mListener;
    /*Views*/
    private List<Person> mFeeds = new ArrayList<>();
    private FeedRVAdapter mFeedListAdapter;
    private DBHelper mDBHelper;
    public LocationModel location;

    public HomeFeedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeFeedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFeedFragment newInstance() {
        HomeFeedFragment fragment = new HomeFeedFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        location = PreferenceEditor.getInstance(getContext()).getLocation();
        mWebSocketClient = ((OurApplication)getActivity().getApplicationContext()).getClient();
        mWebSocketClient.addWebSocketListener(this);

        mDBHelper = new DBHelper(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_feed, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFeeds = mDBHelper.getFeedDataAll();
        mFeedListAdapter = new FeedRVAdapter(mFeeds);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(getContext().getApplicationContext());
        recyclerView.setLayoutManager(llm);

        recyclerView.setAdapter(mFeedListAdapter);

        /* Commented the below part and added onClickListener to sender_message in FeedRVAdapter for the below functionality */

        /*recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), recyclerView,
                new Util.ClickListener() {
                    // @Override
                    public void onClick(View view, int position) {
                        Person item = mFeeds.get(position);
                        final Intent discussionIntent = new Intent(getActivity(), DiscussionActivity.class);
                        discussionIntent.putExtra(Keys.KEY_ID, item.getPostId());
                        startActivity(discussionIntent);
                        Toast.makeText(getContext().getApplicationContext(), item.getMessage() + " is selected!",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onLongClick(View view, int position) {}
                }));*/


        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            /**
             * This method is called when swipe refresh is pulled down
             */
            @Override
            public void onRefresh() {
                // Refresh items
                getUpdatedFeeds();
            }

        });

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                //HTTP requst to fetch data for Homefeed
                getUpdatedFeeds();
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
    public void onAttach(Context context) {
        super.onAttach(context);
     /*   if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mWebSocketClient.removeWebSocketListener(this);
    }

    @Override
    public void onTextMessage(String message) {
        JSONObject msgObject = null;
        try {
            msgObject = new JSONObject(message);
            Log.d(TAG, "TYPE:" + msgObject.optString(Keys.KEY_TYPE) + ":");

            if (msgObject.optString(Keys.KEY_TYPE).equals("F")) {
                Log.d(TAG, "I am message type F:" + ":" + msgObject.optString(Keys.KEY_NAME) );
                try {
                    Person person = new ObjectMapper().readValue(message,Person.class);
                    mFeeds.add(0, person);
                    mFeedListAdapter.notifyDataSetChanged();
                    mDBHelper.insertFeedData(person.toString(), "WS");
                    notify(person.getSenderName(),person.getMessage(),person.getPhotoId(),person.getPostId());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void notify(String notificationTitle, String notificationMessage, String notificationIcon , String postid) {
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService
                (Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(getContext(), DiscussionActivity.class);
        notificationIntent.putExtra(Keys.KEY_ID, postid);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, notificationIntent, 0);

        Notification notification = new Notification.Builder(getContext())
                .setAutoCancel(true)
                .setContentTitle(notificationTitle)
                .setContentText(notificationMessage)
                .setSmallIcon(R.mipmap.app_icon)
                .setLargeIcon(Helper.decodeImageString(notificationIcon))
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setStyle(new Notification.BigTextStyle()
                        .bigText(notificationMessage))
                // .setSmallIcon(setImageBitmap(Helper.decodeImageString(notificationIcon)))
                .setContentIntent(pendingIntent).build();
        // hide the notification after its selected
        // notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, notification);
    }

    @Override
    public void onOpen() {}

    @Override
    public void onClose() {}

    private void getUpdatedFeeds(){
        HomeFeedReqModel reqModel = new HomeFeedReqModel("F","5",location.getLongitude(),
                location.getLongitude(),mDBHelper.getFeedDataLatestTime());

        Call<FeedRespModel> queryHomeFeeds = ((OurApplication)getActivity().getApplicationContext())
                .getRestApi().queryHomeFeed(reqModel);
        queryHomeFeeds.enqueue(new Callback<FeedRespModel>() {
            @Override
            public void onResponse(Response<FeedRespModel> response, Retrofit retrofit) {
                ArrayList<Person> data = response.body().getData();
                for (int i = 0; i < data.size(); i++) {
                    mDBHelper.insertFeedData(data.toString(), "HTTP");
                    mFeeds.add(0, data.get(i));
                    mFeedListAdapter.notifyItemInserted(0);
                }
                mSwipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "No more Feeds to Load", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "Query failed");
                mSwipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "Loading Feeds Failed", Toast.LENGTH_LONG).show();
            }
        });
    }
}