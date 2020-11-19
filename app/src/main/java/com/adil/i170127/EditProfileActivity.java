package com.adil.i170127;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {

    private static int PICK_IMAGE = 101;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference reference;


    Toolbar toolbar;
    RelativeLayout rl;

    EditText fname, lname, number, bio, date;
    Button male, female, none, save;
    TextView title;

    String f_name, l_name, no, bio_data, dt, gender, img, id;
    ImageView profile;
    Uri imagePath;
    User userData;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_edit_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users").child(user.getUid());

        rl = findViewById(R.id.bar_layout);
        toolbar = rl.findViewById(R.id.myAppBar);
        title = rl.findViewById(R.id.title);
        fname = findViewById(R.id.fname);
        lname = findViewById(R.id.lname);
        number = findViewById(R.id.number);
        bio = findViewById(R.id.bio);
        date = findViewById(R.id.date);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        none = findViewById(R.id.none);
        save = findViewById(R.id.save);
        profile = findViewById(R.id.civ);

        title.setText("Edit Profile");
        title.setTextColor(Color.BLACK);
        addOrRemoveProperty(title, RelativeLayout.ALIGN_PARENT_LEFT, true);
        addOrRemoveProperty(title, RelativeLayout.CENTER_IN_PARENT, false);

        id = user.getUid().trim();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userData = (User) getIntent().getSerializableExtra("UserData"); //Obtaining data
        }

        Picasso.get().load(userData.getImgUri()).fit().into(profile);
        fname.setText(userData.getFname());
        lname.setText(userData.getLname());
        number.setText(userData.getNumber());
        bio.setText(userData.getBio());
        date.setText(userData.getDate());
        gender = userData.getGender();

        id = user.getUid().trim();


        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image."), PICK_IMAGE);
            }
        });

        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = male.getText().toString().trim();
                male.setBackgroundResource(R.drawable.gender_filled);
            }
        });
        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = female.getText().toString().trim();
                female.setBackgroundResource(R.drawable.gender_filled);
            }
        });
        none.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = none.getText().toString().trim();
                none.setBackgroundResource(R.drawable.gender_filled);
            }
        });

        final String key = reference.push().getKey();

        Log.d("In Edit Profile: ", key);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                f_name = fname.getText().toString().trim();
                l_name = lname.getText().toString().trim();
                dt = date.getText().toString().trim();
                no = number.getText().toString().trim();
                bio_data = bio.getText().toString().trim();

                if (TextUtils.isEmpty(f_name)) {
                    Toast.makeText(EditProfileActivity.this, "Please Enter First Name", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(l_name)) {
                    Toast.makeText(EditProfileActivity.this, "Please Enter Last Name", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(dt)) {
                    Toast.makeText(EditProfileActivity.this, "Please Enter date of Birth", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(no)) {
                    Toast.makeText(EditProfileActivity.this, "Please Enter number", Toast.LENGTH_LONG).show();
                    return;
                }

                if (imagePath != null) {
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                    storageReference = storageReference.child(user.getUid());
                    storageReference.putFile(imagePath)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                                    task
                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    img = uri.toString();
                                                    reference.addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                                String key = ds.getKey();
                                                                reference.child(key).child("fname").setValue(f_name);
                                                                reference.child(key).child("lname").setValue(l_name);
                                                                reference.child(key).child("number").setValue(no);
                                                                reference.child(key).child("bio").setValue(bio_data);
                                                                reference.child(key).child("date").setValue(dt);
                                                                reference.child(key).child("gender").setValue(gender);
                                                                reference.child(key).child("imgUri").setValue(img);
                                                                startActivity(new Intent(EditProfileActivity.this, HomeActivity.class));
                                                                finish();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(
                                            EditProfileActivity.this,
                                            "Failed to Upload Image",
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            });
                } else {
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                String key = ds.getKey();
                                reference.child(key).child("fname").setValue(f_name);
                                reference.child(key).child("lname").setValue(l_name);
                                reference.child(key).child("number").setValue(no);
                                reference.child(key).child("bio").setValue(bio_data);
                                reference.child(key).child("date").setValue(dt);
                                reference.child(key).child("gender").setValue(gender);
                                startActivity(new Intent(EditProfileActivity.this, HomeActivity.class));
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
        //removeProfile();
    }

    private String getKey() {
        final String[] key = new String[1];
        reference = database.getReference("Users").child(user.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    key[0] = ds.getKey();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return key[0];
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data.getData() != null) {
            imagePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                profile.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void addOrRemoveProperty(View view, int property, boolean flag) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        if (flag) {
            layoutParams.addRule(property);
        } else {
            layoutParams.removeRule(property);
        }
        view.setLayoutParams(layoutParams);
    }
}