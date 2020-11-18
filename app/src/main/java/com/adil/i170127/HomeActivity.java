package com.adil.i170127;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.nfc.cardemulation.HostNfcFService;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
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

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;


public class HomeActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    DatabaseReference reference;
    StorageReference storageReference;
    FirebaseUser user;

    Toolbar toolbar;
    DrawerLayout dl;
    ActionBarDrawerToggle t;
    NavigationView nv;
    RecyclerView rv;
    UserAdapter MyRvAdapter;

    TextView title,nav_email, nav_name;
    CircleImageView civ, search_img;
    List<User> users;
    User userData;

    public void nav_logout(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        Toast.makeText(HomeActivity.this, "Logout",Toast.LENGTH_SHORT).show();
    }

    public void set_navigation(){
        nv.setItemIconTintList(null);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {
                    case R.id.logout:
                        nav_logout();
                        break;
                    default:
                        return true;
                }
                return true;

            }
        });
    }

    public void retrieve_user(){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    userData = ds.getValue(User.class);
                    nav_name.setText(userData.getFname() + " " + userData.getLname());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void set_navHeaderFields(){
        nav_email.setText(user.getEmail());
        storageReference.child(user.getUid()).getDownloadUrl()
            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).fit().centerCrop().into(civ);
                    Picasso.get().load(uri).fit().centerCrop().into(search_img);
                }
            });
    }

    public void open_bottomSheet(){
        search_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileBottomSheet profileBottomSheet = new ProfileBottomSheet();
                profileBottomSheet.show(getSupportFragmentManager(), "My Profile");
            }
        });
    }

    public void read_users(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    for(DataSnapshot ds2 : ds.getChildren()){
                        User new_user = ds2.getValue(User.class);
                        Log.d("IN HOME RV ",  ds.getKey());
                        Log.d("IN HOME RV ",  ds2.getKey());
//                        Log.d("IN HOME RV", new_user.getFname());
//                        Log.d("IN HOME RV", new_user.getLname());
//                        Log.d("IN HOME RV", new_user.getNumber());


                        assert new_user != null;
                        assert user != null;

                        users.add(new_user);
                    }
                }
                MyRvAdapter = new UserAdapter(HomeActivity.this, users);
                rv.setAdapter(MyRvAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }





    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_home);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child(user.getUid());
        storageReference = FirebaseStorage.getInstance().getReference();

        toolbar = findViewById(R.id.myAppBar);
        rv = findViewById(R.id.recycler_view);
        dl = (DrawerLayout)findViewById(R.id.nav_drawer);
        nv = (NavigationView)findViewById(R.id.nv);
        View header=nv.getHeaderView(0);
        civ = header.findViewById(R.id.profile);
        nav_email = header.findViewById(R.id.email);
        nav_name = header.findViewById(R.id.name);
        title = toolbar.findViewById(R.id.title);
        search_img = findViewById(R.id.profile_img);

        title.setText("BistroChat");
        toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24);
        t = new ActionBarDrawerToggle(this, dl, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        dl.addDrawerListener(t);
        t.setDrawerIndicatorEnabled(true);
        t.syncState();

        users = new ArrayList<User>();

        set_navigation();
        retrieve_user();
        set_navHeaderFields();
        open_bottomSheet();

        read_users();


//        Log.d("IN HOME RV", users.get(0).getFname());
//        Log.d("IN HOME RV", users.get(0).getLname());
//        Log.d("IN HOME RV", users.get(0).getNumber());
//
//        Log.d("IN HOME RV", users.get(1).getFname());
//        Log.d("IN HOME RV", users.get(1).getLname());
//        Log.d("IN HOME RV", users.get(1).getNumber());

        MyRvAdapter = new UserAdapter(this, users);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this);
        rv.setLayoutManager(lm);
        rv.setAdapter(MyRvAdapter);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        t.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        t.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(t.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_menu, menu);

        if(menu instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }

        return true;
    }
}