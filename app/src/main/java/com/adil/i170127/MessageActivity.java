package com.adil.i170127;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView username;
    CircleImageView profile_pic;

    ImageView send_msg, send_img;
    EditText send_text;

    List<Chat> chats;
    MessageAdpater MyRvAdapter;
    RecyclerView rv;

    FirebaseUser fuser;
    DatabaseReference reference;

    User userData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_message);

        toolbar = findViewById(R.id.appBar);
        setSupportActionBar(toolbar);

        toolbar.setContentInsetsAbsolute(0,0);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        rv = findViewById(R.id.recycler_view);
        rv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        rv.setLayoutManager(linearLayoutManager);

        profile_pic = findViewById(R.id.userimg);
        username = findViewById(R.id.username);
        send_img = findViewById(R.id.send_img);
        send_msg = findViewById(R.id.btn_send);
        send_text = findViewById(R.id.text_send);

        Intent intent = getIntent();
        final String user_id = intent.getStringExtra("userId");

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");

        retrieveUser();

        send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msg = send_text.getText().toString().trim();

                if(!msg.equals("")){
                    sendMessage(fuser.getUid(), user_id, msg);
                } else {
                    Toast.makeText(MessageActivity.this, "Please write your message", Toast.LENGTH_SHORT).show();
                }
                send_text.setText("");

            }
        });


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = null;
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    for(DataSnapshot ds2 : ds.getChildren()){
                        user = ds2.getValue(User.class);
                        if (user_id.equals(user.getId()) == true){
                            userData = user;
                            username.setText(user.getFname()+ " " + user.getLname());
                            Picasso.get().load(user.getImgUri()).fit().into(profile_pic);
                            readMessage(user_id, user.getImgUri());
                            break;
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void retrieveUser(){
        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileBottomSheet profileBottomSheet = new ProfileBottomSheet(userData, 1);
                profileBottomSheet.show(getSupportFragmentManager(), "User Profile");
            }
        });
    }


    private void sendMessage(String sender, String receiver, String message){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);

        reference.child("Chats").push().setValue(hashMap);

    }

    private void readMessage(final String userId, final String imgUrl){
        chats = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chats.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Chat chat = ds.getValue(Chat.class);
                    Log.d("In Chat: ", ds.getKey());
                    Log.d("Reading Chats: ",chat.getMessage() );
                    if( chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userId)) {
                        chats.add(chat);
                    }
                    if( chat.getReceiver().equals(userId) && chat.getSender().equals(fuser.getUid())) {
                        chats.add(chat);
                    }

                }
                MyRvAdapter = new MessageAdpater(MessageActivity.this, chats, imgUrl);
                rv.setAdapter(MyRvAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}