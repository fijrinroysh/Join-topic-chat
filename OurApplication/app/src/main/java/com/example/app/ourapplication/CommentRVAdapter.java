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

        PersonViewHolder3(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            senderName = (TextView) itemView.findViewById(R.id.sender_name);
            senderMessage = (TextView) itemView.findViewById(R.id.sender_message);
            senderPhoto = (ImageView) itemView.findViewById(R.id.sender_photo);
            messageTime = (TextView) itemView.findViewById(R.id.message_time);


        }
    }


    public class PersonViewHolder4 extends RecyclerView.ViewHolder {
        CardView cv;
        TextView senderName;
        TextView receiverName;
        TextView senderMessage;
        ImageView senderPhoto;
        ImageView messagePhoto;
        TextView messageTime;

        PersonViewHolder4(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            senderName = (TextView) itemView.findViewById(R.id.sender_name);
            receiverName = (TextView) itemView.findViewById(R.id.receiver_name);
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
        if (mFeeds.get(i).getType().equals("F")) {
            return 1;
        } else  {
            return 0;
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
                    PersonViewHolder3 vh1 = (PersonViewHolder3) viewHolder;
                    vh1.senderName.setText(item.getSenderName());
                    //vh1.receiverName.setText(item.getReceiverName());
                    vh1.senderMessage.setText(item.getMessage());
                    vh1.messageTime.setText(Helper.getRelativeTime(item.getTimeMsg()));
                    //vh1.senderPhoto.setImageBitmap(Helper.decodeImageString(item.getPhotoId()));
                    setAnimation(vh1.cv, i);
                    break;
                case 1:
                    PersonViewHolder4 vh2 = (PersonViewHolder4) viewHolder;
                    vh2.senderName.setText(item.getSenderName());
                    vh2.receiverName.setText(item.getReceiverName());
                    vh2.senderMessage.setText(item.getMessage());
                    vh2.messageTime.setText(Helper.getRelativeTime(item.getTimeMsg()));
                    vh2.messagePhoto.setImageBitmap(Helper.decodeImageString(item.getPhotoMsg()));
                    vh2.senderPhoto.setImageBitmap(Helper.decodeImageString(item.getPhotoId()));
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