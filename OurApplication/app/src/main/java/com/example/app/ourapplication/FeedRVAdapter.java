package com.example.app.ourapplication;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

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
        if (mFeeds.get(i).mPhotoMsg.equals("")) {
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
        Person item = mFeeds.get(i);
        switch (viewHolder.getItemViewType()) {
                case 1:
                    PersonViewHolder1 vh1 = (PersonViewHolder1) viewHolder;
                    vh1.senderName.setText(item.mSenderName);
                    vh1.senderMessage.setText(item.mMessage);
                    vh1.messageTime.setText(Helper.getRelativeTime(item.mTimeMsg));
                    vh1.senderPhoto.setImageBitmap(Helper.decodeImageString(item.mPhotoId));
                    setAnimation(vh1.cv, i);
                    break;
                case 2:
                    PersonViewHolder2 vh2 = (PersonViewHolder2) viewHolder;
                    vh2.senderName.setText(item.mSenderName);
                    vh2.senderMessage.setText(item.mMessage);
                    vh2.messageTime.setText(Helper.getRelativeTime(item.mTimeMsg));
                    vh2.messagePhoto.setImageBitmap(Helper.decodeImageString(item.mPhotoMsg));
                    vh2.senderPhoto.setImageBitmap(Helper.decodeImageString(item.mPhotoId));
                    setAnimation(vh2.cv, i);
                    break;
            }

        Log.d(TAG, "onBindViewHolder :" + i);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    private void setAnimation(View viewToAnimate, int position)
    {

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