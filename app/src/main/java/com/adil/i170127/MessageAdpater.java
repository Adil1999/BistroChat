package com.adil.i170127;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdpater extends RecyclerView.Adapter<MessageAdpater.MyViewHolder> {

    public static final int msg_left = 0;
    public static final int msg_right = 1;

    Context context;
    List<Chat> chats;
    String imageUrl;

    FirebaseUser fuser;

    public MessageAdpater(Context c, List<Chat> chats, String imgUrl){
        super();
        this.context = c;
        this.chats = chats;
        this.imageUrl = imgUrl;
    }

    @NonNull
    @Override
    public MessageAdpater.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == msg_right){
            View itemView = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdpater.MyViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdpater.MyViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Chat chat = chats.get(position);
        Log.d("In Message Adapter: ",chat.getMessage());
        holder.show_message.setText(chat.getMessage());
        Picasso.get().load(imageUrl).into(holder.profile_pic);
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView show_message;
        public CircleImageView profile_pic;
        public MyViewHolder(View itemView){
            super(itemView);
            show_message = itemView.findViewById(R.id.show_message);
            profile_pic = itemView.findViewById(R.id.profile_image);
        }

    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        if(chats.get(position).getSender().equals(fuser.getUid())){
            return msg_right;
        } else {
            return  msg_left;
        }
    }
}