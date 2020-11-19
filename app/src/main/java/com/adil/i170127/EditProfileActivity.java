package com.adil.i170127;

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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class EditProfileActivity extends AppCompatActivity {

    private static int PICK_IMAGE = 101;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference reference;


    Toolbar toolbar;
    RelativeLayout rl;

    EditText fname,lname,number,bio,date;
    Button male,female,none,save;
    TextView title;

    String f_name,l_name,no,bio_data,dt,gender,img,id;
    ImageView profile;
    Uri imagePath = null;
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
            userData = (User)getIntent().getSerializableExtra("UserData"); //Obtaining data
        }

        Picasso.get().load(userData.getImgUri()).fit().into(profile);
        fname.setText(userData.getFname());
        lname.setText(userData.getLname());


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
    private void addOrRemoveProperty(View view, int property, boolean flag){
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        if(flag){
            layoutParams.addRule(property);
        }else {
            layoutParams.removeRule(property);
        }
        view.setLayoutParams(layoutParams);
    }
}