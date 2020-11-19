package com.adil.i170127;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileBottomSheet extends BottomSheetDialogFragment {


    TextView profile_name, profile_number, profile_gender, tag;
    CircleImageView profile_pic, editBtn;
    User userData;
    int flag;


    ProfileBottomSheet(User user, int flag){
        this.userData = user;
        this.flag = flag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_bottom_sheet, container, false);

        tag = v.findViewById(R.id.tag);
        profile_name = v.findViewById(R.id.profile_name);
        profile_pic = v.findViewById(R.id.profile_pic);
        profile_number = v.findViewById(R.id.profile_number);
        profile_gender = v.findViewById(R.id.profile_gender);
        editBtn = v.findViewById(R.id.edit_btn);

        if(flag == 1){  //meaning user flag
            tag.setText("User Profile");
        }

        Picasso.get().load(userData.getImgUri()).fit().centerCrop().into(profile_pic);
        profile_name.setText(userData.getFname() + " " + userData.getLname());
        profile_number.setText(userData.getNumber());
        profile_gender.setText(userData.getGender());

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                intent.putExtra("UserData", userData);
                startActivity(intent);
                //Toast.makeText(getContext(), "Edit Button Called", Toast.LENGTH_SHORT).show();
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
