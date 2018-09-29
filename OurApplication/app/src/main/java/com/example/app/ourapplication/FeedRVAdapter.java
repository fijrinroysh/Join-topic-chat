package com.example.app.ourapplication;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import android.util.TypedValue;

import com.example.app.ourapplication.pref.PreferenceEditor;
import com.example.app.ourapplication.rest.model.request.LocationModel;
import com.example.app.ourapplication.rest.model.request.SubscribeReqModel;
import com.example.app.ourapplication.rest.model.response.Person;
import com.example.app.ourapplication.rest.model.response.SuccessRespModel;
import com.example.app.ourapplication.util.Helper;
import com.example.app.ourapplication.wss.WebSocketListener;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.squareup.picasso.Picasso;
import com.example.app.ourapplication.util.Helper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ROYSH on 6/23/2016.
 */
public class FeedRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = FeedRVAdapter.class.getSimpleName();


    private int lastPosition = -1;
    private List<Person> mFeeds;
    private Context mContext;
    private String userid;
    private RelativeLayout parent_rlyt;
    private ImageView subscribe;
    private String subscription="subscribe";
    private  ImageView iv;


    FeedRVAdapter(Context context, List<Person> mFeeds) {
        this.mContext = context;
        this.mFeeds = mFeeds;
    }
   private final String userId = PreferenceEditor.getInstance(mContext).getLoggedInUserName();
    private  String token ;

    public class PersonViewHolder1 extends RecyclerView.ViewHolder {
        CardView cv;
        RelativeLayout parent_rlyt;
        TextView senderName;
        TextView senderMessage;
        ImageView senderPhoto;
        TextView messageTime;
        ImageView subscribe;
        ImageView unsubscribe;

        PersonViewHolder1(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            parent_rlyt = (RelativeLayout) itemView.findViewById(R.id.card_view1);
            senderName = (TextView) itemView.findViewById(R.id.sender_name);
            senderMessage = (TextView) itemView.findViewById(R.id.sender_message);
            senderPhoto = (ImageView) itemView.findViewById(R.id.sender_photo);
            messageTime = (TextView) itemView.findViewById(R.id.message_time);
            subscribe = (ImageView) itemView.findViewById(R.id.subscribe);
            unsubscribe = (ImageView) itemView.findViewById(R.id.unsubscribe);
         //   token=((OurApplication) itemView.getContext()).getUserToken();
        }
    }

    public class PersonViewHolder2 extends RecyclerView.ViewHolder {
        CardView cv;
        TextView senderName;
        TextView senderMessage;
        ImageView senderPhoto;
        ImageView messagePhoto;
        TextView messageTime;
        ImageView subscribe;
        ImageView unsubscribe;

        PersonViewHolder2(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            senderName = (TextView) itemView.findViewById(R.id.sender_name);
            senderMessage = (TextView) itemView.findViewById(R.id.sender_message);
            senderPhoto = (ImageView) itemView.findViewById(R.id.sender_photo);
            messageTime = (TextView) itemView.findViewById(R.id.message_time);
            messagePhoto = (ImageView) itemView.findViewById(R.id.message_photo);
            subscribe = (ImageView) itemView.findViewById(R.id.subscribe);
            unsubscribe = (ImageView) itemView.findViewById(R.id.unsubscribe);
       //     token=((OurApplication) itemView.getContext()).getUserToken();
        }
    }

    public class PersonViewHolder3 extends RecyclerView.ViewHolder {
        CardView cv;
        TextView senderName;
        TextView senderMessage;
        ImageView senderPhoto;
        private SurfaceView mSurfaceView;
        private SimpleExoPlayer mPlayer;
        //private AspectRatioFrameLayout mAspectRatioLayout;
        private PlaybackControlView mPlaybackControlView;
        TextView messageTime;
        ProgressBar videoLoaderProgressBar;
        ImageView videoThumbnail;
        ImageView playIcon;
        ImageView subscribe;
        ImageView unsubscribe;

        PersonViewHolder3(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            senderName = (TextView) itemView.findViewById(R.id.sender_name);
            senderMessage = (TextView) itemView.findViewById(R.id.sender_message);
            senderPhoto = (ImageView) itemView.findViewById(R.id.sender_photo);
            messageTime = (TextView) itemView.findViewById(R.id.message_time);
            mSurfaceView = (SurfaceView) itemView.findViewById(R.id.surface_view);
          //  mAspectRatioLayout = (AspectRatioFrameLayout) itemView.findViewById(R.id.aspect_ratio_layout);
            mPlaybackControlView = (PlaybackControlView) itemView.findViewById(R.id.player_view);
            videoLoaderProgressBar = (ProgressBar) itemView.findViewById(R.id.video_loader_progress_bar);
            videoThumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            playIcon = (ImageView) itemView.findViewById(R.id.video_play_img_btn);
            subscribe = (ImageView) itemView.findViewById(R.id.subscribe);
            unsubscribe = (ImageView) itemView.findViewById(R.id.unsubscribe);
            //token=((OurApplication) itemView.getContext()).getUserToken();
        }
    }

    public class PersonViewHolder4 extends RecyclerView.ViewHolder {

        TextView senderName;
        TextView senderMessage;
        ImageView senderPhoto;
        TextView messageTime;
        ImageView subscribe;
        ImageView unsubscribe;

        PersonViewHolder4(View itemView) {
            super(itemView);
            senderName = (TextView) itemView.findViewById(R.id.sender_name);
            senderMessage = (TextView) itemView.findViewById(R.id.sender_message);
            senderPhoto = (ImageView) itemView.findViewById(R.id.sender_photo);
            messageTime = (TextView) itemView.findViewById(R.id.message_time);
            subscribe = (ImageView) itemView.findViewById(R.id.subscribe);
            unsubscribe = (ImageView) itemView.findViewById(R.id.unsubscribe);
           // token=((OurApplication) itemView.getContext()).getUserToken();
        }
    }



    @Override
    public int getItemCount() {
        return mFeeds.size();
    }

    @Override
    public int getItemViewType(int i) {
        if (mFeeds.get(i).getType().equals("F")){
            if(mFeeds.get(i).getPhotoMsg() != null && !mFeeds.get(i).getPhotoMsg().isEmpty() && mFeeds.get(i).getPhotoMsg().contains("/file_download/Pictures/"))
            {
                return 2;
            }
            else if(mFeeds.get(i).getPhotoMsg() != null && !mFeeds.get(i).getPhotoMsg().isEmpty() && mFeeds.get(i).getPhotoMsg().contains("/file_download/Videos/"))
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
            mContext=viewGroup.getContext();
            return new PersonViewHolder1(v);
        } else if (viewType == 2) {
            Log.d(TAG, "PersonViewHolder2 created");
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_item_image, viewGroup, false);
            return new PersonViewHolder2(v);
        } else if (viewType == 3) {
            Log.d(TAG, "PersonViewHolder3 created");
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_item_exo_video, viewGroup, false);
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
        final String postid;
        switch (viewHolder.getItemViewType()) {
            case 1:
                final PersonViewHolder1 vh1 = (PersonViewHolder1) viewHolder;
                postid=item.getPostId();
                vh1.senderName.setText(item.getSenderName());
                vh1.senderMessage.setText(item.getMessage());
                vh1.messageTime.setText(Helper.getRelativeTime(item.getTimeMsg()));
                // Picasso.with(mContext).load(item.getPhotoId()).resize(50, 50).into(vh1.senderPhoto);
                Log.d(TAG, "SUBSCRIPTION :" + item.getSubscriptionFlag());
                Log.d(TAG, "sender name :" + item.getUserId());
                Log.d(TAG, "userid :" + userId);
                token=OurApplication.getUserToken();

                if(item.getSubscriptionFlag()!=null && item.getSubscriptionFlag().substring(1, 1).equals("0")) {
                    Log.d(TAG, "I enter here:1" );
                    vh1.subscribe.setImageResource(R.mipmap.subscribe_icon);
                    subscription="subscribe";
                }
                else if(item.getSubscriptionFlag()!=null && item.getSubscriptionFlag().substring(1, 1).equals("1"))
                {
                    Log.d(TAG, "I enter here:2" );
                    vh1.subscribe.setImageResource(R.mipmap.unsubscribe_icon);
                    subscription="unsubscribe";

                }

                Picasso(item.getPhotoId(), vh1.senderPhoto);

                openProfile(item,vh1.senderPhoto);

                openDiscussion(item, vh1.cv);

                setAnimation(vh1.cv, i);

                vh1.subscribe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (subscription.equals("subscribe")) {
                            Log.d(TAG, "subscriprion:" + subscription);
                            vh1.subscribe.setImageResource(R.mipmap.unsubscribe_icon);
                            Helper.PostSubscription(v, token, item.getPostId(), "Y");
                            subscription = "unsubscribe";

                        } else {
                            Log.d(TAG, "unsubscriprion:" + subscription);
                            vh1.subscribe.setImageResource(R.mipmap.subscribe_icon);
                            Helper.PostSubscription(v, token, item.getPostId(), "N");
                            subscription = "subscribe";
                        }
                    }
                });

                break;

            case 2:

                final PersonViewHolder2 vh2 = (PersonViewHolder2) viewHolder;
                vh2.senderName.setText(item.getSenderName());
                vh2.senderMessage.setText(item.getMessage());
                vh2.messageTime.setText(Helper.getRelativeTime(item.getTimeMsg()));
                Picasso(item.getPhotoMsg(), vh2.messagePhoto);
                Log.d(TAG, "IMAGE URL :" + item.getPhotoMsg());
                Picasso(item.getPhotoId(), vh2.senderPhoto);
                token=OurApplication.getUserToken();

                Log.d(TAG, "SUBSCRIPTION :" + item.getSubscriptionFlag());
                if(item.getSubscriptionFlag()!=null && item.getSubscriptionFlag().substring(1,1).equals("0")) {
                    Log.d(TAG, "I enter here:1" );
                    vh2.subscribe.setImageResource(R.mipmap.subscribe_icon);
                    subscription="subscribe";
                }
                else if(item.getSubscriptionFlag()!=null && item.getSubscriptionFlag().substring(1,1).equals("1"))
                {
                    Log.d(TAG, "I enter here:2" );
                    vh2.subscribe.setImageResource(R.mipmap.unsubscribe_icon);
                    subscription="unsubscribe";

                }

                openProfile(item,vh2.senderPhoto);
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

                vh2.subscribe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (subscription.equals("subscribe")) {
                            Log.d(TAG, "subscriprion:" + subscription);
                            vh2.subscribe.setImageResource(R.mipmap.unsubscribe_icon);
                            Helper.PostSubscription(v, token, item.getPostId(), "Y");
                            subscription = "unsubscribe";

                        } else {
                            Log.d(TAG, "unsubscriprion:" + subscription);
                            vh2.subscribe.setImageResource(R.mipmap.subscribe_icon);
                            Helper.PostSubscription(v, token, item.getPostId(), "N");
                            subscription = "subscribe";
                        }
                    }
                });
                break;

            case 3:

                final PersonViewHolder3 vh3 = (PersonViewHolder3) viewHolder;

                vh3.senderName.setText(item.getSenderName());
                vh3.senderMessage.setText(item.getMessage());
                vh3.messageTime.setText(Helper.getRelativeTime(item.getTimeMsg()));
                token=OurApplication.getUserToken();
                Log.d(TAG, "SUBSCRIPTION :" + item.getSubscriptionFlag());
                if(item.getSubscriptionFlag()!=null && item.getSubscriptionFlag().substring(1,1).equals("0")) {
                    Log.d(TAG, "I enter here:1" );
                    vh3.subscribe.setImageResource(R.mipmap.subscribe_icon);
                    subscription="subscribe";
                }
                else if(item.getSubscriptionFlag()!=null && item.getSubscriptionFlag().substring(1,1).equals("1"))
                {
                    Log.d(TAG, "I enter here:2" );
                    vh3.subscribe.setImageResource(R.mipmap.unsubscribe_icon);
                    subscription="unsubscribe";

                }
                Log.d(TAG, "IMAGE URL :" + item.getPhotoMsg());
                int index = item.getPhotoMsg().lastIndexOf('/');
                String Thumbnail_URL = item.getPhotoMsg().substring(0, index);
                 String Thumbnail_filename = item.getPhotoMsg().substring(index + 1);
                Log.d(TAG, "Thumbnail_URL" + Thumbnail_URL);
                Log.d(TAG, "Thumbnail_filename" + Thumbnail_filename.substring(0, Thumbnail_filename.indexOf('.')) + ".jpg");
                String thumbnailfilename = Thumbnail_URL.concat("/video_thumbnail_").concat(Thumbnail_filename.substring(0, Thumbnail_filename.indexOf('.')) + ".jpg");
                Log.d(TAG, "Video Thumbnail file name :" + thumbnailfilename);
                Picasso(thumbnailfilename, vh3.videoThumbnail);
                vh3.playIcon.setVisibility(View.VISIBLE);

                // initialize player
                Handler handler = new Handler();
                ExtractorsFactory extractor = new DefaultExtractorsFactory();
                DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("ExoPlayer Demo");
                vh3.mPlayer = ExoPlayerFactory.newSimpleInstance(
                        mContext,
                        new DefaultTrackSelector(handler),
                        new DefaultLoadControl()
                );

                vh3.mPlaybackControlView.requestFocus();
                vh3.mPlaybackControlView.setPlayer(vh3.mPlayer);


                // initialize source
                MediaSource videoSource = new ExtractorMediaSource(
                        Uri.parse(item.getPhotoMsg()),
                        dataSourceFactory,
                        extractor,
                        null,
                        null
                );
                vh3.mPlayer.prepare(videoSource);

                vh3.videoLoaderProgressBar.setVisibility(View.VISIBLE);
                vh3.videoThumbnail.setVisibility(View.VISIBLE);

                Log.d(TAG, "Video Url" + ": " + item.getPhotoMsg());

                SurfaceHolder.Callback mSurfaceHolderCallback = new SurfaceHolder.Callback() {

                    @Override
                    public void surfaceCreated(SurfaceHolder surfaceHolder) {
                        if (vh3.mPlayer != null) {
                            vh3.mPlayer.setVideoSurfaceHolder(surfaceHolder);
                        }
                    }

                    @Override
                    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                    }


                    @Override
                    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                        if (vh3.mPlayer != null) {
                            vh3.mPlayer.setVideoSurfaceHolder(null);
                        }
                    }

                };
                vh3.mSurfaceView.getHolder().addCallback(mSurfaceHolderCallback);


                SimpleExoPlayer.VideoListener mVideoListener = new SimpleExoPlayer.VideoListener() {
                    @Override
                    public void onVideoSizeChanged ( int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio){

                        vh3.mSurfaceView.getHolder().setFixedSize(vh3.cv.getWidth(),(int) (((double) height / width) * vh3.cv.getWidth()));
                        Log.d(TAG, "Surface view params: " + width + " " + height + " " + vh3.cv.getWidth() + " " + (((double) height / width) * vh3.cv.getWidth()) + " " + (int) (((double) height / width) * vh3.cv.getWidth()) );

              //  vh3.mAspectRatioLayout.setAspectRatio(pixelWidthHeightRatio);
            }

                @Override
                public void onRenderedFirstFrame (Surface surface){
                    //ViewGroup.LayoutParams layPar= vh3.videoThumbnail.getLayoutParams();

                    vh3.videoLoaderProgressBar.setVisibility(View.INVISIBLE);

                    vh3.playIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                vh3.mPlayer.setPlayWhenReady(true);
                                vh3.videoThumbnail.setVisibility(View.INVISIBLE);
                                vh3.playIcon.setVisibility(View.INVISIBLE);
                                vh3.mPlaybackControlView.hide();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });


            }

            @Override
                public void onVideoDisabled (DecoderCounters counters){

            }
        };

                vh3.mPlayer.setVideoListener(mVideoListener);


                ExoPlayer.EventListener mEventListener = new ExoPlayer.EventListener() {
                    @Override
                    public void onLoadingChanged(boolean isLoading) {

                    }

                    @Override
                    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                    }

                    @Override
                    public void onTimelineChanged(Timeline timeline, Object manifest) {

                    }

                    @Override
                    public void onPlayerError(ExoPlaybackException error) {

                    }

                    @Override
                    public void onPositionDiscontinuity() {

                    }
                };
                vh3.mPlayer.addListener(mEventListener);

                vh3.mSurfaceView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vh3.mPlaybackControlView.show();

                    }
                });

                Picasso(item.getPhotoId(), vh3.senderPhoto);
                openProfile(item,vh3.senderPhoto);
                openDiscussion(item, vh3.cv);
                setAnimation(vh3.cv, i);
                vh3.subscribe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (subscription.equals("subscribe")) {
                            Log.d(TAG, "subscriprion:" + subscription);
                            vh3.subscribe.setImageResource(R.mipmap.unsubscribe_icon);
                            Helper.PostSubscription(v, token, item.getPostId(), "Y");
                            subscription = "unsubscribe";

                        } else {
                            Log.d(TAG, "unsubscriprion:" + subscription);
                            vh3.subscribe.setImageResource(R.mipmap.subscribe_icon);
                            Helper.PostSubscription(v, token, item.getPostId(), "N");
                            subscription = "subscribe";
                        }
                    }
                });
                break;


            case 4:
               final PersonViewHolder4 vh4 = (PersonViewHolder4) viewHolder;
                vh4.senderName.setText(item.getSenderName());
                vh4.senderMessage.setText(item.getMessage());
                token=OurApplication.getUserToken();
                vh4.messageTime.setText(Helper.getRelativeTime(item.getTimeMsg()));
/*                Log.d(TAG, "SUBSCRIPTION :" + item.getSubscriptionFlag());
                if(item.getSubscriptionFlag()!=null && item.getSubscriptionFlag().substring(1,1).equals("0")) {
                    Log.d(TAG, "I enter here:1" );
                    vh4.subscribe.setImageResource(R.mipmap.subscribe_icon);
                    subscription="subscribe";
                }
                else if(item.getSubscriptionFlag()!=null && item.getSubscriptionFlag().substring(1,1).equals("1"))
                {
                    Log.d(TAG, "I enter here:2" );
                    vh4.subscribe.setImageResource(R.mipmap.unsubscribe_icon);
                    subscription="unsubscribe";

                }
*/
                Picasso(item.getPhotoId(), vh4.senderPhoto);
  /*             vh4.subscribe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (subscription.equals("subscribe")) {
                            Log.d(TAG, "subscriprion:" + subscription);
                            vh4.subscribe.setImageResource(R.mipmap.unsubscribe_icon);
                            Helper.PostSubscription(v, token, item.getPostId(), "Y");
                            subscription = "unsubscribe";

                        } else {
                            Log.d(TAG, "unsubscriprion:" + subscription);
                            vh4.subscribe.setImageResource(R.mipmap.subscribe_icon);
                            Helper.PostSubscription(v, token, item.getPostId(), "N");
                            subscription = "subscribe";
                        }
                    }
                });*/
                break;

        }
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
        switch (holder.getItemViewType()) {
            case 3:
                PersonViewHolder3 vh3 = (PersonViewHolder3) holder;
                vh3.mPlayer.setPlayWhenReady(false);
                break;
        }

    }


    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);

        switch (holder.getItemViewType()) {
            case 3:
                PersonViewHolder3 vh3 = (PersonViewHolder3) holder;
                if (vh3.mPlayer != null) {
                    vh3.mPlayer.stop();
                    vh3.mPlayer.release();
                    vh3.mPlayer = null;
                }
                break;
        }
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
                //profileIntent.putExtra(Keys.KEY_ID, person.getPostId());
                profileIntent.putExtra("person", person);
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
                discussionIntent.putExtra("person", person);
                v.getContext().startActivity(discussionIntent);
            }
        });
    }






}