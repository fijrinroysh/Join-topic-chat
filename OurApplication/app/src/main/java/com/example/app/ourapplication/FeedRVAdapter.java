package com.example.app.ourapplication;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

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

    FeedRVAdapter(List<Person> mFeeds) {
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

    @Override
    public int getItemCount() {
        return mFeeds.size();
    }

    @Override
    public int getItemViewType(int i) {
        if (mFeeds.get(i).getPhotoMsg().equals("")) {
            return 1;
        } else  {
            return 2;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder pvh;
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
            Log.d(TAG,  "PersonViewHolder1 created");
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_item1, viewGroup, false);
            return new PersonViewHolder1(v);
        }
        else {
            Log.d(TAG,  "PersonViewHolder2 created");
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_item2, viewGroup, false);
            return new PersonViewHolder2(v);}
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        final Person item = mFeeds.get(i);
        switch (viewHolder.getItemViewType()) {
            case 1:
                PersonViewHolder1 vh1 = (PersonViewHolder1) viewHolder;
                vh1.senderName.setText(item.getSenderName());
                vh1.senderMessage.setText(item.getMessage());
                vh1.messageTime.setText(Helper.getRelativeTime(item.getTimeMsg()));
                vh1.senderPhoto.setImageBitmap(Helper.decodeImageString(item.getPhotoId()));
                vh1.cv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Intent discussionIntent = new Intent(v.getContext(), DiscussionActivity.class);
                        discussionIntent.putExtra(Keys.KEY_ID, item.getPostId());
                        v.getContext().startActivity(discussionIntent);
                    }
                });

                vh1.senderPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

          /* Below code is to open the Profile of the sender but now it opens the profile of the user */

              final Intent profileIntent = new Intent(v.getContext(), ProfileActivity.class);
              // profileIntent.putExtra(Keys.KEY_ID, item.mPostId);
              v.getContext().startActivity(profileIntent);

        /* Below code is to open the Sender Photo onclick */

        /*ImageView senderPhoto = (ImageView) v.findViewById(R.id.sender_photo);
        BitmapDrawable imagedrawable = (BitmapDrawable) senderPhoto.getDrawable();
        Bitmap imagebitmap = imagedrawable.getBitmap();
        Dialog builder = new Dialog(v.getContext(),android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //nothing;
            }
        });

        ImageView imageView = new ImageView(v.getContext());
        imageView.setImageBitmap(imagebitmap) ;
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        builder.addContentView(imageView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        builder.show();*/
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
                vh2.cv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Intent discussionIntent = new Intent(v.getContext(), DiscussionActivity.class);
                        discussionIntent.putExtra(Keys.KEY_ID, item.getPostId());
                        v.getContext().startActivity(discussionIntent);
                    }
                });
                vh2.senderPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

          /* Below code is to open the Profile of the sender but now it opens the profile of the user */

                        final Intent profileIntent = new Intent(v.getContext(), ProfileActivity.class);
                        // profileIntent.putExtra(Keys.KEY_ID, item.mPostId);
                        v.getContext().startActivity(profileIntent);

        /* Below code is to open the Sender Photo onclick */

        /*ImageView senderPhoto = (ImageView) v.findViewById(R.id.sender_photo);
        BitmapDrawable imagedrawable = (BitmapDrawable) senderPhoto.getDrawable();
        Bitmap imagebitmap = imagedrawable.getBitmap();
        Dialog builder = new Dialog(v.getContext(),android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //nothing;
            }
        });

        ImageView imageView = new ImageView(v.getContext());
        imageView.setImageBitmap(imagebitmap) ;
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        builder.addContentView(imageView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        builder.show();*/
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
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        //Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), android.R.anim.fade_in);
        Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), (position > lastPosition) ? R.anim.up_from_bottom: android.R.anim.fade_in);
        viewToAnimate.startAnimation(animation);
        lastPosition = position;
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }
}