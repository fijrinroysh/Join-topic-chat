package com.example.app.ourapplication;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.app.ourapplication.util.Helper;

import java.util.List;

/**
 * Created by ROYSH on 6/23/2016.
 */
public class RVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private final String TAG = RVAdapter.class.getSimpleName();

    private int lastPosition = -1;


    private List<Person> mFeeds;

    RVAdapter(List<Person> mFeeds) {
        this.mFeeds = mFeeds;
    }

    public class PersonViewHolder1 extends RecyclerView.ViewHolder {
        CardView cv;
        TextView senderName;
        TextView receiverName;
        TextView senderMessage;
        ImageView senderPhoto;
        ImageView messagePhoto;
        TextView messageTime;

        PersonViewHolder1(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            senderName = (TextView) itemView.findViewById(R.id.sender_name);
            receiverName = (TextView) itemView.findViewById(R.id.receiver_name);
            senderMessage = (TextView) itemView.findViewById(R.id.sender_message);
            senderPhoto = (ImageView) itemView.findViewById(R.id.sender_photo);
            messageTime = (TextView) itemView.findViewById(R.id.message_time);
           // messagePhoto = (ImageView) itemView.findViewById(R.id.message_photo);

        }
    }


    public class PersonViewHolder2 extends RecyclerView.ViewHolder {
        CardView cv;
        TextView senderName;
        TextView receiverName;
        TextView senderMessage;
        ImageView senderPhoto;
        ImageView messagePhoto;
        TextView messageTime;

        PersonViewHolder2(View itemView) {
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
        if (mFeeds.get(i).getPhotoMsg().length() == 0) {
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
            Log.d(TAG,  "PersonViewHolder1 created");
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_item1, viewGroup, false);
            return new PersonViewHolder1(v);
        }
            else{
            Log.d(TAG,  "PersonViewHolder2 created");
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_item2, viewGroup, false);
            return new PersonViewHolder2(v);}
    }



    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        Person item = mFeeds.get(i);
        switch (viewHolder.getItemViewType()) {
                case 0:
                    PersonViewHolder1 vh1 = (PersonViewHolder1) viewHolder;
                    vh1.senderName.setText(item.getSenderName());
                    vh1.receiverName.setText(item.getReceiverName());
                    vh1.senderMessage.setText(item.getMessage());
                    vh1.messageTime.setText(item.getTimeMsg());
                    vh1.senderPhoto.setImageBitmap(Helper.decodeImageString(item.getPhotoId()));
                    setAnimation(vh1.cv, i);
                    break;
                case 1:
                    PersonViewHolder2 vh2 = (PersonViewHolder2) viewHolder;
                    vh2.senderName.setText(item.getSenderName());
                    vh2.receiverName.setText(item.getReceiverName());
                    vh2.senderMessage.setText(item.getMessage());
                    vh2.messageTime.setText(item.getTimeMsg());
                    vh2.messagePhoto.setImageBitmap(Helper.decodeImageString(item.getPhotoMsg()));
                    vh2.senderPhoto.setImageBitmap(Helper.decodeImageString(item.getPhotoId()));
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
        if (position > lastPosition)
        {

           /* Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    0.0f, Animation.RELATIVE_TO_SELF, -5.0f);

            animation.setDuration(400);
            animation.setFillAfter(true);
            animation.setFillEnabled(true);*/

            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), android.R.anim.fade_in);
            viewToAnimate.startAnimation(animation);

            lastPosition = position;
        }
    }

/*
    @Override
    public void onViewDetachedFromWindow(final RecyclerView.ViewHolder holder)
    {
        ((RVAdapter)pv1).clearAnimation();
    }

    public void clearAnimation()
    {
        cv.clearAnimation();
    }

*/

}