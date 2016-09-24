package com.example.app.ourapplication;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app.ourapplication.util.Helper;

import java.util.List;

/**
 * Created by ROYSH on 6/23/2016.
 */
public class RVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private final String TAG = RVAdapter.class.getSimpleName();



    List<Person> mFeeds;

    RVAdapter(List<Person> mFeeds) {
        this.mFeeds = mFeeds;
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


    @Override
    public int getItemCount() {
        return mFeeds.size();
    }

    @Override
    public int getItemViewType(int i) {
        if (mFeeds.get(i).photoMsg.length() == 0) {
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
            switch (viewHolder.getItemViewType()) {
                case 0:
                    PersonViewHolder1 vh1 = (PersonViewHolder1) viewHolder;
                    vh1.senderName.setText(mFeeds.get(i).sendername);
                    vh1.receiverName.setText(mFeeds.get(i).receivername);
                    vh1.senderMessage.setText(mFeeds.get(i).msg);
                    vh1.messageTime.setText(mFeeds.get(i).timeMsg);
                    vh1.senderPhoto.setImageBitmap(Helper.decodeImageString(mFeeds.get(i).photoId));
                    break;
                case 1:
                    PersonViewHolder2 vh2 = (PersonViewHolder2) viewHolder;
                    vh2.senderName.setText(mFeeds.get(i).sendername);
                    vh2.receiverName.setText(mFeeds.get(i).receivername);
                    vh2.senderMessage.setText(mFeeds.get(i).msg);
                    vh2.messageTime.setText(mFeeds.get(i).timeMsg);
                    vh2.messagePhoto.setImageBitmap(Helper.decodeImageString(mFeeds.get(i).photoMsg));
                    vh2.senderPhoto.setImageBitmap(Helper.decodeImageString(mFeeds.get(i).photoId));
                    break;


                //setAnimation(viewHolder.container, i);

            }

        Log.d(TAG, "onBindViewHolder :" + i);
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);


    }

/*
    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }


    @Override
    public void onViewDetachedFromWindow(final RecyclerView.ViewHolder holder)
    {
        ((CustomViewHolder)holder).clearAnimation();
    }

    public void clearAnimation()
    {
        mRootLayout.clearAnimation();
    }


*/

}
