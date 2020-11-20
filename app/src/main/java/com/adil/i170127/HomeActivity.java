package com.adil.i170127;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.cardemulation.HostNfcFService;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.service.autofill.UserData;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.google.firebase.database.core.view.QueryParams;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
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
    RelativeLayout rl;
    UserAdapter MyRvAdapter;

    TextView nav_email, nav_name;
    CircleImageView civ, search_img;
    EditText searchbar;
    List<User> users;
    List<String> contactsList;
    User userData;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_home);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users").child(user.getUid());
        storageReference = FirebaseStorage.getInstance().getReference();

        rl = findViewById(R.id.bar_layout);
        toolbar = rl.findViewById(R.id.myAppBar);
        rv = findViewById(R.id.recycler_view);
        dl = (DrawerLayout) findViewById(R.id.nav_drawer);
        nv = (NavigationView) findViewById(R.id.nv);
        View header = nv.getHeaderView(0);
        civ = header.findViewById(R.id.profile);
        searchbar = findViewById(R.id.searchbar);
        nav_email = header.findViewById(R.id.email);
        nav_name = header.findViewById(R.id.name);
        search_img = findViewById(R.id.profile_img);

        toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24);
        t = new ActionBarDrawerToggle(this, dl, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        dl.addDrawerListener(t);
        t.setDrawerIndicatorEnabled(true);
        t.syncState();

        users = new ArrayList<User>();
        searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        set_navigation();
        retrieve_user();
        open_bottomSheet();

        //read_users();
        getContactList();

        MyRvAdapter = new UserAdapter(this, users);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this);
        rv.setLayoutManager(lm);
        rv.setAdapter(MyRvAdapter);
    }

    public void nav_logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        Toast.makeText(HomeActivity.this, "Logout", Toast.LENGTH_SHORT).show();
    }

    public void set_navigation() {
        nv.setItemIconTintList(null);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
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

    public void retrieve_user() {
        reference = database.getReference("Users").child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    userData = ds.getValue(User.class);
                    nav_name.setText(userData.getFname() + " " + userData.getLname());
                    nav_email.setText(user.getEmail());
                    Picasso.get().load(userData.getImgUri()).fit().centerCrop().into(civ);
                    Picasso.get().load(userData.getImgUri()).fit().centerCrop().into(search_img);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getContactList() {
        contactsList = new ArrayList<>();
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        while (cursor.moveToNext()) {
            String phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if (contactsList.contains(phone)) {
                cursor.moveToNext();
            } else {
                contactsList.add(phone);

            }
        }

        Log.d("ContactsList: ", contactsList.get(0));
        Log.d("ContactsList: ", contactsList.get(1));
        Log.d("ContactsList: ", contactsList.get(2));
        Log.d("ContactsList: ", contactsList.get(3));

        read_users();
        //getActualUser();
        Log.d("contact: ", String.valueOf(contactsList.size()));

    }

    public void open_bottomSheet() {
        search_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileBottomSheet profileBottomSheet = new ProfileBottomSheet(userData, 0);
                profileBottomSheet.show(getSupportFragmentManager(), "My Profile");
            }
        });
    }

    public void read_users() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(searchbar.getText().toString().equals("")){
                    users.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (!user.getUid().equals(ds.getKey())) {
                            for (DataSnapshot ds2 : ds.getChildren()) {
                                User new_user = ds2.getValue(User.class);
                                assert new_user != null;
                                assert user != null;
                                for (String number : contactsList) {
                                    if (new_user.getNumber().equals(number)) {
                                        users.add(ds2.getValue(User.class));
                                    }
                                }
                            }
                        }
                    }
                    MyRvAdapter = new UserAdapter(HomeActivity.this, users);
                    rv.setAdapter(MyRvAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void searchUsers(final String s) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.d("In search: ", ds.getKey());
                    if (!user.getUid().equals(ds.getKey())) {
                        ds.getRef().orderByChild("fname").startAt(s).endAt(s + "\uf8ff")
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                                        Log.d("In search: ", ds.getKey());
                                        User new_user = ds.getValue(User.class);
                                        for (String number : contactsList) {
                                            if (new_user.getNumber().equals(number)) {
                                                users.add(ds.getValue(User.class));
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
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

//    private void status(final String status){
//        reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
//        HashMap<String, Object> hashMap = new HashMap<>();
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                    String key = ds.getKey();
//                    reference.child(key).child("status").setValue(status);
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

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        t.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        t.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (t.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_menu, menu);

        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }

        return true;
    }
}