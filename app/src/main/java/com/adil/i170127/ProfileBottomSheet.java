package com.adil.i170127;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileBottomSheet extends BottomSheetDialogFragment {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    DatabaseReference reference;
    StorageReference storageReference;
    FirebaseUser user;

    TextView profile_name, profile_number, profile_gender;
    CircleImageView profile_pic, editBtn;
    User userData;

    public void set_userData(){
        storageReference.child(user.getUid()).getDownloadUrl()
            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).fit().centerCrop().into(profile_pic);
                }
            });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    userData = ds.getValue(User.class);
                    profile_name.setText(userData.getFname() + " " + userData.getLname());
                    profile_number.setText(userData.getNumber());
                    profile_gender.setText(userData.getGender());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("Users").child(user.getUid());
        storageReference = FirebaseStorage.getInstance().getReference();

        View v = inflater.inflate(R.layout.profile_bottom_sheet, container, false);

        profile_name = v.findViewById(R.id.profile_name);
        profile_pic = v.findViewById(R.id.profile_pic);
        profile_number = v.findViewById(R.id.profile_number);
        profile_gender = v.findViewById(R.id.profile_gender);
        editBtn = v.findViewById(R.id.edit_btn);

        set_userData();

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Edit Button Called", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        View view = View.inflate(getContext(), R.layout.profile_bottom_sheet, null);
        dialog.setContentView(view);
        ((View) view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }
}
