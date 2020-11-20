package com.adil.i170127;

import android.content.Context;
import android.content.Intent;
import android.os.TestLooperManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {

    Context context;
    List<User> users;
    String message;

    public UserAdapter(Context c, List<User> users){
        super();
        this.context = c;
        this.users = users;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.user_row, parent, false);
        return new UserAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final User user = users.get(position);
        holder.profile_name.setText(user.getFname() + " " + user.getLname());
        Picasso.get().load(user.getImgUri()).into(holder.profile_pic);
        getLastMessage(user.getId(), holder.msg);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("userId", user.getId());
                context.startActivity(intent);
            }
        });

        if(user.getStatus().equals("online")){
            holder.img_on.setVisibility(View.VISIBLE);
        } else {
            holder.img_on.setVisibility((View.GONE));
        }

//        Log.d("IN HOME RV", users.get(position).getFname());
//        Log.d("IN HOME RV", users.get(position).getLname());
//        Log.d("IN HOME RV", users.get(position).getNumber());
//
//        String var = Integer.toString(getItemCount());
//        Log.i("size", var);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView profile_name, msg;
        public CircleImageView profile_pic, img_on;
        public MyViewHolder(View itemView){
            super(itemView);
            profile_name = itemView.findViewById(R.id.profile_name);
            profile_pic = itemView.findViewById(R.id.profile_pic);
            msg = itemView.findViewById(R.id.msg);
            img_on = itemView.findViewById(R.id.img_on);
        }

    }

    private void getLastMessage(final String userId, final TextView lastMessage){
        message = "xyz";
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Chat chat = ds.getValue(Chat.class);
                    if( chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userId)) {
                        message = chat.getMessage();
                    }
                    if( chat.getReceiver().equals(userId) && chat.getSender().equals(fuser.getUid())) {
                        message = chat.getMessage();
                    }
                }
                switch (message){
                    case "xyz":
                        lastMessage.setText("No Messages Yet!!!");
                        break;
                    default:
                        lastMessage.setText(message);
                        break;
                }
                message = "default";

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
