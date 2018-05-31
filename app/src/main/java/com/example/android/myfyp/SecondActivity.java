package com.example.android.myfyp;

import android.animation.Animator;
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

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    FloatingActionButton fab, fab1, fab2;
    LinearLayout fabLayout1, fabLayout2;
    View fabBGLayout;
    boolean isFABOpen=false;

    private ProgressDialog progDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDataRef, mDataRef2, mDataRef3;
    private FirebaseDatabase mDatabase;
    private Button logout;
    private String key;
    private String [] arrayDelete;
    private FirebaseDatabase firebaseDatabase;
    private clubAdapter adapter;
    private String uId = FirebaseAuth.getInstance().getCurrentUser().toString();

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef;

    clubModel [] arrayName ;
    String username, email, age;;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        fab = (FloatingActionButton)findViewById(R.id.fab);
        fabLayout1= (LinearLayout) findViewById(R.id.fabLayout1);
        fabLayout2= (LinearLayout) findViewById(R.id.fabLayout2);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2= (FloatingActionButton) findViewById(R.id.fab2);
        fabBGLayout=findViewById(R.id.fabBGLayout);
        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        OneSignal.sendTag("User_ID",firebaseAuth.getCurrentUser().getUid());

        mDataRef = firebaseDatabase.getReference().child("Item Information");
        mDataRef2 = firebaseDatabase.getReference().child("Clubs");
        mDataRef3 = firebaseDatabase.getReference().child("Users");

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

                        startActivity(intent);
                    }
                    public void onItemLongPress(View childView, final int position) {
                        if(firebaseAuth.getCurrentUser().getUid().equals("XHR842kZD3cTZTwz7nM5LWJESW72")){
                            final clubModel ClubModel1 = clubModelList.get(position);
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(SecondActivity.this);
                            alertDialog.setTitle("Delete?");

                            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int which) {
                                    dialog.cancel();
                                }
                            });

                            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    mDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            arrayDelete = new String[(int) dataSnapshot.getChildrenCount()];
                                            int x = 0;
                                            for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {
                                                key = postsnapshot.getKey();
                                                arrayDelete[x] = key;
                                                x += 1;
                                            }
                                            String getUrl = ClubModel1.getImageLink();
                                            storageRef = storage.getReferenceFromUrl(getUrl);
                                            storageRef.delete();
                                            dataSnapshot.getRef().child(arrayDelete[position]).removeValue();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            });
                            alertDialog.show();
                        }
                    }
                })
        );

        progDialog=ProgressDialog.show(this,null,"Wait.....");
        progDialog.setContentView(new ProgressBar(this));
        mDataRef3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(firebaseAuth.getCurrentUser().getUid()) && !firebaseAuth.getCurrentUser().getUid().equals("XHR842kZD3cTZTwz7nM5LWJESW72")) {
                    AccountHeader headerResult = new AccountHeaderBuilder()
                            .withActivity(SecondActivity.this)
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
                    PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(2).withName("Inbox");
                    PrimaryDrawerItem item3 = new PrimaryDrawerItem().withIdentifier(3).withName("List of Clubs with search");
                    PrimaryDrawerItem item4 = new PrimaryDrawerItem().withIdentifier(4).withName("List of joined for students");
                    PrimaryDrawerItem item5 = new PrimaryDrawerItem().withIdentifier(5).withName("Logout");


                    Drawer result = new DrawerBuilder()
                            .withActivity(SecondActivity.this)
                            .withAccountHeader(headerResult)
                            .withToolbar(toolbar)
                            .addDrawerItems(
                                    item1,
                                    item2,
                                    item3,
                                    item4,
                                    new DividerDrawerItem(),
                                    item5
                            )
                            .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                                @Override
                                public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                    switch(position){
                                        case 1:
                                            startActivity(new Intent(SecondActivity.this, ProfileActivity.class));
                                            break;
                                        case 2:
                                            startActivity(new Intent(SecondActivity.this, Inbox.class));
                                            break;
                                        case 3:
                                            startActivity(new Intent(SecondActivity.this, ListOfClubsActivity.class));
                                            break;
                                        case 4:
                                            startActivity(new Intent(SecondActivity.this, List_of_joined.class));
                                            break;
                                        case 5:
                                            break;
                                        case 6:
                                            Logout();
                                            break;
                                    }
                                    return true;
                                }
                            })
                            .build();
                }
                else if (dataSnapshot.hasChild(firebaseAuth.getCurrentUser().getUid()) && firebaseAuth.getCurrentUser().getUid().equals("XHR842kZD3cTZTwz7nM5LWJESW72")){

                    AccountHeader headerResult = new AccountHeaderBuilder()
                            .withActivity(SecondActivity.this)
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
                    PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(2).withName("Inbox");
                    PrimaryDrawerItem item3 = new PrimaryDrawerItem().withIdentifier(4).withName("Edit and view posted");
                    PrimaryDrawerItem item4 = new PrimaryDrawerItem().withIdentifier(3).withName("List of Clubs with search");
                    PrimaryDrawerItem item5 = new PrimaryDrawerItem().withIdentifier(5).withName("Logout");


                    Drawer result = new DrawerBuilder()
                            .withActivity(SecondActivity.this)
                            .withAccountHeader(headerResult)
                            .withToolbar(toolbar)
                            .addDrawerItems(
                                    item1,
                                    item2,
                                    item3,
                                    item4,
                                    new DividerDrawerItem(),
                                    item5
                            )
                            .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                                @Override
                                public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                    switch(position){
                                        case 1:
                                            startActivity(new Intent(SecondActivity.this, ProfileActivity.class));
                                            break;
                                        case 2:
                                            startActivity(new Intent(SecondActivity.this, Inbox.class));
                                            break;
                                        case 3:
                                            startActivity(new Intent(SecondActivity.this, ActivityPosted.class));
                                            break;
                                        case 4:
                                            startActivity(new Intent(SecondActivity.this, ListOfClubsActivity.class));
                                            break;
                                        case 5:
                                            break;
                                        case 6:
                                            Logout();
                                            break;
                                    }
                                    return true;
                                }
                            })
                            .build();
                }
                else{
                    AccountHeader headerResult = new AccountHeaderBuilder()
                            .withActivity(SecondActivity.this)
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
                    PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(2).withName("Inbox");
                    PrimaryDrawerItem item3 = new PrimaryDrawerItem().withIdentifier(3).withName("Edit and view posted");
                    PrimaryDrawerItem item4 = new PrimaryDrawerItem().withIdentifier(4).withName("List of Clubs with search");
                    PrimaryDrawerItem item5 = new PrimaryDrawerItem().withIdentifier(5).withName("List of pendings for clubs");
                    PrimaryDrawerItem item6 = new PrimaryDrawerItem().withIdentifier(6).withName("List of successful for clubs");
                    PrimaryDrawerItem item7 = new PrimaryDrawerItem().withIdentifier(7).withName("Logout");

                    Drawer result = new DrawerBuilder()
                            .withActivity(SecondActivity.this)
                            .withAccountHeader(headerResult)
                            .withToolbar(toolbar)
                            .addDrawerItems(
                                    item1,
                                    item2,
                                    item3,
                                    item4,
                                    item5,
                                    item6,
                                    new DividerDrawerItem(),
                                    item7
                            )
                            .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                                @Override
                                public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                    switch(position){
                                        case 1:
                                            startActivity(new Intent(SecondActivity.this, ClubProfileActivity.class));
                                            break;
                                        case 2:
                                            startActivity(new Intent(SecondActivity.this, Inbox.class));
                                            break;
                                        case 3:
                                            startActivity(new Intent(SecondActivity.this, ActivityPosted.class));
                                            break;
                                        case 4:
                                            startActivity(new Intent(SecondActivity.this, ListOfClubsActivity.class));
                                            break;
                                        case 5:
                                            startActivity(new Intent(SecondActivity.this, List_of_pending.class));
                                            break;
                                        case 6:
                                            startActivity(new Intent(SecondActivity.this, List_of_successful.class));
                                            break;
                                        case 7:
                                            break;
                                        case 8:
                                            Logout();
                                            break;
                                    }
                                    return true;
                                }
                            })
                            .build();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





//        AccountHeader headerResult = new AccountHeaderBuilder()
//
//                .withActivity(this)
//                .withHeaderBackground(R.drawable.header)
//                .addProfiles(
//                        new ProfileDrawerItem().withName(firebaseAuth.getCurrentUser().getDisplayName()).withEmail(firebaseAuth.getCurrentUser().getEmail())
////                                .withIcon(getResources().getDrawable(R.drawable.profile))
//                )
//                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
//                    @Override
//                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
//                        return false;
//                    }
//                })
//                .build();
//
//        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName("Profile");
//        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(2).withName("Inbox");
//        PrimaryDrawerItem item3 = new PrimaryDrawerItem().withIdentifier(3).withName("List of Clubs with search");
//        PrimaryDrawerItem item4 = new PrimaryDrawerItem().withIdentifier(4).withName("Logout");
//        PrimaryDrawerItem item5 = new PrimaryDrawerItem().withIdentifier(5).withName("Edit and view posted");
//        PrimaryDrawerItem item6 = new PrimaryDrawerItem().withIdentifier(6).withName("List of users");
//        PrimaryDrawerItem item7 = new PrimaryDrawerItem().withIdentifier(7).withName("List of pendings for clubs");
//        PrimaryDrawerItem item8 = new PrimaryDrawerItem().withIdentifier(8).withName("List of successful for clubs");
//        PrimaryDrawerItem item9 = new PrimaryDrawerItem().withIdentifier(9).withName("List of joined for students");
//
//        Drawer result = new DrawerBuilder()
//                .withActivity(this)
//                .withAccountHeader(headerResult)
//                .withToolbar(toolbar)
//                .addDrawerItems(
//                        item1,
//                        item2,
//                        item3,
//                        item4,
//                        new DividerDrawerItem(),
//                        item5,
//                        item6,
//                        item7,
//                        item8,
//                        item9
//                )
//                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
//                    @Override
//                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
//                        switch(position){
//                            case 1:
//                                mDataRef3.addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(DataSnapshot dataSnapshot) {
//                                        if (dataSnapshot.hasChild(firebaseAuth.getCurrentUser().getUid())){
//                                            if (!firebaseAuth.getCurrentUser().getUid().equals("XHR842kZD3cTZTwz7nM5LWJESW72")) {
//                                                startActivity(new Intent(SecondActivity.this, ProfileActivity.class));
//                                            }
//                                        }else{
//                                            startActivity(new Intent(SecondActivity.this, ClubProfileActivity.class));
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(DatabaseError databaseError) {
//
//                                    }
//                                });
//                                break;
//                            case 2:
//                                startActivity(new Intent(SecondActivity.this, Inbox.class));
//                                break;
//                            case 3:
//                                startActivity(new Intent(SecondActivity.this, ListOfClubsActivity.class));
//                                break;
//                            case 4:
//                                Logout();
//                                break;
//                            case 5:
//                                break;
//                            case 6:
//                                startActivity(new Intent(SecondActivity.this, ActivityPosted.class));
//                                break;
//                            case 7:
//                                startActivity(new Intent(SecondActivity.this, Users.class));
//                                break;
//                            case 8:
//                                startActivity(new Intent(SecondActivity.this, List_of_pending.class));
//                                break;
//                            case 9:
//                                startActivity(new Intent(SecondActivity.this, List_of_successful.class));
//                                break;
//                            case 10:
//                                startActivity(new Intent(SecondActivity.this, List_of_joined.class));
//                                break;
//                        }
//                        return true;
//                    }
//                })
//                .build();

//        progDialog=ProgressDialog.show(this,null,"Wait.....");
//        progDialog.setContentView(new ProgressBar(this));
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
                                if(!isFABOpen){
                                    showFABMenu();
                                    fab1.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            startActivity(new Intent(SecondActivity.this, AddClubs.class));
                                        }
                                    });
                                    fab2.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            startActivity(new Intent(SecondActivity.this, AddActivity.class));
                                        }
                                    });
                                }else{
                                    closeFABMenu();
                                }
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
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(SecondActivity.this, MainActivity.class));
    }

    private void showFABMenu(){
        isFABOpen=true;
        fabLayout1.setVisibility(View.VISIBLE);
        fabLayout2.setVisibility(View.VISIBLE);
        fabBGLayout.setVisibility(View.VISIBLE);

        fab.animate().rotationBy(180);
        fabLayout1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fabLayout2.animate().translationY(-getResources().getDimension(R.dimen.standard_100));
    }

    private void closeFABMenu(){
        isFABOpen=false;
        fabBGLayout.setVisibility(View.GONE);
        fab.animate().rotationBy(-180);
        fabLayout1.animate().translationY(0);
        fabLayout2.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if(!isFABOpen){
                    fabLayout1.setVisibility(View.GONE);
                    fabLayout2.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(isFABOpen){
            closeFABMenu();
        }else{
            super.onBackPressed();
        }
    }
}
