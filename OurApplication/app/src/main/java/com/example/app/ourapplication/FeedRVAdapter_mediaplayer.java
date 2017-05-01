package com.example.app.ourapplication;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.app.ourapplication.pref.PreferenceEditor;
import com.example.app.ourapplication.rest.model.response.Person;
import com.example.app.ourapplication.util.Helper;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by ROYSH on 6/23/2016.
 */
public class FeedRVAdapter_mediaplayer extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = FeedRVAdapter_mediaplayer.class.getSimpleName();


    private int lastPosition = -1;
    private List<Person> mFeeds;
    private Context mContext;

    FeedRVAdapter_mediaplayer(Context context, List<Person> mFeeds) {
        this.mContext = context;
        this.mFeeds = mFeeds;
    }
    String userId = PreferenceEditor.getInstance(mContext).getLoggedInUserName();

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
        ProgressBar videoLoaderProgressBar;
        ImageView videoThumbnail;
        ImageView playIcon;

        PersonViewHolder3(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            senderName = (TextView) itemView.findViewById(R.id.sender_name);
            senderMessage = (TextView) itemView.findViewById(R.id.sender_message);
            senderPhoto = (ImageView) itemView.findViewById(R.id.sender_photo);
            messageTime = (TextView) itemView.findViewById(R.id.message_time);
            messageVideo = (VideoView) itemView.findViewById(R.id.message_video);
            videoLoaderProgressBar = (ProgressBar) itemView.findViewById(R.id.video_loader_progress_bar);
            videoThumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            playIcon = (ImageView) itemView.findViewById(R.id.video_play_img_btn);
        }
    }

    public class PersonViewHolder4 extends RecyclerView.ViewHolder {

        TextView senderName;
        TextView senderMessage;
        ImageView senderPhoto;
        TextView messageTime;

        PersonViewHolder4(View itemView) {
            super(itemView);
            senderName = (TextView) itemView.findViewById(R.id.sender_name);
            senderMessage = (TextView) itemView.findViewById(R.id.sender_message);
            senderPhoto = (ImageView) itemView.findViewById(R.id.sender_photo);
            messageTime = (TextView) itemView.findViewById(R.id.message_time);
        }
    }



    @Override
    public int getItemCount() {
        return mFeeds.size();
    }

    @Override
    public int getItemViewType(int i) {
        if (mFeeds.get(i).getType().equals("F")){
            if(mFeeds.get(i).getPhotoMsg() != null && !mFeeds.get(i).getPhotoMsg().isEmpty() && mFeeds.get(i).getPhotoMsg().contains("/images/"))
            {
                return 2;
            }
            else if(mFeeds.get(i).getPhotoMsg() != null && !mFeeds.get(i).getPhotoMsg().isEmpty() && mFeeds.get(i).getPhotoMsg().contains("/videos/"))
            {
                return 3;
            }
            else {return 1;}
        }else if (mFeeds.get(i).getType().equals("C"))
        {
            return 4;
        }

        else return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        if (viewType == 1) {
            Log.d(TAG, "PersonViewHolder1 created");
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_item1, viewGroup, false);
            return new PersonViewHolder1(v);
        } else if (viewType == 2) {
            Log.d(TAG, "PersonViewHolder2 created");
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_item_image, viewGroup, false);
            return new PersonViewHolder2(v);
        } else if (viewType == 3) {
            Log.d(TAG, "PersonViewHolder3 created");
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_item_video, viewGroup, false);
            return new PersonViewHolder3(v);
        }
        else if (viewType == 4) {
            Log.d(TAG, "PersonViewHolder4 created");
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_item_comment, viewGroup, false);
            return new PersonViewHolder4(v);
        }
        else {return null;}
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
                // Picasso.with(mContext).load(item.getPhotoId()).resize(50, 50).into(vh1.senderPhoto);
                Picasso(item.getPhotoId(), vh1.senderPhoto);

                openProfile(item, vh1.senderPhoto);

                openDiscussion(item, vh1.cv);

                setAnimation(vh1.cv, i);
                break;

            case 2:

                PersonViewHolder2 vh2 = (PersonViewHolder2) viewHolder;
                vh2.senderName.setText(item.getSenderName());
                vh2.senderMessage.setText(item.getMessage());
                vh2.messageTime.setText(Helper.getRelativeTime(item.getTimeMsg()));
                //vh2.messagePhoto.setImageBitmap(Helper.decodeImageString(item.getPhotoMsg()));
                //Picasso.with(mContext).load(item.getPhotoMsg()).into(vh2.messagePhoto);
                Picasso(item.getPhotoMsg(), vh2.messagePhoto);
                Log.d(TAG, "IMAGE URL :" + item.getPhotoMsg());
                //vh2.senderPhoto.setImageBitmap(Helper.decodeImageString(item.getPhotoId()));
                // Picasso.with(mContext).load(item.getPhotoId()).resize(50, 50).into(vh2.senderPhoto);
                Picasso(item.getPhotoId(), vh2.senderPhoto);
                openProfile(item, vh2.senderPhoto);


                openDiscussion(item, vh2.cv);

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
                final MediaController mediacontroller = new MediaController(mContext);
                MediaMetadataRetriever metaRetriver = new MediaMetadataRetriever();

                vh3.senderName.setText(item.getSenderName());
                vh3.senderMessage.setText(item.getMessage());
                vh3.messageTime.setText(Helper.getRelativeTime(item.getTimeMsg()));

                Log.d(TAG, "IMAGE URL :" + item.getPhotoMsg());
                int index=item.getPhotoMsg().lastIndexOf('/');
                String Thumbnail_URL=item.getPhotoMsg().substring(0, index);
                String Thumbnail_filename=item.getPhotoMsg().substring(index + 1);
                Log.d(TAG, "Thumbnail_URL" + Thumbnail_URL);
                Log.d(TAG, "Thumbnail_filename" + Thumbnail_filename.substring(0, Thumbnail_filename.indexOf('.')) + ".jpg");
                String thumbnailfilename=Thumbnail_URL.concat("/video_thumbnail_").concat(Thumbnail_filename.substring(0, Thumbnail_filename.indexOf('.')) + ".jpg");
                Log.d(TAG, "Video Thumbnail file name :" + thumbnailfilename);
                Picasso(thumbnailfilename, vh3.videoThumbnail);


               // vh3.videoThumbnail.setImageResource(R.drawable.mickey);
                Uri video = Uri.parse(item.getPhotoMsg());
                vh3.messageVideo.setVideoURI(video);
                vh3.messageVideo.requestFocus();
                vh3.videoLoaderProgressBar.setVisibility(View.VISIBLE);
                vh3.videoThumbnail.setVisibility(View.VISIBLE);
                vh3.playIcon.setVisibility(View.VISIBLE);
                Log.d(TAG, "Video Url" + ": " + item.getPhotoMsg());


/*                    byte[] art = metaRetriver.getEmbeddedPicture();
                    Bitmap thumbnailImage = BitmapFactory.decodeByteArray(art, 0, art.length);
                    vh3.videoThumbnail.setImageBitmap(thumbnailImage);
                    Bitmap thumbnailImage = metaRetriver.getFrameAtTime(2 * 1000000, MediaMetadataRetriever.OPTION_CLOSEST);
                    vh3.videoThumbnail.setImageBitmap(thumbnailImage);*/

/*Code added instead of earlier code commented below -- start of code */
        /*        vh3.playIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            vh3.videoLoaderProgressBar.setVisibility(View.VISIBLE);
                            vh3.playIcon.setVisibility(View.INVISIBLE);
                            Uri video = Uri.parse(item.getPhotoMsg());
                            vh3.messageVideo.setVideoURI(video);
                            vh3.messageVideo.requestFocus();
                            Log.d(TAG, "Video Url" + ": " + item.getPhotoMsg());


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });


                vh3.messageVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    // Close the progress bar and play the video
                    public void onPrepared(MediaPlayer mp) {
                        Log.d(TAG, "Video is prepared");
                        //vh3.messageVideo.bringToFront();
                        //vh3.messageVideo.setFocusable(true);
                        vh3.messageVideo.seekTo(0);
                        vh3.videoLoaderProgressBar.setVisibility(View.INVISIBLE);
                        vh3.videoThumbnail.setVisibility(View.INVISIBLE);
                        mediacontroller.setAnchorView(vh3.messageVideo);
                        vh3.messageVideo.setMediaController(mediacontroller);
                        //vh3.messageVideo.start();

                    }
                });*/
   // -- end of code

/*-- Earlier Code*/
                vh3.messageVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    // Close the progress bar and play the video
                    public void onPrepared(MediaPlayer mp) {
                        Log.d(TAG, "Video is prepared");
                        //vh3.messageVideo.bringToFront();
                        //vh3.messageVideo.setFocusable(true);
                        vh3.messageVideo.seekTo(0);
                        vh3.videoLoaderProgressBar.setVisibility(View.INVISIBLE);
                        vh3.videoThumbnail.setVisibility(View.INVISIBLE);

                        vh3.playIcon.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {

                                    vh3.playIcon.setVisibility(View.INVISIBLE);
                                    mediacontroller.setAnchorView(vh3.messageVideo);
                                    vh3.messageVideo.setMediaController(mediacontroller);
                                    vh3.messageVideo.start();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });


                    }
                });
                //--end of comment */




                vh3.messageVideo.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {

                        Log.d(TAG, "Error What" + ": " + what);
                        Log.d(TAG, "Error Extra" + ": " + extra);


                        return false;
                    }
                });




                //vh3.senderPhoto.setImageBitmap(Helper.decodeImageString(item.getPhotoId()));
                // Picasso.with(mContext).load(item.getPhotoId()).resize(50, 50).into(vh3.senderPhoto);
                Picasso(item.getPhotoId(), vh3.senderPhoto);

                openProfile(item, vh3.senderPhoto);
                openDiscussion(item, vh3.cv);


                setAnimation(vh3.cv, i);

                break;


            case 4:
                PersonViewHolder4 vh4 = (PersonViewHolder4) viewHolder;
                vh4.senderName.setText(item.getSenderName());
                vh4.senderMessage.setText(item.getMessage());
                vh4.messageTime.setText(Helper.getRelativeTime(item.getTimeMsg()));

                // Picasso.with(mContext).load(item.getPhotoId()).resize(50, 50).into(vh1.senderPhoto);
                Picasso(item.getPhotoId(), vh4.senderPhoto);



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

        super.onViewRecycled(holder);

    }

    void Picasso(String URL, ImageView imageView) {
        Picasso.with(mContext).load(URL)
                .placeholder(R.drawable.mickey)
                .error(R.drawable.mickey)
                .into(imageView);
    }

    void Picasso_thumbnail(String URL, ImageView imageView) {
        Picasso.with(mContext).load(URL)
                .into(imageView);
    }


    void openProfile(final Person person, ImageView senderPhoto) {
        senderPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

          /* Below code is to open the Profile of the sender  */

                final Intent profileIntent = new Intent(v.getContext(), ProfileActivity.class);
                profileIntent.putExtra(Keys.KEY_ID, person.getPostId());
                if (!person.getUserId().equals(userId)) {
                    v.getContext().startActivity(profileIntent);
                }

            }
        });

    }

    void openDiscussion(final Person person, CardView cv) {

        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent discussionIntent = new Intent(v.getContext(), DiscussionActivity.class);
                discussionIntent.putExtra(Keys.KEY_ID, person.getPostId());
                v.getContext().startActivity(discussionIntent);
            }
        });
    }
}