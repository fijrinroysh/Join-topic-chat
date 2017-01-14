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
public class CommentRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private final String TAG = CommentRVAdapter.class.getSimpleName();

    private int lastPosition = -1;


    private List<Person> mFeeds;

    CommentRVAdapter(List<Person> mFeeds) {
        this.mFeeds = mFeeds;
    }

    public class PersonViewHolder3 extends RecyclerView.ViewHolder {
        CardView cv;
        TextView senderName;
        TextView senderMessage;
        ImageView senderPhoto;
        TextView messageTime;
        ImageView messagePhoto;

        PersonViewHolder3(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            senderName = (TextView) itemView.findViewById(R.id.sender_name);
            senderMessage = (TextView) itemView.findViewById(R.id.sender_message);
            senderPhoto = (ImageView) itemView.findViewById(R.id.sender_photo);
            messageTime = (TextView) itemView.findViewById(R.id.message_time);
            messagePhoto = (ImageView) itemView.findViewById(R.id.message_photo);
        }
    }


    public class PersonViewHolder4 extends RecyclerView.ViewHolder {
        CardView cv;
        TextView senderName;
        TextView senderMessage;
        ImageView senderPhoto;
        TextView messageTime;

        PersonViewHolder4(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
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
        if (mFeeds.get(i).mType.equals("F")) {
            return 0;
        } else  {
            return 1;
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
                if (viewType == 0) {
            Log.d(TAG,  "PersonViewHolder3 created");
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_item3, viewGroup, false);
            return new PersonViewHolder3(v);
        }
            else{
            Log.d(TAG,  "PersonViewHolder4 created");
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_item4, viewGroup, false);
            return new PersonViewHolder4(v);}
    }



    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        Person item = mFeeds.get(i);
        switch (viewHolder.getItemViewType()) {
                case 0:
                    PersonViewHolder3 vh3 = (PersonViewHolder3) viewHolder;
                    vh3.senderName.setText(item.mSenderName);
                    vh3.senderMessage.setText(item.mMessage);
                    vh3.messageTime.setText(Helper.getRelativeTime(item.mTimeMsg));
                    vh3.messagePhoto.setImageBitmap(Helper.decodeImageString(item.mPhotoMsg));
                    vh3.senderPhoto.setImageBitmap(Helper.decodeImageString(item.mPhotoId));
                    setAnimation(vh3.cv, i);
                    break;
                case 1:
                    PersonViewHolder4 vh4 = (PersonViewHolder4) viewHolder;
                    vh4.senderName.setText(item.mSenderName);
                    vh4.senderMessage.setText(item.mMessage);
                    vh4.messageTime.setText(Helper.getRelativeTime(item.mTimeMsg));
                    vh4.senderPhoto.setImageBitmap(Helper.decodeImageString(item.mPhotoId));
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
                    if (position > lastPosition)
            {
                Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), android.R.anim.fade_in);
                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }


    }



    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }


}