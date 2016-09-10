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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app.ourapplication.util.Helper;

import java.util.List;

/**
 * Created by ROYSH on 6/23/2016.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder> {


    private final String TAG = RVAdapter.class.getSimpleName();

    List<Person> mFeeds;

    RVAdapter(List<Person> mFeeds) {
        this.mFeeds = mFeeds;
    }




    public class PersonViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView senderName;
        TextView senderMessage;
        ImageView senderPhoto;
        ImageView messagePhoto;
        TextView messageTime;

        PersonViewHolder(View itemView) {
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
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_item, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        Log.d(TAG, "PersoniewHolder");
        return pvh;
    }



    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {

        personViewHolder.senderName.setText(mFeeds.get(i).name);

        personViewHolder.senderMessage.setText(mFeeds.get(i).msg);

        personViewHolder.messageTime.setText(mFeeds.get(i).timeMsg);

        personViewHolder.messagePhoto.setImageBitmap(Helper.decodeImageString(mFeeds.get(i).photoMsg));

        personViewHolder.senderPhoto.setImageBitmap(Helper.decodeImageString(mFeeds.get(i).photoId));

        Log.d(TAG, "onBindViewHolder :" + i);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


}
