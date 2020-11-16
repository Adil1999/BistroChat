package com.adil.i170127;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    Toolbar toolbar;
    TextView title;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_home);
        firebaseAuth = FirebaseAuth.getInstance();

        toolbar = findViewById(R.id.myAppBar);
        title = toolbar.findViewById(R.id.title);
        setSupportActionBar(toolbar);

        title.setText("BistroChat");
        toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, MainActivity.class));
            }
        });

    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        user = firebaseAuth.getCurrentUser();
//        if(user != null){
//            //Toast.makeText(HomeActivity.this, "User is Logged in "+user.getEmail(), Toast.LENGTH_LONG).show();
//        } else {
//            startActivity(new Intent(HomeActivity.this, MainActivity.class));
//            finish();
//        }
//    }
}