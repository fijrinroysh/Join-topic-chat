package com.example.app.ourapplication;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app.ourapplication.util.Helper;
import com.example.app.ourapplication.wss.WebSocketClient;
import com.example.app.ourapplication.wss.WebSocketListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFeedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFeedFragment extends Fragment implements WebSocketListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private WebSocketClient mWebSocketClient;

    private OnFragmentInteractionListener mListener;
    private final String TAG = HomeFeedFragment.class.getSimpleName();
    /*Views*/
    private List<Person> mFeeds = new ArrayList<>();
    private FeedRVAdapter mFeedListAdapter;
    private DBHelper mDBHelper ;//= new DBHelper(getContext());
    //    private BottomBar mBottomBar;
//    private FragNavController fragNavController;
    private String mReceiver;
    private String mReceiverid;
    public static View view;
    public static Activity activity;

    public HomeFeedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFeedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFeedFragment newInstance(String param1, String param2) {
        HomeFeedFragment fragment = new HomeFeedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.activity_home_feed, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mDBHelper = new DBHelper(getContext());
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        //mFeeds = getIntent().getParcelableArrayListExtra(Keys.PERSON_LIST);

        mReceiver = activity.getIntent().getStringExtra(Keys.KEY_TITLE);
        mReceiverid = activity.getIntent().getStringExtra(Keys.KEY_ID);
        mFeeds = mDBHelper.getFeedData(mReceiverid);
        mFeedListAdapter = new FeedRVAdapter(mFeeds);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mReceiver);


        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv);

        LinearLayoutManager llm = new LinearLayoutManager(getContext().getApplicationContext());
        recyclerView.setLayoutManager(llm);


        recyclerView.setAdapter(mFeedListAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(activity.getApplicationContext(),recyclerView,
                new Util.ClickListener() {
           // @Override
            public void onClick(View view, int position) {
                Person item = mFeeds.get(position);
                final Intent discussionIntent = new Intent(activity, DiscussionActivity.class);
                discussionIntent.putExtra(Keys.KEY_PERSON, item);
                startActivity(discussionIntent);
                Toast.makeText(getContext().getApplicationContext(), item.getMessage() + " is selected!",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return view;
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
        activity=(Activity) getContext();
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
        void onFragmentInteraction(Uri uri);
    }


    public void onTextMessage(String message) {
        JSONObject msgObject = null;
        try {

            msgObject = new JSONObject(message);
            Log.d(TAG, "TYPE:" + msgObject.optString(Keys.KEY_TYPE) + ":");

            if (msgObject.optString(Keys.KEY_TYPE).equals("F")) {
                Log.d(TAG, "I am message type F:" + mReceiver + ":" + msgObject.optString(Keys.KEY_NAME) + ":" +
                        msgObject.optString(Keys.KEY_TO) + ":");

                if ((msgObject.optString(Keys.KEY_NAME).equals(mReceiverid)) ||
                        (msgObject.optString(Keys.KEY_TO).equals(mReceiverid))) {
                    Log.d(TAG, "I am here" + mReceiver + ":" + msgObject.optString(Keys.KEY_NAME) + ":" +
                            msgObject.optString(Keys.KEY_TO));
                    mFeeds.add(0, parseFeeds(message));
                    mFeedListAdapter.notifyDataSetChanged();
                }
                mDBHelper.insertFeedData(message);
                Notify(mDBHelper.getProfileInfo(msgObject.optString(Keys.KEY_NAME), 1),
                        msgObject.optString(Keys.KEY_MESSAGE),
                        mDBHelper.getProfileInfo(msgObject.optString(Keys.KEY_NAME), 2));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Person parseFeeds(String message) {
        Date now = new Date();
        JSONObject msgObject = null;
        Person message_return = null;
        try {
            msgObject = new JSONObject(message);

            message_return = new Person(msgObject.optString(Keys.KEY_TYPE),
                    msgObject.optString(Keys.KEY_ID),
                    mDBHelper.getProfileInfo(msgObject.optString(Keys.KEY_NAME), 1),
                    mDBHelper.getProfileInfo(msgObject.optString(Keys.KEY_TO), 1),
                    msgObject.optString(Keys.KEY_MESSAGE),
                    mDBHelper.getProfileInfo(msgObject.optString(Keys.KEY_NAME), 2),
                    msgObject.optString(Keys.KEY_IMAGE),
                    msgObject.optString(Keys.KEY_TIME)
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return message_return;
    }


    private void Notify(String notificationTitle, String notificationMessage, String notificationIcon) {
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService
                (getContext().NOTIFICATION_SERVICE);
        @SuppressWarnings("deprecation")
        //  Intent notificationIntent = new Intent(this,NotificationView.class);
                Intent notificationIntent = new Intent(getContext(), HomeFeedFragment.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, notificationIntent, 0);

        Notification notification = new Notification.Builder(getContext())
                .setAutoCancel(true)
                .setContentTitle(notificationTitle)
                .setContentText(notificationMessage)
                .setSmallIcon(R.mipmap.ic_launcher)
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

   /* public interface ClickListener {
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }*/

  /*@Override
    public void onDestroy() {
        super.onDestroy();
       // mWebSocketClient.removeWebSocketListener(this);
    }*/

    @Override
    public void onOpen() {
    }

    @Override
    public void onClose() {
    }




}
