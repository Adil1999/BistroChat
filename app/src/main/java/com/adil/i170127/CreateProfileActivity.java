package com.adil.i170127;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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

import java.io.IOException;

public class CreateProfileActivity extends AppCompatActivity {

    private static int PICK_IMAGE = 123;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference reference;


    Toolbar toolbar;

    EditText fname,lname,number,bio,date;
    Button male,female,none,save;
    TextView appTitle;
    String f_name,l_name,no,bio_data,dt,gender,img,id;
    ImageView profile;
    Uri imagePath = null;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_create_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users").child(user.getUid());

        toolbar = (Toolbar) findViewById(R.id.myAppBar);
        appTitle = toolbar.findViewById(R.id.title);
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

        appTitle.setText("Create Profile");
        appTitle.setTextColor(Color.BLACK);
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

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                f_name = fname.getText().toString().trim();
                l_name = lname.getText().toString().trim();
                dt = date.getText().toString().trim();
                no = number.getText().toString().trim();
                bio_data = bio.getText().toString().trim();

//                Log.d("ID", id);
//                Log.i("fname: ", f_name);
//                Log.i("lname: ", l_name);
//                Log.i("date: ", dt);
//                Log.i("number: ", no);
//                Log.i("bio_data: ", bio_data);

                if(TextUtils.isEmpty(f_name)){
                    Toast.makeText(CreateProfileActivity.this, "Please Enter First Name", Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(l_name)){
                    Toast.makeText(CreateProfileActivity.this, "Please Enter Last Name", Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(dt)){
                    Toast.makeText(CreateProfileActivity.this, "Please Enter date of Birth", Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(no)){
                    Toast.makeText(CreateProfileActivity.this, "Please Enter number", Toast.LENGTH_LONG).show();
                    return;
                }

                if(imagePath != null){
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
                                            //User object = new User(id, f_name, l_name, dt, gender, no, bio_data, img);
                                            Log.d("ID: ", id);
                                            reference.push().setValue(new User(user.getUid(), f_name, l_name, dt, gender, no, bio_data, img));
                                            startActivity(new Intent(CreateProfileActivity.this, HomeActivity.class));
                                            finish();
                                            Toast.makeText(
                                                    CreateProfileActivity.this,
                                                    id,
                                                    Toast.LENGTH_LONG
                                            ).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(
                                                    CreateProfileActivity.this,
                                                    "Failed to Upload Image",
                                                    Toast.LENGTH_LONG
                                            ).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(
                                    CreateProfileActivity.this,
                                    "Failed to Upload Image",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });

                } else {
                    Toast.makeText(CreateProfileActivity.this, "Please select picture", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}