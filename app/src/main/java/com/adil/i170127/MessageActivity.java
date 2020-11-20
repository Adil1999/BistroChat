package com.adil.i170127;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    private static int PICK_IMAGE = 102;

    Toolbar toolbar;
    TextView username, status;
    CircleImageView profile_pic;

    ImageView send_msg, send_img;
    EditText send_text;
    Uri imagePath = null;

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
        status = findViewById(R.id.status);
        send_img = findViewById(R.id.send_img);
        send_msg = findViewById(R.id.btn_send);
        send_text = findViewById(R.id.text_send);

        Intent intent = getIntent();
        final String user_id = intent.getStringExtra("userId");

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");

        retrieveUser();

        send_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image."), PICK_IMAGE);
            }
        });

        send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msg = send_text.getText().toString().trim();

                if(imagePath != null){
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference("Chats");
                    storageReference = storageReference.child(fuser.getUid());
                    storageReference.putFile(imagePath)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                                    task
                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String img = uri.toString();
                                                    sendMessage(fuser.getUid(), user_id, img, true);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(
                                                            MessageActivity.this,
                                                            "Failed to Upload Image",
                                                            Toast.LENGTH_LONG
                                                    ).show();
                                                }
                                            });
                                }
                            });
                } else {

                    if(!msg.equals("")){
                        sendMessage(fuser.getUid(), user_id, msg, false);
                    } else {
                        Toast.makeText(MessageActivity.this, "Please write your message", Toast.LENGTH_SHORT).show();
                    }
                    send_text.setText("");
                }

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
                            if(user.getStatus().equals("online")){
                                status.setText("is online");
                            }
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

//    private void status(final String status){
//        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
//        HashMap<String, Object> hashMap = new HashMap<>();
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                    String key = ds.getKey();
//                    ref.child(key).child("status").setValue(status);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        status("online");
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        status("offline");
//    }

    private void retrieveUser(){
        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileBottomSheet profileBottomSheet = new ProfileBottomSheet(userData, 1);
                profileBottomSheet.show(getSupportFragmentManager(), "User Profile");
            }
        });
    }


    private void sendMessage(String sender, String receiver, String message, boolean isImg){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isImg", isImg);
        reference.child("Chats").push().setValue(hashMap);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data.getData() != null) {
            imagePath = data.getData();
        }
        super.onActivityResult(requestCode, resultCode, data);
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