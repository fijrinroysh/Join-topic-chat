package com.example.app.ourapplication;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.app.ourapplication.rest.model.response.Person;
import com.example.app.ourapplication.util.Helper;

import java.util.List;

/**
 * Created by ROYSH on 6/23/2016.
 */
public class FeedRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = FeedRVAdapter.class.getSimpleName();

    private int lastPosition = -1;
    private List<Person> mFeeds;
    private Context mContext;

    FeedRVAdapter(Context context,List<Person> mFeeds) {
        this.mContext = context;
        this.mFeeds = mFeeds;
    }

    public class PersonViewHolder1 extends RecyclerView.ViewHolder {
        CardView cv;
        TextView senderName;
        TextView senderMessage;
        ImageView senderPhoto;
        TextView messageTime;

        PersonViewHolder1(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            senderName = (TextView) itemView.findViewById(R.id.sender_name);
            senderMessage = (TextView) itemView.findViewById(R.id.sender_message);
            senderPhoto = (ImageView) itemView.findViewById(R.id.sender_photo);
            messageTime = (TextView) itemView.findViewById(R.id.message_time);
        }
    }

    public class PersonViewHolder2 extends RecyclerView.ViewHolder {
        CardView cv;
        TextView senderName;
        TextView senderMessage;
        ImageView senderPhoto;
        ImageView messagePhoto;
        TextView messageTime;

        PersonViewHolder2(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            senderName = (TextView) itemView.findViewById(R.id.sender_name);
            senderMessage = (TextView) itemView.findViewById(R.id.sender_message);
            senderPhoto = (ImageView) itemView.findViewById(R.id.sender_photo);
            messageTime = (TextView) itemView.findViewById(R.id.message_time);
            messagePhoto = (ImageView) itemView.findViewById(R.id.message_photo);
        }
    }

    public class PersonViewHolder3 extends RecyclerView.ViewHolder {
        CardView cv;
        TextView senderName;
        TextView senderMessage;
        ImageView senderPhoto;
        VideoView messageVideo;
        TextView messageTime;

        PersonViewHolder3(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            senderName = (TextView) itemView.findViewById(R.id.sender_name);
            senderMessage = (TextView) itemView.findViewById(R.id.sender_message);
            senderPhoto = (ImageView) itemView.findViewById(R.id.sender_photo);
            messageTime = (TextView) itemView.findViewById(R.id.message_time);
            messageVideo = (VideoView) itemView.findViewById(R.id.message_video);
        }
    }

    @Override
    public int getItemCount() {
        return mFeeds.size();
    }

    @Override
    public int getItemViewType(int i) {
        if (mFeeds.get(i).getPhotoMsg().equals(""))
        {
            return 1;
        }
        else if(mFeeds.get(i).getPhotoMsg().equals("TBD - condition for image"))
        {
            return 2;
        }
        else
        {
            return 3;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        //  RecyclerView.ViewHolder pvh;
        // View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_item, viewGroup, false);
       /* switch (viewType) {
            case 0:
                //pvh = new PersonViewHolder1(v);
                return new PersonViewHolder1(v);
                break;
            case 1:
                //pvh =
                return new PersonViewHolder2(v);
                break;
        }
        return pvh;*/
        if (viewType == 1) {
            Log.d(TAG, "PersonViewHolder1 created");
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_item1, viewGroup, false);
            return new PersonViewHolder1(v);
        } else if (viewType == 2) {
            Log.d(TAG, "PersonViewHolder2 created");
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_item2, viewGroup, false);
            return new PersonViewHolder2(v);
        } else {

            Log.d(TAG, "PersonViewHolder3 created");
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_item_video, viewGroup, false);
            return new PersonViewHolder3(v);
        }
        }

        @Override
        public void onBindViewHolder (RecyclerView.ViewHolder viewHolder,int i){
            final Person item = mFeeds.get(i);
            switch (viewHolder.getItemViewType()) {
                case 1:
                    PersonViewHolder1 vh1 = (PersonViewHolder1) viewHolder;
                    vh1.senderName.setText(item.getSenderName());
                    vh1.senderMessage.setText(item.getMessage());
                    vh1.messageTime.setText(Helper.getRelativeTime(item.getTimeMsg()));
                    if (item.getPhotoId().equals("noimage")) {
                        vh1.senderPhoto.setImageResource(R.drawable.profile);
                    } else {
                        vh1.senderPhoto.setImageBitmap(Helper.decodeImageString(item.getPhotoId()));
                    }

                    vh1.senderPhoto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

          /* Below code is to open the Profile of the sender but now it opens the profile of the user */

                            final Intent profileIntent = new Intent(v.getContext(), ProfileActivity.class);
                            profileIntent.putExtra(Keys.KEY_ID, item.getPostId());
                            v.getContext().startActivity(profileIntent);


                        }
                    });


                    vh1.cv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final Intent discussionIntent = new Intent(v.getContext(), DiscussionActivity.class);
                            discussionIntent.putExtra(Keys.KEY_ID, item.getPostId());
                            v.getContext().startActivity(discussionIntent);
                        }
                    });

                    setAnimation(vh1.cv, i);
                    break;

                case 2:

                    PersonViewHolder2 vh2 = (PersonViewHolder2) viewHolder;
                    vh2.senderName.setText(item.getSenderName());
                    vh2.senderMessage.setText(item.getMessage());
                    vh2.messageTime.setText(Helper.getRelativeTime(item.getTimeMsg()));
                    vh2.messagePhoto.setImageBitmap(Helper.decodeImageString(item.getPhotoMsg()));
                    vh2.senderPhoto.setImageBitmap(Helper.decodeImageString(item.getPhotoId()));

                    vh2.senderPhoto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            // Below code is to open the Profile of the sender

                            final Intent profileIntent = new Intent(v.getContext(), ProfileActivity.class);
                            profileIntent.putExtra(Keys.KEY_ID, item.getPostId());
                            v.getContext().startActivity(profileIntent);


                        }
                    });


                    vh2.cv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final Intent discussionIntent = new Intent(v.getContext(), DiscussionActivity.class);
                            discussionIntent.putExtra(Keys.KEY_ID, item.getPostId());
                            v.getContext().startActivity(discussionIntent);
                        }
                    });

                    vh2.messagePhoto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ImageView dialogmessagePhoto = (ImageView) v.findViewById(R.id.message_photo);
                            BitmapDrawable imagedrawable = (BitmapDrawable) dialogmessagePhoto.getDrawable();
                            Bitmap imagebitmap = imagedrawable.getBitmap();
                            Dialog builder = new Dialog(v.getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                            builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
                            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    //nothing;
                                }
                            });

                            ImageView imageView = new ImageView(v.getContext());
                            imageView.setImageBitmap(imagebitmap);
                            imageView.setAdjustViewBounds(true);
                            builder.addContentView(imageView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT));
                            builder.show();
                        }
                    });
                    setAnimation(vh2.cv, i);
                    break;

                case 3:

                    final PersonViewHolder3 vh3 = (PersonViewHolder3) viewHolder;
//                    final ProgressDialog pDialog = new ProgressDialog(mContext);
//                    pDialog.show();
                    vh3.senderName.setText(item.getSenderName());
                    vh3.senderMessage.setText(item.getMessage());
                    vh3.messageTime.setText(Helper.getRelativeTime(item.getTimeMsg()));




                    try {
                        // Start the MediaController
                        String VideoURL = "http://ec2-54-254-164-222.ap-southeast-1.compute.amazonaws.com/video/testvideo30mb.mp4";


                        MediaController mediacontroller = new MediaController(mContext);
                        mediacontroller.setAnchorView(vh3.messageVideo);
                        // Get the URL from String VideoURL
                        vh3.messageVideo.setMediaController(mediacontroller);
//                        Uri video = Uri.parse("\"" +item.getPhotoMsg()+ "\"");
                        Uri video = Uri.parse(VideoURL);
                        vh3.messageVideo.setVideoURI(video);

                        Log.d(TAG, "Video Url" + ": " + item.getPhotoMsg());


                    } catch (Exception e) {
                        Log.e("Error", e.getMessage());
                        e.printStackTrace();
                    }

                    vh3.messageVideo.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                        @Override
                        public boolean onError(MediaPlayer mp, int what, int extra) {

                            Log.d(TAG, "Error What" + ": " + what);
                            Log.d(TAG, "Error Extra" + ": " + extra);


                            return false;
                        }
                    });


                    vh3.messageVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        // Close the progress bar and play the video
                        public void onPrepared(MediaPlayer mp) {
                            // pDialog.dismiss();
                            Log.d(TAG, "Video is prepared");
                            //vh3.messageVideo.bringToFront();
                            //vh3.messageVideo.setFocusable(true);
                            resizeView(vh3.messageVideo,mContext.getResources().getDisplayMetrics().widthPixels,
                                    mContext.getResources().getDisplayMetrics().heightPixels);

                            vh3.messageVideo.requestFocus();
                            vh3.messageVideo.start();
//                            pDialog.dismiss();
                        }
                    });




                    vh3.senderPhoto.setImageBitmap(Helper.decodeImageString(item.getPhotoId()));


                    vh3.senderPhoto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            // Below code is to open the Profile of the sender

                            final Intent profileIntent = new Intent(v.getContext(), ProfileActivity.class);
                            profileIntent.putExtra(Keys.KEY_ID, item.getPostId());
                            v.getContext().startActivity(profileIntent);


                        }
                    });


                    vh3.cv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final Intent discussionIntent = new Intent(v.getContext(), DiscussionActivity.class);
                            discussionIntent.putExtra(Keys.KEY_ID, item.getPostId());
                            v.getContext().startActivity(discussionIntent);
                        }
                    });

/*
                vh3.messageVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        VideoView dialogmessageVideo= (VideoView) v.findViewById(R.id.message_video);
                       // BitmapDrawable imagedrawable = (BitmapDrawable) dialogmessagePhoto.getDrawable();
                       // Bitmap imagebitmap = imagedrawable.getBitmap();
                        Dialog builder = new Dialog(v.getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                        builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
                        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                //nothing;
                            }
                        });

                        VideoView videoView = new VideoView(v.getContext());
                        Uri video = Uri.parse(item.getPhotoMsg());
                        videoView.setVideoURI(video);
                        videoView.start();

                        builder.addContentView(videoView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT));
                        builder.show();
                    }
                });
*/
                    setAnimation(vh3.cv, i);

                    break;
            }
        }

        @Override
        public void onAttachedToRecyclerView (RecyclerView recyclerView){
            super.onAttachedToRecyclerView(recyclerView);
        }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        //Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), android.R.anim.fade_in);
        Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), (position > lastPosition) ? R.anim.up_from_bottom : android.R.anim.fade_in);
        viewToAnimate.startAnimation(animation);
        lastPosition = position;
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }


    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {

        //holder.itemView.messageVideo.pause();
        super.onViewRecycled(holder);

    }

    public static void resizeView(View view, int width, int height) {
        ViewGroup.LayoutParams layout = view.getLayoutParams();
        layout.width = width;
//        layout.height = height;
        view.setLayoutParams(layout);
    }

}