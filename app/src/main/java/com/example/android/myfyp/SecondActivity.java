package com.example.android.myfyp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;


import com.google.firebase.auth.FirebaseAuth;
import com.onesignal.OneSignal;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class SecondActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    ArrayList<clubModel> clubModelList;

    private ProgressDialog progDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDataRef, mDataRef2, mDataRef3;
    private FirebaseDatabase mDatabase;
    private Button logout;
    private String key;
    private String [] arrayDelete;
    private FloatingActionButton fab = null;
    private FirebaseDatabase firebaseDatabase;
    private clubAdapter adapter;
    private String uId = FirebaseAuth.getInstance().getCurrentUser().toString();
    clubModel [] arrayName ;
    String username, email, age;;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        fab = (FloatingActionButton)findViewById(R.id.fab);
        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

//        // OneSignal Initialization
//        OneSignal.startInit(this)
//                .setNotificationOpenedHandler(new NotificationOpenedHandler(this))
//                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
//                .unsubscribeWhenNotificationsAreDisabled(true)
//                .init();
        OneSignal.sendTag("User_ID",firebaseAuth.getCurrentUser().getEmail());

        mDataRef = firebaseDatabase.getReference().child("Item Information");
        mDataRef2 = firebaseDatabase.getReference().child("Clubs");
        mDataRef3 = firebaseDatabase.getReference().child("Users");

//        mDataRef = mDataRef.child("Item Information");


//        mDataRef = mDataRef.child("Users").child(firebaseAuth.getCurrentUser().getUid());
//        mDataRef.addValueEventListener(new ValueEventListener() {
//        @Override
//        public void onDataChange(DataSnapshot dataSnapshot) {
//            UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
//            username =  userProfile.getUserName().toString();
//            age = userProfile.getUserAge().toString();
//            email = userProfile.getUserEmail().toString();
//            Log.d("before","#############################" + username);
//        }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Toast.makeText(SecondActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
//            }
//        });

//        finish();

//        logout = (Button)findViewById(R.id.btnLogout);

//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Logout();
//            }
//        });

//        super.onCreate(savedInstanceState);
//          setContentView(R.layout.activity_second);

        recyclerView = findViewById(R.id.rvv);
        toolbar = (Toolbar)findViewById(R.id.toolbarMain);
        toolbar.setTitle("Club and Societies");

        clubModelList = new ArrayList<>();
        mDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                clubModelList.clear();
                for (DataSnapshot child: snapshot.getChildren()) {
                    clubModel ClubModel = child.getValue(clubModel.class);
                    clubModelList.add(ClubModel);
                }
                arrayName = new clubModel[clubModelList.size()];
                arrayName = clubModelList.toArray(arrayName);
                adapter = new clubAdapter(SecondActivity.this, clubModelList);
                recyclerView.setAdapter(adapter);
                progDialog.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });
//        int[] images = {R.drawable.club, R.drawable.club, R.drawable.club, R.drawable.club, R.drawable.club, R.drawable.club, R.drawable.club};

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);


        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(SecondActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
                    public void onItemClick(View view,  int position) {
                        clubModel ClubModel1 = clubModelList.get(position);
                        Intent intent = new Intent(SecondActivity.this, ViewActivity.class);
                        intent.putExtra("myname", ClubModel1.getItem_name());
                        intent.putExtra("myplace", ClubModel1.getItem_place());
                        intent.putExtra("myprice",ClubModel1.getItem_price());
                        intent.putExtra("myurl",ClubModel1.getImageLink());
                        intent.putExtra("mykey",mDataRef.getKey().toString());


                        Log.d("****itemname","" + ClubModel1.getItem_name());
                        Log.d("****itemplace","" + ClubModel1.getItem_place());
                        Log.d("****itemprice","" + ClubModel1.getItem_price());
                        startActivity(intent);
                    }
                    public void onItemLongPress(View childView, int position) {
                    }
                })
        );
        AccountHeader headerResult = new AccountHeaderBuilder()

                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName(firebaseAuth.getCurrentUser().getDisplayName()).withEmail(firebaseAuth.getCurrentUser().getEmail())
//                                .withIcon(getResources().getDrawable(R.drawable.profile))
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName("Profile");
        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(2).withName("List of users");
        PrimaryDrawerItem item3 = new PrimaryDrawerItem().withIdentifier(3).withName("Edit and View Posted");
        PrimaryDrawerItem item4 = new PrimaryDrawerItem().withIdentifier(4).withName("Logout");
        PrimaryDrawerItem item5 = new PrimaryDrawerItem().withIdentifier(5).withName("Inbox");
        PrimaryDrawerItem item6 = new PrimaryDrawerItem().withIdentifier(6).withName("List of Clubs with search");

        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .withToolbar(toolbar)
                .addDrawerItems(
                        item1,
                        item2,
                        item3,
                        item4,
                        item5,
                        item6
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch(position){
                            case 1:
                                mDataRef3.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild(firebaseAuth.getCurrentUser().getUid())){
                                            if (!firebaseAuth.getCurrentUser().getUid().equals("XHR842kZD3cTZTwz7nM5LWJESW72")) {
                                                startActivity(new Intent(SecondActivity.this, ProfileActivity.class));
                                            }
                                        }else{
                                            startActivity(new Intent(SecondActivity.this, ClubProfileActivity.class));
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                break;
                            case 2:
                                startActivity(new Intent(SecondActivity.this, Users.class));
                                break;
                            case 3:
                                startActivity(new Intent(SecondActivity.this, ActivityPosted.class));
                                break;
                            case 4:
                                Logout();
                                break;
                            case 5:
                                startActivity(new Intent(SecondActivity.this, Inbox.class));
                                break;
                            case 6:
                                startActivity(new Intent(SecondActivity.this, ListOfClubsActivity.class));
                                break;
                        }
                        return true;
                    }
                })
                .build();
        Log.d("after","###############################"+ username);

//        progDialog = new ProgressDialog(SecondActivity.this,
//                R.style.Theme_AppCompat_DayNight_NoActionBar);
//        progDialog.setIndeterminate(true);
//        progDialog.setMessage("Wait....");
//        progDialog.show();

        progDialog=ProgressDialog.show(this,null,"Wait.....");
        Log.d("after","############################### wait");
        progDialog.setContentView(new ProgressBar(this));

        mDataRef3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(firebaseAuth.getCurrentUser().getUid())){
                    if (!firebaseAuth.getCurrentUser().getUid().equals("XHR842kZD3cTZTwz7nM5LWJESW72")) {
                        fab.setVisibility(View.GONE);
                    }else{
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(SecondActivity.this, AddClubs.class));
                            }
                        });
                    }
                }else{
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(SecondActivity.this, AddActivity.class));
                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void Logout(){
        Log.d("out","###############################"+ username);
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(SecondActivity.this, MainActivity.class));
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        switch(item.getItemId()){
//            case R.id.logoutMenu:{
//                Logout();
//            }
//            case R.id.profileMenu:
//                startActivity(new Intent(SecondActivity.this, ProfileActivity.class));
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onBackPressed() {
        Log.d("backpressed", "onBackPressed Called");
        finish();
    }
}
