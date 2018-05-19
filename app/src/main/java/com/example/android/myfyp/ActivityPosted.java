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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class ActivityPosted extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    ArrayList<clubModel> clubModelList;

    private ProgressDialog progDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDataRef;
    private FirebaseDatabase mDatabase;
    private Button logout;
    private int pos;
    private String key;
    private int [] arrayPosition;
    private String [] arrayUrl;
    private String [] arrayDelete;
    private FloatingActionButton fab = null;
    private FirebaseDatabase firebaseDatabase;
    private clubAdapter adapter;
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private StorageReference storageRef;

    clubModel [] arrayName ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posted);

        fab = (FloatingActionButton)findViewById(R.id.fabbb);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        mDataRef = firebaseDatabase.getReference().child("Item Information");

        recyclerView = findViewById(R.id.rvv);
        toolbar = (Toolbar)findViewById(R.id.toolbarMain);
        toolbar.setTitle("Long Press to Delete");

        clubModelList = new ArrayList<>();
        mDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                clubModelList.clear();
                arrayPosition = new int[(int)snapshot.getChildrenCount()];
                arrayUrl = new String[(int)snapshot.getChildrenCount()];
                pos = 0;
                int x = 0;
                for (DataSnapshot child: snapshot.getChildren()) {
                    clubModel ClubModel = child.getValue(clubModel.class);
                    String owner = firebaseAuth.getCurrentUser().getUid();

                    if(owner.equals(ClubModel.getItem_owner() )){
                        ClubModel.setItem_position(pos);
                        ClubModel.setParentkey(child.getKey().toString());
                        mDataRef.child(child.getKey()).setValue(ClubModel);
                        arrayPosition[x] = pos;
                        arrayUrl[x] = ClubModel.getImageLink();
                        clubModelList.add(ClubModel);
                        x += 1;
                    }
                    pos +=1;
                }
                arrayName = new clubModel[clubModelList.size()];
                arrayName = clubModelList.toArray(arrayName);
                adapter = new clubAdapter(ActivityPosted.this, clubModelList);
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
                new RecyclerItemClickListener(ActivityPosted.this, new RecyclerItemClickListener.OnItemClickListener() {
                     public void onItemClick(View view,  int position) {
                        clubModel ClubModel1 = clubModelList.get(position);
                        Intent intent = new Intent(ActivityPosted.this, ViewActivity.class);
                        intent.putExtra("myname", ClubModel1.getItem_name());
                        intent.putExtra("myplace", ClubModel1.getItem_place());
                        intent.putExtra("myprice",ClubModel1.getItem_price());
                        intent.putExtra("myurl",ClubModel1.getImageLink());
                        intent.putExtra("myowner", ClubModel1.getItem_owner());
                        intent.putExtra("mykey",ClubModel1.getParentkey());
                        startActivity(intent);
                    }

                    public void onItemLongPress(View childView, final int position) {

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ActivityPosted.this);
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
                                        String getUrl = arrayUrl[position];
                                        storageRef = storage.getReferenceFromUrl(getUrl);
                                        storageRef.delete();
                                        dataSnapshot.getRef().child(arrayDelete[arrayPosition[position]]).removeValue();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        });
                        alertDialog.show();
                    }
                })
        );

        progDialog=ProgressDialog.show(this,null,"Wait.....");
        progDialog.setContentView(new ProgressBar(this));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(ActivityPosted.this, SecondActivity.class));
            }
        });
    }
}
