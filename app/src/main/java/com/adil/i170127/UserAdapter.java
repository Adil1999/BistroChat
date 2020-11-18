package com.adil.i170127;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {

    Context context;
    List<User> users;

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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("userId", user.getId());
                context.startActivity(intent);
            }
        });

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
        public TextView profile_name;
        public CircleImageView profile_pic;
        public MyViewHolder(View itemView){
            super(itemView);
            profile_name = itemView.findViewById(R.id.profile_name);
            profile_pic = itemView.findViewById(R.id.profile_pic);
        }

    }

}
